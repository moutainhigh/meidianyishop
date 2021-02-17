/*********************3.4***********************BEGIN*/
-- 医师评价回复
create table if not exists `b2c_doctor_comment_reply`
(
    `id`          int(11)       not null auto_increment,
    `comment_id`  int(11)       not null comment '医师评价表id',
    `reply_note`  varchar(1000) NOT NULL DEFAULT '' COMMENT '回复内容',
    `is_delete`   tinyint(1)    not null default 0 comment '删除',
    `create_time` timestamp     not null default current_timestamp,
    `update_time` timestamp     not null default current_timestamp on update current_timestamp comment '最后修改时间',
    primary key (`id`)
) comment ='医师评价回复';

-- 同城配送账号表
CREATE TABLE IF NOT EXISTS `b2c_city_service_account` (
    `id`  int NOT NULL AUTO_INCREMENT ,
    `delivery_id`  varchar(20) NOT NULL COMMENT '配送公司Id' ,
    `appkey`  varchar(50) NULL DEFAULT NULL COMMENT '配送公司分配的appkey' ,
    `appsecret`  varchar(100) NULL DEFAULT NULL COMMENT '配送公司分配的appsecret' ,
    `account_status`  tinyint(1) NULL DEFAULT 1 COMMENT '账号状态： 0 审核通过 1 审核中 2 审核不通过' ,
    `custom_name`  varchar(50) NULL DEFAULT '' COMMENT '自定义名称' ,
    `status`  tinyint(1) NULL DEFAULT 0 COMMENT '系统设置状态： 1 启用 0 禁用' ,
    `create_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP ,
    `update_time`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    PRIMARY KEY (`id`)
)COMMENT='同城配送账号表';

-- 医师返利数据表
create table if not exists `b2c_doctor_total_rebate` (
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `doctor_id` int(11)   NOT NULL DEFAULT '0' COMMENT '医师id',
    `total_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '累计获得返利金额',
    `blocked_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '冻结余额',
    `final_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '总返利金额，total_money为提现后金额',
    `is_delete`     tinyint(1)   NOT NULL DEFAULT '0',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key(`id`),
    KEY `doctor_id` (`doctor_id`)
)comment ='医师返利数据表';

-- 同城配送订单表
CREATE TABLE IF NOT EXISTS `b2c_city_service_order` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `batch_no` VARCHAR(50) COMMENT '发货批次号',
    `order_sn` VARCHAR(50) COMMENT '订单号',
    `service_account_id` INT COMMENT '签约账号关联ID',
    `fee` DECIMAL(10,2) COMMENT '实际运费(单位：元)，运费减去优惠券费用',
    `deliverfee` DECIMAL(10,2) COMMENT '运费',
    `couponfee` DECIMAL(10,2) COMMENT '优惠券费用',
    `tips` DECIMAL(10,2) COMMENT '小费',
    `insurancefee` DECIMAL(10,2) COMMENT '保价费',
    `distance` DECIMAL(10,2) COMMENT '配送距离',
    `waybill_id` VARCHAR(100) COMMENT '配送单号',
    `order_status` VARCHAR(20) COMMENT '配送状态',
    `finish_code` VARCHAR(50) COMMENT '收货码',
    `pickup_code` VARCHAR(50) COMMENT '取货码',
    `dispatch_duration` INT COMMENT '骑手接单时间(单位s)',
    `agent_name` VARCHAR(50) COMMENT '骑手姓名',
    `agent_phone` VARCHAR(20) COMMENT '骑手电话',
    `action_time` DATETIME COMMENT '状态变更时间',
    `action_msg` VARCHAR(500) NULL   COMMENT '附加信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deduct_fee`  decimal(10,2) NULL DEFAULT 0 COMMENT '扣除的违约金',
    PRIMARY KEY (`id`),
    INDEX `batch_no` (`batch_no`),
    INDEX `order_sn` (`order_sn`),
    INDEX `waybill_id` (`waybill_id`),
    INDEX `order_status` (`order_status`)
)COMMENT='同城配送订单表';

-- 门店账户表
CREATE TABLE if not exists `b2c_store_account` (
    `account_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '门店账号ID',
    `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '所属店铺id',
    `sys_id` int(10) NOT NULL DEFAULT '0' COMMENT '所属账户id',
    `mobile` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号',
    `account_name` varchar(50) DEFAULT '' COMMENT '账户名称',
    `wx_nick_name` varchar(50) default '' comment '门店账户绑定微信昵称',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `account_type` tinyint(1) DEFAULT '1' COMMENT '账户类型1:店员，2：店长',
    `status` tinyint(1) DEFAULT '0' COMMENT '账户状态0:禁用，1：启用',
    `del_flag` tinyint(1) DEFAULT '0' COMMENT '是否已删除0:否，1：是',
    `account_passwd` varchar(64)  DEFAULT NULL COMMENT '账号密码',
    `store_list` varchar(191)  DEFAULT NULL COMMENT '可用门店id,逗号隔开',
    `update_time`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`account_id`),
    KEY `mobile` (`mobile`),
    KEY `account_name` (`account_name`)
) comment '门店账户表';

