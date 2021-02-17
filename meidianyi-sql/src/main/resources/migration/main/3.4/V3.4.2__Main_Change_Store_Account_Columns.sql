-- 门店账户添加关联用户
ALTER TABLE `b2c_store_account` ADD COLUMN `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联用户Id';
