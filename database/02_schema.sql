USE shihua_ai_flower;

CREATE TABLE IF NOT EXISTS sys_user (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  gender TINYINT DEFAULT 0,
  phone VARCHAR(16),
  email VARCHAR(64),
  avatar VARCHAR(255),
  role VARCHAR(16) NOT NULL DEFAULT 'USER',
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS flower_category (
  category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(32) NOT NULL,
  parent_id BIGINT NOT NULL DEFAULT 0,
  sort_order INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS flower (
  flower_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  flower_name VARCHAR(64) NOT NULL,
  category_id BIGINT,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  cover_image VARCHAR(255),
  detail_images JSON,
  description TEXT,
  flower_language VARCHAR(255),
  care_guide TEXT,
  sales_count INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_flower_category (category_id),
  INDEX idx_flower_status_sales (status, sales_count),
  CONSTRAINT fk_flower_category FOREIGN KEY (category_id) REFERENCES flower_category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_address (
  address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  receiver VARCHAR(32) NOT NULL,
  phone VARCHAR(16) NOT NULL,
  province VARCHAR(32),
  city VARCHAR(32),
  district VARCHAR(32),
  detail VARCHAR(255) NOT NULL,
  is_default TINYINT NOT NULL DEFAULT 0,
  INDEX idx_address_user (user_id),
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart (
  cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  flower_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  add_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_cart_user_flower (user_id, flower_id),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  CONSTRAINT fk_cart_flower FOREIGN KEY (flower_id) REFERENCES flower(flower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  status TINYINT NOT NULL DEFAULT 0,
  address_id BIGINT,
  remark VARCHAR(255),
  pay_time DATETIME,
  deliver_time DATETIME,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_order_user_status (user_id, status),
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES user_address(address_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_item (
  item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  flower_id BIGINT NOT NULL,
  flower_name VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  subtotal DECIMAL(12,2) NOT NULL,
  INDEX idx_order_item_order (order_id),
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_order_item_flower FOREIGN KEY (flower_id) REFERENCES flower(flower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS flower_knowledge (
  knowledge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  oxford_class_id INT UNIQUE,
  flower_name VARCHAR(64),
  flower_name_en VARCHAR(64),
  flower_language VARCHAR(255),
  care_guide TEXT,
  story TEXT,
  related_flower_ids JSON
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS review (
  review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  flower_id BIGINT NOT NULL,
  order_id BIGINT,
  rating TINYINT NOT NULL,
  content TEXT,
  sentiment VARCHAR(16),
  keywords VARCHAR(255),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_review_flower (flower_id),
  CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  CONSTRAINT fk_review_flower FOREIGN KEY (flower_id) REFERENCES flower(flower_id),
  CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ai_card (
  card_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  flower_id BIGINT,
  relation VARCHAR(16),
  occasion VARCHAR(16),
  prompt TEXT,
  generated_content TEXT,
  is_used TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ai_card_user (user_id),
  CONSTRAINT fk_ai_card_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  CONSTRAINT fk_ai_card_flower FOREIGN KEY (flower_id) REFERENCES flower(flower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS browse_record (
  record_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  flower_id BIGINT NOT NULL,
  browse_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_browse_user_time (user_id, browse_time),
  CONSTRAINT fk_browse_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
  CONSTRAINT fk_browse_flower FOREIGN KEY (flower_id) REFERENCES flower(flower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS system_log (
  log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT,
  action VARCHAR(64) NOT NULL,
  detail VARCHAR(512),
  ip VARCHAR(64),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_system_log_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

