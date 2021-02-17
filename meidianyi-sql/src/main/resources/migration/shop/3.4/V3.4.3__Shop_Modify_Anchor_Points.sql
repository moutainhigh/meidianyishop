ALTER TABLE `b2c_anchor_points` MODIFY COLUMN `platform` varchar(20) NOT NULL DEFAULT '' COMMENT '平台  wxapp admin' AFTER `module`;
ALTER TABLE `b2c_anchor_points` MODIFY COLUMN `device` varchar(20) NOT NULL DEFAULT '' COMMENT '设备  android  ios   pc' AFTER `platform`;
-- 分销员推广中心收藏商品表
CREATE TABLE IF NOT EXISTS `b2c_distributor_collection` (
	`id` INT (11) NOT NULL AUTO_INCREMENT,
	`distributor_id` INT(11) NOT NULL COMMENT '分销员id',
	`collection_goods_id` text COMMENT '分销员收藏的商品id集合',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	PRIMARY KEY (`id`),
	KEY `distributor_id` (`distributor_id`)
) COMMENT = '分销员推广中心收藏商品表';