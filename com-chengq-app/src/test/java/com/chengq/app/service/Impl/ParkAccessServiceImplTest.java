package com.chengq.app.service.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.chengq.api.entity.Park;
import com.chengq.api.entity.Role;
import com.chengq.api.entity.User;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.ParkMapper;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.util.ParkContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkAccessServiceImplTest {

    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private ParkMapper parkMapper;

    @InjectMocks
    private ParkAccessServiceImpl parkAccessService;

    @AfterEach
    void tearDown() {
        ParkContext.clear();
    }

    @Test
    void listAccessibleParks_nullUserId_returnsEmpty() {
        assertTrue(parkAccessService.listAccessibleParks(null).isEmpty());
    }

    @Test
    void listAccessibleParks_admin_returnsAllParksOrdered() {
        UserRole ur = new UserRole();
        ur.setUserId(1L);
        ur.setRoleId(10L);
        ur.setParkId(null);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        Role admin = new Role();
        admin.setId(10L);
        admin.setName("ADMIN");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(admin));

        Park p1 = park(1L, "A");
        Park p2 = park(2L, "B");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        List<Park> out = parkAccessService.listAccessibleParks(1L);
        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
    }

    @Test
    void listAccessibleParks_nonAdmin_explicitPark_filters() {
        UserRole ur = new UserRole();
        ur.setUserId(2L);
        ur.setRoleId(20L);
        ur.setParkId(2L);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        Role user = new Role();
        user.setId(20L);
        user.setName("USER");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(user));

        Park p1 = park(1L, "A");
        Park p2 = park(2L, "B");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        List<Park> out = parkAccessService.listAccessibleParks(2L);
        assertEquals(1, out.size());
        assertEquals(2L, out.get(0).getId());
    }

    @Test
    void listAccessibleParks_nonAdmin_onlyNullParkIds_returnsEmpty() {
        UserRole ur = new UserRole();
        ur.setUserId(3L);
        ur.setRoleId(20L);
        ur.setParkId(null);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        Role user = new Role();
        user.setId(20L);
        user.setName("USER");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(user));

        Park p1 = park(1L, "First");
        Park p2 = park(2L, "Second");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        List<Park> out = parkAccessService.listAccessibleParks(3L);
        assertTrue(out.isEmpty());
    }

    @Test
    void listAccessibleParks_nonAdmin_twoRowsTwoParks_returnsBoth() {
        UserRole ur1 = new UserRole();
        ur1.setUserId(4L);
        ur1.setRoleId(20L);
        ur1.setParkId(1L);
        UserRole ur2 = new UserRole();
        ur2.setUserId(4L);
        ur2.setRoleId(20L);
        ur2.setParkId(2L);
        when(userRoleMapper.selectList(any())).thenReturn(Arrays.asList(ur1, ur2));

        Role user = new Role();
        user.setId(20L);
        user.setName("USER");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(user));

        Park p1 = park(1L, "A");
        Park p2 = park(2L, "B");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        List<Park> out = parkAccessService.listAccessibleParks(4L);
        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
    }

    @Test
    void resolveAndSetCurrentPark_validHeader_setsThatPark() {
        User u = new User();
        u.setId(1L);

        UserRole ur = new UserRole();
        ur.setUserId(1L);
        ur.setRoleId(10L);
        ur.setParkId(2L);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        Role role = new Role();
        role.setId(10L);
        role.setName("USER");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(role));

        Park p1 = park(1L, "A");
        Park p2 = park(2L, "B");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        parkAccessService.resolveAndSetCurrentPark(u, 2L);
        assertEquals(2L, ParkContext.getParkId());
    }

    @Test
    void resolveAndSetCurrentPark_invalidHeader_fallsBackToFirstAllowed() {
        User u = new User();
        u.setId(1L);

        UserRole ur = new UserRole();
        ur.setUserId(1L);
        ur.setRoleId(10L);
        ur.setParkId(2L);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        Role role = new Role();
        role.setId(10L);
        role.setName("USER");
        when(roleMapper.selectBatchIds(anyList())).thenReturn(Collections.singletonList(role));

        Park p1 = park(1L, "A");
        Park p2 = park(2L, "B");
        when(parkMapper.selectList(any())).thenReturn(Arrays.asList(p1, p2));

        parkAccessService.resolveAndSetCurrentPark(u, 999L);
        assertEquals(2L, ParkContext.getParkId());
    }

    private static Park park(Long id, String name) {
        Park p = new Park();
        p.setId(id);
        p.setName(name);
        return p;
    }
}
