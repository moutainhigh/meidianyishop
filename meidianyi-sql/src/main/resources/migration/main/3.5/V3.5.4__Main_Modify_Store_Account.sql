ALTER TABLE `b2c_store_account` CHANGE COLUMN `pharmacist_id` `is_pharmacist` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是药师';
ALTER TABLE `b2c_store_account` ADD COLUMN `signature`  text  COMMENT '药师签名' AFTER `is_pharmacist`;
ALTER TABLE `b2c_store_account` ADD COLUMN `auth_time`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'  COMMENT '认证时间' AFTER `signature`;

