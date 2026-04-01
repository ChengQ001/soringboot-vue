-- =========================================================
-- com-chengq 项目数据库初始化脚本（全量）
-- 1) 建库 + 建表
-- 2) 插入初始角色/用户/园区/菜单
-- 3) 插入默认用户-角色（仅 admin → ADMIN）
--
-- 首次仅有 ADMIN 角色与 admin 账号；无 tb_role_menu 预置（ADMIN 登录后走全量菜单，由服务端处理）。
-- 其它角色与角色-菜单绑定请在系统中配置。
--
-- 默认登录（密码：123456）：admin / 17688888888
-- =========================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS com_chengq
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE com_chengq;

-- 清理旧数据（建议初始化时执行）
DROP TABLE IF EXISTS tb_user_role;
DROP TABLE IF EXISTS tb_role_menu;
DROP TABLE IF EXISTS tb_park;
DROP TABLE IF EXISTS tb_menu;
DROP TABLE IF EXISTS tb_role;
DROP TABLE IF EXISTS tb_user;

-- 创建用户表
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) COMMENT '用户名',
    phone VARCHAR(20) COMMENT '手机号（唯一）',
    password VARCHAR(100) COMMENT '密码（加密存储）',
    description VARCHAR(200) COMMENT '用户描述',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    UNIQUE KEY idx_phone (phone),
    INDEX idx_username (username),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS tb_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) COMMENT '角色名称（唯一）',
    description VARCHAR(200) COMMENT '角色描述',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    UNIQUE KEY idx_name (name),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建菜单表
CREATE TABLE IF NOT EXISTS tb_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) COMMENT '菜单名称',
    code VARCHAR(50) COMMENT '菜单编码',
    path VARCHAR(100) COMMENT '菜单路径',
    parent_id BIGINT COMMENT '父节点ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序(越小越靠前)',
    description VARCHAR(200) COMMENT '菜单描述',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort (sort_order),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 已有库升级：若表已存在但无 sort_order，可单独执行 sql/migrate_tb_menu_sort_order.sql

-- 创建园区表
CREATE TABLE IF NOT EXISTS tb_park (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) COMMENT '园区名称（唯一）',
    description VARCHAR(200) COMMENT '园区描述',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    UNIQUE KEY idx_name (name),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区表';

-- 创建角色菜单映射表
CREATE TABLE IF NOT EXISTS tb_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT COMMENT '角色ID',
    menu_id BIGINT COMMENT '菜单ID',
    park_id BIGINT COMMENT '园区ID',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单映射表';

-- 创建用户角色映射表
CREATE TABLE IF NOT EXISTS tb_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    role_id BIGINT COMMENT '角色ID',
    park_id BIGINT COMMENT '园区ID',
    created_at DATETIME COMMENT '创建时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50) COMMENT '创建人名称',
    updated_at DATETIME COMMENT '更新时间',
    updated_by BIGINT COMMENT '更新人ID',
    updated_by_name VARCHAR(50) COMMENT '更新人名称',
    version INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    deleted INT DEFAULT 0 COMMENT '是否删除（逻辑删除）',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色映射表';

-- ---------------------------------------------------------
-- 初始化：角色
-- ---------------------------------------------------------
INSERT IGNORE INTO tb_role
  (name, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('ADMIN', '系统管理员', NOW(), 1, 'system', NOW(), 1, 'system');

-- ---------------------------------------------------------
-- 初始化：用户（密码：123456，BCrypt 加密后）
-- ---------------------------------------------------------
INSERT IGNORE INTO tb_user
  (username, phone, password, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('admin', '17688888888', '$2a$10$7q4QMsPIQPZhyEkttFQQ9uNPQhHxcT1ZtdFYwCQOLDIWMsWXYvNV6',
   '系统管理员', NOW(), 1, 'system', NOW(), 1, 'system');

-- ---------------------------------------------------------
-- 初始化：园区
-- ---------------------------------------------------------
INSERT IGNORE INTO tb_park
  (name, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('ChengQ园区', '默认园区（授权使用）', NOW(), 1, 'system', NOW(), 1, 'system');

-- ---------------------------------------------------------
-- 初始化：菜单
-- ---------------------------------------------------------
-- 1) 根菜单（先插入，避免在同一条 INSERT 中再查询 tb_menu）
INSERT IGNORE INTO tb_menu
  (name, code, path, parent_id, sort_order, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('系统管理', 'system', '/admin', NULL, 0, '系统管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  ('授权', 'auth', '/admin', NULL, 10, '授权菜单', NOW(), 1, 'system', NOW(), 1, 'system');

-- 2) 获取父节点 ID（用变量避免 MySQL 1093: target table for update in FROM clause）
SET @system_menu_id := (SELECT id FROM tb_menu WHERE code='system' AND deleted=0 LIMIT 1);
SET @auth_menu_id := (SELECT id FROM tb_menu WHERE code='auth' AND deleted=0 LIMIT 1);

-- 3) 子菜单
INSERT IGNORE INTO tb_menu
  (name, code, path, parent_id, sort_order, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  -- 业务（系统管理）
  ('用户管理', 'user', '/admin/users', @system_menu_id, 0, '用户管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  ('角色管理', 'role', '/admin/roles', @system_menu_id, 10, '角色管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  ('菜单管理', 'menu', '/admin/menus', @system_menu_id, 20, '菜单管理菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  -- 授权（角色-菜单 / 用户-角色）
  ('角色-菜单绑定', 'bind-role-menu', '/admin/bind-role-menu', @auth_menu_id, 0, '角色-菜单绑定菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  ('用户-角色绑定', 'bind-user-role', '/admin/bind-user-role', @auth_menu_id, 10, '用户-角色绑定菜单', NOW(), 1, 'system', NOW(), 1, 'system'),
  ('园区管理', 'park-manage', '/admin/parks', @auth_menu_id, 20, '园区管理菜单', NOW(), 1, 'system', NOW(), 1, 'system');

-- ---------------------------------------------------------
-- 初始化：用户-角色绑定（用于“个人信息”里展示角色）
-- ---------------------------------------------------------
INSERT IGNORE INTO tb_user_role
  (user_id, role_id, park_id, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  (
    (SELECT id FROM tb_user WHERE username='admin' AND deleted=0 LIMIT 1),
    (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),
    1,
    NOW(), 1, 'system', NOW(), 1, 'system'
  );
