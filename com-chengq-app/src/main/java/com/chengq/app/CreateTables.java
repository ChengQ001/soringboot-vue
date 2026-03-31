package com.chengq.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateTables {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/com_chengq?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root123456";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            // 删除旧表（如果存在）
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_user_role");
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_role_menu");
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_park");
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_menu");
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_role");
            stmt.executeUpdate("DROP TABLE IF EXISTS tb_user");
            System.out.println("Old tables dropped successfully");
            
            // 创建用户表
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS tb_user (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "username VARCHAR(50) COMMENT '用户名'," +
                    "phone VARCHAR(20) COMMENT '手机号（唯一）'," +
                    "password VARCHAR(100) COMMENT '密码（加密存储）'," +
                    "description VARCHAR(200) COMMENT '用户描述'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "UNIQUE KEY idx_phone (phone)," +
                    "INDEX idx_username (username)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'";
            stmt.executeUpdate(createUserTableSQL);
            System.out.println("User table created successfully");
            
            // 创建角色表
            String createRoleTableSQL = "CREATE TABLE IF NOT EXISTS tb_role (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "name VARCHAR(50) COMMENT '角色名称（唯一）'," +
                    "description VARCHAR(200) COMMENT '角色描述'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "UNIQUE KEY idx_name (name)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表'";
            stmt.executeUpdate(createRoleTableSQL);
            System.out.println("Role table created successfully");
            
            // 创建菜单表
            String createMenuTableSQL = "CREATE TABLE IF NOT EXISTS tb_menu (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "name VARCHAR(50) COMMENT '菜单名称'," +
                    "code VARCHAR(50) COMMENT '菜单编码'," +
                    "path VARCHAR(100) COMMENT '菜单路径'," +
                    "parent_id BIGINT COMMENT '父节点ID'," +
                    "sort_order INT NOT NULL DEFAULT 0 COMMENT '排序(越小越靠前)'," +
                    "description VARCHAR(200) COMMENT '菜单描述'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "INDEX idx_parent_id (parent_id)," +
                    "INDEX idx_sort (sort_order)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表'";
            stmt.executeUpdate(createMenuTableSQL);
            System.out.println("Menu table created successfully");

            // 创建园区表
            String createParkTableSQL = "CREATE TABLE IF NOT EXISTS tb_park (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "name VARCHAR(100) COMMENT '园区名称（唯一）'," +
                    "description VARCHAR(200) COMMENT '园区描述'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "UNIQUE KEY idx_name (name)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区表'";
            stmt.executeUpdate(createParkTableSQL);
            System.out.println("Park table created successfully");
            
            // 创建角色菜单映射表
            String createRoleMenuTableSQL = "CREATE TABLE IF NOT EXISTS tb_role_menu (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "role_id BIGINT COMMENT '角色ID'," +
                    "menu_id BIGINT COMMENT '菜单ID'," +
                    "park_id BIGINT COMMENT '园区ID'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "INDEX idx_role_id (role_id)," +
                    "INDEX idx_menu_id (menu_id)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单映射表'";
            stmt.executeUpdate(createRoleMenuTableSQL);
            System.out.println("RoleMenu table created successfully");
            
            // 创建用户角色映射表
            String createUserRoleTableSQL = "CREATE TABLE IF NOT EXISTS tb_user_role (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'," +
                    "user_id BIGINT COMMENT '用户ID'," +
                    "role_id BIGINT COMMENT '角色ID'," +
                    "park_id BIGINT COMMENT '园区ID'," +
                    "created_at DATETIME COMMENT '创建时间'," +
                    "created_by BIGINT COMMENT '创建人ID'," +
                    "created_by_name VARCHAR(50) COMMENT '创建人名称'," +
                    "updated_at DATETIME COMMENT '更新时间'," +
                    "updated_by BIGINT COMMENT '更新人ID'," +
                    "updated_by_name VARCHAR(50) COMMENT '更新人名称'," +
                    "version INT DEFAULT 1 COMMENT '版本号（乐观锁）'," +
                    "deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）'," +
                    "INDEX idx_user_id (user_id)," +
                    "INDEX idx_role_id (role_id)," +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色映射表'";
            stmt.executeUpdate(createUserRoleTableSQL);
            System.out.println("UserRole table created successfully");
            
            // 插入初始数据
            // 插入角色数据
            String insertRoleSQL = "INSERT IGNORE INTO tb_role (name, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) " +
                    "VALUES ('ADMIN', '系统管理员', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('USER', '普通用户', NOW(), 1, 'system', NOW(), 1, 'system')";
            stmt.executeUpdate(insertRoleSQL);
            
            // 插入用户数据（密码为123456的BCrypt加密值）
            String insertUserSQL = "INSERT IGNORE INTO tb_user (username, phone, password, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) " +
                    "VALUES ('admin', '13800138000', '$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6', '系统管理员', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('user', '13900139000', '$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6', '普通用户', NOW(), 1, 'system', NOW(), 1, 'system')";
            stmt.executeUpdate(insertUserSQL);

            // 插入默认园区数据
            String insertParkSQL = "INSERT IGNORE INTO tb_park (name, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) " +
                    "VALUES ('ChengQ园区', '默认园区（授权使用）', NOW(), 1, 'system', NOW(), 1, 'system')";
            stmt.executeUpdate(insertParkSQL);
            
            // 插入菜单数据
            String insertRootMenuSQL = "INSERT IGNORE INTO tb_menu (name, code, path, parent_id, sort_order, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) " +
                    "VALUES " +
                    "('系统管理', 'system', '/admin', NULL, 0, '系统管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('授权', 'auth', '/admin', NULL, 10, '授权菜单', NOW(), 1, 'system', NOW(), 1, 'system')";
            stmt.executeUpdate(insertRootMenuSQL);

            // 获取父节点 ID：用变量避免 MySQL 1093 限制
            stmt.executeUpdate("SET @system_menu_id := (SELECT id FROM tb_menu WHERE code='system' AND deleted=0 LIMIT 1)");
            stmt.executeUpdate("SET @auth_menu_id := (SELECT id FROM tb_menu WHERE code='auth' AND deleted=0 LIMIT 1)");

            String insertChildMenuSQL = "INSERT IGNORE INTO tb_menu (name, code, path, parent_id, sort_order, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) " +
                    "VALUES " +
                    "('用户管理', 'user', '/admin/users', @system_menu_id, 0, '用户管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('角色管理', 'role', '/admin/roles', @system_menu_id, 10, '角色管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('菜单管理', 'menu', '/admin/menus', @system_menu_id, 20, '菜单管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('角色-菜单绑定', 'bind-role-menu', '/admin/bind-role-menu', @auth_menu_id, 0, '角色-菜单绑定菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('用户-角色绑定', 'bind-user-role', '/admin/bind-user-role', @auth_menu_id, 10, '用户-角色绑定菜单', NOW(), 1, 'system', NOW(), 1, 'system'), " +
                    "('园区管理', 'park-manage', '/admin/parks', @auth_menu_id, 20, '园区管理菜单', NOW(), 1, 'system', NOW(), 1, 'system')";
            stmt.executeUpdate(insertChildMenuSQL);
            
            // 插入角色-菜单映射数据（两层关系：USER 映射 system 根 + system 子节点）
            // 注意：这里用 INSERT IGNORE，避免重复插入。
            String insertRoleMenuSQL =
                    "INSERT IGNORE INTO tb_role_menu (role_id, menu_id, park_id, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name) VALUES "
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='system' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='user' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='role' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='menu' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='auth' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='bind-role-menu' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='bind-user-role' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='park-manage' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='USER' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='system' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='USER' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='user' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='USER' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='role' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + "),"
                            + "("
                            + "  (SELECT id FROM tb_role WHERE name='USER' AND deleted=0 LIMIT 1),"
                            + "  (SELECT id FROM tb_menu WHERE code='menu' AND deleted=0 LIMIT 1),"
                            + "  NULL, NOW(), 1, 'system', NOW(), 1, 'system'"
                            + ") ;";
            stmt.executeUpdate(insertRoleMenuSQL);

            System.out.println("Initial data inserted successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
