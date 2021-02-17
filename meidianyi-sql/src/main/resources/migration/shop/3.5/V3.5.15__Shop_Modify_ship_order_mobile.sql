ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `shipping_mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '发货人员手机号' AFTER `shipping_platform`;
ALTER TABLE `b2c_part_order_goods_ship` ADD COLUMN `confirm_mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '收货人员手机号' AFTER `confirm_platform`;
