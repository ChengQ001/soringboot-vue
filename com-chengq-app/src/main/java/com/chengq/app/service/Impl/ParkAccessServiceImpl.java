package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.Park;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.User;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.ParkMapper;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.service.interfaces.IParkAccessService;
import com.chengq.app.util.ParkContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkAccessServiceImpl implements IParkAccessService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final ParkMapper parkMapper;

    @Override
    public List<Park> listAccessibleParks(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        List<UserRole> urs = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        if (urs == null || urs.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = urs.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        boolean admin = roles != null && roles.stream().anyMatch(r -> r != null && "ADMIN".equals(r.getName()));
        List<Park> allOrdered = parkMapper.selectList(new LambdaQueryWrapper<Park>().orderByAsc(Park::getId));
        if (admin) {
            return allOrdered;
        }
        // 非管理员：仅从 tb_user_role 中出现的非空 park_id 收集园区（去重，顺序同 tb_park.id）
        LinkedHashSet<Long> parkIds = new LinkedHashSet<>();
        for (UserRole ur : urs) {
            if (ur != null && ur.getParkId() != null) {
                parkIds.add(ur.getParkId());
            }
        }
        if (parkIds.isEmpty()) {
            return new ArrayList<>();
        }
        return allOrdered.stream()
                .filter(p -> p != null && parkIds.contains(p.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void resolveAndSetCurrentPark(User user, Long requestedParkId) {
        if (user == null || user.getId() == null) {
            ParkContext.setParkId(null);
            return;
        }
        List<Park> allowed = listAccessibleParks(user.getId());
        if (allowed.isEmpty()) {
            ParkContext.setParkId(null);
            return;
        }
        Long resolved = requestedParkId;
        if (resolved != null && allowed.stream().anyMatch(p -> p.getId().equals(resolved))) {
            ParkContext.setParkId(resolved);
            return;
        }
        ParkContext.setParkId(allowed.get(0).getId());
    }
}
