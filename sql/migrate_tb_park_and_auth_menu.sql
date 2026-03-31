-- 1) 创建园区表
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

-- 2) 默认园区数据
INSERT IGNORE INTO tb_park
  (name, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('ChengQ园区', '默认园区（授权使用）', NOW(), 1, 'system', NOW(), 1, 'system');

-- 3) 授权菜单下新增园区管理菜单
SET @auth_menu_id := (SELECT id FROM tb_menu WHERE code='auth' AND deleted=0 LIMIT 1);

INSERT IGNORE INTO tb_menu
  (name, code, path, parent_id, sort_order, description, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  ('园区管理', 'park-manage', '/admin/parks', @auth_menu_id, 20, '园区管理菜单', NOW(), 1, 'system', NOW(), 1, 'system');

-- 4) 绑定 ADMIN 角色权限
INSERT IGNORE INTO tb_role_menu
  (role_id, menu_id, park_id, created_at, created_by, created_by_name, updated_at, updated_by, updated_by_name)
VALUES
  (
    (SELECT id FROM tb_role WHERE name='ADMIN' AND deleted=0 LIMIT 1),
    (SELECT id FROM tb_menu WHERE code='park-manage' AND deleted=0 LIMIT 1),
    NULL, NOW(), 1, 'system', NOW(), 1, 'system'
  );

