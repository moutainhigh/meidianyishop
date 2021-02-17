ALTER TABLE `b2c_order_goods` ADD COLUMN `goods_quality_ratio` varchar(512) NOT NULL DEFAULT '' COMMENT '规格系数' AFTER `goods_name`;
ALTER TABLE `b2c_order_goods` ADD COLUMN `goods_production_enterprise` varchar(512) NOT NULL DEFAULT '' COMMENT '生产厂家' AFTER `goods_quality_ratio`;
ALTER TABLE `b2c_order_goods` ADD COLUMN `goods_approval_number` varchar(128) NOT NULL DEFAULT '' COMMENT '批准文号' AFTER `goods_production_enterprise`;
