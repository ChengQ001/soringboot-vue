package com.chengq.app.service.Impl;

import com.chengq.api.entity.Park;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.User;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.UserMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.ParkLoginItem;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.app.exception.BizCodes;
import com.chengq.app.exception.BizException;
import com.chengq.app.service.interfaces.IParkAccessService;
import com.chengq.app.service.interfaces.IUserService;
import com.chengq.app.util.JwtUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务层实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final IParkAccessService parkAccessService;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            RoleMapper roleMapper,
            IParkAccessService parkAccessService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.parkAccessService = parkAccessService;
        initUsers();
    }

    /**
     * 空库时仅插入 admin（与 sql/init_database.sql 一致；角色绑定仍依赖库内数据）
     */
    private void initUsers() {
        try {
            int count = userMapper.countTotalUsers();
            if (count == 0) {
                log.info("Empty database: inserting default admin user");
                User admin = new User();
                admin.setUsername("admin");
                admin.setPhone("17688888888");
                admin.setPassword("$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6");
                admin.setDescription("系统管理员");
                userMapper.insert(admin);
                log.info("Default admin user inserted");
            }
        } catch (Exception e) {
            log.warn("Failed to initialize default user: {}", e.getMessage());
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String phone = loginRequest.getPhone() == null ? null : loginRequest.getPhone().trim();
        log.debug("Login attempt, phone present: {}", phone != null);

        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("phone", phone));
        if (user == null) {
            throw new BizException(BizCodes.UNAUTHORIZED, "手机号或密码错误");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BizException(BizCodes.UNAUTHORIZED, "手机号或密码错误");
        }

        String token = jwtUtil.generateToken(user);
        User full = getUserByUsername(user.getUsername());
        String primaryRole = "USER";
        LoginResponse resp = new LoginResponse("Bearer " + token, full.getUsername(), primaryRole);
        resp.setId(full.getId());
        resp.setPhone(full.getPhone());
        if (full.getRoles() != null) {
            for (Role r : full.getRoles()) {
                if (r != null && r.getName() != null) {
                    resp.getRoles().add(r.getName());
                    resp.getRoleIds().add(r.getId());
                }
            }
            if (!resp.getRoles().isEmpty()) {
                resp.setRole(resp.getRoles().get(0));
            }
        }
        List<Park> parks = parkAccessService.listAccessibleParks(full.getId());
        for (Park p : parks) {
            if (p != null && p.getId() != null) {
                resp.getParks().add(new ParkLoginItem(p.getId(), p.getName()));
            }
        }
        if (!resp.getParks().isEmpty()) {
            resp.setDefaultParkId(resp.getParks().get(0).getId());
        }
        log.debug("Login success for userId {}", full.getId());
        return resp;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        user.setPermissions(Arrays.asList("user:read"));

        try {
            List<UserRole> userRoles = userRoleMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, user.getId()));
            if (userRoles != null && !userRoles.isEmpty()) {
                List<Long> roleIds = new ArrayList<>();
                for (UserRole ur : userRoles) {
                    if (ur != null && ur.getRoleId() != null) {
                        roleIds.add(ur.getRoleId());
                    }
                }
                if (!roleIds.isEmpty()) {
                    List<Role> roles = roleMapper.selectBatchIds(roleIds);
                    user.setRoles(roles);
                }
            }
        } catch (Exception e) {
            log.debug("Load roles for user {} failed: {}", username, e.getMessage());
        }

        return user;
    }

    @Override
    public String register(RegisterRequest registerRequest) {
        try {
            log.info("Registering user: {}, phone: {}", registerRequest.getUsername(), registerRequest.getPhone());

            User byPhone = userMapper.selectByPhone(registerRequest.getPhone());
            if (byPhone != null) {
                throw new BizException(BizCodes.CONFLICT, "手机号已被注册");
            }

            User byName = userMapper.selectByUsername(registerRequest.getUsername());
            if (byName != null) {
                throw new BizException(BizCodes.CONFLICT, "用户名已被占用");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPhone(registerRequest.getPhone());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            if (registerRequest.getDescription() != null && !registerRequest.getDescription().isBlank()) {
                user.setDescription(registerRequest.getDescription().trim());
            }

            userMapper.insert(user);
            log.info("User registered successfully: {}", registerRequest.getUsername());
            return "注册成功，请登录";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to register user: {}", e.getMessage(), e);
            throw new BizException(BizCodes.INTERNAL_ERROR, "注册失败：" + (e.getMessage() != null ? e.getMessage() : "未知原因"), e);
        }
    }

    @Override
    public String updateUser(UpdateUserRequest request) {
        try {
            log.info("Updating user with id: {}", request.getId());

            User user = userMapper.selectById(request.getId());
            if (user == null) {
                throw new BizException(BizCodes.NOT_FOUND, "未找到用户，编号：" + request.getId());
            }

            if (request.getUsername() != null) {
                User existingUser = userMapper.selectByUsername(request.getUsername());
                if (existingUser != null && !existingUser.getId().equals(request.getId())) {
                    throw new BizException(BizCodes.CONFLICT, "用户名已被占用：" + request.getUsername());
                }
                user.setUsername(request.getUsername());
            }

            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User currentUser = (User) authentication.getPrincipal();
                user.setUpdatedBy(currentUser.getId());
                user.setUpdatedByName(currentUser.getUsername());
                log.debug("Set updatedBy={}, updatedByName={}", currentUser.getId(), currentUser.getUsername());
            }

            userMapper.updateById(user);
            log.info("User updated successfully: {}", request.getId());
            return "User updated successfully: " + request.getId();
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage());
            throw new BizException(BizCodes.INTERNAL_ERROR, "更新用户失败：" + (e.getMessage() != null ? e.getMessage() : "未知原因"), e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }
}
