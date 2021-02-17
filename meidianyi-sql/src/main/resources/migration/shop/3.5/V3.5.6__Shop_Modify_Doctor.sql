ALTER TABLE `b2c_doctor` ADD COLUMN `auth_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'  COMMENT '认证时间' AFTER `user_token`;
