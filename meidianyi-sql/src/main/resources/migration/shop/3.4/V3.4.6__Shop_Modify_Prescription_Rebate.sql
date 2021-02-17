ALTER TABLE `b2c_prescription_rebate` ADD COLUMN `real_rebate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际返利金额' AFTER `total_rebate_money`;
ALTER TABLE `b2c_prescription_item` ADD COLUMN `real_rebate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际返利金额' AFTER `total_rebate_money`;
