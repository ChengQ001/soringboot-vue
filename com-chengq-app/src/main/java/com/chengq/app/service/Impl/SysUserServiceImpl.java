package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.User;
import com.chengq.app.exception.BizCodes;
import com.chengq.app.exception.BizException;
import com.chengq.app.mapper.UserMapper;
import com.chengq.api.model.SysUserAddRequest;
import com.chengq.api.model.SysUserUpdateRequest;
import com.chengq.api.model.SysUserVO;
import com.chengq.app.service.interfaces.ISysUserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_USERNAME = "admin";

    private static boolean isProtectedAdmin(User user) {
        return user != null && ADMIN_USERNAME.equalsIgnoreCase(user.getUsername());
    }

    @Override
    public List<SysUserVO> listUsers() {
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getId));
        return users.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public SysUserVO addUser(SysUserAddRequest request) {
        if (!StringUtils.hasText(request.getPhone())) {
            throw new BizException(BizCodes.BAD_REQUEST, "手机号不能为空");
        }
        if (userMapper.selectByPhone(request.getPhone().trim()) != null) {
            throw new BizException(BizCodes.CONFLICT, "手机号已存在");
        }
        if (StringUtils.hasText(request.getUsername())) {
            User exists = userMapper.selectByUsername(request.getUsername().trim());
            if (exists != null) {
                throw new BizException(BizCodes.CONFLICT, "用户名已存在");
            }
        }
        User user = new User();
        user.setUsername(trimToNull(request.getUsername()));
        user.setPhone(request.getPhone().trim());
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setDescription(trimToNull(request.getDescription()));
        userMapper.insert(user);
        return toVo(userMapper.selectById(user.getId()));
    }

    @Override
    public SysUserVO updateUser(SysUserUpdateRequest request) {
        if (request.getId() == null) {
            throw new BizException(BizCodes.BAD_REQUEST, "用户ID不能为空");
        }
        User user = userMapper.selectById(request.getId());
        if (user == null) {
            throw new BizException(BizCodes.NOT_FOUND, "用户不存在");
        }
        if (StringUtils.hasText(request.getPhone())) {
            String phone = request.getPhone().trim();
            User other = userMapper.selectByPhone(phone);
            if (other != null && !other.getId().equals(user.getId())) {
                throw new BizException(BizCodes.CONFLICT, "手机号已被其他用户使用");
            }
            user.setPhone(phone);
        }
        if (request.getUsername() != null) {
            String name = trimToNull(request.getUsername());
            if (name != null) {
                User other = userMapper.selectByUsername(name);
                if (other != null && !other.getId().equals(user.getId())) {
                    throw new BizException(BizCodes.CONFLICT, "用户名已被占用");
                }
            }
            user.setUsername(name);
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getDescription() != null) {
            user.setDescription(trimToNull(request.getDescription()));
        }
        userMapper.updateById(user);
        return toVo(userMapper.selectById(user.getId()));
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BizException(BizCodes.BAD_REQUEST, "用户ID不能为空");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(BizCodes.NOT_FOUND, "用户不存在");
        }
        if (isProtectedAdmin(user)) {
            throw new BizException(BizCodes.FORBIDDEN, "admin 账号为系统保留账号，禁止删除");
        }
        userMapper.deleteById(id);
    }

    private SysUserVO toVo(User u) {
        if (u == null) {
            return null;
        }
        SysUserVO vo = new SysUserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setPhone(u.getPhone());
        vo.setDescription(u.getDescription());
        vo.setCreatedAt(u.getCreatedAt());
        vo.setUpdatedAt(u.getUpdatedAt());
        return vo;
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
