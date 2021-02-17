-- 处方表增加订单号
ALTER TABLE `b2c_prescription` ADD COLUMN `order_sn`  varchar(20) NOT NULL DEFAULT '' COMMENT '订单号' AFTER `pos_code`;
