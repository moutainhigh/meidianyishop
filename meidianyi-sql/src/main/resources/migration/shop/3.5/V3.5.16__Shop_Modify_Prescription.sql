ALTER TABLE `b2c_prescription` ADD COLUMN `doctor_signature` varchar(512) NOT NULL DEFAULT ''  COMMENT '医师签名' AFTER `settlement_flag`;
ALTER TABLE `b2c_prescription` ADD COLUMN `pharmacist_signature` varchar(512) NOT NULL DEFAULT ''  COMMENT '药师签名' AFTER `doctor_signature`;
ALTER TABLE `b2c_prescription` ADD COLUMN `cachet` varchar(512) NOT NULL DEFAULT ''  COMMENT '医院公章' AFTER `pharmacist_signature`;
