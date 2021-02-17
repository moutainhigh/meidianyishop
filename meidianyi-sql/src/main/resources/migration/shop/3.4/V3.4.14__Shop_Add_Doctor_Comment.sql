ALTER TABLE `b2c_doctor_comment` ADD COLUMN `patient_name` varchar(20) NOT NULL COMMENT '患者名称' AFTER `patient_id`;
ALTER TABLE `b2c_doctor_comment` ADD COLUMN `doctor_name` varchar(20) NOT NULL COMMENT '医师名称' AFTER `doctor_id`;
ALTER TABLE `b2c_doctor_comment` ADD COLUMN `order_id` int(11) NOT NULL COMMENT '订单id' AFTER `doctor_name`;
ALTER TABLE `b2c_doctor_comment` ADD COLUMN `doctor_code` varchar(20) NOT NULL COMMENT '医师院内编码' AFTER `doctor_id`;
