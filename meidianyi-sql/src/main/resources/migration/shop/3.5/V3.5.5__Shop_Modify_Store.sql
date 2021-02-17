ALTER TABLE `b2c_store` ADD COLUMN `store_express` tinyint(1) NULL DEFAULT 0 COMMENT '门店配送 1支持' AFTER `pick_time_detail`;
