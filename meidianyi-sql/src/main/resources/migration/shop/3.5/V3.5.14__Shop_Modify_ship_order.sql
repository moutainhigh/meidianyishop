ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `order_sn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号' AFTER `order_goods_id`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `batch_no` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '批次号' AFTER `order_sn`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `goods_id` mediumint(8) NOT NULL DEFAULT 0 COMMENT '商品id' AFTER `batch_no`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `goods_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商品名称' AFTER `goods_id`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `product_id` mediumint(8) NOT NULL DEFAULT 0 COMMENT '规格id' AFTER `goods_name`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `send_number` smallint(5) NOT NULL DEFAULT 1 COMMENT '数量' AFTER `product_id`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `shipping_type` tinyint(3) DEFAULT 0 NOT NULL COMMENT '快递类型0快递 1自提 2同城配送 3门店配送' AFTER `goods_attr`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `shipping_user_id` int(11) DEFAULT 0 NOT NULL COMMENT '发货人员userId' AFTER `shipping_time`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `shipping_account_id` int(11) DEFAULT 0 NOT NULL COMMENT '发货人员id' AFTER `shipping_time`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `shipping_platform` tinyint(3) DEFAULT 0 NOT NULL COMMENT '发货平台' AFTER `shipping_account_id`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `confirm_user_id` int(11) DEFAULT 0 NOT NULL COMMENT '收货人员userId' AFTER `confirm_time`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `confirm_account_id` int(11) DEFAULT 0 NOT NULL COMMENT '收货人员id' AFTER `confirm_time`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `confirm_platform` tinyint(3) DEFAULT 0 NULL COMMENT '收货操作平台' AFTER `confirm_account_id`;
ALTER TABLE `b2c_part_order_goods_ship` MODIFY COLUMN `create_time` timestamp(0) DEFAULT 0 NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间' AFTER `confirm_time`;