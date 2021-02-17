/*********************3.4***********************BEGIN*/
-- 埋点表
create table if not exists `b2c_anchor_points` (
    `id`          int(11)      not null auto_increment comment 'id',
    `event`       varchar(255) not null default '' comment '事件',
    `event_name`       varchar(255) not null default '' comment '事件名称',
    `event_type`       tinyint(1) not null default  0 comment '事件类型 0前段 1后端',
    `page`        varchar(255) not null default '' comment '页面',
    `module`    varchar(255) not null default '' comment '功能模块',
    `platform`    tinyint(1)   not null default 0 comment '平台 1 wxapp 2admin',
    `device`      tinyint(1)   not null default 0 comment '设备 1 android 2 ios  3 pc',
    `store_id`    int(11)      not null default 0 comment '门店id',
    `user_id`     int(11)      not null default 0 comment '用户id',
    `key`         varchar(255) not null default '' comment '参数',
    `value`       varchar(255) not null default '' comment '参数值',
    `update_time` datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    `create_time` datetime     not null default current_timestamp comment '添加时间',
    primary key (`id`)
) comment ='埋点';

-- 门店数据概览
create table `b2c_store_order_summary_trend` (
  `id`                   int(11)        not null auto_increment,
  `ref_date`             date           not null comment '2018-09-04',
  `type`                 tinyint(2)     not null comment '1,7,30,90',
  `store_id`             int(8)          default null comment '门店ID',
  `order_pay_user_num`   int(11)        not null comment '付款人数',
  `total_paid_money`     decimal(10, 2)      default null comment '消费金额',
  `order_pay_num`        int(11) default 0  null comment '成交订单数',
  `order_num`            int(11) default 0  not null comment '下单数',
  `order_user_num`       int(11) default 0  null comment '下单人数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  primary key (`id`),
  key `ref_type` (`ref_date`, `type`) using btree
)comment ='门店数据概览';

/*********************3.4*************************END*/
