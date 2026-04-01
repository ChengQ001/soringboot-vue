-- 已有库升级：用户-角色行 park_id 为空时，登录后无法出现在园区列表（现逻辑仅从 tb_user_role.park_id 非空收集）
-- 按需执行：将仍为 NULL 的行挂到「第一个园区」

UPDATE tb_user_role ur
SET ur.park_id = (SELECT id FROM tb_park WHERE deleted = 0 ORDER BY id LIMIT 1)
WHERE ur.deleted = 0
  AND ur.park_id IS NULL;
