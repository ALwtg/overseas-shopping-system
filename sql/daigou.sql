-- ============================================
-- 海外物品代购系统 - 数据库初始化脚本
-- Database: MySQL 8.0
-- ============================================

CREATE DATABASE IF NOT EXISTS `daigou` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `daigou`;

-- -------------------------------------------
-- 1. 用户表
-- -------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `role` TINYINT DEFAULT 0 COMMENT '角色: 0-普通用户, 1-代购商家, 2-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -------------------------------------------
-- 2. 商家表
-- -------------------------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `store_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
  `license_no` VARCHAR(50) DEFAULT NULL COMMENT '营业执照号',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `store_desc` TEXT DEFAULT NULL COMMENT '店铺简介',
  `logo` VARCHAR(255) DEFAULT NULL COMMENT '店铺Logo',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '所在国家/地区',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已封禁',
  `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '店铺评分(1-5)',
  `total_sales` INT DEFAULT 0 COMMENT '总销量',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- -------------------------------------------
-- 3. 商品分类表
-- -------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID, 0为顶级分类',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- -------------------------------------------
-- 4. 商品信息表
-- -------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `description` TEXT DEFAULT NULL COMMENT '商品详情描述',
  `origin_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价(外币)',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价(RMB)',
  `currency` VARCHAR(10) DEFAULT 'USD' COMMENT '原币种',
  `stock` INT DEFAULT 0 COMMENT '库存数量',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `images` TEXT DEFAULT NULL COMMENT '商品图片(JSON数组)',
  `source_country` VARCHAR(50) DEFAULT NULL COMMENT '来源国家',
  `brand` VARCHAR(100) DEFAULT NULL COMMENT '品牌',
  `spec` VARCHAR(200) DEFAULT NULL COMMENT '规格参数',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-下架, 1-上架, 2-审核中, 3-审核拒绝',
  `is_hot` TINYINT DEFAULT 0 COMMENT '是否热销: 0-否, 1-是',
  `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐: 0-否, 1-是',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

-- -------------------------------------------
-- 5. 收货地址表
-- -------------------------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) NOT NULL COMMENT '区县',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `zip_code` VARCHAR(20) DEFAULT NULL COMMENT '邮政编码',
  `is_default` TINYINT DEFAULT 0 COMMENT '默认地址: 0-否, 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- -------------------------------------------
-- 6. 购物车表
-- -------------------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `selected` TINYINT DEFAULT 1 COMMENT '是否选中: 0-否, 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- -------------------------------------------
-- 7. 订单主表
-- -------------------------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '买家用户ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `freight` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
  `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0-待付款, 1-待采购, 2-运输中, 3-清关中, 4-派送中, 5-已完成, 6-已取消, 7-退款中',
  `pay_type` TINYINT DEFAULT 0 COMMENT '支付方式: 0-微信, 1-支付宝, 2-银行卡',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人',
  `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `receiver_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '买家备注',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- -------------------------------------------
-- 8. 订单明细表
-- -------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称(快照)',
  `product_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片(快照)',
  `price` DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `sub_total` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- -------------------------------------------
-- 9. 物流轨迹表
-- -------------------------------------------
DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流记录ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `node` VARCHAR(30) NOT NULL COMMENT '物流节点: purchased/shipped/customs/clearance/delivering/delivered',
  `node_name` VARCHAR(50) NOT NULL COMMENT '节点中文名称',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '当前位置',
  `description` VARCHAR(300) DEFAULT NULL COMMENT '详细说明',
  `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `logistics_no` VARCHAR(50) DEFAULT NULL COMMENT '物流单号',
  `time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- -------------------------------------------
-- 10. 评价表
-- -------------------------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_item_id` BIGINT NOT NULL COMMENT '订单明细ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户ID',
  `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `rating` TINYINT NOT NULL COMMENT '评分(1-5)',
  `content` TEXT DEFAULT NULL COMMENT '评价内容',
  `images` TEXT DEFAULT NULL COMMENT '评价图片(JSON数组)',
  `reply` TEXT DEFAULT NULL COMMENT '商家回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- -------------------------------------------
-- 11. 收藏表
-- -------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- -------------------------------------------
-- 12. 系统公告表
-- -------------------------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `publisher` VARCHAR(50) DEFAULT NULL COMMENT '发布人',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布, 2-已撤回',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ============================================
-- 初始数据
-- ============================================

-- 管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', 'admin123', '系统管理员', 2, 1);

