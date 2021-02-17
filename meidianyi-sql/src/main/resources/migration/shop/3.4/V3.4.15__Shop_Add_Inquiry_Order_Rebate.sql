-- 医师问诊订单返利表
create table if not exists `b2c_inquiry_order_rebate` (
  `id` int(11) not null auto_increment,
  `order_sn` varchar(64)  not null default '' comment '订单号',
  `doctor_id` int(11) not null default '0' comment '医师id',
  `total_money` decimal(10,2) not null default '0.00' comment '问诊金额',
  `total_rebate_money` decimal(10,2) not null default '0.00' comment '返利金额',
  `status` tinyint(1) not null default '0' comment '状态  0待返利 1已返利',
  `reason` varchar(256) NOT NULL DEFAULT ''  COMMENT '未返利原因',
  `is_delete` tinyint(1) not null default '0' comment '删除',
  `rebate_time` timestamp not null default '0000-00-00 00:00:00'comment  '返利日期',
  `create_time` timestamp not null default current_timestamp,
  `update_time` timestamp not null default current_timestamp on update current_timestamp comment '最后修改时间',
  primary key (`id`),
  key `order_sn` (`order_sn`),
  key `doctor_id` (`order_sn`)
)comment='医师问诊订单返利表';