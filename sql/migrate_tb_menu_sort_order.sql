-- 已有库升级：为 tb_menu 增加排序字段（若列已存在会报错，可忽略或先检查 information_schema）
ALTER TABLE tb_menu
  ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT '排序(越小越靠前)' AFTER parent_id;

ALTER TABLE tb_menu
  ADD INDEX idx_sort (sort_order);

-- 可选：为已有行赋默认顺序（按 id）
UPDATE tb_menu SET sort_order = id * 10 WHERE deleted = 0;