-- 测试用户 (密码: user123)
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `avatar`, `role`, `status`) VALUES
('xiaoming', 'user123', '小明同学', '13800138001', 'https://placehold.co/120x120/1976D2/fff?text=XM', 0, 1),
('lily', 'user123', 'Lily', '13900139002', 'https://placehold.co/120x120/E91E63/fff?text=LL', 0, 1),
('zhangsan', 'user123', '张三', '13700137003', 'https://placehold.co/120x120/FF9800/fff?text=ZS', 1, 1);

-- 商家数据
INSERT INTO `merchant` (`user_id`, `store_name`, `license_no`, `contact_phone`, `store_desc`, `location`, `status`, `rating`, `balance`) VALUES
(3, '美国好物直邮店', 'US-LIC-20240101', '+1-5550101', '专注美国本土品牌正品代购，支持专柜采购，全程视频溯源。主营美妆护肤、保健品、潮牌服饰等品类。', '美国', 1, 4.9, 25680.00);

-- 分类数据
INSERT INTO `category` (`name`, `parent_id`, `icon`, `sort_order`, `status`) VALUES
('美妆护肤', 0, '💄', 1, 1),
('数码电子', 0, '📱', 2, 1),
('时尚服饰', 0, '👗', 3, 1),
('母婴用品', 0, '🍼', 4, 1),
('食品保健', 0, '🥜', 5, 1),
('运动户外', 0, '⚽', 6, 1),
('家居生活', 0, '🏠', 7, 1),
('奢侈品', 0, '💎', 8, 1);

-- 子分类
INSERT INTO `category` (`name`, `parent_id`, `icon`, `sort_order`, `status`) VALUES
('面部护理', 1, '✨', 1, 1),
('彩妆香水', 1, '💋', 2, 1),
('身体护理', 1, '🧴', 3, 1),
('手机配件', 2, '📲', 1, 1),
('电脑办公', 2, '💻', 2, 1),
('智能穿戴', 2, '⌚', 3, 1);

