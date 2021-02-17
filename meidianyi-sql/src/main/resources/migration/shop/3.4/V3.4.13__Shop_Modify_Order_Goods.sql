ALTER TABLE `b2c_order_goods` ADD COLUMN `prescription_detail_code`  varchar(64) NOT NULL DEFAULT '' COMMENT '处方明细号' AFTER `prescription_code`;
