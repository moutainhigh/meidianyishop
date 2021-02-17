ALTER TABLE `b2c_prescription_item` MODIFY COLUMN `real_rebate_money`  decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '实际返利金额' ;

ALTER TABLE `b2c_prescription_item` ADD COLUMN `platform_rebate_proportion`  decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '平台返利比例' AFTER `real_rebate_money`;
ALTER TABLE `b2c_prescription_item` ADD COLUMN `platform_rebate_money`  decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '平台返利金额' AFTER `platform_rebate_proportion`;
ALTER TABLE `b2c_prescription_item` ADD COLUMN `platform_real_rebate_money`  decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '平台实际返利金额' AFTER `platform_rebate_money`;

ALTER TABLE `b2c_prescription_rebate` ADD COLUMN `platform_rebate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台返利金额' AFTER `real_rebate_money`;
ALTER TABLE `b2c_prescription_rebate` ADD COLUMN `platform_real_rebate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台实际返利金额' AFTER `platform_rebate_money`;

ALTER TABLE `b2c_inquiry_order` ADD COLUMN `rebate_proportion`  decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '返利比例' AFTER `image_url`;
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `total_rebate_money`  decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '返利金额' AFTER `rebate_proportion`;
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `platform_rebate_proportion`  decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '平台返利比例' AFTER `total_rebate_money`;
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `platform_rebate_money`  decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '平台返利金额' AFTER `platform_rebate_proportion`;
ALTER TABLE `b2c_inquiry_order_rebate` ADD COLUMN `platform_rebate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台实际返利金额' AFTER `total_rebate_money`;

create table if not exists `b2c_order_goods_platform_rebate` (
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `shop_id` int(11)   NOT NULL DEFAULT '0' COMMENT '店铺id',
    `rec_id` int(11)   NOT NULL DEFAULT '0' COMMENT '商品行id',
    `total_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品总金额',
    `can_calculate_money`  decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '可计算返利金额',
    `goods_sharing_proportion`  decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '分成比例',
    `platform_rebate_money` decimal(10,4)  NOT NULL DEFAULT '0.00' COMMENT '平台总返利金额',
    `platform_real_rebate_money` decimal(10,4)  NOT NULL DEFAULT '0.00' COMMENT '平台实际返利金额',
    `status`  tinyint(1)   NOT NULL DEFAULT '0' COMMENT '0待返利 1已返利 2未返利',
    `is_delete`     tinyint(1)   NOT NULL DEFAULT '0',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key(`id`),
    KEY `shop_id` (`shop_id`),
    KEY `rec_id` (`rec_id`)
)comment ='平台返利数据表';