-- 商品数据
INSERT INTO `product` (`category_id`, `merchant_id`, `name`, `description`, `origin_price`, `price`, `currency`, `stock`, `sales`, `cover_image`, `source_country`, `brand`, `status`, `is_hot`, `is_recommend`) VALUES
(1, 1, 'Estée Lauder 特润修护肌透精华露（小棕瓶）50ml', '雅诗兰黛明星产品！全新第7代小棕瓶精华，采用 ChronoluxCB™ 生物节律科技，修护肌肤日夜节律，强韧肌底屏障。适合所有肤质，尤其针对干燥、暗沉、细纹等问题。美国专柜正品质保，支持扫码验证。', 108.00, 789.00, 'USD', 150, 520, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&h=400&fit=crop', '美国', 'Estée Lauder', 1, 1, 1),

(1, 1, 'La Mer 海蓝之谜经典面霜 60ml', 'La Mer面霜蕴含品牌灵魂成分——神奇活性精萃 Miracle Broth™，深层滋养修护受损肌肤，改善肤质粗糙与细纹问题，令肌肤焕发健康光泽。60ml大容量装，美国百货公司专柜采购，附购物凭证。', 380.00, 2780.00, 'USD', 45, 180, 'https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=400&h=400&fit=crop', '美国', 'La Mer', 1, 1, 1),

(1, 1, 'SK-II 护肤精华露（神仙水）230ml', '超过90%的PITERA™精华成分，调节肌肤水油平衡，改善毛孔粗大和暗沉问题，让肌肤透出晶莹剔透的光采。日本配方美国版，230ml超大瓶装性价比超高。连续使用28天可见显著改善效果。', 185.00, 1350.00, 'USD', 88, 340, 'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=400&h=400&fit=crop', '美国', 'SK-II', 1, 1, 1),

(2, 1, 'Apple AirPods Pro 2代 无线降噪蓝牙耳机', 'Apple最新款AirPods Pro 2，搭载H2芯片，降噪能力提升2倍，自适应通透模式更自然。支持MagSafe无线充电，单次续航可达6小时。USB-C接口充电盒，兼容性更强。原封未拆美版，享受全球联保服务。', 249.00, 1820.00, 'USD', 60, 210, 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=400&h=400&fit=crop', '美国', 'Apple', 1, 1, 0),

(2, 1, 'Apple Watch Ultra 2 钛金属 GPS+蜂窝版 49mm', 'Apple迄今最坚固、功能最强的手表。钛金属表壳，双频GPS精准定位，水深达40米的防水性能。内置36小时超长续航模式，专为探险者和运动精英打造。配备全新S9 SiP芯片，响应速度更快。', 799.00, 5850.00, 'USD', 12, 35, 'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop', '美国', 'Apple', 1, 0, 1),

(3, 1, 'Canada Goose 加拿大鹅 Expedition 派克大衣 黑色M码', '加拿大鹅Expedition系列是品牌的标志性款式，采用Arctic Tech®面料，填充625蓬松度的白鸭绒，抵御零下30度严寒。可拆卸郊狼毛领，实用口袋设计。黑色经典百搭，冬季必备单品。美国奥莱折扣入，价格美丽！', 995.00, 7280.00, 'CAD', 15, 42, 'https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=400&h=400&fit=crop', '加拿大', 'Canada Goose', 1, 0, 1),

(3, 1, 'Supreme x Nike 联名卫衣 Box Logo 经典红', 'Supreme与Nike联名限定款卫衣，胸前Box Logo刺绣精致立体，重磅纯棉毛圈布料质感上乘。红色Logo配色最为经典抢手。美版尺码偏大，建议选小一码。全新带吊牌，附购买小票。', 198.00, 1450.00, 'USD', 25, 95, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop', '美国', 'Supreme', 1, 1, 0),

(4, 1, 'Enfamil 美赞臣 Premium 婴儿奶粉 1段 823g×2罐装', '美赞臣Premium系列婴幼儿配方奶粉1段，适用于0-12个月宝宝。富含DHA和ARA促进脑部发育，含益生元组合呵护肠道健康，添加维生素E和铁元素增强免疫力。非转基因原料，美国FDA认证。两罐装更划算，美国超市直购。', 38.99, 285.00, 'USD', 200, 480, 'https://images.unsplash.com/photo-1520422222652-ef704102f283?w=400&h=400&fit=crop', '美国', 'Enfamil', 1, 1, 0),

(5, 1, 'Garden of Life 生命花园 蛋白质粉 巧克力味 900g', '生命花园有机植物蛋白粉，采用豌豆蛋白、苜蓿蛋白等22种植物蛋白来源，每份提供24g高含量蛋白质。无人工甜味剂无麸质，巧克力口味醇厚浓郁。适合健身增肌、素食人群及日常营养补充。美国iHerb直邮。', 44.99, 330.00, 'USD', 90, 170, 'https://images.unsplash.com/photo-1593095948071-474c5cc2c6e9?w=400&h=400&fit=crop', '美国', 'Garden of Life', 1, 0, 0),

(5, 1, 'Swisse 斯维诗 血橙胶原蛋白精华口服液 500ml×2瓶', '澳洲Swisse血橙精华，源自西西里血橙萃取，富含天然维生素C和胶原蛋白肽。每天一瓶帮助肌肤恢复弹性光泽，改善松弛暗沉。酸酸甜甜的口感很好喝。两瓶装一个月用量，澳洲药房采购。', 18.99, 140.00, 'AUD', 280, 650, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400&h=400&fit=crop', '澳大利亚', 'Swisse', 1, 1, 1),

(6, 1, 'Nike Air Jordan 1 Retro High OG "Chicago" 芝加哥', 'AJ1芝加哥配色是Air Jordan系列的元祖级配色之一，白红黑三色搭配永不过时。全粒面皮革鞋面，Nike Air气垫缓震中底，橡胶抓地外底。2023复刻版本，做工细节到位。美版正品，附原盒防盗扣。', 180.00, 1320.00, 'USD', 20, 68, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop', '美国', 'Nike', 1, 1, 1);

-- 地址测试数据
INSERT INTO `address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(1, '小明', '13800138001', '江苏省', '南京市', '江宁区', '弘景大道1号南京工程学院学生公寓', 1);

-- 公告数据
INSERT INTO `announcement` (`title`, `content`, `publisher`, `status`, `sort_order`) VALUES
('欢迎使用海外物品代购平台！', '欢迎来到我们的海外代购平台！本平台致力于为您提供安全便捷的海外商品代购服务。我们合作的商家均经过严格资质审核，所有商品均为海外正规渠道采购。如需任何帮助，请联系在线客服。', '管理员', 1, 1),
('关于春节假期物流调整通知', '春节期间（1月28日-2月4日），国际物流时效将受影响，预计延迟3-5个工作日。期间正常接单，但发货时间顺延至假期结束后按顺序处理。感谢您的理解和支持！', '运营部', 1, 2),
('新商家入驻优惠政策', '即日起至本月底，新入驻商家免收前三个月平台服务费！同时享受首页推荐位曝光扶持。欢迎有海外采购能力的优质商家入驻合作。点击"申请入驻"了解更多详情。', '招商部', 1, 3);
