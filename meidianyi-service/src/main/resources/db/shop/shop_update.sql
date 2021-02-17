-- /***********************2.7********************BEGIN*/
-- --  修改用户持有优惠券表结构
-- -- ALTER TABLE `b2c_customer_avail_coupons` MODIFY `limit_order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '满多少可用';
--
--
-- -- 更正错误备注
-- ALTER TABLE `b2c_member_card` MODIFY COLUMN `store_use_switch` tinyint(1) NOT NULL DEFAULT 0 COMMENT '可否在门店使用  0不可以 1可以';
-- /***********************2.7*********************END*/
--
--
-- /***********************2.8********************BEGIN*/
--
-- -- 2020年1月10日 16:06:24 购物车表增加商品规格
-- -- ALTER TABLE `b2c_cart` CHANGE COLUMN `goods_specs` `prd_desc` text NULL COMMENT '例如,颜色:黑色';
-- -- ALTER TABLE `b2c_cart` MODIFY COLUMN `prd_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '规格描述，格式例子：颜色:红色 尺码:s';
--
-- --订单必填增加默认
-- ALTER TABLE `b2c_order_must` MODIFY COLUMN `must_content` varchar(100) NOT NULL DEFAULT '' COMMENT '必填信息';
-- -- 2020年2月6日 秒杀表添加初始销量字段
-- ALTER TABLE `b2c_sec_kill_define` ADD COLUMN `base_sale` int(8) DEFAULT '0' COMMENT '初始销量';
-- --20200207 申请分销员审核字段类型优化
-- -- ALTER TABLE `b2c_distributor_apply` MODIFY COLUMN `activation_fields` text DEFAULT NULL COMMENT '审核校验';
--
-- -- 2020年2月18日14:50:58 孔德成 抽奖奖品增加账户余额
-- ALTER TABLE `b2c_lottery_prize` ADD COLUMN `award_account` decimal(10,2) DEFAULT '0.00' COMMENT '用户余额';
--
--
-- -- 2020年2月20日17:11:57  字段默认存储从{}改为[]
-- -- ALTER TABLE `b2c_member_card` MODIFY COLUMN `store_list` varchar(191) NOT NULL DEFAULT '[]' COMMENT '可用门店';
--
-- -- 2020年2月20日 liufei  修改字段注释，新增字段
-- ALTER TABLE `b2c_user_summary_trend` MODIFY COLUMN `order_user_data` int(11) NOT NULL COMMENT '成交客户数（付款用户数:distinct(userId)）';
-- ALTER TABLE `b2c_user_summary_trend` MODIFY COLUMN `order_user_num` int(11) DEFAULT '0' COMMENT '下单用户数(distinct(userId)生成订单就算)';
-- ALTER TABLE `b2c_user_summary_trend` ADD COLUMN `pay_people_num` int(11) DEFAULT '0' COMMENT '付款人数';
-- ALTER TABLE `b2c_user_summary_trend` ADD COLUMN `order_people_num` int(11) DEFAULT '0' COMMENT '下单人数';
--
--
-- -- 修复table
--
--
-- /***********************2.8*********************END*/
--
-- /***********************2.9********************BEGIN*/
--
-- -- 2020年2月11日 砍价杀表添加初始销量、绑定手机号字段
-- ALTER TABLE `b2c_bargain` ADD COLUMN `need_bind_mobile` tinyint(1) DEFAULT '0' COMMENT '是否需要绑定手机号，1是';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `initial_sales` int(9) DEFAULT '0' COMMENT '初始销量';
--
-- -- 2020年2月11日 pictorial 分享图片缓存表添加活动id字段
-- ALTER TABLE `b2c_pictorial` ADD COLUMN `activity_id` int(10) DEFAULT NULL COMMENT '活动id';
-- -- 2020年2月10日15:46:57  拼团表增加字段
-- ALTER TABLE `b2c_group_buy_define` ADD  COLUMN `level` int(11) NOT NULL DEFAULT 0 COMMENT '优先级' ;
-- ALTER TABLE `b2c_group_buy_define` ADD COLUMN `begin_num` int(11) NOT NULL DEFAULT 0 COMMENT '初始成团数' ;
-- -- 2020-02-11 新加服务承诺关联表和服务承诺表新加类型和优先级字段
-- CREATE TABLE IF NOT EXISTS  `b2c_pledge_related` (
--   `id` int(9) NOT NULL AUTO_INCREMENT COMMENT 'ID',
--   `pledge_id` int(9) NOT NULL DEFAULT '0' COMMENT '承诺id',
--   `type` tinyint(1) DEFAULT NULL COMMENT '指定商品范围:1 商品id,2 商家分类id,3 商品品牌id',
--   `related_id` int(9) NOT NULL DEFAULT '0' COMMENT '相关的id',
--   `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`),
--   key `bpr_pledge_id`(`pledge_id`)
-- )COMMENT='服务承诺关联表';
-- ALTER TABLE `b2c_pledge` ADD COLUMN `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '商品范围:1全部商品,2指定商品' ;
-- ALTER TABLE `b2c_pledge` ADD COLUMN `level` int(6) NOT NULL DEFAULT 0 COMMENT '商品优先级' ;
-- INSERT IGNORE INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2013, 1, 1);
-- INSERT IGNORE INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2014, 1, 1);
-- INSERT IGNORE INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2015, 1, 1);
-- CREATE TABLE IF NOT EXISTS  `b2c_user_remark` (
--   `id`          mediumint(10) unsigned NOT NULL AUTO_INCREMENT,
--   `user_id`     mediumint(8) unsigned NOT NULL DEFAULT '0',
--   `remark`      TEXT COMMENT '会员备注',
--   `add_time`    timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
--   `is_delete`   tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT'0:未删除；1删除',
--   PRIMARY KEY (`id`),
--   key `user_id` (`user_id`)
-- )COMMENT='会员备注';
-- --
-- ALTER TABLE `b2c_user_remark` MODIFY COLUMN `id` mediumint(10)  NOT NULL AUTO_INCREMENT;
-- ALTER TABLE `b2c_user_remark` MODIFY COLUMN `user_id` mediumint(8)  NOT NULL DEFAULT '0';
-- ALTER TABLE `b2c_user_remark` MODIFY COLUMN `is_delete` tinyint(1)  NOT NULL DEFAULT '0' COMMENT'0:未删除；1删除';
--
-- --2020-02-20 常乐 分销分组表添加 用户是否可选择
-- -- ALTER TABLE `b2c_distributor_group` ADD  COLUMN `can_select` tinyint(1) DEFAULT 1 NULL   COMMENT '支持用户选择 1：支持；0：不支持';
--
-- --2020-02-20 一口价 支持打包一口价折扣和减金额两种方式
-- ALTER TABLE `b2c_package_sale` ADD  COLUMN `package_type` tinyint(1) DEFAULT '0' COMMENT '活动类型0金额1折扣';
-- ALTER TABLE `b2c_package_sale` ADD  COLUMN `total_ratio` decimal(4,2) DEFAULT '0.00' COMMENT '结算比例';
--
--
-- --2020-03-30 用户优惠券使用时间允许为null
-- ALTER TABLE `b2c_customer_avail_coupons` MODIFY COLUMN `used_time` timestamp NULL DEFAULT '0000-00-00 00:00:00';
--
-- --2020-03-30 退款订单生成时保存是否自动退款的快照
-- ALTER TABLE `b2c_return_order` ADD COLUMN `is_auto_return` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0否；1是';
-- /***********************2.9*********************END*/
--
-- /***********************2.10*********************BEGIN*/
-- -- 2020年2月26日10:46:57  拼团活动商品信息记录表添加商品id字段
-- ALTER TABLE `b2c_group_buy_product_define` ADD COLUMN `goods_id` int(8) NOT NULL DEFAULT 0 COMMENT '商品id';
-- -- 2020年2月26日10:46:57  秒杀活动商品信息记录表添加商品id字段
-- ALTER TABLE `b2c_sec_kill_product_define` ADD COLUMN `goods_id` int(8) NOT NULL DEFAULT 0 COMMENT '商品id';
-- -- 2020年2月26日10:46:57  秒杀活动表添加优先级字段
-- ALTER TABLE `b2c_sec_kill_define` ADD COLUMN `first` tinyint(3) NOT NULL DEFAULT 0 COMMENT '优先级';
-- -- 2020年2月26日10:46:57  秒杀活动表修改goods_id类型为字符串
-- -- ALTER TABLE `b2c_sec_kill_define` DROP  INDEX IF exists `goods_id`;
-- -- ALTER TABLE `b2c_sec_kill_define` MODIFY COLUMN `goods_id` text  COMMENT '商品ID';
-- -- 2020年2月26日20:13:50 拼团活动表goods_id 字段有int转换为string
-- ALTER TABLE `b2c_group_buy_define` MODIFY COLUMN `goods_id` text NOT NULL COMMENT '商品id';
-- -- 2020年2月27日16:35:50 好友助力新增单天助力限制字段
-- -- ALTER TABLE `b2c_friend_promote_activity` ADD COLUMN `promote_times_per_day` int(8) null default 0 comment '单个用户每天最多可帮忙助力次数';
--
-- -- 2020年2月28日 加价购活动添加换购商品运费策略字段
-- ALTER TABLE `b2c_purchase_price_define` ADD COLUMN `redemption_freight` tinyint(1)  NOT NULL DEFAULT '0' COMMENT '换购商品运费策略，0免运费，1使用原商品运费模板';
--
-- -- 2020年03月03日 添加包邮,自定义权益字段
-- ALTER TABLE `b2c_member_card` ADD COLUMN `custom_rights` text COMMENT '自定义权益';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `freeship_limit` tinyint(3) DEFAULT -1 COMMENT '-1：不包邮，0:不限制，1：持卡有效期内，2：年，3：季，4：月，5：周，6：日';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `freeship_num` int(11) DEFAULT 0 COMMENT '周期内包邮次数';
--
-- -- 2020年03月04日优惠券礼包活动修改用户优惠券表
-- ALTER TABLE `b2c_customer_avail_coupons` ADD COLUMN `access_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '发放活动的订单的订单编号';
-- ALTER TABLE `b2c_customer_avail_coupons` MODIFY COLUMN `access_mode` tinyint(1) NOT NULL DEFAULT '0' COMMENT '获取方式，0：发放，1：领取，2：优惠券礼包自动发放';
--
-- -- 2020年03月05日 积分兑换记录表增加删除标识
-- ALTER TABLE `b2c_integral_mall_record` ADD COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1删除';
--
-- -- 2020年03月11日 会员卡表添加续费相关字段
-- ALTER TABLE `b2c_member_card` ADD COLUMN `renew_member_card` tinyint(1) DEFAULT 0 COMMENT '0:不可续费，1:可续费';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `renew_type` tinyint(1) DEFAULT 0 COMMENT '0:现金 1：积分';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `renew_num` decimal(10, 2) DEFAULT 0.00 COMMENT '现金或积分数量';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `renew_time` int(11) DEFAULT NULL COMMENT '续费时间' ;
-- ALTER TABLE `b2c_member_card` ADD COLUMN `renew_date_type` tinyint(1) DEFAULT NULL COMMENT '0:日，1:周 2: 月';
--
--
--
-- -- 商品表和规格表设置市场价字段可以为null
-- ALTER TABLE b2c_goods MODIFY market_price DECIMAL(10,2) COMMENT '市场价格，多规格时取最高';
-- ALTER TABLE b2c_goods_spec_product MODIFY prd_market_price DECIMAL(10,2) COMMENT '市场价格';
-- ALTER TABLE b2c_goods_spec_product_bak MODIFY prd_market_price DECIMAL(10,2) COMMENT '市场价格';
-- -- 恢复之前被他人误删的商品主键
-- -- ALTER TABLE `b2c_goods` ADD PRIMARY KEY ( `goods_id` );
-- -- 2020年03月12日 会员卡表添加不可与优惠券共用字段
-- ALTER TABLE `b2c_member_card` ADD COLUMN `cannot_use_coupon` tinyint(1) DEFAULT 0 COMMENT '是否和会员卡一起使用0:可以1：不可以' ;
-- -- 2020年03月12日 会员卡表添加自定义权益开关字段
-- ALTER TABLE `b2c_member_card` ADD COLUMN `custom_rights_flag` tinyint(1) DEFAULT 0 COMMENT '自定义权益开关';
--
--
-- -- 瓜分积分 添加活动规则说明
-- -- ALTER TABLE `b2c_group_integration_define` ADD COLUMN `activity_copywriting` TEXT COMMENT '活动规则说明';
--
-- -- 2020-03-17 用户卡表添加包邮快照信息字段
-- ALTER TABLE `b2c_user_card` ADD COLUMN `free_limit` tinyint(3) DEFAULT -1 COMMENT '-1：不包邮，0:不限制，1：持卡有效期内，2：年，3：季，4：月，5：周，6：日';
-- ALTER TABLE `b2c_user_card` ADD COLUMN `free_num` int(11) DEFAULT 0 COMMENT '周期内包邮次数';
--
-- -- 2020-03-19 商品表添加统计字段
-- -- ALTER TABLE `b2c_goods` ADD COLUMN `pv` int(11) DEFAULT '0' COMMENT '7天访问量';
-- -- ALTER TABLE `b2c_goods` ADD COLUMN `comment_num` int(11) DEFAULT '0' COMMENT '评论数';
--
-- -- 2020-03-20 商品导入信息结果详情表修改字段
-- ALTER TABLE b2c_goods_import_detail CHANGE COLUMN error_msg error_code TINYINT(3) NOT NULL DEFAULT 0 COMMENT '导入数据错误码，0表示正确 非0对应错误码';
--
-- -- 2020-03-24 评价表添加置顶字段
-- ALTER TABLE `b2c_comment_goods` ADD COLUMN `is_top` TINYINT ( 2 ) DEFAULT '0' COMMENT '是否置顶';
-- ALTER TABLE `b2c_comment_goods` ADD COLUMN `top_time` TIMESTAMP NULL DEFAULT NULL COMMENT '置顶时间';
--
-- --2020年3月26日 打包一口价 商品数可以为空
-- ALTER TABLE `b2c_package_sale` MODIFY COLUMN `goods_number_1` mediumint(11) NULL DEFAULT 0 COMMENT '分组商品数' ;
-- ALTER TABLE `b2c_package_sale` MODIFY COLUMN `goods_number_2` mediumint(11) NULL DEFAULT 0 COMMENT '分组商品数' ;
-- ALTER TABLE `b2c_package_sale` MODIFY COLUMN `goods_number_3` mediumint(11) NULL DEFAULT 0 COMMENT '分组商品数' ;
--
-- -- 2020-04-17 拼团抽奖表添加活动说明字段
-- ALTER TABLE `b2c_group_draw` ADD COLUMN `activity_copywriting` text COMMENT '活动说明';
--
--
--
-- -- 20200427优惠券礼包活动添加购物车展示选项
-- ALTER TABLE `b2c_coupon_pack` ADD COLUMN `show_cart` tinyint(1) DEFAULT '1' COMMENT '购物车是否展示，1是';
--
-- -- 20200508微信退款记录表增加订单号字段长度（预售订单补款退款时长度超限制）
-- ALTER TABLE `b2c_order_refund_record` MODIFY COLUMN `order_sn` varchar(22) NOT NULL DEFAULT '' COMMENT '订单编号';
-- -- 20200526 李晓冰商品导入详情添加是否导入成功字段
-- ALTER TABLE `b2c_goods_import` ADD COLUMN `is_finish` tinyint(1) DEFAULT '0' COMMENT '商品导入操作是否完成';
--
-- -- 2020年5月27日 kdc 地址表增加默认字段,删除字段
-- ALTER TABLE `b2c_user_address`    ADD COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0未删除 1删除';
-- -- ALTER TABLE `b2c_user_address`    ADD COLUMN `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是默认地址 0不是 1是';
-- /***********************2.10*********************END*/
--
-- /***********************2.11*********************BEGIN*/
-- -- 2020-03-26 砍价支持选择多商品
-- ALTER TABLE `b2c_bargain` MODIFY COLUMN `goods_id` varchar(9999)  COMMENT '商品ID';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `first` int(9) NOT NULL DEFAULT 0 COMMENT '优先级';
-- CREATE TABLE IF NOT EXISTS `b2c_bargain_goods` (
--   `id` int(9) NOT NULL AUTO_INCREMENT,
--   `bargain_id` int(9) DEFAULT '0' COMMENT '砍价活动主键',
--   `goods_id` int(9) DEFAULT '0',
--   `expectation_price` decimal(10,2) DEFAULT '0.00' COMMENT '指定金额结算模式的砍价底价 或 砍到任意金额结算模式的结算金额上限',
--   `floor_price` decimal(10,2) DEFAULT '0.00' COMMENT '任意金额结算模式的结算金额底价',
--   `stock` int(9) DEFAULT '0' COMMENT '活动库存',
--   `sale_num` int(9) DEFAULT '0' COMMENT '销量',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--   PRIMARY KEY (`id`)
-- )COMMENT='砍价活动商品表';
--
-- -- 2020-03-30 门店表添加支持同城配送字段
-- ALTER TABLE `b2c_store` ADD COLUMN `city_service` tinyint(1) DEFAULT '0' COMMENT '支持同城配送 1:支持';
--
-- -- 2020-03-30 新增b2c_article表
-- CREATE TABLE IF NOT EXISTS `b2c_article` (
--   `article_id` int(11) NOT NULL AUTO_INCREMENT,
--   `category_id` int(11) NOT NULL DEFAULT '1' COMMENT '文章分类',
--   `title` varchar(256) DEFAULT NULL,
--   `author` varchar(50)  DEFAULT NULL,
--   `keyword` varchar(256) DEFAULT NULL COMMENT '标签',
--   `desc` varchar(1024)  DEFAULT NULL COMMENT '文章描述',
--   `content` text ,
--   `is_recommend` tinyint(1) DEFAULT '0' COMMENT '1:推荐',
--   `is_top` tinyint(1) DEFAULT '0' COMMENT '1:置顶',
--   `status` tinyint(1) DEFAULT '0' COMMENT '0未发布,1已发布',
--   `pub_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
--   `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
--   `last_visit_time` datetime DEFAULT NULL,
--   `pv` int(11) DEFAULT NULL,
--   `show_footer` tinyint(1) DEFAULT '0' COMMENT '0:不在footer显示，1：显示',
--   `part_type` tinyint(1) DEFAULT '0' COMMENT '文章所属类型：0普通，1门店公告类文章',
--   `cover_img` varchar(50) DEFAULT NULL COMMENT '封面图片路径',
--   `is_del` tinyint(1) DEFAULT '0' COMMENT '0未删除,1已删除',
--   PRIMARY KEY (`article_id`),
--   KEY `is_recommend` (`is_recommend`),
--   KEY `is_top` (`is_top`)
-- );
--
-- -- 2020年3月30日 kdc 预售改为多商品 增加优先级 和预告时间
-- ALTER TABLE `b2c_presale` MODIFY COLUMN `goods_id` varchar(1000) NOT NULL DEFAULT 0 COMMENT '商品id 1,2,4' ;
-- ALTER TABLE `b2c_presale` ADD COLUMN `pre_time` int(8) NOT NULL DEFAULT 0 COMMENT '预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数' ;
-- ALTER TABLE `b2c_presale` ADD COLUMN `first` int(8) NULL DEFAULT 1 COMMENT '优先级' ;
--
-- CREATE TABLE IF NOT EXISTS `b2c_live_goods` (
--   `id`  int NOT NULL AUTO_INCREMENT,
--   `live_id`  int NOT NULL COMMENT '直播表关联ID',
--   `room_id`  int NOT NULL COMMENT '直播间ID',
--   `goods_id`  int NULL DEFAULT 0,
--   `cover_img`  varchar(255) NULL COMMENT '商品图',
--   `url`  varchar(255) NULL COMMENT '小程序路径',
--   `price`  decimal(10,2) NULL,
--   `name`  varchar(255) NULL COMMENT '商品名称',
--   `add_cart_num`  int NULL DEFAULT 0 COMMENT '加购数',
--   `price_end`  decimal(10,2) NULL DEFAULT 0 COMMENT '另一个价格',
--   `price_type`  tinyint(1) NULL DEFAULT 1 COMMENT '价格形式：1一口价 2价格区间 3显示折扣价',
--   `del_flag`  tinyint(1) NULL DEFAULT 0,
--   `del_time`  datetime NULL,
--   PRIMARY KEY (`id`),
--   INDEX `live_id` (`live_id`),
--   INDEX `room_id` (`room_id`),
--   INDEX `goods_id` (`goods_id`)
-- );
--
-- CREATE TABLE IF NOT EXISTS `b2c_live_broadcast` (
--   `id`  int NOT NULL AUTO_INCREMENT ,
--   `room_id`  int NOT NULL COMMENT '直播间ID' ,
--   `name`  varchar(255) NOT NULL COMMENT '直播间名称' ,
--   `live_status`  smallint NULL DEFAULT 0 COMMENT '直播状态 101: 直播中, 102: 未开始, 103: 已结束, 104: 禁播, 105: 暂停中, 106: 异常, 107: 已过期' ,
--   `start_time`  datetime NULL COMMENT '计划开始时间' ,
--   `end_time`  datetime NULL COMMENT '计划结束时间' ,
--   `anchor_name`  varchar(100) NULL DEFAULT NULL COMMENT '主播名' ,
--   `cover_img`  varchar(255) NULL DEFAULT NULL COMMENT '封面图片 url' ,
--   `anchor_img`  varchar(255) NULL DEFAULT NULL COMMENT '直播间图片url' ,
--   `add_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP ,
--   `update_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP ,
--   `del_flag`  tinyint(1) NULL DEFAULT 0,
--   `del_time`  datetime NULL,
--   PRIMARY KEY (`id`),
--   UNIQUE INDEX `room_id` (`room_id`)
-- );
-- -- 2020年04月03日 商品规格表添加规格重量属性
-- ALTER TABLE b2c_goods MODIFY goods_weight DECIMAL(10,3) DEFAULT NULL COMMENT '商品重量，默认规格重量或自定义规格中的最小重量';
-- ALTER TABLE b2c_goods_spec_product add COLUMN prd_weight DECIMAL(10,3) DEFAULT NULL COMMENT '规格重量';
-- ALTER TABLE b2c_goods_spec_product_bak add COLUMN prd_weight DECIMAL(10,3) DEFAULT NULL COMMENT '规格重量';
--
-- -- 2020年04月06日  新增会员卡续费表
-- CREATE TABLE IF NOT EXISTS `b2c_card_renew` (
--   `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
--   `user_id` int(20) NOT NULL DEFAULT '0' COMMENT '用户id',
--   `card_id` int(20) NOT NULL DEFAULT '0' COMMENT '续费会员卡id',
--   `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
--   `add_time` timestamp NULL DEFAULT NULL COMMENT '续费时间',
--   `renew_money` decimal(10,2) DEFAULT '0.00' COMMENT '续费金额',
--   `renew_time` int(11) DEFAULT NULL COMMENT '续费时间',
--   `renew_date_type` tinyint(1) DEFAULT NULL COMMENT '0:日，1:周 2: 月',
--   `renew_type` tinyint(1) DEFAULT '0' COMMENT '0:现金 1：积分',
--   `payment` varchar(90) NOT NULL COMMENT '支付方式',
--   `pay_code` varchar(30) NOT NULL DEFAULT '' COMMENT '支付代号',
--   `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付Id，用于发送模板消息',
--   `message` varchar(191) DEFAULT '' COMMENT '备注',
--   `renew_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '续费单号',
--   `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单应付金额',
--   `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 0：待支付，1：已完成',
--   `member_card_no` varchar(32) DEFAULT '0' COMMENT '会员卡NO',
--   `member_card_redunce` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡抵扣金额',
--   `use_score` decimal(10,2) DEFAULT '0.00' COMMENT '积分抵扣金额',
--   `use_account` decimal(10,2) DEFAULT '0.00' COMMENT '用户消费余额',
--   `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
--   `ali_trade_no` varchar(60) DEFAULT '' COMMENT '支付宝交易单号',
--   `renew_expire_time` timestamp NULL DEFAULT NULL COMMENT '续费后过期时间',
--   PRIMARY KEY (`id`)
-- );
--
-- -- 商品标签修改del_flag字段类型，添加is_none字段
-- ALTER TABLE b2c_goods_label MODIFY del_flag TINYINT not NULL default 0 COMMENT '是否删除 0否 1是';
-- ALTER TABLE b2c_goods_label add COLUMN is_none TINYINT(1) DEFAULT 0 COMMENT '是否不选择商品： 1：是  0： 否';
--
-- -- 商品表添加直播间id字段
-- -- ALTER TABLE b2c_goods add COLUMN room_id int(4) COMMENT '直播间id';
-- -- 2020年04月10日 添加自定义激活配置
-- ALTER TABLE `b2c_member_card` ADD COLUMN `custom_options` text COMMENT '自定义激活信息配置';
--
--
-- -- 2020年04月10日 添加自定义激活配置
-- -- ALTER TABLE `b2c_member_card` ADD COLUMN `custom_options` text COMMENT '自定义激活信息配置';
--
-- -- 2020-04-10 分裂优惠券分享领取记录
-- CREATE TABLE IF NOT EXISTS `b2c_division_receive_record` (
--   `id` mediumint(8) NOT NULL AUTO_INCREMENT,
--   `user` mediumint(8)  NOT NULL DEFAULT '0' COMMENT '分享的user_id',
--   `user_id` mediumint(8)  NOT NULL DEFAULT '0' COMMENT '分享后领取的user_id',
--   `coupon_id` mediumint(8)  NOT NULL DEFAULT '0' COMMENT '分裂优惠券id 对应优惠券表中id',
--   `coupon_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '分裂优惠券的sn唯一标识',
--   `receive_num` int(11) NOT NULL DEFAULT '0' COMMENT '可领取个数',
--   `receive_per_num` smallint(3) NOT NULL DEFAULT '0' COMMENT '分裂优惠券领券人数是否限制 0不限制 1限制',
--   `source` tinyint(2) DEFAULT NULL COMMENT '送券活动来源',
--   `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '打折或减价量',
--   `receive_coupon_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '领取的sn唯一标识',
--   `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0主分裂优惠券 1 点击链接领取的',
--   `is_share` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0未分享 1分享',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
--   PRIMARY KEY (`id`),
--   KEY `user` (`user`)
-- );
-- -- 2020年4月13日 增加分裂优惠券是否可用
-- ALTER TABLE `b2c_customer_avail_coupons` ADD COLUMN `division_enabled` tinyint(1) NOT NULL  DEFAULT 0 COMMENT '是否可用,0可用 1不可用(只适用分裂优惠券)';
--
-- -- 2020年04月13日 添加自定义激活信息
-- ALTER TABLE `b2c_card_examine` ADD COLUMN `custom_options` text COMMENT '自定义激活信息';
--
-- -- 2020年04月13日 修改会员卡续费表字段
-- ALTER TABLE `b2c_card_renew` MODIFY COLUMN `id` int(20) NOT NULL AUTO_INCREMENT;
--
-- -- 2020年04月14日 添加会员卡同步标签
-- ALTER TABLE `b2c_member_card` ADD COLUMN `card_tag` tinyint(1) DEFAULT 0 COMMENT '是否开启给领卡用户打标签0否，1是';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `card_tag_id` varchar(20) COMMENT '领卡打标签id';
--
-- -- 2020年04月14日 添加会员卡转赠字段
-- ALTER TABLE `b2c_member_card` ADD COLUMN `card_give_away` tinyint(1) DEFAULT 0 COMMENT '0:不可转赠，1:可以转赠';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `card_give_continue` tinyint(1) DEFAULT 0 COMMENT '0:不可继续转赠，1:可以继续转赠';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `most_give_away` int(10) DEFAULT 0 COMMENT '最多可转赠多少次 0不限制';
--
--
-- -- 2020年04月15日 添加限次卡转赠记录表
-- CREATE TABLE IF NOT EXISTS `b2c_give_card_record` (
--   `id`int(20) NOT NULL AUTO_INCREMENT,
--   `user_id` int(8)  not null default 0 comment '转赠人用户ID',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '转赠时间',
--   `card_no` varchar(32) default '' not null comment '转赠会员卡号',
--   `get_user_id` int(8) not null default 0 comment '获赠人用户ID',
--   `get_time` timestamp null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '领取时间',
--   `get_card_no` varchar(32) default '' not null comment '获赠会员卡号',
--   `flag` tinyint(1) default '0' comment '正常 1放弃 2 转赠成功',
--   `deadline` timestamp NULL DEFAULT NULL comment '链接截止时间',
--   PRIMARY KEY (`id`)
-- );
--
-- -- 2020年04月15日 在用户卡表中添加转赠字段
-- ALTER TABLE `b2c_user_card` ADD COLUMN `give_away_status` tinyint(1) DEFAULT 0 COMMENT '0:正常，1:转赠中，2转赠成功';
-- ALTER TABLE `b2c_user_card` ADD COLUMN `give_away_surplus` int(11) DEFAULT NULL COMMENT '卡剩余赠送次数';
-- ALTER TABLE `b2c_user_card` ADD COLUMN `card_source` tinyint(1) NOT NULL DEFAULT 0 COMMENT '卡来源 0:正常  2 别人转赠 ';
--
-- -- 2020年04月16日 修改备注
-- ALTER TABLE `b2c_user_card` MODIFY COLUMN `flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0:正常，1:删除 2 转赠中 3 已转赠';
--
-- -- 2020年04月16日 用户标签添加字段
-- ALTER TABLE `b2c_user_tag` ADD COLUMN `source` smallint(2) NOT NULL DEFAULT 0 COMMENT '标签来源 0 后台设置 1领券 2领卡';
-- ALTER TABLE `b2c_user_tag` ADD COLUMN `tool_id` int(11) COMMENT '优惠券或会员卡id';
-- ALTER TABLE `b2c_user_tag` ADD COLUMN `times` smallint(5) DEFAULT 1 COMMENT '打标签次数，会员卡或优惠券过期停用时次数减一，为0时删除';
--
-- --订单返利商品表添加 商品行ID
-- ALTER TABLE `b2c_order_goods_rebate` ADD COLUMN `rec_id` int(11) NOT NULL DEFAULT 0 COMMENT '商品行ID';
--
--
--
--
-- -- 2020年04月24日 添加 已选活动商品表
-- CREATE TABLE IF NOT EXISTS `b2c_checked_goods_cart` (
-- 	`id` int(20) NOT NULL AUTO_INCREMENT,
-- 	`action` TINYINT(1) DEFAULT 0 COMMENT '活动类型: 1：限次卡兑换',
-- 	`identity_id` VARCHAR(50) DEFAULT '0' NULL COMMENT '活动ID',
-- 	`user_id` INT(8) NOT NULL DEFAULT 0 COMMENT '用户ID',
-- 	`goods_id` INT(8) NOT NULL DEFAULT 0 COMMENT '商品ID',
-- 	`product_id` INT(11) NOT NULL DEFAULT 0 COMMENT '商品规格组合的产品ID',
-- 	`goods_number` INT(8) NOT NULL DEFAULT 1 COMMENT '商品数量',
-- 	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
-- 	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
-- 	PRIMARY KEY (`id`),
-- 	INDEX `user_id` (`user_id`),
--   	INDEX `action` (`action`),
--   	INDEX `identity_id` (`identity_id`),
--   	INDEX `product_id` (`product_id`)
-- );
-- -- 2020年5月14日 虚拟商品会员卡下单增加会员卡卡号字段
-- ALTER TABLE `b2c_virtual_order` ADD COLUMN `send_card_no` varchar(32) NOT NULL COMMENT '订单发送的会员卡no' ;
-- -- 2020-05-21 渠道分析字段设置默认值
-- ALTER TABLE `b2c_channel` ALTER COLUMN `page_id` SET DEFAULT '0';
-- ALTER TABLE `b2c_channel` ALTER COLUMN `goods_id` SET DEFAULT '0';
-- /*********************2.11*************************END*/
--
-- /*********************2.12*************************START*/
-- -- 营销日历表活动
-- CREATE TABLE IF NOT EXISTS `b2c_market_calendar_activity` (
--   `id` int(8) NOT NULL AUTO_INCREMENT,
--   `calendar_id` int(8) NOT NULL DEFAULT '0' COMMENT '营销日历Id',
--   `sys_cal_act_id` int(8) NOT NULL DEFAULT '0' COMMENT '来源营销日历Id',
--   `activity_type` varchar(16)   NOT NULL DEFAULT '0' COMMENT '具体营销活动类型',
--   `activity_id` int(8) NOT NULL DEFAULT '0' COMMENT '具体营销活动Id',
--   `recommend_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推荐类型：0站内文本，1外部链接',
--   `recommend_link` varchar(100)   NOT NULL DEFAULT '' COMMENT '推荐链接',
--   `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已删除：0否，1是',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   `is_sync` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据是否已同步到system：目前只同步是否使用的数量',
--   `recommend_title` varchar(32)   NOT NULL DEFAULT '' COMMENT '推荐标题',
--   PRIMARY KEY (`id`),
--   KEY `calendar_id` (`calendar_id`),
--   KEY `sys_cal_act_id` (`sys_cal_act_id`)
-- );
-- -- 营销日历表
-- CREATE TABLE IF NOT EXISTS `b2c_market_calendar` (
--   `id` int(8) NOT NULL AUTO_INCREMENT,
--   `event_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '事件名称',
--   `event_time` date DEFAULT NULL  COMMENT '事件时间',
--   `event_desc` text COMMENT '事件说明',
--   `pub_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布状态：0未发布，1已发布',
--   `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已删除：0否，1是',
--   `source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '来源：0admin自己添加，1system推荐',
--   `source_id` int(8) NOT NULL DEFAULT '0' COMMENT '来源：来源营销事件id',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`)
-- );
--
--
-- -- 2020年04月20日 会员卡添加折扣不予营销活动公用
-- ALTER TABLE `b2c_member_card` ADD COLUMN `cannot_use_action` varchar(10) DEFAULT NULL COMMENT '不能与哪些营销活动共用 1会员价 2限时降价 3首单特惠';
--
-- -- 2020年04月21日 加价购给参加活动的用户打标签
-- ALTER TABLE `b2c_purchase_price_define` ADD COLUMN `activity_tag` tinyint(1) DEFAULT '0' COMMENT '是否给参加活动的用户打标签，1是';
-- ALTER TABLE `b2c_purchase_price_define` ADD COLUMN `activity_tag_id` varchar(20) DEFAULT NULL COMMENT '参加活动打标签id';
--
-- -- 2020年04月21日 限时降价给参加活动的用户打标签、预告
-- ALTER TABLE `b2c_reduce_price` ADD COLUMN `pre_time` int(8) DEFAULT '0' COMMENT '预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数';
-- ALTER TABLE `b2c_reduce_price` ADD COLUMN `activity_tag` tinyint(1) DEFAULT '0' COMMENT '是否给参加活动的用户打标签，1是';
-- ALTER TABLE `b2c_reduce_price` ADD COLUMN `activity_tag_id` varchar(20) DEFAULT NULL COMMENT '参加活动打标签id';
--
-- -- 2020年04月21日 秒杀给参加活动的用户打标签、预告
-- ALTER TABLE `b2c_sec_kill_define` ADD COLUMN `pre_time` int(8) DEFAULT '0' COMMENT '预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数';
-- ALTER TABLE `b2c_sec_kill_define` ADD COLUMN `activity_tag` tinyint(1) DEFAULT '0' COMMENT '是否给参加活动的用户打标签，1是';
-- ALTER TABLE `b2c_sec_kill_define` ADD COLUMN `activity_tag_id` varchar(20) DEFAULT NULL COMMENT '参加活动打标签id';
--
-- -- 2020年04月21日 砍价给参加活动的用户打标签、自定义活动规则
-- ALTER TABLE `b2c_bargain` ADD COLUMN `activity_copywriting` text COMMENT '自定义活动说明';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `launch_tag` tinyint(1) DEFAULT '0' COMMENT '是否给发起砍价用户打标签';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `launch_tag_id` varchar(20) DEFAULT NULL COMMENT '发起砍价活动用户打标签id';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `attend_tag` tinyint(1) DEFAULT '0' COMMENT '是否参与砍价用户打标签';
-- ALTER TABLE `b2c_bargain` ADD COLUMN `attend_tag_id` varchar(20) DEFAULT NULL COMMENT '参与砍价活动用户打标签id';
--
-- -- 2020年04月22日-常乐-普通优惠券添加用户打标签，与部分营销活动叠加使用配置
-- ALTER TABLE `b2c_mrking_voucher` ADD COLUMN `coupon_tag` tinyint(1) DEFAULT '0' COMMENT '是否领取优惠券用户打标签 0:否；1：是';
-- ALTER TABLE `b2c_mrking_voucher` ADD COLUMN `coupon_tag_id` varchar(20) DEFAULT NULL COMMENT '领取优惠券用户打标签id';
-- ALTER TABLE `b2c_mrking_voucher` ADD COLUMN `coupon_overlay` tinyint(1) NOT NULL DEFAULT '0'  comment '是否与限时降价、首单特惠、会员价活动共用 0共用 1不共用 ';
-- -- 2020年4月23日 kdc 拼团增加用户打标签,预告
-- ALTER TABLE `b2c_group_buy_define`    ADD COLUMN `pre_time` int(8) NOT NULL DEFAULT 0 COMMENT '预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数' ;
-- ALTER TABLE `b2c_group_buy_define`    ADD COLUMN `activity_tag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '参加活动打标签  0:否；1：是' ;
-- ALTER TABLE `b2c_group_buy_define`    ADD COLUMN `activity_tag_id` varchar(30) NOT NULL COMMENT '参加活动打标签id' ;
-- -- 2020年4月24日 kdc 拼团增加 活动说明
-- ALTER TABLE `b2c_group_buy_define`  ADD COLUMN `activity_copywriting` text NOT NULL COMMENT '活动说明';
-- -- 幸运大抽奖  限制免费,未中奖积分奖励 次数限制
-- ALTER TABLE `b2c_lottery`   ADD COLUMN `chance_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '次数限制 0每人 1每人每天';
-- ALTER TABLE `b2c_lottery`   MODIFY COLUMN `free_chances` int(8) NULL DEFAULT NULL COMMENT '免费抽奖次数 0不限制 -1不可免费抽奖 ';
-- ALTER TABLE `b2c_lottery`   MODIFY COLUMN `no_award_score` int(8) NULL DEFAULT NULL COMMENT '未中奖奖励积分 0不赠送积分 ';
-- ALTER TABLE `b2c_lottery`   MODIFY COLUMN `share_chances` int(8) NULL DEFAULT NULL COMMENT '分享最多获得次数 0 不限制次数' ;
-- ALTER TABLE `b2c_lottery`   MODIFY COLUMN `score_chances` int(8) NULL DEFAULT NULL COMMENT '积分最多抽奖次数 0不限制次数' ;
-- -- 支付有礼 注释修改  商品范围  状态
-- ALTER TABLE `b2c_pay_award`   MODIFY COLUMN `goods_area_type` mediumint(5) NULL DEFAULT 0 COMMENT '商品范围类型 1全部商品 0 部分商品' ;
-- ALTER TABLE `b2c_pay_award`   MODIFY COLUMN `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 1启用 0停用' ;
--
--
-- -- 返利策略 佣金计算方式
-- ALTER TABLE `b2c_distribution_strategy` ADD COLUMN `strategy_type` tinyint(1) NULL DEFAULT 0 COMMENT '佣金计算方式;0:商品实际支付金额*佣金比例；1：商品实际利润（实际支付金额-成本价）* 佣金比例';
--
-- -- 2020.04.26 好友助力 添加活动规则说明
-- ALTER TABLE `b2c_friend_promote_activity` ADD COLUMN `activity_copywriting` text COMMENT '活动说明';
-- -- 批量发货记录表
-- create table  IF NOT EXISTS `b2c_bulkshipment_record` (
--   `id`          int not null auto_increment,
--   `sys_id`      int(9)  default null comment '操作员ID',
--   `account_id`  int(9)  default null comment 'SUB操作员ID',
--   `total_num`   int          default 0 comment '总数',
--   `success_num` int          default 0 comment '成功数',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   primary key (`id`)
-- );
--
--
-- -- 批量发货详细记录表
-- create table IF NOT EXISTS `b2c_bulkshipment_record_detail` (
--   `id`            int(9) not null auto_increment,
--   `batch_id`      int(9) not null comment'批次id',
--   `status`        tinyint(1)  not null  comment '0成功 1 失败',
--   `fail_reason`   varchar(100) null  default '' comment'失败原因',
--   `order_sn`      varchar(20) null  default ''comment'订单号',
--   `shipping_name` varchar(50) null  comment'快递公司',
--   `shipping_no`   varchar(20) null comment '快递单号',
--   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   primary key (`id`)
-- );
-- -- 评论表添加买家秀相关字段
-- ALTER TABLE `b2c_comment_goods` ADD COLUMN `is_show` tinyint(2) DEFAULT '0' COMMENT '是否买家秀';
-- ALTER TABLE `b2c_comment_goods` ADD COLUMN `show_time` timestamp NULL DEFAULT NULL COMMENT '买家秀时间';
--
-- -- 退款订单表增加售后方式
-- ALTER TABLE `b2c_return_order` ADD COLUMN `return_source` tinyint(1) DEFAULT '1' COMMENT '售后发起来源：0商家手动发起，1用户主动申请，2订单异常系统自动发起';
-- ALTER TABLE `b2c_return_order` ADD COLUMN `return_source_type` tinyint(1) DEFAULT '0' COMMENT '售后发起来源类型：0改价失败自动售后，1微信支付失败，2活动自动售后';
-- /*********************2.12*************************END*/
--
-- /*********************2.13*************************START*/
-- -- 门店新增自提相关字段
-- ALTER TABLE `b2c_store` ADD COLUMN `pick_time_action` tinyint(1) DEFAULT '1' COMMENT '自提取货时间类型';
-- ALTER TABLE `b2c_store` ADD COLUMN `pick_time_detail` varchar(50) DEFAULT NULL COMMENT '自提时间明细';
--
-- -- 会员卡添加兑换商品限制时间类型和次数
-- ALTER TABLE `b2c_member_card` ADD COLUMN `period_limit` tinyint(1) DEFAULT NULL COMMENT '0:不限制，1：日，2：周，3：月，4：季度，5：年';
-- ALTER TABLE `b2c_member_card` ADD COLUMN `period_num` int(11) DEFAULT NULL COMMENT '周期内允许兑换次数';
--
-- -- 用户导入添加备注
-- ALTER TABLE `b2c_user_import_detail` ADD COLUMN `remark` text COMMENT '会员备注';
--
-- -- 会员卡充值记录表补充字段
-- ALTER TABLE `b2c_charge_money` ADD COLUMN `return_money` decimal(10,2) DEFAULT '0.00' COMMENT '已退金额';
-- ALTER TABLE `b2c_charge_money` ADD COLUMN `after_charge_money` decimal(10,2) DEFAULT '0.00' COMMENT '充值后卡余额';
-- ALTER TABLE `b2c_charge_money` ADD COLUMN `change_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '充值类型 1发卡 2用户充值 3 管理员操作';
--
-- -- 黄壮壮 2020年05月20日 添加会卡审核表字段
-- ALTER TABLE `b2c_card_examine` ADD COLUMN `sys_id` int(9) UNSIGNED DEFAULT NULL COMMENT '操作员ID';
-- ALTER TABLE `b2c_card_examine` ADD COLUMN `account_id` int(9) UNSIGNED DEFAULT NULL COMMENT 'SUB操作员ID';
-- ALTER TABLE `b2c_card_examine` ADD COLUMN `account_type` tinyint(1) DEFAULT 0 COMMENT '账户类型：0店铺账号，1系统账号';
--
-- -- 分销员统计信息
-- ALTER TABLE `b2c_user_total_fanli` ADD COLUMN `final_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总返利金额，total_money为提现后金额';
--
-- -- 增加物流公司
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (25, 'DISTRIBUTOR_906984', '', '华东配-万象', '落地配 华东华北华南', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (26, 'DISTRIBUTOR_13363915', '', '安鲜达供应链', '落地配 全国', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (27, 'DISTRIBUTOR_901497', '',  '晟邦物流-生鲜仓配-华东区域', '落地配 华东、华北', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (28, 'DISTRIBUTOR_11113521', '',  '成都东骏-生鲜仓配', '落地配 西南', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (29, 'DISTRIBUTOR_13195952', '',  '菜鸟瞄鲜生', '顺丰 全国', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (30, 'DISTRIBUTOR_13174102', '',  'D速', '落地配 青岛仓', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (31, 'DISTRIBUTOR_1256888', '',  '芝麻开门落地配', '落地配 华东、福州仓', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (32, 'DISTRIBUTOR_13456083', '',  '芝麻开门-菜鸟生鲜-安鲜达', '快递 全国', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (33, 'DISTRIBUTOR_13494868', '',  '河北新智慧', '快递 河北区域', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (34, 'jieanda', 'jieanda',  '捷安达国际速递', '捷安达国际速递', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (35, 'PJ', 'pjbest',  '品骏物流', '品骏物流', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (36, 'topspeedex', 'topspeedex',  '中运全速', '中运全速', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (37, 'xdexpress', 'xdexpress',  '迅达速递', '迅达速递', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (38, 'ewe', 'ewe',  'EWE全球快递', 'EWE全球快递', '0', 0, 1, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (39, 'exfresh',  '', '中欧物流', '中欧物流', '0', 0, 0, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (40, 'aae', 'aae''',  'AAE全球专递', 'AAE全球专递', '0', 0, 0, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (41, 'annengwuliu', '',  '安鲜达单', '安鲜达单', '0', 0, 0, '', 0, 0);
-- INSERT IGNORE INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (42, 'no_number_express', '',  '无单号物流', '无单号物流', '0', 0, 1, '', 0, 0);
--
-- -- 2020.06.19 修改用户邀请保护时间类型
-- ALTER TABLE b2c_user MODIFY COLUMN `invite_protect_date` datetime DEFAULT null COMMENT '邀请保护时间';
--
-- /***********************2.13*********************END*/
-- /***********************3.1-医疗*********************START*/
-- /***********************3.1-医疗*********************END*/
--
-- /*********************3.2***********************BEGIN*/
-- /*********************3.2*************************END*/

/*********************3.2***********************BEGIN*/
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `rebate_proportion` decimal(6,4) DEFAULT '0.0000' COMMENT '返利比例';
ALTER TABLE `b2c_inquiry_order` ADD COLUMN `total_rebate_money` decimal(10,4) DEFAULT '0.0000' COMMENT '返利金额';

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
/*********************3.2*************************END*/
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

-- 埋点表
create table if not exists `b2c_anchor_points` (
    `id`          int(11)      not null auto_increment comment 'id',
    `event`       varchar(255) not null default '' comment '事件',
    `event_name`       varchar(255) not null default '' comment '事件名称',
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

/*********************3.4*************************END*/
