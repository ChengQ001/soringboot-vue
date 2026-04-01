package com.chengq.app.service.Impl;

import com.chengq.api.entity.Park;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.User;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.UserMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.api.model.DeleteUserRequest;
import com.chengq.api.model.LoginRequest;
import com.chengq.api.model.LoginResponse;
import com.chengq.api.model.ParkLoginItem;
import com.chengq.api.model.RegisterRequest;
import com.chengq.api.model.UpdateUserRequest;
import com.chengq.api.model.UserRequest;
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
 * 实现用户相关的业务逻辑，使用MyBatis Plus进行数据库操作
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
     * 初始化用户数据到数据库
     */
    private void initUsers() {
        try {
            // 检查是否已有用户数据
            int count = userMapper.countTotalUsers();
            if (count == 0) {
                log.info("Initializing default users");
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setPhone("17688888888");
                admin.setPassword("$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6");
                admin.setDescription("系统管理员");
                userMapper.insert(admin);
                
                User user = new User();
                user.setUsername("user");
                user.setPhone("13900139000");
                user.setPassword("$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6");
                user.setDescription("普通用户");
                userMapper.insert(user);
                
                log.info("Default users initialized successfully");
            }
        } catch (Exception e) {
            log.warn("Failed to initialize users: {}", e.getMessage());
        }
    }

    /**
     * 认证用户
     */
    public boolean authenticate(String username, String password) {
        try {
            User user = getUserByUsername(username);
            if (user == null) {
                log.warn("User not found: {}", username);
                return false;
            }
            log.info("User found: {}, password in DB: {}", username, user.getPassword());
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            log.info("Password match result: {}", matches);
            return matches;
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 用户登录
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // LoginRequest.phone：前端传入手机号（后端使用手机号查 tb_user + BCrypt 校验密码）
            String phone = (loginRequest.getPhone() == null ? null : loginRequest.getPhone().trim());
            System.out.println("Login attempt for phone: " + phone);
            
            // 使用MyBatis Plus的selectOne方法查询用户
            User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("phone", phone));
            System.out.println("User found: " + user);
            
            if (user == null) {
                System.out.println("User not found for phone: " + phone);
                throw new RuntimeException("Invalid phone or password");
            }
            
            // 校验密码是否匹配数据库（JWT 登录的关键鉴权点：输入密码明文 vs DB BCrypt 密文）
            boolean matches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!matches) {
                System.out.println("Password mismatch for phone: " + phone);
                throw new RuntimeException("Invalid phone or password");
            }

            // 签发 JWT：subject 放用户名（用于后续 JwtAuthenticationFilter loadUserByUsername(username））
            String token = jwtUtil.generateToken(user);
            System.out.println("Token generated: " + token);

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

            System.out.println("Login successful for phone: " + phone);
            return resp;
            
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    public User getUserByUsername(String username) {
        User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        // 设置权限列表（从数据库查询或硬编码）
        user.setPermissions(Arrays.asList("user:read"));

        // 加载用户角色（用于前端展示 + ROLE_ 授权）
        try {
            List<UserRole> userRoles = userRoleMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                            .eq(UserRole::getUserId, user.getId())
            );
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

    /**
     * 获取所有用户列表
     */
    @Override
    public List<String> getUsers(UserRequest request) {
        log.info("Getting all users from database");
        List<User> users = userMapper.selectList(null);
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    /**
     * 获取所有用户列表（测试方法）
     */
    @Override
    public List<String> getUsersTest(UserRequest request) {
        log.info("Getting all users test from database");
        List<User> users = userMapper.selectList(null);
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    /**
     * 创建新用户
     */
    @Override
    public String createUser(UserRequest request) {
        try {
            log.info("Creating user: {}", request.getUsername());
            
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            
            userMapper.insert(user);
            return "User created successfully: " + request.getUsername();
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @Override
    public String deleteUser(DeleteUserRequest request) {
        try {
            log.info("Deleting user with id: {}", request.getId());
            User user = userMapper.selectById(request.getId());
            if (user != null && "admin".equalsIgnoreCase(user.getUsername())) {
                throw new RuntimeException("admin 账号为系统保留账号，禁止删除");
            }
            userMapper.deleteById(request.getId());
            return "User deleted successfully: " + request.getId();
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @Override
    public String register(RegisterRequest registerRequest) {
        try {
            log.info("Registering user: {}, phone: {}", registerRequest.getUsername(), registerRequest.getPhone());

            User byPhone = userMapper.selectByPhone(registerRequest.getPhone());
            if (byPhone != null) {
                throw new RuntimeException("手机号已被注册");
            }

            User byName = userMapper.selectByUsername(registerRequest.getUsername());
            if (byName != null) {
                throw new RuntimeException("用户名已被占用");
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
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @Override
    public String updateUser(UpdateUserRequest request) {
        try {
            log.info("Updating user with id: {}", request.getId());
            
            // 查询用户是否存在
            User user = userMapper.selectById(request.getId());
            if (user == null) {
                throw new RuntimeException("User not found with id: " + request.getId());
            }
            
            // 更新用户信息
            if (request.getUsername() != null) {
                // 检查用户名是否已被其他用户使用
                User existingUser = userMapper.selectByUsername(request.getUsername());
                if (existingUser != null && !existingUser.getId().equals(request.getId())) {
                    throw new RuntimeException("Username already exists: " + request.getUsername());
                }
                user.setUsername(request.getUsername());
            }
            
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // 角色现在通过用户角色关联表管理
            
            // 获取当前登录用户信息
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
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    /**
     * 加载用户详情（实现UserDetailsService接口）
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        return user;
    }
}
