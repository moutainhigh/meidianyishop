-- 门店账户添加关注公众号
ALTER TABLE `b2c_store_account` ADD COLUMN `official_open_id` varchar(128) DEFAULT NULL COMMENT '公众号openid';
ALTER TABLE `b2c_store_account` ADD COLUMN `is_bind` tinyint(1) DEFAULT '0' COMMENT '是否已绑定';
