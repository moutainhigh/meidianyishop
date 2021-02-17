ALTER TABLE `b2c_store_account` ADD COLUMN `user_token`  varchar(256) NOT NULL DEFAULT '' COMMENT '用户token' AFTER `user_id`;
