ALTER TABLE `b2c_doctor` ADD COLUMN `is_fetch`  tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否拉取过' AFTER `consultation_total_money`;
ALTER TABLE `b2c_doctor` ADD COLUMN `signature`  varchar(128) NOT NULL DEFAULT '' COMMENT '医师签名' AFTER `is_fetch`;
