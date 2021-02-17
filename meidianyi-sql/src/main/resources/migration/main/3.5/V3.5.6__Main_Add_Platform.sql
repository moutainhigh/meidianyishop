-- 平台返利数据表
create table if not exists `b2c_platform_total_rebate` (
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `shop_id` int(11)   NOT NULL DEFAULT '0' COMMENT '店铺id',
    `total_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '累计获得返利金额',
    `final_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '总返利金额，total_money为提现后金额',
    `is_delete`     tinyint(1)   NOT NULL DEFAULT '0',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key(`id`),
    KEY `shop_id` (`shop_id`)
)comment ='平台返利数据表';