-- 医师评价表增加置顶 kdc
alter table `b2c_doctor_comment` add column `top` int(11) not null default 0 comment '置顶' after `comm_note`;


create table if not exists `b2c_prescription_rebate` (
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `prescription_code` varchar(64)  NOT NULL DEFAULT '' COMMENT '处方号',
    `doctor_id` int(11)   NOT NULL DEFAULT '0' COMMENT '医师id',
    `total_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '处方包含药品总金额',
    `total_rebate_money` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '返利总金额',
    `status` tinyint(1)  NOT NULL DEFAULT '0' COMMENT '0待返利 1已返利 2未返利',
    `reason` varchar(256) NOT NULL DEFAULT ''  COMMENT '未返利原因',
    `rebate_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '返利日期',
    `is_delete`     tinyint(1)   NOT NULL DEFAULT '0',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key(`id`),
    KEY `doctor_id` (`doctor_id`)
)comment ='处方药品返利表';

create table if not exists `b2c_doctor_withdraw` (
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `doctor_id` int(11)   NOT NULL DEFAULT '0' COMMENT '医师id',
    `type` tinyint(1)   NOT NULL DEFAULT '0' COMMENT '提现类型  1微信公众号钱包提现 2小程序',
    `status` tinyint(1)   NOT NULL DEFAULT '1' COMMENT '处理状态 1待审核 2拒绝 3已审核待出账 4出账成功',
    `order_sn` varchar(64)   NOT NULL DEFAULT '' COMMENT '提现单号',
    `withdraw_user_num` varchar(20)   NOT NULL DEFAULT '' COMMENT '用户提现序号',
    `withdraw_num` varchar(20)   NOT NULL DEFAULT '' COMMENT '流水号',
    `withdraw_cash` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '提现金额',
    `withdraw` decimal(10,2)  NOT NULL DEFAULT '0.00' COMMENT '可提现金额',
    `desc` text COMMENT '备注',
    `refuse_desc` text COMMENT '驳回原因',
    `check_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '审核时间',
    `refuse_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '驳回时间',
    `billing_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '出账时间',
    `fail_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '失败时间',
    `desc_time`   timestamp    NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '备注时间',
    `withdraw_source` text COMMENT '申请时提现配置',
    `real_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    primary key(`id`),
    KEY `doctor_id` (`doctor_id`)
)comment ='医生返利提现申请表';

ALTER TABLE `b2c_prescription_item` ADD COLUMN `goods_sharing_proportion` decimal(10,4) DEFAULT '0.0000' COMMENT '商品分成比例' AFTER `medicine_price`;
ALTER TABLE `b2c_prescription_item` ADD COLUMN `rebate_proportion` decimal(6,4) DEFAULT '0.0000' COMMENT '返利比例' AFTER `goods_sharing_proportion`;
ALTER TABLE `b2c_prescription_item` ADD COLUMN `total_rebate_money` decimal(10,4) DEFAULT '0.0000' COMMENT '返利金额' AFTER `rebate_proportion`;
ALTER TABLE `b2c_prescription` ADD COLUMN `settlement_flag` tinyint(1) DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算' AFTER `is_valid`;
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `settlement_flag` tinyint(1) DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算' AFTER `is_delete`;

ALTER TABLE `b2c_store` ADD COLUMN `store_code` varchar(50) default '' comment '门店编号' after `store_name`;

-- 门店商品添加药品相关字段
ALTER TABLE b2c_store_goods ADD goods_common_name VARCHAR(512) AFTER goods_id;
ALTER TABLE b2c_store_goods ADD goods_quality_ratio VARCHAR(512) AFTER goods_common_name;
ALTER TABLE b2c_store_goods ADD goods_approval_number VARCHAR(512) AFTER goods_quality_ratio;
ALTER TABLE b2c_store_goods ADD goods_production_enterprise VARCHAR(512) AFTER goods_approval_number;
ALTER TABLE b2c_store_goods ADD is_delete tinyint(1) default 0 comment '是否删 0否 1是';
ALTER TABLE b2c_store_goods ADD goods_store_sn varchar(512) comment '药房商品唯一码' AFTER goods_production_enterprise;


/*********************3.4*************************END*/
