package com.chengq.app.service.Impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.chengq.api.entity.Menu;
import com.chengq.api.entity.RoleMenu;
import com.chengq.api.entity.UserRole;
import com.chengq.app.mapper.MenuMapper;
import com.chengq.app.mapper.RoleMapper;
import com.chengq.app.mapper.RoleMenuMapper;
import com.chengq.app.mapper.UserRoleMapper;
import com.chengq.app.util.ParkContext;
import com.chengq.app.util.UserContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplParkScopeTest {

    @Mock
    private MenuMapper menuMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RoleMenuMapper roleMenuMapper;
    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    @AfterEach
    void tearDown() {
        ParkContext.clear();
    }

    @Test
    void getMenuTree_admin_returnsFullTreeWithoutReadingRoleMenu() {
        Menu root = menu(1L, "root", null);
        Menu child = menu(2L, "child", 1L);
        when(menuMapper.selectAllMenus()).thenReturn(Arrays.asList(root, child));

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(() -> UserContext.getCurrentUserId()).thenReturn(1L);
            uc.when(() -> UserContext.hasRole("ADMIN")).thenReturn(true);

            List<Menu> tree = menuService.getMenuTree();
            assertEquals(1, tree.size());
            assertEquals(1L, tree.get(0).getId());
            assertNotNull(tree.get(0).getChildren());
            assertEquals(1, tree.get(0).getChildren().size());
            assertEquals(2L, tree.get(0).getChildren().get(0).getId());
        }
        verify(roleMenuMapper, never()).selectList(any());
        verify(userRoleMapper, never()).selectList(any());
    }

    @Test
    void getMenuTree_nonAdmin_noParkContext_returnsEmpty() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class);
             MockedStatic<ParkContext> pc = mockStatic(ParkContext.class)) {
            uc.when(() -> UserContext.getCurrentUserId()).thenReturn(1L);
            pc.when(() -> ParkContext.getParkId()).thenReturn(null);

            assertTrue(menuService.getMenuTree().isEmpty());
        }
    }

    @Test
    void getMenuTree_parentOnlyBound_doesNotShowUnboundChildren() {
        UserRole ur = new UserRole();
        ur.setUserId(10L);
        ur.setRoleId(100L);
        ur.setParkId(5L);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        RoleMenu rm = new RoleMenu();
        rm.setRoleId(100L);
        rm.setMenuId(1L);
        rm.setParkId(5L);
        RoleMenu globalChild = new RoleMenu();
        globalChild.setRoleId(100L);
        globalChild.setMenuId(20L);
        globalChild.setParkId(null);
        when(roleMenuMapper.selectList(any()))
                .thenReturn(Collections.singletonList(rm))
                .thenReturn(Collections.singletonList(globalChild));

        Menu root = menu(1L, "系统", null);
        Menu leaf = menu(20L, "用户", 1L);
        Menu leaf2 = menu(21L, "角色", 1L);
        when(menuMapper.selectAllMenus()).thenReturn(Arrays.asList(root, leaf, leaf2));

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class);
             MockedStatic<ParkContext> pc = mockStatic(ParkContext.class)) {
            uc.when(() -> UserContext.getCurrentUserId()).thenReturn(10L);
            pc.when(() -> ParkContext.getParkId()).thenReturn(5L);

            List<Menu> tree = menuService.getMenuTree();
            assertEquals(1, tree.size());
            assertEquals("系统", tree.get(0).getName());
            assertTrue(tree.get(0).getChildren() == null || tree.get(0).getChildren().isEmpty());
        }
    }

    @Test
    void getMenuTree_nonAdmin_filtersByParkScopedRoleMenu() {
        UserRole ur = new UserRole();
        ur.setUserId(10L);
        ur.setRoleId(100L);
        ur.setParkId(5L);
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        RoleMenu rm = new RoleMenu();
        rm.setRoleId(100L);
        rm.setMenuId(20L);
        rm.setParkId(5L);
        when(roleMenuMapper.selectList(any()))
                .thenReturn(Collections.singletonList(rm))
                .thenReturn(Collections.emptyList());

        Menu root = menu(1L, "系统", null);
        Menu leaf = menu(20L, "用户", 1L);
        when(menuMapper.selectAllMenus()).thenReturn(Arrays.asList(root, leaf));

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class);
             MockedStatic<ParkContext> pc = mockStatic(ParkContext.class)) {
            uc.when(() -> UserContext.getCurrentUserId()).thenReturn(10L);
            pc.when(() -> ParkContext.getParkId()).thenReturn(5L);

            List<Menu> tree = menuService.getMenuTree();
            assertEquals(1, tree.size());
            assertEquals("系统", tree.get(0).getName());
            assertEquals(1, tree.get(0).getChildren().size());
            assertEquals("用户", tree.get(0).getChildren().get(0).getName());
        }
    }

    private static Menu menu(Long id, String name, Long parentId) {
        Menu m = new Menu();
        m.setId(id);
        m.setName(name);
        m.setParentId(parentId);
        m.setSortOrder(0);
        return m;
    }
}
