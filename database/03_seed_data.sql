USE shihua_ai_flower;

INSERT INTO sys_user (username, password, role, phone)
VALUES
  ('admin', '$2a$10$replace_with_bcrypt_hash', 'ADMIN', '13800000000'),
  ('demo', '$2a$10$replace_with_bcrypt_hash', 'USER', '13900000000')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO flower_category (category_id, category_name, parent_id, sort_order)
VALUES
  (1, '玫瑰', 0, 1),
  (2, '百合', 0, 2),
  (3, '康乃馨', 0, 3)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

INSERT INTO flower (flower_id, flower_name, category_id, price, stock, cover_image, description, flower_language, care_guide, sales_count)
VALUES
  (1001, '红玫瑰礼盒', 1, 99.00, 100, '/uploads/rose.jpg', '经典红玫瑰礼盒', '热烈的爱', '斜剪花茎，保持清水，每日换水。', 20),
  (1002, '香水百合花束', 2, 89.00, 80, '/uploads/lily.jpg', '清雅香水百合花束', '纯洁与祝福', '避免阳光直射，摘除水面以下叶片。', 12),
  (1003, '康乃馨花篮', 3, 79.00, 60, '/uploads/carnation.jpg', '适合母亲节的康乃馨花篮', '感恩与母爱', '放置阴凉处，及时补水。', 16)
ON DUPLICATE KEY UPDATE flower_name = VALUES(flower_name);

INSERT INTO flower_knowledge (oxford_class_id, flower_name, flower_name_en, flower_language, care_guide, related_flower_ids)
VALUES
  (12, '玫瑰', 'Rose', '爱情、热烈、真挚', '斜剪花茎，保持清水，每日换水。', JSON_ARRAY(1001)),
  (45, '康乃馨', 'Carnation', '感恩、母爱、祝福', '放置阴凉处，及时补水。', JSON_ARRAY(1003))
ON DUPLICATE KEY UPDATE flower_name = VALUES(flower_name);

