ALTER TABLE `b2c_prescription_item` ADD COLUMN `can_calculate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '可计算返利金额' AFTER `medicine_price`;
ALTER TABLE `b2c_prescription_rebate` ADD COLUMN `can_calculate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '可计算返利金额' AFTER `total_money`;
