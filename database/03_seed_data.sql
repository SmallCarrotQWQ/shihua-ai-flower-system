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
  (3, '康乃馨', 0, 3),
  (4, '向日葵', 0, 4)
ON DUPLICATE KEY UPDATE
  category_name = VALUES(category_name),
  sort_order = VALUES(sort_order);

INSERT INTO flower (flower_id, flower_name, category_id, price, stock, cover_image, description, flower_language, care_guide, sales_count, status)
VALUES
  (1001, '红玫瑰礼盒', 1, 99.00, 100, '/uploads/rose.jpg', '经典红玫瑰礼盒，适合表达热烈爱意。', '热烈的爱、真挚告白', '斜剪花茎，保持清水，每日换水。', 20, 1),
  (1002, '香水百合花束', 2, 89.00, 80, '/uploads/lily.jpg', '清雅香水百合花束，适合祝福与探望。', '纯洁、祝福、百年好合', '避免阳光直射，摘除水面以下叶片。', 12, 1),
  (1003, '康乃馨花篮', 3, 79.00, 60, '/uploads/carnation.jpg', '适合母亲节和长辈祝福的康乃馨花篮。', '感恩、母爱、温柔祝福', '放置阴凉处，及时补水。', 16, 1),
  (1004, '向日葵花束', 4, 69.00, 50, '/uploads/sunflower.jpg', '明亮元气的向日葵花束，适合鼓励与感谢。', '阳光、信念、积极向上', '保持通风，避免高温暴晒。', 10, 1)
ON DUPLICATE KEY UPDATE
  flower_name = VALUES(flower_name),
  category_id = VALUES(category_id),
  price = VALUES(price),
  stock = VALUES(stock),
  description = VALUES(description),
  flower_language = VALUES(flower_language),
  care_guide = VALUES(care_guide),
  status = VALUES(status);

INSERT INTO flower_knowledge (oxford_class_id, flower_name, flower_name_en, flower_language, care_guide, related_flower_ids)
VALUES
  (12, '玫瑰', 'Rose', '爱情、热烈、真挚', '斜剪花茎，保持清水，每日换水。', JSON_ARRAY(1001)),
  (45, '康乃馨', 'Carnation', '感恩、母爱、祝福', '放置阴凉处，及时补水。', JSON_ARRAY(1003))
ON DUPLICATE KEY UPDATE
  flower_name = VALUES(flower_name),
  flower_language = VALUES(flower_language),
  care_guide = VALUES(care_guide),
  related_flower_ids = VALUES(related_flower_ids);
