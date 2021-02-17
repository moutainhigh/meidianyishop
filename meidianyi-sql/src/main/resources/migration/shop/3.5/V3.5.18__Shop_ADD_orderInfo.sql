ALTER TABLE `b2c_order_info` ADD COLUMN `lat` varchar(20) NOT NULL DEFAULT '' COMMENT '精度' AFTER `patient_id`;
ALTER TABLE `b2c_order_info` ADD COLUMN `lng` varchar(20) NOT NULL DEFAULT '' COMMENT '维度' AFTER `lat`;
