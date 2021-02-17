-- 处方表增加处方总金额
ALTER TABLE `b2c_prescription` ADD COLUMN `total_price`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '处方总金额' AFTER `patient_sign`;
