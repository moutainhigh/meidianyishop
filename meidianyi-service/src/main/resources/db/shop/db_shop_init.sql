

CREATE TABLE `b2c_attend_share_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `record_id` int(11) NOT NULL DEFAULT '0' COMMENT '对应b2c_share_award_record表id',
  `share_id` int(9) NOT NULL DEFAULT '0' COMMENT '活动ID',
  `goods_id` int(9) DEFAULT '0' COMMENT '商品ID',
  `user_id` int(9) NOT NULL DEFAULT '0' COMMENT '参与分享活动用户ID，即点击别人分享链接查看商品的用户id',
  `is_new` tinyint(1) DEFAULT '0' COMMENT '是否是新用户：0否，1是',
  `launch_user_id` int(9) NOT NULL DEFAULT '0' COMMENT '触发分享活动用户ID，即分享商品的用户id',
  `level` tinyint(1) DEFAULT '0' COMMENT '参加活动时的活动进行等级1,2,3',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `attend_share_user` (`record_id`,`user_id`)
)COMMENT='用户点击分享链接触发分享生效记录表';



CREATE TABLE `b2c_bargain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bargain_name` varchar(120) NOT NULL COMMENT '活动名称',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `expectation_number` int(6) NOT NULL DEFAULT '0' COMMENT '砍价预期人数',
  `expectation_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预期砍价最低金额',
  `bargain_min` float DEFAULT NULL COMMENT '首次返利比例小',
  `bargain_max` float DEFAULT NULL COMMENT '首次返利比例大',
  `stock` int(6) NOT NULL DEFAULT '0' COMMENT '库存',
  `sale_num` int(6) NOT NULL DEFAULT '0' COMMENT '销售量',
  `mrking_voucher_id` varchar(200) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1可用，0停用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `reward_coupon_id` varchar(200) DEFAULT NULL COMMENT '砍价失败发放优惠券',
  `share_config` text COMMENT '分享设置',
  `bargain_type` tinyint(1) DEFAULT '0' COMMENT '砍价类型0定人1任意价',
  `floor_price` decimal(10,2) DEFAULT '0.00' COMMENT '任意低价',
  `bargain_money_type` tinyint(1) DEFAULT '0' COMMENT '砍价计算模式',
  `bargain_fixed_money` decimal(10,2) DEFAULT '0.00' COMMENT '固定金额',
  `bargain_min_money` decimal(10,2) DEFAULT '0.00' COMMENT '最低价',
  `bargain_max_money` decimal(10,2) DEFAULT '0.00' COMMENT '最高价',
  `free_freight` tinyint(1) DEFAULT '0' COMMENT '0不免运费，使用原商品运费模板   1免运费',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`)
)COMMENT='砍价活动表';



CREATE TABLE `b2c_bargain_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `bargain_id` int(11) NOT NULL DEFAULT '0' COMMENT '活动id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `prd_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品id',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品价格',
  `bargain_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已砍价格',
  `user_number` int(6) NOT NULL DEFAULT '0' COMMENT '砍价人数',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0砍价中1成功2失败（成功即扣库存）',
  `is_ordered` int(1) NOT NULL DEFAULT '0' COMMENT '是否下单',
  `order_sn` varchar(20) NOT NULL DEFAULT '0' COMMENT '订单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `user_bargain` (`user_id`,`bargain_id`),
  KEY `bargain_id` (`bargain_id`)
)COMMENT='砍价发起表';



CREATE TABLE `b2c_bargain_user_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `record_id` int(11) NOT NULL DEFAULT '0' COMMENT '对应b2c_bargain_record表id',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `bargain_money` decimal(10,2) NOT NULL DEFAULT '0.00',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `bargain_user` (`record_id`,`user_id`)
)COMMENT='砍价用户表';



CREATE TABLE `b2c_batch_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `prd_sn` varchar(65) NOT NULL DEFAULT '' COMMENT '商家编码',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '规格价格',
  `act_id` int(11) NOT NULL DEFAULT '1' COMMENT '导入批次',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='批量改价规格价格对,寺库专用';



CREATE TABLE `b2c_batch_profit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `brand_id` int(11) DEFAULT '0' COMMENT '品牌id',
  `sort_id` int(11) DEFAULT '0' COMMENT '分类id',
  `act_id` int(11) DEFAULT '1' COMMENT '导入批次',
  `profit_per` decimal(10,2) NOT NULL DEFAULT '0.00',
  `file_name` varchar(200) NOT NULL DEFAULT '' COMMENT '文件名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='品牌分类毛利对 寺库专用';



CREATE TABLE `b2c_brand_classify` (
  `classify_id` int(11) NOT NULL AUTO_INCREMENT,
  `classify_name` varchar(90) NOT NULL DEFAULT '',
  `first` smallint(2) NOT NULL DEFAULT '0' COMMENT '优先级',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`classify_id`)
)COMMENT='店铺自定义品牌分类';



CREATE TABLE `b2c_card_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_id` int(11) DEFAULT '0' COMMENT '卡号id',
  `action` tinyint(1) DEFAULT '1' COMMENT '领取码获得方式 1：自动生成 2：导入',
  `name` varchar(200) NOT NULL COMMENT '批次名称',
  `code_size` tinyint(1) DEFAULT '0' COMMENT '领取码位数',
  `card_size` tinyint(1) DEFAULT NULL COMMENT '卡号位数',
  `card_pwd_size` tinyint(1) DEFAULT NULL COMMENT '卡密码位数',
  `number` int(11) DEFAULT '0' COMMENT '发放数量',
  `code_prefix` varchar(10) DEFAULT NULL COMMENT '领取码前缀',
  `card_prefix` varchar(10) DEFAULT NULL COMMENT '卡前缀',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `card_id` (`card_id`),
  KEY `action` (`action`)
)COMMENT='会员卡批次表';



CREATE TABLE `b2c_card_consumer` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `card_id` int(20) NOT NULL DEFAULT '0' COMMENT '会员卡id',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '消费的卡余额',
  `count` smallint(3) NOT NULL DEFAULT '0' COMMENT '消费次数',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消费类型 0是普通卡 1限次卡',
  `reason_id` varchar(100) NOT NULL COMMENT '充值原因模板id',
  `reason` varchar(191) DEFAULT NULL COMMENT '消费原因',
  `message` varchar(191) NOT NULL DEFAULT '' COMMENT '备注',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `exchang_count` smallint(3) NOT NULL DEFAULT '0' COMMENT '兑换次数',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='用户会员卡消费记录表';



CREATE TABLE `b2c_card_examine` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `card_id` int(11) NOT NULL DEFAULT '0' COMMENT '会云卡id',
  `card_no` varchar(32) NOT NULL DEFAULT '0' COMMENT '会员卡no',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '审核状态 1审核中 2通过 3拒绝',
  `desc` varchar(512) DEFAULT NULL COMMENT '备注',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真是姓名',
  `cid` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `province_code` mediumint(10) DEFAULT NULL COMMENT '所在地',
  `city_code` mediumint(10) DEFAULT NULL COMMENT '所在地',
  `district_code` mediumint(10) DEFAULT NULL COMMENT '所在地',
  `sex` char(5) DEFAULT NULL COMMENT '性别',
  `birthday_year` int(4) DEFAULT NULL COMMENT '生日',
  `birthday_month` int(2) DEFAULT NULL COMMENT '生日',
  `birthday_day` int(2) DEFAULT NULL COMMENT '生日',
  `marital_status` tinyint(1) DEFAULT NULL COMMENT '婚姻状况',
  `education` tinyint(1) DEFAULT NULL COMMENT '教育程度',
  `industry_info` tinyint(1) DEFAULT NULL COMMENT '所在行业',
  `pass_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '通过时间',
  `refuse_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '拒绝时间',
  `refuse_desc` varchar(512) DEFAULT NULL COMMENT '拒绝理由',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `def_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='会员卡激活审核表';



CREATE TABLE `b2c_card_receive_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_id` int(11) NOT NULL DEFAULT '0' COMMENT '卡号id',
  `batch_id` int(11) NOT NULL COMMENT '批次id',
  `group_id` int(11) NOT NULL DEFAULT '1' COMMENT '分组id',
  `code` varchar(15) DEFAULT NULL COMMENT '领取码',
  `card_no` varchar(30) DEFAULT NULL COMMENT '卡号',
  `card_pwd` varchar(20) DEFAULT NULL COMMENT '卡密码',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '领取人',
  `receive_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '领取时间',
  `error_msg` varchar(200) DEFAULT NULL COMMENT '错误说明',
  `status` tinyint(1) DEFAULT '0' COMMENT '1: 可用 0：禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `card_id` (`card_id`),
  KEY `batch_id` (`batch_id`)
)COMMENT='会员卡领取码表';



CREATE TABLE `b2c_card_upgrade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `old_card_id` int(11) NOT NULL COMMENT '升级前卡id',
  `new_card_id` int(11) NOT NULL COMMENT '升级后卡id',
  `old_grade` varchar(20) NOT NULL COMMENT '升级前卡等级',
  `new_grade` varchar(20) NOT NULL COMMENT '升级后卡等级',
  `old_card_name` varchar(20) NOT NULL,
  `new_card_name` varchar(20) NOT NULL,
  `grade_condition` varchar(200) NOT NULL DEFAULT '' COMMENT '条件',
  `operate` varchar(200) NOT NULL DEFAULT '' COMMENT '操作备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='等级卡升级记录';



CREATE TABLE `b2c_cart` (
  `cart_id` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '门店id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `goods_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '商品sn',
  `goods_name` varchar(120) NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_specs` text COMMENT '例如,颜色:黑色',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格产品id',
  `prd_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '规格sn',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品价格',
  `is_checked` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否选中',
  `cart_number` smallint(5) NOT NULL DEFAULT '0' COMMENT '数量',
  `original_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '加入购物车时的价格',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型 0 普通 ',
  `extend_id` int(11) NOT NULL DEFAULT '0' COMMENT '扩展字段:对应type的类型 ',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`cart_id`),
  KEY `user_id` (`cart_id`,`store_id`)
)COMMENT='购物车 `b2c_cart` 孔德成';



CREATE TABLE `b2c_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '渠道页id',
  `page_id` int(11) NOT NULL COMMENT 'page_id',
  `goods_id` int(11) NOT NULL COMMENT 'goods_id',
  `channel_name` varchar(100) NOT NULL DEFAULT '' COMMENT '渠道页名称',
  `source_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '来源类型 0自定义 1商品',
  `share` varchar(191) NOT NULL DEFAULT '' COMMENT '分享码',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常，1废除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='渠道页面';



CREATE TABLE `b2c_channel_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `channel_id` int(11) NOT NULL COMMENT '渠道页id',
  `user_id` int(11) NOT NULL COMMENT 'userid',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '类型 1新用 0老用户',
  `count` int(11) NOT NULL COMMENT '访问次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='渠道统计';



CREATE TABLE `b2c_channel_statistical` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `page_id` int(11) NOT NULL DEFAULT '0',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `channel_id` text COMMENT '渠道id',
  `channel_all_pv` text,
  `channel_all_uv` text,
  `channel_new_pv` text,
  `channel_new_uv` text,
  `channel_old_pv` text,
  `channel_old_uv` text,
  `all_pv` text,
  `all_uv` text,
  `new_pv` text,
  `new_uv` text,
  `old_pv` text,
  `old_uv` text,
  `ref_date` date NOT NULL COMMENT '2019-03-04',
  `ref_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1昨天 7天 30天',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `ref_date` (`ref_date`)
)COMMENT='渠道表';



CREATE TABLE `b2c_charge_money` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `card_id` int(20) NOT NULL DEFAULT '0' COMMENT '会员卡id',
  `charge` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '充值的钱',
  `count` smallint(3) NOT NULL DEFAULT '0' COMMENT '充值次数',
  `payment` varchar(90) NOT NULL COMMENT '支付方式',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消费类型 0是普通卡 1限次卡',
  `reason_id` varchar(100) NOT NULL COMMENT '充值原因模板id',
  `reason` varchar(191) DEFAULT NULL COMMENT '充值原因',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `message` varchar(191) NOT NULL DEFAULT '' COMMENT '备注',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 0：待支付，1：已取消，2：已完成',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单应付金额',
  `charge_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0按规则 1自定义',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `ali_trade_no` varchar(60) NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
  `exchang_count` smallint(3) NOT NULL DEFAULT '0' COMMENT '兑换充值次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='用户充值记录表';



CREATE TABLE `b2c_code` (
  `code_id` mediumint(11) NOT NULL AUTO_INCREMENT COMMENT '二维码id',
  `type` smallint(2) NOT NULL DEFAULT '0' COMMENT '分类：1店铺，2商品，3服务，4会员卡，5优惠券',
  `param_id` varchar(64) NOT NULL DEFAULT '' COMMENT '记录唯一值，由url和对应参数产生',
  `type_url` varchar(100) NOT NULL DEFAULT '' COMMENT 'type对应的app页面地址',
  `qrcode_img` varchar(200) NOT NULL DEFAULT '' COMMENT '二维码位置',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标记位',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标记位',
  `channel` varchar(20) NOT NULL DEFAULT '0' COMMENT '渠道分享码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`code_id`)
)COMMENT='二维码存储表';



CREATE TABLE `b2c_comment_award` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL COMMENT '活动名称',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `is_forever` tinyint(1) DEFAULT NULL COMMENT '是否永久',
  `level` int(9) DEFAULT '1' COMMENT '优先级',
  `goods_type` tinyint(4) NOT NULL COMMENT '触发条件 1全部商品 2指定商品 3 实际品论比较少的商品',
  `goods_ids` varchar(199) DEFAULT NULL COMMENT '对应商品',
  `comment_num` int(8) DEFAULT NULL COMMENT '品论数',
  `comment_type` tinyint(4) NOT NULL COMMENT '评价类型 1评价即送 2 自定义',
  `comment_words` int(8) DEFAULT NULL COMMENT '评价字数条件',
  `has_pic_num` tinyint(4) DEFAULT NULL COMMENT '嗮图',
  `has_five_stars` tinyint(4) DEFAULT NULL COMMENT '五星好评',
  `award_type` int(2) NOT NULL COMMENT '奖品类型 1积分 2优惠卷 3 余额 4幸运大抽奖 5自定义',
  `score` int(11) NOT NULL COMMENT '积分数',
  `activity_id` varchar(200) DEFAULT NULL COMMENT ' 评价奖励活动id，逗号分隔  优惠卷或者抽奖',
  `account` decimal(10,2) DEFAULT '0.00' COMMENT '用户余额',
  `award_num` int(8) DEFAULT NULL COMMENT '奖品份数',
  `send_num` int(8) DEFAULT '0' COMMENT '奖品送出份数',
  `award_path` varchar(100) DEFAULT NULL COMMENT '设置链接',
  `award_img` varchar(100) DEFAULT NULL COMMENT '活动图片',
  `first_comment_goods` tinyint(1) DEFAULT NULL COMMENT '首次平价商品',
  `status` tinyint(2) DEFAULT '1' COMMENT '状态：1启用',
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='评价有礼活动';



CREATE TABLE `b2c_comment_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `commstar` tinyint(1) NOT NULL COMMENT '评价星级',
  `user_score` int(11) NOT NULL DEFAULT '0' COMMENT '评价可得积分',
  `anonymousflag` tinyint(1) NOT NULL COMMENT '匿名状态 0.未匿名；1.匿名',
  `commtag` varchar(100) DEFAULT '' COMMENT '评价标签',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `order_sn` varchar(20) NOT NULL COMMENT '订单编号',
  `comm_note` varchar(255) DEFAULT NULL COMMENT '评论内容',
  `comm_img` varchar(1000) DEFAULT NULL COMMENT '评论图片',
  `comment_award_id` int(11) DEFAULT NULL COMMENT '评价有礼活动id',
  `flag` tinyint(1) DEFAULT '0' COMMENT '0:未审批,1:审批通过,2:审批未通过',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '1:删除',
  `is_shop_add` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否商家增加：0不是，1是',
  `bogus_username` varchar(32) NOT NULL DEFAULT '' COMMENT '用户名称：商家添加时使用',
  `bogus_user_avatar` varchar(100) NOT NULL DEFAULT '' COMMENT '用户头像：商家添加时使用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `rec_id` int(9) DEFAULT '0' COMMENT 'order_goods的rec_id',
  `prd_id` int(9) DEFAULT '0' COMMENT '商品规格id',
  PRIMARY KEY (`id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='商品评价表';



CREATE TABLE `b2c_comment_goods_answer` (
  `answer_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL COMMENT '回复的商品评论id',
  `content` text COMMENT '回复内容',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`answer_id`),
  KEY `comment_id` (`comment_id`)
)COMMENT='商品评价回复表';



CREATE TABLE `b2c_comment_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` int(11) NOT NULL COMMENT '门店id',
  `technician_id` int(11) NOT NULL DEFAULT '0' COMMENT '技师id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `commstar` tinyint(1) NOT NULL COMMENT '评价星级',
  `user_score` int(11) DEFAULT '0' COMMENT '评价可得积分',
  `anonymousflag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '匿名状态 0.未匿名；1.匿名',
  `commtag` varchar(100) NOT NULL DEFAULT '' COMMENT '评价标签',
  `service_id` int(11) NOT NULL COMMENT '服务id',
  `order_sn` varchar(20) NOT NULL COMMENT '订单编号',
  `comm_note` varchar(255) NOT NULL COMMENT '评论内容',
  `comm_img` varchar(1000) DEFAULT '[]' COMMENT '评论图片',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未审批,1:审批通过,2:审批未通过',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`),
  KEY `service_id` (`service_id`)
)COMMENT='服务评价表';



CREATE TABLE `b2c_coopen_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '针对用户群体： 1: 初次访问新用户 2: 全部用户 3:未支付的用户',
  `name` varchar(50) NOT NULL COMMENT '活动名称',
  `title` varchar(100) NOT NULL COMMENT '宣传语',
  `is_forever` int(11) NOT NULL DEFAULT '0' COMMENT '是否永久有效 0:无效 1:有效',
  `bg_imgs` varchar(255) NOT NULL DEFAULT '[]' COMMENT '背景图',
  `start_date` datetime NOT NULL COMMENT '有效期-起始',
  `end_date` datetime NOT NULL COMMENT '有效期-结束',
  `first` int(11) NOT NULL DEFAULT '1' COMMENT '优先级',
  `activity_action` tinyint(1) DEFAULT '1' COMMENT '活动类型：0无奖励 1：普通优惠卷 2：分裂优惠卷 3：幸运大抽奖 4: 余额 5:奖品  6:积分 7：自定义',
  `mrking_voucher_id` varchar(500) NOT NULL COMMENT '活动优惠券，逗号分隔',
  `lottery_id` int(11) NOT NULL DEFAULT '0' COMMENT '抽奖活动id',
  `customize_img_path` varchar(191) NOT NULL DEFAULT '' COMMENT '活动有礼跳转活动图片路径',
  `customize_url` varchar(191) NOT NULL DEFAULT '' COMMENT '活动有礼跳转活动链接',
  `give_score` decimal(10,2) DEFAULT '0.00' COMMENT '积分',
  `give_account` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `award_num` int(11) DEFAULT '-1' COMMENT '发放数量',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1: 正常 0: 关闭',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0',
  `del_time` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='开屏有礼活动';



CREATE TABLE `b2c_coopen_activity_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '活动id',
  `user_id` int(11) NOT NULL,
  `activity_action` tinyint(1) DEFAULT '1' COMMENT '活动类型：1：活动送券 2：大转盘抽奖 3：跳转自定义链接 4: 积分 5:余额  6:分裂',
  `comment` varchar(200) NOT NULL DEFAULT '' COMMENT '说明',
  `receive_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '领取时间',
  `mrking_voucher_id` varchar(500) DEFAULT NULL COMMENT '已领取的优惠券',
  `lottery_id` int(11) DEFAULT NULL COMMENT '抽奖id',
  `give_num` decimal(10,2) DEFAULT '0.00' COMMENT '积分或者余额数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='开屏有礼活动记录';



CREATE TABLE `b2c_coupon_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_action` tinyint(1) DEFAULT '1' COMMENT '活动类型：1：活动送券 2：大转盘抽奖 3：跳转自定义链接',
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '针对用户群体： 1: 新用户 2: 全部用户',
  `name` varchar(50) NOT NULL COMMENT '活动名称',
  `title` varchar(100) NOT NULL COMMENT '宣传语',
  `bg_action` tinyint(4) NOT NULL DEFAULT '1' COMMENT '背景图',
  `start_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '有效期-起始',
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '有效期-结束',
  `mrking_voucher_id` varchar(500) NOT NULL COMMENT '活动优惠券，逗号分隔',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1: 正常 0: 关闭',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `customize_img_path` varchar(191) NOT NULL DEFAULT '' COMMENT '活动有礼跳转活动图片路径',
  `customize_url` varchar(191) NOT NULL DEFAULT '' COMMENT '活动有礼跳转活动链接',
  PRIMARY KEY (`id`)
)COMMENT='优惠券活动表';



CREATE TABLE `b2c_coupon_activity_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '活动id',
  `user_id` int(11) NOT NULL,
  `receive_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '领取时间',
  `mrking_voucher_id` varchar(500) DEFAULT NULL COMMENT '已领取的优惠券',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='活动送券记录表';



CREATE TABLE `b2c_coupon_pack` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `act_name` varchar(100) NOT NULL COMMENT '活动名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `pack_name` varchar(20) NOT NULL COMMENT '礼包名称',
  `limit_get_times` int(11) NOT NULL DEFAULT '0' COMMENT '单用户领取限制次数，0不限制',
  `total_amount` int(11) NOT NULL DEFAULT '0' COMMENT '总数量',
  `issued_amount` int(11) NOT NULL DEFAULT '0' COMMENT '已发放数量',
  `access_mode` tinyint(1) NOT NULL DEFAULT '0' COMMENT '获取方式，0：现金购买，1：积分购买，2直接领取',
  `access_cost` decimal(10,2) DEFAULT '0.00' COMMENT '价格（现金或积分，直接领取时该值为0）',
  `act_rule` text COMMENT '活动规则',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '开启状态1:开启，0:停用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='优惠券礼包';



CREATE TABLE `b2c_coupon_pack_voucher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `voucher_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券id',
  `act_id` int(11) NOT NULL DEFAULT '0' COMMENT '所属优惠券礼包id',
  `total_amount` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '总数量',
  `immediately_grant_amount` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '立即发放数量',
  `timing_every` int(11) unsigned DEFAULT '0' COMMENT '每个时间单位间隔（1为无间隔）',
  `timing_unit` tinyint(1) DEFAULT '0' COMMENT '定时发放的时间单位，0：自然天，1：自然周，2自然月',
  `timing_time` int(11) DEFAULT '0' COMMENT '定时发放的时间,周1-7，月1-31，自然天不填',
  `timing_amount` int(11) unsigned DEFAULT '0' COMMENT '定时发放的数量',
  `del_flag` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `voucher_id` (`voucher_id`),
  KEY `act_id` (`act_id`)
);



CREATE TABLE `b2c_coupon_payreward_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '活动id',
  `user_id` int(11) NOT NULL,
  `receive_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '领取时间',
  `mrking_voucher_id` varchar(500) DEFAULT NULL COMMENT '已领取的优惠券',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='支付送券记录表';



CREATE TABLE `b2c_customer_avail_coupons` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `coupon_sn` varchar(30) NOT NULL DEFAULT '',
  `user_id` mediumint(8) NOT NULL DEFAULT '0',
  `act_type` mediumint(5) NOT NULL DEFAULT '0' COMMENT 'user_id不为空时1:经销商等级打折,为空时1:首次下单优惠，2减价，3打折',
  `act_id` mediumint(8) NOT NULL DEFAULT '0',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0为减价，1为打折',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '打折或减价量',
  `act_desc` varchar(128) NOT NULL DEFAULT '',
  `limit_order_amount` mediumint(8) NOT NULL DEFAULT '0',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 未使用 1 已使用 ',
  `used_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `access_mode` tinyint(1) NOT NULL DEFAULT '0' COMMENT '获取方式，0：发放，1：领取',
  `access_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '发放活动id',
  `notify_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '通知时间',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '优惠订单编号',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除,1：删除',
  `get_source` tinyint(2) NOT NULL DEFAULT '0' COMMENT '//1表单送券2支付送券3活动送券4积分兑换5直接领取6分裂优惠券7crm领券8幸运大抽奖9定向发券',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `coupon_sn` (`coupon_sn`),
  KEY `user_id` (`user_id`)
)COMMENT='发放优惠券 用户持有的优惠券';



CREATE TABLE `b2c_decorate_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL,
  `link_action` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1: 网页跳转  2： 小程序跳转',
  `title` varchar(50) DEFAULT NULL COMMENT '小程序名称',
  `path_name` varchar(255) DEFAULT NULL COMMENT '链接名称',
  `link_path` varchar(255) NOT NULL COMMENT '跳转链接',
  `appid` varchar(100) DEFAULT NULL COMMENT '小程序appid',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
  `del_time` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `shop_decorate_link` (`shop_id`,`link_action`,`del_flag`)
)COMMENT='页面装修 > 网页/小程序跳转表';



CREATE TABLE `b2c_deliver_fee_template` (
  `deliver_template_id` int(11) NOT NULL AUTO_INCREMENT,
  `template_name` varchar(191) NOT NULL DEFAULT '' COMMENT '模板名称',
  `template_content` text COMMENT '模板内容，json存储',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0运费模板,1重量运费模板',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`deliver_template_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='运费模板表';



CREATE TABLE `b2c_distribution_order` (
  `ref_date` char(7) DEFAULT NULL COMMENT '2018-07',
  `province` varchar(20) DEFAULT NULL COMMENT '省',
  `city` varchar(20) DEFAULT NULL COMMENT '市',
  `district` varchar(20) DEFAULT NULL COMMENT '区',
  `province_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '省份编号',
  `city_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '城市编号',
  `district_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '区县编号',
  `pay_order_money` decimal(10,2) DEFAULT NULL COMMENT '付款金额',
  `pay_user_num` int(11) DEFAULT NULL COMMENT '付款人数',
  `uv` int(11) DEFAULT NULL COMMENT '访客数',
  `uv_pay_ratio` decimal(4,2) DEFAULT NULL COMMENT '转化率',
  `order_num` int(11) DEFAULT NULL COMMENT '订单数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `ref_date` (`ref_date`),
  KEY `pay_order_money` (`pay_order_money`)
)COMMENT='交易订单地区分布';



CREATE TABLE `b2c_distribution_strategy` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `strategy_name` varchar(120) NOT NULL COMMENT '策略名称',
  `strategy_level` tinyint(3) NOT NULL COMMENT '策略等级',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `self_purchase` tinyint(1) NOT NULL DEFAULT '0' COMMENT '自购返利',
  `cost_protection` tinyint(1) NOT NULL DEFAULT '0' COMMENT '成本保护',
  `fanli_ratio` float NOT NULL DEFAULT '0' COMMENT '返利比例（%的系数）',
  `rebate_ratio` float DEFAULT '0' COMMENT '间接',
  `fanli_ratio_2` float NOT NULL DEFAULT '0' COMMENT '二级返利比例（%的系数）',
  `rebate_ratio_2` float DEFAULT '0' COMMENT '间接',
  `fanli_ratio_3` float NOT NULL DEFAULT '0' COMMENT '三级返利比例（%的系数）',
  `rebate_ratio_3` float DEFAULT '0' COMMENT '间接',
  `fanli_ratio_4` float NOT NULL DEFAULT '0' COMMENT '四级返利比例（%的系数）',
  `rebate_ratio_4` float DEFAULT '0' COMMENT '间接',
  `fanli_ratio_5` float NOT NULL DEFAULT '0' COMMENT '五级返利比例（%的系数）',
  `rebate_ratio_5` float DEFAULT '0' COMMENT '间接',
  `recommend_type` tinyint(4) DEFAULT NULL COMMENT '0:全部商品1:部分商品',
  `recommend_goods_id` text COMMENT '返利商品ids',
  `recommend_cat_id` text COMMENT '返利分类ids',
  `status` tinyint(2) DEFAULT '0' COMMENT '1停用',
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '1删除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `recommend_sort_id` varchar(300) DEFAULT NULL COMMENT '返利商家分类ids',
  `send_coupon` tinyint(1) DEFAULT '0' COMMENT '赠送优惠券',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `first_rebate` tinyint(1) NOT NULL DEFAULT '0' COMMENT '邀请新用户下首单返利',
  `first_ratio` float DEFAULT '0' COMMENT '首单返利金额',
  `first_ratio_2` float DEFAULT '0' COMMENT '首单返利金额',
  `first_ratio_3` float DEFAULT '0' COMMENT '首单返利金额',
  `first_ratio_4` float DEFAULT '0' COMMENT '首单返利金额',
  `first_ratio_5` float DEFAULT '0' COMMENT '首单返利金额',
  PRIMARY KEY (`id`)
)COMMENT='返利策略';



CREATE TABLE `b2c_distribution_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '日期',
  `type` tinyint(1) NOT NULL COMMENT '1,7,30',
  `tag_id` int(11) NOT NULL COMMENT '标签id',
  `tag_name` varchar(50) NOT NULL COMMENT '标签内容',
  `pay_order_num` int(11) DEFAULT '0' COMMENT '付款订单数',
  `pay_order_money` decimal(10,2) DEFAULT '0.00' COMMENT '付款金额',
  `pay_user_num` int(11) DEFAULT '0' COMMENT '付款人数',
  `pay_goods_number` int(11) DEFAULT '0' COMMENT '付款商品件数',
  `has_mobile_num` int(11) DEFAULT '0' COMMENT '下单有手机号的用户',
  `has_user_num` int(11) DEFAULT '0' COMMENT '用户数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_type_tag` (`ref_date`,`type`,`tag_id`)
)COMMENT='标签用户下单统计';



CREATE TABLE `b2c_distribution_withdraw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `type` tinyint(1) DEFAULT NULL COMMENT '提现类型  1微信公众号钱包提现 2小程序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '处理状态 1待审核 2拒绝 3已审核待出账 4出账成功 5失败',
  `order_sn` varchar(20) NOT NULL COMMENT '提现单号',
  `withdraw_user_num` varchar(20) NOT NULL COMMENT '用户提现序号',
  `withdraw_num` varchar(20) NOT NULL COMMENT '流水号',
  `withdraw_cash` decimal(10,2) NOT NULL COMMENT '提现金额',
  `withdraw` decimal(10,2) NOT NULL COMMENT '可提现金额',
  `desc` text COMMENT '备注',
  `refuse_desc` text COMMENT '驳回原因',
  `check_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '审核时间',
  `refuse_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '驳回时间',
  `billing_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '出账时间',
  `fail_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '失败时间',
  `desc_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '备注时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `withdraw_source` varchar(20) NOT NULL DEFAULT '' COMMENT '申请时提现配置',
  `real_name` varchar(32) DEFAULT NULL COMMENT '真实姓名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`)
)COMMENT='分销提现记录';



CREATE TABLE `b2c_distributor_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `status` tinyint(2) NOT NULL DEFAULT '0',
  `msg` text COMMENT '审核内容',
  `del_flag` tinyint(2) NOT NULL DEFAULT '0',
  `activation_fields` varchar(1000) DEFAULT NULL COMMENT '审核校验',
  `config_fields` varchar(500) DEFAULT NULL COMMENT '审核字段',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `is_auto_pass` tinyint(2) NOT NULL DEFAULT '0' COMMENT '审核类型 0：手动审核；1"自动审核',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='分销原申请记录';



CREATE TABLE `b2c_distributor_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分组id',
  `group_name` varchar(32) NOT NULL COMMENT '分组名字',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否为默认',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='分销员分组表';



CREATE TABLE `b2c_distributor_level` (
  `id` int(6) NOT NULL AUTO_INCREMENT,
  `level_id` tinyint(2) NOT NULL DEFAULT '0' COMMENT '等级',
  `level_name` varchar(32) DEFAULT NULL COMMENT '等级名称',
  `level_up_route` tinyint(1) NOT NULL DEFAULT '0' COMMENT '升级类型：0自动，1手动',
  `invite_number` int(10) DEFAULT NULL COMMENT '邀请人数量（uo_route=0有效）',
  `total_distribution_money` decimal(10,2) DEFAULT NULL COMMENT '推广金额（uo_route=0有效）',
  `total_buy_money` decimal(10,2) DEFAULT NULL COMMENT '推广和消费总额（uo_route=0有效）',
  `level_user_ids` text COMMENT '等级用户id（uo_route=1有效）',
  `level_status` tinyint(2) DEFAULT '0' COMMENT '状态:0停用，1启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `level_id` (`level_id`)
)COMMENT='分销员等级表';



CREATE TABLE `b2c_distributor_level_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `is_go_up` tinyint(1) NOT NULL DEFAULT '0' COMMENT '升降：0降，1升',
  `old_level` tinyint(2) NOT NULL DEFAULT '1' COMMENT '旧等级',
  `old_level_name` varchar(32) DEFAULT NULL COMMENT '旧等级名字',
  `new_level` tinyint(2) NOT NULL DEFAULT '1' COMMENT '新等级',
  `new_level_name` varchar(32) DEFAULT NULL COMMENT '新等级名字',
  `update_note` varchar(120) DEFAULT NULL COMMENT '更新备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='分销员等级变更表';



CREATE TABLE `b2c_fanli_goods_statistics` (
  `prd_id` int(11) NOT NULL,
  `prd_sn` varchar(30) DEFAULT NULL COMMENT '规格编码',
  `goods_id` int(11) DEFAULT NULL,
  `cat_id` int(11) DEFAULT NULL COMMENT '分类id',
  `sale_number` int(11) DEFAULT NULL COMMENT '销量',
  `prd_total_fanli` decimal(10,2) DEFAULT '0.00' COMMENT '商品返利总金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)COMMENT='商品返利统计';



CREATE TABLE `b2c_first_special` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '活动名称',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束日期',
  `batch_discount` tinyint(1) DEFAULT '0' COMMENT '批量打几折',
  `batch_reduce` decimal(10,2) DEFAULT NULL COMMENT '批量减多少',
  `batch_final_price` decimal(10,2) DEFAULT NULL COMMENT '批量折后价',
  `is_batch_integer` tinyint(1) DEFAULT '0' COMMENT '是否批量取整',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1：启用 0：禁用',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL,
  `limit_amount` int(11) DEFAULT '0',
  `first` tinyint(1) DEFAULT '1' COMMENT '优先级',
  `share_config` text COMMENT '分享设置',
  `is_forever` tinyint(1) DEFAULT '0' COMMENT '是否永久',
  `limit_flag` tinyint(1) DEFAULT '0' COMMENT '超限购购买标记',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='首单特惠定义表';



CREATE TABLE `b2c_first_special_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_special_id` int(11) NOT NULL COMMENT '限时减价活动ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `discount` decimal(10,2) DEFAULT NULL COMMENT '打几折',
  `reduce_price` decimal(10,2) DEFAULT NULL COMMENT '减多少钱',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '折后价格',
  PRIMARY KEY (`id`)
)COMMENT='首单特惠商品';



CREATE TABLE `b2c_first_special_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_special_id` int(11) NOT NULL COMMENT '限时减价活动ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `prd_id` int(11) NOT NULL COMMENT '规格id',
  `prd_price` decimal(10,2) DEFAULT NULL COMMENT '折后价格',
  PRIMARY KEY (`id`)
)COMMENT='首单特惠商品规格';



CREATE TABLE `b2c_footprint_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `user_id` int(11) NOT NULL,
  `count` int(11) DEFAULT '1' COMMENT '浏览次数',
  `type` tinyint(2) DEFAULT '0' COMMENT '0 老用户 1新用户',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='用户足迹';



CREATE TABLE `b2c_form_page` (
  `page_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '表单页id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `page_name` varchar(60) NOT NULL DEFAULT '',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0未发布，1已发布 2已关闭 3 已删除',
  `page_content` longtext COMMENT '页面内容，json格式存储',
  `form_cfg` longtext COMMENT '表单配置，json格式存储',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `is_forever_valid` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1永久有效，0期限内有效',
  `submit_num` int(11) NOT NULL DEFAULT '0' COMMENT '反馈数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`page_id`)
)COMMENT='表单页面';



CREATE TABLE `b2c_form_submit_details` (
  `rec_id` int(10) NOT NULL AUTO_INCREMENT,
  `page_id` int(10) NOT NULL,
  `submit_id` int(10) NOT NULL COMMENT '表单提交id，对应b2c_form_submit_list的submit_id',
  `user_id` int(10) NOT NULL,
  `module_name` varchar(255) DEFAULT NULL COMMENT '模块名称',
  `module_type` varchar(255) DEFAULT NULL COMMENT '模块类型',
  `module_value` text COMMENT '模块的值',
  `cur_idx` varchar(32) NOT NULL COMMENT '装修模块保存id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`rec_id`),
  KEY `submit_id` (`submit_id`),
  KEY `page_id` (`page_id`),
  KEY `user_id` (`user_id`),
  KEY `module_type` (`module_type`(191)),
  KEY `module_name` (`module_name`(191))
)COMMENT='表单提交详情';



CREATE TABLE `b2c_form_submit_list` (
  `submit_id` int(10) NOT NULL AUTO_INCREMENT,
  `page_id` int(10) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `open_id` varchar(255) DEFAULT NULL COMMENT '微信openid',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '微信昵称',
  `send_score` int(6) DEFAULT NULL COMMENT '送积分',
  `send_coupons` varchar(200) DEFAULT NULL COMMENT '送优惠券',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`submit_id`),
  KEY `page_id` (`page_id`),
  KEY `user_id` (`user_id`)
)COMMENT='表单提交列表';



CREATE TABLE `b2c_free_shipping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `expire_type` tinyint(1) DEFAULT '0' COMMENT '0:固定日期 1：永久有效',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `type` tinyint(1) NOT NULL COMMENT '条件 1全部 0部分',
  `recommend_goods_id` text COMMENT '指定商品可用',
  `recommend_cat_id` text COMMENT '指定分类可用',
  `recommend_sort_id` text COMMENT '指定商家分类可用',
  `status` tinyint(1) DEFAULT '0' COMMENT '1停用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '1删除',
  `del_time` datetime DEFAULT NULL,
  `level` tinyint(2) DEFAULT '0' COMMENT '优先级 默认0',
  PRIMARY KEY (`id`)
)COMMENT='满包邮详情';



CREATE TABLE `b2c_free_shipping_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shipping_id` int(11) NOT NULL COMMENT '包邮活动ID',
  `con_type` int(11) NOT NULL COMMENT '包邮条件 0满金额 1满件数',
  `money` decimal(10,2) NOT NULL COMMENT '满金额',
  `num` int(11) NOT NULL COMMENT '满件数',
  `area` text COMMENT '包邮地区',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `area_list` varchar(255) DEFAULT NULL,
  `area_text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `shipping_id` (`shipping_id`)
)COMMENT='满包邮规则';



CREATE TABLE `b2c_friend_promote_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `act_code` varchar(32) NOT NULL DEFAULT '' COMMENT '活动编码',
  `act_name` varchar(120) NOT NULL DEFAULT '' COMMENT '活动名称',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动起始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动截止时间',
  `reward_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '奖励类型：0赠送商品，1折扣商品，2赠送优惠券',
  `reward_content` text COMMENT '奖励内容',
  `reward_duration` int(8) NOT NULL DEFAULT '0' COMMENT '奖励有效期',
  `reward_duration_unit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '奖励有效期单位：0小时，1天，2周，3月，4年',
  `promote_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '所需助力值',
  `promote_times` int(5) NOT NULL DEFAULT '1' COMMENT '所需助力次数',
  `launch_limit_duration` int(5) NOT NULL DEFAULT '0' COMMENT '发起次数限制时长，0不限制',
  `launch_limit_unit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发起次数限制时长单位：0天，1周，2月，3年',
  `launch_limit_times` tinyint(3) NOT NULL DEFAULT '0' COMMENT '发起限制次数，0不限制',
  `share_create_times` tinyint(3) NOT NULL DEFAULT '1' COMMENT '好友分享可获得助力次数',
  `promote_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '单次助力值类型：0平均，1随机',
  `promote_condition` tinyint(1) NOT NULL DEFAULT '0' COMMENT '好友助力条件：0可不授权个人信息，1必须授权',
  `failed_send_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '助力失败赠送类型：0不赠送，1优惠券，2积分',
  `failed_send_content` int(8) NOT NULL DEFAULT '0' COMMENT '助力失败赠送内容',
  `activity_share_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '助力活动分享样式类型：0默认样式，1自定义样式',
  `custom_share_word` varchar(400) NOT NULL DEFAULT '' COMMENT '自定义分享样式文案',
  `share_img_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '自定义分享图片类型：0首页截图，1自定义图片',
  `custom_img_path` varchar(100) NOT NULL DEFAULT '' COMMENT '自定义分享样式图片路径',
  `is_block` tinyint(1) NOT NULL DEFAULT '0' COMMENT '活动状态：0未停用，1已停用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识：0未删除，1已删除',
  `use_discount` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否可与会员卡折扣、优惠券叠加使用：0不可叠加，1可叠加',
  `use_score` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可使用积分抵扣部分金额：0不可抵扣，1可抵扣',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `act_code` (`act_code`),
  KEY `act_name` (`act_name`),
  KEY `shop_id` (`shop_id`)
)COMMENT='好友助力活动表';



CREATE TABLE `b2c_friend_promote_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `launch_id` int(11) DEFAULT '0' COMMENT '助力活动发起id',
  `user_id` int(11) DEFAULT '0' COMMENT '助力会员id',
  `promote_id` int(11) DEFAULT '0' COMMENT '助力活动id',
  `promote_value` int(11) DEFAULT '0' COMMENT '助力值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '助力时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `launch_id` (`launch_id`),
  KEY `user_id` (`user_id`),
  KEY `promote_id` (`promote_id`)
)COMMENT='好友助力明细表';



CREATE TABLE `b2c_friend_promote_launch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '发起会员id',
  `promote_id` int(11) NOT NULL DEFAULT '0' COMMENT '助力活动id',
  `promote_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '助力状态：0助力中，1助力完成待领取，2助力完成已领取，3助力失效，4助力未完成失败',
  `order_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '助力完成生产的订单编码',
  `launch_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发起时间',
  `success_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '助力成功时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识：0未删除，1已删除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `order_sn` (`order_sn`),
  KEY `user_id` (`user_id`),
  KEY `promote_id` (`promote_id`)
)COMMENT='好友助力发起表';



CREATE TABLE `b2c_friend_promote_times` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `launch_id` int(11) NOT NULL DEFAULT '0' COMMENT '助力活动发起id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '助力会员id',
  `share_times` int(8) NOT NULL DEFAULT '0' COMMENT '分享的次数',
  `own_promote_times` int(8) NOT NULL DEFAULT '0' COMMENT '总的所有助力次数',
  `is_auth` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有授权增加次数：0没有，1有',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '助力时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `launch_id` (`launch_id`),
  KEY `user_id` (`user_id`)
)COMMENT='可助力次数表';



CREATE TABLE `b2c_gift` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `level` smallint(4) NOT NULL DEFAULT '0' COMMENT '优先级',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `goods_id` text COMMENT '活动商品',
  `rule` text COMMENT '赠品策略',
  `explain` text COMMENT '说明',
  `status` tinyint(1) DEFAULT '1',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `level` (`level`)
)COMMENT='赠品活动';



CREATE TABLE `b2c_gift_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gift_id` int(11) NOT NULL COMMENT '赠品活动id',
  `product_id` int(11) NOT NULL COMMENT '规格id',
  `product_number` int(11) NOT NULL COMMENT '库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `gift_id` (`gift_id`),
  KEY `product_id` (`product_id`)
)COMMENT='赠品规格商品';



CREATE TABLE `b2c_give_gift_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `act_name` varchar(120) DEFAULT NULL COMMENT '活动时间',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '活动开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '活动结束时间',
  `level` smallint(8) DEFAULT '0' COMMENT '优先级',
  `due_time_type` tinyint(1) DEFAULT '0' COMMENT '是否永久有效 0 否 1是',
  `act_type_first_served` tinyint(1) DEFAULT '0' COMMENT '活动玩法：先到先得 1开启',
  `act_type_timing_open` tinyint(1) DEFAULT '0' COMMENT ' 活动玩法：定时开奖 1开启',
  `act_type_direct_giving` tinyint(1) DEFAULT '0' COMMENT '活动玩法：直接送礼 1开启',
  `recommend_goods_id` varchar(200) DEFAULT NULL COMMENT '指定商品可用',
  `status` tinyint(1) DEFAULT NULL COMMENT '活动状态：1启用',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识：0未删除，1已删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '跟新时间',
  PRIMARY KEY (`id`)
)COMMENT='送礼_活动主表';



CREATE TABLE `b2c_give_gift_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `give_gift_id` int(10) DEFAULT NULL COMMENT '礼单ID',
  `user_id` int(10) DEFAULT NULL COMMENT '用户id',
  `product_id` varchar(255) DEFAULT NULL,
  `goods_number` varchar(255) DEFAULT NULL COMMENT '已选商品数',
  `gift_type` tinyint(2) DEFAULT NULL COMMENT '赠送方式：1:直接送礼 2:先到先得 3:定时开奖',
  `draw_time` timestamp NULL DEFAULT NULL COMMENT '开奖时间',
  `message` varchar(200) DEFAULT NULL COMMENT '祝福消息',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 1启用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '跟新时间',
  PRIMARY KEY (`id`)
)COMMENT='送礼_礼物车';



CREATE TABLE `b2c_give_gift_receive` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `main_order_sn` varchar(50) NOT NULL COMMENT '主单号',
  `gift_id` int(11) NOT NULL COMMENT '送礼ID',
  `gift_cart_id` int(11) NOT NULL COMMENT '送礼购物车ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `order_sn` varchar(50) DEFAULT NULL COMMENT '订单号',
  `product_id` int(11) NOT NULL COMMENT '规格ID',
  `address_id` int(11) DEFAULT NULL COMMENT '用户地址ID',
  `status` tinyint(1) DEFAULT '0' COMMENT '0：未提交地址 1：已送礼 2：待开奖 3：未抢到',
  `status_name` varchar(50) DEFAULT NULL COMMENT '状态名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `gift_id` (`gift_id`),
  KEY `gift_cart_id` (`gift_cart_id`),
  KEY `user_id` (`user_id`),
  KEY `order_sn` (`order_sn`),
  KEY `main_order_sn` (`main_order_sn`)
)COMMENT='送礼_记录表';



CREATE TABLE `b2c_give_voucher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `act_name` varchar(50) NOT NULL COMMENT '活动名称',
  `number` int(6) DEFAULT NULL COMMENT '参与人数',
  `have_pay` int(4) DEFAULT NULL COMMENT '有交易记录',
  `no_pay` int(4) DEFAULT NULL COMMENT '无交易记录',
  `max_count` int(20) DEFAULT NULL COMMENT '购买次数大于',
  `min_count` int(20) DEFAULT NULL COMMENT '购买次数小于',
  `card_id` text COMMENT '会员卡',
  `tag_id` text COMMENT '标签',
  `act_id` int(11) DEFAULT NULL COMMENT '优惠券',
  `max_ave_price` decimal(10,2) DEFAULT NULL COMMENT '均价大于',
  `min_ave_price` decimal(10,2) DEFAULT NULL COMMENT '均价小于',
  `user` text COMMENT '手动添加会员',
  `send_condition` text COMMENT '筛选发放人条件',
  `send_status` tinyint(1) DEFAULT '0' COMMENT '发放状态:0未发放，1已发放',
  `send_action` tinyint(1) DEFAULT '1' COMMENT '发放类型:0立即发放，1定时发放',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发放开始时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='发券记录';



CREATE TABLE `b2c_goods` (
  `goods_id` int(8) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `cat_id` int(5) NOT NULL DEFAULT '0',
  `goods_sn` varchar(60) NOT NULL DEFAULT '',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `brand_id` int(11) NOT NULL DEFAULT '0' COMMENT '品牌id',
  `goods_ad` varchar(1024) NOT NULL DEFAULT '' COMMENT '广告词',
  `goods_number` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `goods_weight` decimal(10,3) NOT NULL DEFAULT '0.000',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `shop_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `cost_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '成本价',
  `goods_desc` text,
  `goods_img` varchar(500) NOT NULL DEFAULT '',
  `is_on_sale` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在售，1在售，0下架',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `goods_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品',
  `deliver_template_id` int(5) NOT NULL DEFAULT '0' COMMENT '运费模板id',
  `goods_sale_num` int(8) NOT NULL DEFAULT '0' COMMENT '销售数量',
  `goods_collect_num` int(8) NOT NULL DEFAULT '0' COMMENT '收藏数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '审核状态,0待审核 1 审核通过 2 违规下架',
  `reason` text COMMENT '违规下架原因',
  `sub_account_id` int(11) NOT NULL DEFAULT '0' COMMENT '子帐号id，主要用于官方店铺',
  `sale_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `limit_buy_num` int(11) NOT NULL DEFAULT '0' COMMENT '最少起购数量，0不限购',
  `unit` varchar(60) NOT NULL DEFAULT '' COMMENT '商品单位',
  `limit_max_num` int(11) NOT NULL DEFAULT '0' COMMENT '最多起购数量，0不限购',
  `sale_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '上架状态,0立即上架， 1审核通过 2 加入仓库',
  `sort_id` int(11) NOT NULL DEFAULT '0',
  `goods_video` varchar(191) NOT NULL DEFAULT '' COMMENT '视频',
  `goods_video_img` varchar(191) NOT NULL DEFAULT '' COMMENT '视频首图',
  `goods_video_size` int(11) NOT NULL DEFAULT '0' COMMENT '视频尺寸',
  `goods_video_id` int(11) NOT NULL DEFAULT '0' COMMENT '视频id',
  `goods_page_id` int(11) NOT NULL DEFAULT '0' COMMENT '详情页装修模板id',
  `is_page_up` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在文本区域上方',
  `is_card_exclusive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否会员卡专属',
  `base_sale` int(8) NOT NULL DEFAULT '0' COMMENT '初始销量',
  `source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商品来源,0：店铺自带；1、2..等：不同类型店铺第三方抓取自带商品来源',
  `is_control_price` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否控价：0不控价，1控价（不可修改价格）',
  `can_rebate` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否分销改价',
  `promotion_language_switch` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否使用分销推广语0关闭，1使用',
  `promotion_language` varchar(400) NOT NULL DEFAULT '' COMMENT '推广语',
  `deliver_place` varchar(191) DEFAULT NULL COMMENT '发货地址',
  `share_config` varchar(500) DEFAULT NULL COMMENT '分享配置',
  `is_default_product` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1默认规格，0自定义规格（多规格）',
  UNIQUE KEY `goods_id` (`goods_id`,`shop_id`),
  UNIQUE KEY `goods_sn` (`goods_sn`,`shop_id`),
  KEY `goods_id_2` (`goods_id`),
  KEY `shop_id` (`shop_id`),
  KEY `cat_id` (`cat_id`)
)COMMENT='商品表 `b2c_goods`';



CREATE TABLE `b2c_goods_bak` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `bak_date` date DEFAULT NULL COMMENT '备份日期：例2018-09-05',
  `goods_id` int(8) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `cat_id` int(5) NOT NULL DEFAULT '0',
  `sort_id` int(11) NOT NULL DEFAULT '0',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `shop_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_number` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `is_on_sale` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在售，1在售，0下架',
  `goods_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `brand_id` int(11) NOT NULL DEFAULT '0' COMMENT '品牌id',
  PRIMARY KEY (`id`)
)COMMENT='goods备份';



CREATE TABLE `b2c_goods_brand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(500) NOT NULL COMMENT '品牌名称',
  `e_name` varchar(500) NOT NULL DEFAULT '' COMMENT '品牌英文名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌logo',
  `first` tinyint(3) NOT NULL DEFAULT '0' COMMENT '优先级',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0为未删除 1为删除',
  `desc` text COMMENT '品牌介绍',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为推荐品牌 0否 1是',
  `classify_id` int(11) NOT NULL DEFAULT '0' COMMENT '品牌所属分类 0未分类 否则是分类id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='商品品牌';



CREATE TABLE `b2c_goods_card_couple` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '会员卡专属商品关联id',
  `card_id` varchar(100) NOT NULL COMMENT '会员卡id',
  `gcta_id` int(11) DEFAULT '0' COMMENT '商品或类型id',
  `type` tinyint(1) DEFAULT '0' COMMENT '标签关联类型： 1：关联商品 2：关联商家分类 3：关联平台分类  4: 关联商品品牌',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='会员卡专享商品关联表';



CREATE TABLE `b2c_goods_img` (
  `img_id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `img_url` varchar(500) NOT NULL DEFAULT '',
  `img_desc` varchar(500) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`img_id`),
  KEY `goods_id` (`goods_id`)
)COMMENT='商品图片表 `b2c_goods_img`';



CREATE TABLE `b2c_goods_import` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `total_num` int(11) DEFAULT '0' COMMENT '导入总数',
  `success_num` int(11) DEFAULT '0' COMMENT '导入成功数',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `import_file_path` varchar(120) NOT NULL COMMENT '导入源文件地址',
  `is_update` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否更新：0新增，1更新',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='商品导入主表';



CREATE TABLE `b2c_goods_import_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_id` int(11) NOT NULL COMMENT '主表id',
  `goods_sn` varchar(32) DEFAULT NULL COMMENT '商品sn',
  `prd_sn` varchar(32) DEFAULT NULL COMMENT '规格sn',
  `goods_name` varchar(120) DEFAULT NULL COMMENT '商品名称',
  `prd_desc` varchar(120) DEFAULT NULL COMMENT '规格描述',
  `error_msg` varchar(100) DEFAULT NULL COMMENT '错误内容',
  `is_success` tinyint(1) NOT NULL DEFAULT '0' COMMENT '导入成功标识：0不成功，1成功',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `goods_sn` (`goods_sn`),
  KEY `prd_sn` (`prd_sn`)
)COMMENT='商品导入明细表';



CREATE TABLE `b2c_goods_label` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `name` varchar(100) NOT NULL COMMENT '标签名称',
  `goods_detail` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否应用于商品详情页： 1：是  0： 否',
  `goods_list` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否应用于商品列表页： 1：是  0： 否',
  `is_all` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否适用于全部商品： 1：是  0： 否',
  `level` smallint(5) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `del_flag` int(1) NOT NULL DEFAULT '0',
  `list_pattern` smallint(5) NOT NULL DEFAULT '0' COMMENT '列表样式',
  `goods_select` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否应用于商品筛选页： 1：是  0： 否',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='商品标签';



CREATE TABLE `b2c_goods_label_couple` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `label_id` varchar(100) NOT NULL COMMENT '标签id',
  `gta_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品或类型id',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标签关联类型： 1：关联商品 2：平台分类 3店鋪分類 4：全部商品',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='商品标签对关联表';



CREATE TABLE `b2c_goods_opai_spec` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL,
  `prd_sn` varchar(64) DEFAULT NULL,
  `prd_price` decimal(10,2) DEFAULT NULL,
  `is_on_sale` tinyint(1) DEFAULT '0' COMMENT '1:上架，0:下架',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '1:删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='欧派记录价格变化表';



CREATE TABLE `b2c_goods_overview_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date DEFAULT NULL COMMENT '2018-09-04',
  `type` tinyint(1) DEFAULT NULL COMMENT '1,7,30',
  `on_shelf_goods_num` int(11) DEFAULT '0' COMMENT '在架商品数',
  `sold_goods_num` int(11) DEFAULT '0' COMMENT '动销商品数(统计时间内，销量不为0的商品数量)',
  `visited_goods_num` int(11) DEFAULT '0' COMMENT '被访问商品数',
  `goods_user_visit` int(11) DEFAULT '0' COMMENT 'uv(商品访客数)',
  `goods_pageviews` int(11) DEFAULT '0' COMMENT 'pv(商品浏览量)',
  `purchase_num` int(11) DEFAULT '0' COMMENT '加购人数',
  `purchase_quantity` int(11) DEFAULT '0' COMMENT '加购件数',
  `paid_goods_num` int(11) DEFAULT '0' COMMENT '付款商品数',
  `order_goods_num` int(11) DEFAULT '0' COMMENT '下单商品数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `ref_type` (`ref_date`,`type`)
) COMMENT='商品概览统计表';



CREATE TABLE `b2c_goods_rebate_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL COMMENT '商品id',
  `product_id` int(11) DEFAULT NULL,
  `advise_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `min_price` decimal(10,2) DEFAULT NULL,
  `max_price` decimal(10,2) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0否，1是',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_goods_spec_product` (
  `prd_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `goods_id` int(10) NOT NULL DEFAULT '0',
  `prd_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `prd_market_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '市场价',
  `prd_cost_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '成本价',
  `prd_number` int(11) NOT NULL DEFAULT '0' COMMENT '当前规格组合产品库存',
  `prd_sn` varchar(65) NOT NULL DEFAULT '' COMMENT '商家编码',
  `prd_codes` varchar(500) NOT NULL DEFAULT '' COMMENT '商品条码',
  `prd_specs` varchar(1024) NOT NULL DEFAULT '',
  `prd_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '规格描述，格式例子：颜色:红色 尺码:s',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `self_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:商家自己添加商品，其他没用',
  `low_shop_price` varchar(1024) NOT NULL DEFAULT '0.00' COMMENT '最低售出价格',
  `prd_img` varchar(1024) NOT NULL DEFAULT '' COMMENT '图片地址',
  `price_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:商家未改价，1：商家改价，2：批量改价，3：毛利改价',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`prd_id`),
  KEY `gsp_goods_id` (`goods_id`),
  KEY `gsp_goods_codes` (`prd_codes`(191)),
  KEY `gsp_prd_sn` (`prd_sn`)
)COMMENT='商品规格组合的产品表 `b2c_goods_spec_product`';



CREATE TABLE `b2c_goods_spec_product_bak` (
  `prd_bak_id` int(10) NOT NULL AUTO_INCREMENT,
  `del_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `prd_id` int(10) NOT NULL DEFAULT '0',
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `goods_id` int(10) NOT NULL DEFAULT '0',
  `prd_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `prd_market_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '市场价',
  `prd_cost_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '成本价',
  `prd_number` int(11) NOT NULL DEFAULT '0' COMMENT '当前规格组合产品库存',
  `prd_sn` varchar(65) NOT NULL DEFAULT '' COMMENT '商家编码',
  `prd_codes` varchar(500) NOT NULL DEFAULT '' COMMENT '商品条码',
  `prd_specs` varchar(1024) NOT NULL DEFAULT '',
  `prd_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '规格描述，格式例子：颜色:红色 尺码:s',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `self_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:商家自己添加商品，其他没用',
  `low_shop_price` varchar(1024) NOT NULL DEFAULT '0.00' COMMENT '最低售出价格',
  `prd_img` varchar(1024) NOT NULL DEFAULT '' COMMENT '图片地址',
  `price_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:商家未改价，1：商家改价，2：批量改价，3：毛利改价',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '规格记录在原表内的添加时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '规格记录在原表内的最后修改时间',
  PRIMARY KEY (`prd_bak_id`),
  KEY `gsp_goods_id` (`goods_id`),
  KEY `gsp_goods_codes` (`prd_codes`(191)),
  KEY `gsp_prd_sn` (`prd_sn`)
)COMMENT='修改表 b2c_goods_spec_product_bak';



CREATE TABLE `b2c_goods_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date DEFAULT NULL COMMENT '统计日期',
  `type` tinyint(2) DEFAULT NULL COMMENT '1,7,30',
  `goods_id` int(11) DEFAULT NULL,
  `new_user_number` int(11) DEFAULT '0' COMMENT '新成交客户数',
  `old_user_number` int(11) DEFAULT '0' COMMENT '老成交客户数',
  `pv` int(11) DEFAULT '0' COMMENT '浏览量',
  `uv` int(11) DEFAULT '0' COMMENT '访客数',
  `cart_uv` int(11) DEFAULT '0' COMMENT '加购人数',
  `paid_uv` int(11) DEFAULT '0' COMMENT '付款人数',
  `paid_goods_number` int(11) DEFAULT '0' COMMENT '付款商品件数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `goods_sales` decimal(10,2) DEFAULT '0.00' COMMENT '销售额',
  `recommend_user_num` int(11) DEFAULT '0' COMMENT '推荐人数',
  `collect_use_num` int(11) DEFAULT '0' COMMENT '收藏人数',
  `share_pv` int(11) DEFAULT '0' COMMENT '分享次数',
  `share_uv` int(11) DEFAULT '0' COMMENT '分享人数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_key` (`ref_date`,`type`,`goods_id`)
)COMMENT='商品概览效果';



CREATE TABLE `b2c_goods_user_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date DEFAULT NULL COMMENT '2018-09-04',
  `type` tinyint(1) DEFAULT NULL COMMENT '1,7,30',
  `goods_id_number` int(11) DEFAULT NULL COMMENT '在售商品数',
  `prd_id_number` int(11) DEFAULT NULL COMMENT '在售规格数',
  `goods_id_visit` int(11) DEFAULT NULL COMMENT '访问商品数',
  `goods_user_visit` int(11) DEFAULT NULL COMMENT 'uv',
  `goods_visit` int(11) DEFAULT NULL COMMENT 'goods pv',
  `cart_user_number` int(11) DEFAULT NULL COMMENT '加购人数',
  `cart_goods_number` int(11) DEFAULT NULL COMMENT '加购件数',
  `paid_goods_number` int(11) DEFAULT NULL COMMENT '付款人数',
  `paid_user_number` int(11) DEFAULT NULL COMMENT '付费用户数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `ref_type` (`ref_date`,`type`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_grade_prd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `prd_id` int(10) NOT NULL,
  `goods_id` int(10) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `grade_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `grade` varchar(65) NOT NULL DEFAULT '' COMMENT '会员卡等级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除0否，1是',
  PRIMARY KEY (`id`),
  KEY `prd_id` (`prd_id`)
)COMMENT='规格对应会员价';



CREATE TABLE `b2c_group_buy_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `limit_amount` smallint(6) NOT NULL COMMENT '成团人数 不小于2人',
  `join_limit` smallint(6) NOT NULL DEFAULT '0' COMMENT '参团限制 0不限制',
  `open_limit` smallint(6) NOT NULL DEFAULT '0' COMMENT '开团限制 0不限制',
  `limit_buy_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '最少购买数 0不限制',
  `limit_max_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '最多购买数 0不限制',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `stock` smallint(6) NOT NULL DEFAULT '0' COMMENT '总库存',
  `sale_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '销量',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '默认成团 ',
  `activity_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '活动类型：1：普通拼团，2：老带新团',
  `is_grouper_cheap` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启团长优惠：0：不开启，1：开启',
  `shipping_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '运费类型 1免运费 2自定义',
  `reward_coupon_id` varchar(200) DEFAULT NULL COMMENT '拼团失败发放优惠券',
  `share_config` text COMMENT '分享设置',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1：启用  0： 禁用 2 代表已无库存',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='拼团活动定义表';



CREATE TABLE `b2c_group_buy_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '拼团活动定义id',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `group_id` int(11) NOT NULL DEFAULT '0' COMMENT '拼团id',
  `user_id` int(11) NOT NULL,
  `is_grouper` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为团长 1：是 0：否',
  `order_sn` varchar(20) NOT NULL COMMENT '订单编号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 拼团中 1:拼团成功 2:拼团失败',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开团时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '成团时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='拼团活动参团明细表';



CREATE TABLE `b2c_group_buy_product_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) NOT NULL COMMENT '拼团定义id',
  `product_id` int(11) NOT NULL COMMENT '商品规格id',
  `group_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团价',
  `stock` smallint(6) NOT NULL DEFAULT '0' COMMENT '库存',
  `sale_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '销量',
  `grouper_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '团长优惠价',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='拼团活动产品规格定义表';



CREATE TABLE `b2c_group_draw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '活动名称',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `goods_id` text COMMENT '参与抽奖的商品id',
  `min_join_num` smallint(6) NOT NULL COMMENT '开奖最小参与人数',
  `pay_money` decimal(10,2) NOT NULL COMMENT '下单支付金额',
  `join_limit` smallint(6) NOT NULL COMMENT '参团限制',
  `open_limit` smallint(6) NOT NULL COMMENT '开团限制',
  `limit_amount` smallint(6) NOT NULL COMMENT '最小成团人数',
  `to_num_show` smallint(6) DEFAULT NULL COMMENT '参与用户达到多少前端展示',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1：启用 0：禁用',
  `is_draw` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已开奖',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `reward_coupon_id` varchar(200) DEFAULT NULL COMMENT '拼团失败发放优惠券',
  PRIMARY KEY (`id`)
)COMMENT='拼团抽奖配置页';



CREATE TABLE `b2c_group_draw_invite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action` int(11) NOT NULL DEFAULT '1' COMMENT '活动类型：1 拼团抽奖',
  `identity_id` int(11) NOT NULL COMMENT '活动id',
  `path` varchar(100) NOT NULL COMMENT '页面路径',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `group_id` int(11) DEFAULT NULL COMMENT '团id',
  `invite_user_id` int(11) NOT NULL COMMENT '邀请用户id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `is_new` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是新用户',
  `is_used` tinyint(4) NOT NULL DEFAULT '0' COMMENT '记录是否已处理(已记录拼团信息)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='拼团抽奖邀请表';



CREATE TABLE `b2c_group_integration_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺id',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `inte_total` int(11) NOT NULL DEFAULT '0' COMMENT '总抽奖积分',
  `inte_group` int(11) NOT NULL DEFAULT '0' COMMENT '每个团总积分',
  `limit_amount` smallint(6) NOT NULL COMMENT '成团人数',
  `join_limit` smallint(6) NOT NULL COMMENT '参团限制',
  `divide_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '瓜分方式：0：按邀请好友数量瓜分，1：好友均分，2：随机瓜分',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1：启用  0： 禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `inte_remain` int(11) NOT NULL DEFAULT '0' COMMENT '剩余积分',
  `is_day_divide` tinyint(1) NOT NULL COMMENT '是否开团24小时自动开奖',
  `param_n` float NOT NULL DEFAULT '0' COMMENT '常数n',
  `is_continue` tinyint(1) NOT NULL DEFAULT '1' COMMENT '继续： 1：继续  0： 结束',
  `advertise` varchar(100) NOT NULL COMMENT '活动宣传语',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='瓜分积分活动配置';



CREATE TABLE `b2c_group_integration_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inte_activity_id` int(11) NOT NULL COMMENT '瓜分积分活动定义id',
  `group_id` int(11) NOT NULL COMMENT '拼团ID',
  `user_id` int(11) NOT NULL,
  `is_grouper` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为团长 1：是 0：否',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 拼团中 1:拼团成功 2:拼团失败',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '参团时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '成团时间',
  `integration` int(11) NOT NULL DEFAULT '0' COMMENT '瓜分到的积分',
  `invite_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '邀请人的数量',
  `invite_user` int(11) NOT NULL DEFAULT '0' COMMENT '邀请人（被谁邀请）',
  `is_new` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是新用户：0：不是，1：是',
  `is_look` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否看过开奖结果',
  `can_integration` int(11) NOT NULL DEFAULT '0' COMMENT '该团可瓜分积分池',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='参团列表';



CREATE TABLE `b2c_income_outcome_detail` (
  `id` mediumint(20) NOT NULL AUTO_INCREMENT,
  `req_id` mediumint(12) NOT NULL DEFAULT '0',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `pay_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `pay_code` varchar(20) NOT NULL DEFAULT '' COMMENT '支付宝:alipay,微信：？，...',
  `pay_code_alias` varchar(20) NOT NULL DEFAULT '' COMMENT '支付宝:alipay,微信：？，...',
  `pay_act_name` varchar(120) NOT NULL DEFAULT '' COMMENT '支付说明',
  `pay_act_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易付款时间',
  `income_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '收入金额',
  `outcome_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '支出金额',
  `pay_fee` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '手续费',
  `total_surplus_amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `trade_no` varchar(32) NOT NULL DEFAULT '' COMMENT '各平台交易号',
  `order_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '网站订单号',
  `order_comp_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单完成状态1：已完成，2:未完成',
  `update_order_status_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新订单状态时间',
  `pay_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '支付类型 1:收入，2：支出',
  `pay_type_name` varchar(32) NOT NULL DEFAULT '' COMMENT '支付类型名称',
  `remark` text COMMENT '自定义备注',
  `no_settle_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否参与结算 0:参入，1：不参与',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `pay_sn` (`pay_sn`),
  KEY `pay_code` (`pay_code`),
  KEY `trade_no` (`trade_no`),
  KEY `order_sn` (`order_sn`),
  KEY `pay_type` (`pay_type`),
  KEY `shop_id` (`shop_id`)
)COMMENT='收支明细表';



CREATE TABLE `b2c_index_foot_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `page_id` int(11) NOT NULL COMMENT '自定义页面id',
  `user_id` int(11) NOT NULL,
  `count` int(11) DEFAULT '1' COMMENT '浏览次数',
  `type` tinyint(2) DEFAULT '0' COMMENT '0 老用户 1新用户',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='自定义足迹';



CREATE TABLE `b2c_integral_mall_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `max_exchange_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '每个用户最大可兑换数量',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动起始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动终止时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1: 正常 0：禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `share_config` text COMMENT '分享设置',
  PRIMARY KEY (`id`)
)COMMENT='积分商城活动定义表';



CREATE TABLE `b2c_integral_mall_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `integral_mall_define_id` int(11) NOT NULL COMMENT '积分商城活动定义表id',
  `product_id` int(11) NOT NULL COMMENT '规格产品id',
  `score` int(11) NOT NULL COMMENT '积分数',
  `stock` smallint(6) NOT NULL COMMENT '库存数',
  `money` decimal(10,2) NOT NULL COMMENT '兑换现金',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='积分商城产品定义表';



CREATE TABLE `b2c_integral_mall_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `integral_mall_define_id` int(11) NOT NULL COMMENT '积分商城活动定义表id',
  `order_sn` varchar(20) DEFAULT NULL COMMENT '订单编号',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `product_id` int(11) NOT NULL COMMENT '产品规格id',
  `score` int(11) NOT NULL COMMENT '消费积分',
  `number` smallint(6) NOT NULL COMMENT '兑换数量',
  `money` decimal(10,2) NOT NULL COMMENT '消耗现金',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='积分商城用户兑换记录表';



CREATE TABLE `b2c_invoice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发票类型',
  `title` varchar(191) NOT NULL COMMENT '公司名称',
  `telephone` varchar(191) DEFAULT NULL COMMENT '公司电话',
  `taxnumber` varchar(191) DEFAULT NULL COMMENT '税号',
  `companyaddress` varchar(191) DEFAULT NULL COMMENT '公司地址',
  `bankname` varchar(191) DEFAULT NULL COMMENT '银行名称',
  `bankaccount` varchar(191) DEFAULT NULL COMMENT '银行账号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='发票表';



CREATE TABLE `b2c_join_draw_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_draw_id` int(11) NOT NULL COMMENT '拼团抽奖id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `group_id` int(11) NOT NULL COMMENT '拼团id',
  `user_id` int(11) NOT NULL COMMENT '抽奖用户id',
  `draw_id` int(11) NOT NULL COMMENT '抽奖序列id',
  `is_win_draw` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已中奖',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='拼团抽奖码记录';



CREATE TABLE `b2c_join_group_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_draw_id` int(11) NOT NULL COMMENT '拼团抽奖id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `group_id` int(11) DEFAULT NULL COMMENT '拼团id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `is_grouper` tinyint(1) DEFAULT '0' COMMENT '是否是团长 1是 0不是',
  `invite_user_id` int(11) DEFAULT NULL COMMENT '邀请人',
  `order_sn` varchar(20) DEFAULT NULL COMMENT '订单编号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:拼团中 1：已成团 2：未成团',
  `draw_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未开奖 1：已开奖',
  `is_win_draw` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已中奖 1：已中奖',
  `open_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开团时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '成团时间(达到最小成团数量就记录)',
  `draw_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开奖时间',
  `invite_user_num` int(11) NOT NULL DEFAULT '0' COMMENT '邀请用户数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_lottery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lottery_name` varchar(120) NOT NULL COMMENT '抽奖名称',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `lottery_explain` varchar(299) NOT NULL COMMENT '抽奖说明',
  `free_chances` int(8) DEFAULT NULL COMMENT '免费抽奖次数',
  `can_share` tinyint(2) DEFAULT NULL COMMENT '是否分享获得次数',
  `share_chances` int(8) DEFAULT NULL COMMENT '分享最多获得次数',
  `can_use_score` tinyint(2) DEFAULT NULL COMMENT '是否可以积分抽奖',
  `score_per_chance` int(8) DEFAULT NULL COMMENT '抽奖一次使用积分',
  `score_chances` int(8) DEFAULT NULL COMMENT '积分最多抽奖次数',
  `no_award_score` int(8) DEFAULT NULL COMMENT '未中奖奖励积分',
  `no_award_image` varchar(199) DEFAULT NULL COMMENT '未中奖图片',
  `no_award_icon` varchar(20) DEFAULT NULL COMMENT '未中奖提示',
  `first_award` varchar(500) DEFAULT NULL COMMENT '一等奖设置（json）',
  `first_award_times` int(8) DEFAULT NULL COMMENT '中奖数',
  `second_award` varchar(500) DEFAULT NULL COMMENT '二等奖设置（json）',
  `second_award_times` int(8) DEFAULT NULL COMMENT '中奖数',
  `third_award` varchar(500) DEFAULT NULL COMMENT '三等奖设置（json）',
  `third_award_times` int(8) DEFAULT NULL COMMENT '中奖数',
  `fourth_award` varchar(500) DEFAULT NULL COMMENT '四等奖设置（json）',
  `fourth_award_times` int(8) DEFAULT NULL COMMENT '中奖数',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态：0停用',
  `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_lottery_prize` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lottery_id` int(11) DEFAULT NULL COMMENT '抽奖编号',
  `chance_numerator` int(11) DEFAULT NULL COMMENT '中奖概率--分子',
  `chance_denominator` int(11) DEFAULT NULL COMMENT '中奖概率--分母',
  `lottery_grade` tinyint(2) DEFAULT NULL COMMENT '中奖等级：1一等奖，2二等奖，3三等奖，4四等奖 5.。。。',
  `lottery_detail` varchar(32) DEFAULT NULL COMMENT '奖品信息',
  `icon_imgs_image` varchar(199) DEFAULT NULL COMMENT '中奖图片',
  `icon_imgs` varchar(20) DEFAULT NULL COMMENT '中奖提示',
  `lottery_type` tinyint(4) DEFAULT NULL COMMENT '选择奖类型 0积分 1 用户余额 2优惠券 3赠品 4 自定义',
  `lottery_number` int(11) DEFAULT NULL COMMENT '奖品份数',
  `award_times` int(11) DEFAULT NULL COMMENT '已发生中奖数',
  `integral_score` int(11) DEFAULT NULL COMMENT '积分数量',
  `coupon_id` int(10) DEFAULT NULL COMMENT '优惠券id',
  `prd_id` int(10) DEFAULT NULL COMMENT '赠品规格id',
  `prd_keep_days` smallint(6) DEFAULT NULL COMMENT '赠品有效期',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '1删除',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
)COMMENT='抽奖活动 奖品及等级';



CREATE TABLE `b2c_lottery_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户编号',
  `lottery_id` int(10) NOT NULL COMMENT '抽奖编号',
  `lottery_source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '抽奖来源:1.登录2.支付',
  `lottery_act_id` int(10) NOT NULL DEFAULT '0' COMMENT '抽奖来源id',
  `chance_source` tinyint(2) DEFAULT NULL COMMENT '抽奖机会来源',
  `lottery_cost` varchar(32) DEFAULT NULL COMMENT '抽奖花费积分',
  `lottery_grade` tinyint(2) DEFAULT NULL COMMENT '中奖等级：0未中奖，1一等奖，2二等奖，3三等奖，4四等奖',
  `lottery_type` tinyint(2) DEFAULT NULL COMMENT '奖励类型',
  `lottery_award` varchar(60) DEFAULT NULL COMMENT '获得奖励',
  `award_info` varchar(500) DEFAULT NULL COMMENT '中奖信息',
  `prd_id` int(10) NOT NULL COMMENT '商品（规格）编号',
  `present_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '赠品状态:0.待领取，1：已领取，2.已过期',
  `order_sn` varchar(60) DEFAULT NULL COMMENT '关联订单',
  `lottery_expired_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '赠品过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_lottery` (`user_id`,`lottery_id`),
  KEY `lottery_id` (`lottery_id`)
)COMMENT='抽奖活动表';



CREATE TABLE `b2c_lottery_share` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户编号',
  `lottery_id` int(10) NOT NULL COMMENT '抽奖编号',
  `share_times` int(8) NOT NULL DEFAULT '0' COMMENT '分享次数',
  `use_share_times` int(8) NOT NULL DEFAULT '0' COMMENT '抽奖次数',
  `use_score_times` int(8) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_lottery` (`user_id`,`lottery_id`)
);



CREATE TABLE `b2c_member_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_name` varchar(20) NOT NULL,
  `card_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:普通会员卡，1:次卡,2:登记卡',
  `bg_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:背景色，1:背景图',
  `bg_color` char(10) DEFAULT NULL COMMENT '背景色',
  `bg_img` varchar(100) DEFAULT NULL COMMENT '背景图',
  `discount` decimal(10,2) DEFAULT NULL COMMENT '折扣',
  `sorce` int(11) DEFAULT NULL COMMENT '开卡送积分',
  `buy_score` text COMMENT '购物送积分策略json数据',
  `expire_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:固定日期 1：自领取之日起 2:不过期',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始日期',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束日期',
  `receive_day` int(11) DEFAULT NULL COMMENT '领取之日起n',
  `date_type` tinyint(1) DEFAULT NULL COMMENT '0:日，1:周 2: 月',
  `activation` tinyint(1) DEFAULT NULL COMMENT '0：不用激活，1：需要激活',
  `receive_code` char(10) DEFAULT NULL COMMENT '领取码',
  `desc` text COMMENT '使用须知',
  `mobile` char(11) DEFAULT NULL COMMENT '联系电话',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1:使用中，2:停止使用',
  `send_money` int(11) DEFAULT NULL COMMENT '开卡送钱',
  `charge_money` text COMMENT '充值活动策略',
  `use_time` int(11) DEFAULT NULL COMMENT '使用时间 1工作日 2双休 0不限制',
  `store_list` varchar(191) NOT NULL DEFAULT '{}' COMMENT '可用门店',
  `count` int(11) DEFAULT NULL COMMENT '卡总次数',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1为删除状态',
  `grade` char(10) NOT NULL DEFAULT '' COMMENT '等级卡的等级',
  `grade_condition` varchar(200) NOT NULL DEFAULT '' COMMENT '等级卡的条件',
  `activation_cfg` varchar(200) DEFAULT NULL COMMENT '激活信息配置',
  `examine` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否审核 0不审核 1审核',
  `discount_goods_id` varchar(299) DEFAULT NULL COMMENT '折扣商品id',
  `discount_cat_id` varchar(299) DEFAULT NULL COMMENT '折扣平台分类id',
  `discount_sort_id` varchar(299) DEFAULT NULL COMMENT '折扣商家分类id',
  `discount_is_all` tinyint(1) NOT NULL DEFAULT '1' COMMENT '折扣商品范围： 0:部分商品，1:全部商品',
  `is_pay` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:直接购买 1:需要购买 2: 需要领取码',
  `pay_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:不支持现金购买，1:支持现金购买',
  `pay_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '购买资金',
  `pay_own_good` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否专属购买商品 0不是 1是',
  `receive_action` tinyint(1) NOT NULL DEFAULT '0' COMMENT '领取方式 1:领取码 2：卡号+密码',
  `is_exchang` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0不可 1部分 2全部',
  `store_use_switch` tinyint(1) NOT NULL DEFAULT '0' COMMENT '可否在门店使用  0可以 1不可以',
  `exchang_goods` varchar(299) DEFAULT NULL COMMENT '可兑换商品id',
  `exchang_freight` tinyint(1) DEFAULT NULL COMMENT '运费策略 0免运费 1使用商品运费策略',
  `exchang_count` int(11) DEFAULT NULL COMMENT '允许兑换次数',
  `stock` int(11) DEFAULT '0' COMMENT '发放总量',
  `limit` int(11) DEFAULT '1' COMMENT '领取限制',
  `discount_brand_id` varchar(299) DEFAULT NULL COMMENT '商品品牌id',
  `send_coupon_switch` tinyint(1) DEFAULT '0' COMMENT '是否开卡送券：0不是，1是',
  `send_coupon_type` tinyint(1) DEFAULT '0' COMMENT '送惠类型：0优惠券，1优惠券礼包',
  `send_coupon_ids` varchar(20) DEFAULT NULL COMMENT '赠送优惠券或礼包id，字符串逗号隔开',
  PRIMARY KEY (`id`)
)COMMENT='会员卡信息';



CREATE TABLE `b2c_message_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '消息类型： 1： 业务处理通知 2： 商家活动通知 3： 活动加入成功提醒',
  `content` text,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='消息模板表';



CREATE TABLE `b2c_message_template_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `open_ma` tinyint(4) NOT NULL DEFAULT '0',
  `open_mp` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)COMMENT='优惠券礼包_礼包内容(优惠券)';



CREATE TABLE `b2c_mp_daily_retain` (
  `ref_date` char(8) NOT NULL COMMENT '时间，如："20180313"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `ref_date` (`ref_date`)
)COMMENT='日留存';



CREATE TABLE `b2c_mp_daily_visit` (
  `ref_date` char(8) NOT NULL COMMENT '时间： 如： "20180313"',
  `session_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '打开次数',
  `visit_pv` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `visit_uv` int(11) NOT NULL DEFAULT '0' COMMENT '访问人数',
  `visit_uv_new` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `stay_time_uv` float NOT NULL DEFAULT '0' COMMENT '人均停留时长 (浮点型，单位：秒)',
  `stay_time_session` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 (浮点型，单位：秒)',
  `visit_depth` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 (浮点型)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_mp_distribution_visit` (
  `ref_date` char(8) NOT NULL COMMENT '时间，如："20180313"',
  `list` text COMMENT '存入所有类型的指标情况',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `ref_date` (`ref_date`)
)COMMENT='访问分布';



CREATE TABLE `b2c_mp_jump` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(64) NOT NULL,
  `app_name` varchar(200) NOT NULL,
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:可用，1:停用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='小程序链接列表';



CREATE TABLE `b2c_mp_jump_usable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL COMMENT '小程序模板id',
  `app_id` varchar(64) NOT NULL COMMENT '跳转小程序appid',
  `usable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:不可用，1：可用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='小程序可用appid记录';



CREATE TABLE `b2c_mp_monthly_retain` (
  `ref_date` char(6) NOT NULL COMMENT '时间，如："201803"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)COMMENT='月留存';



CREATE TABLE `b2c_mp_monthly_visit` (
  `ref_date` char(6) NOT NULL COMMENT '时间，如："201803"',
  `session_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '打开次数',
  `visit_pv` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `visit_uv` int(11) NOT NULL DEFAULT '0' COMMENT '访问人数',
  `visit_uv_new` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `stay_time_uv` float NOT NULL DEFAULT '0' COMMENT '人均停留时长 (浮点型，单位：秒)',
  `stay_time_session` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 (浮点型，单位：秒)',
  `visit_depth` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 (浮点型)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)COMMENT='月趋势';



CREATE TABLE `b2c_mp_official_account_user` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(128) NOT NULL COMMENT '用户的标识，对当前公众号唯一',
  `app_id` varchar(191) NOT NULL COMMENT '授权公众号appId',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '系统账户ID',
  `subscribe` tinyint(1) DEFAULT '0' COMMENT '是否订阅，为0代表此用户没有关注该公众号，拉取不到其余信息。',
  `nickname` varchar(191) DEFAULT '',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户的性别，值为1时是男性，值为2时是女性，值为0时是未知',
  `language` varchar(20) DEFAULT NULL COMMENT '用户的语言，简体中文为zh_CN',
  `city` varchar(50) DEFAULT NULL COMMENT '用户所在城市',
  `province` varchar(50) DEFAULT NULL COMMENT '用户所在省份',
  `country` varchar(50) DEFAULT NULL COMMENT '用户所在国家',
  `headimgurl` varchar(191) DEFAULT NULL COMMENT '用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空',
  `subscribe_time` timestamp NULL DEFAULT NULL COMMENT '用户关注时间，如果用户曾多次关注，则取最后关注时间',
  `unionid` varchar(191) DEFAULT NULL COMMENT '只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段',
  PRIMARY KEY (`rec_id`),
  UNIQUE KEY `openid` (`openid`,`app_id`),
  KEY `bmoau_sys_id` (`sys_id`),
  KEY `unionid` (`unionid`)
);



CREATE TABLE `b2c_mp_scene_record` (
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `path` varchar(50) DEFAULT NULL COMMENT '打开小程序的路径',
  `path_query` varchar(500) DEFAULT NULL COMMENT '打开小程序的query',
  `scene` smallint(6) DEFAULT NULL COMMENT '场景值',
  `share_ticket` varchar(500) DEFAULT NULL COMMENT '转发的ticket',
  `referrer_info` text COMMENT 'referrer信息',
  `count` int(11) DEFAULT NULL COMMENT '记录次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `user_path` (`user_id`,`path`,`scene`),
  KEY `scene_create_time` (`scene`,`create_time`),
  KEY `path_create_time` (`path`,`create_time`)
)COMMENT='小程序初始化场景值';



CREATE TABLE `b2c_mp_summary_trend` (
  `ref_date` char(8) NOT NULL COMMENT '日期',
  `visit_total` int(11) NOT NULL DEFAULT '0' COMMENT '总访问量',
  `share_pv` int(11) NOT NULL DEFAULT '0' COMMENT '转发次数',
  `share_uv` int(11) NOT NULL DEFAULT '0' COMMENT '转发人数',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_mp_template_form_id` (
  `rec_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `form_id` varchar(255) NOT NULL DEFAULT '' COMMENT '小程序提交form_id',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `open_id` varchar(255) NOT NULL DEFAULT '' COMMENT '用户openid',
  `use_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用状态，0未使用，1冻结 2 使用',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1: 发送成功  0：未知',
  `is_visit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已点击访问 1：是 0： 否',
  `template_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用类型，0 初始 1 订单 2 预约 3 优惠券 4 拼团 5卡券 ',
  `mp_link_identity` varchar(255) NOT NULL DEFAULT '' COMMENT '发送消息关联id，如果order_sn等',
  `mp_template_no` varchar(255) NOT NULL DEFAULT '' COMMENT '发送模板编号',
  `mp_template_content` text COMMENT '发送模板消息内容',
  `user_visit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '用户点击访问时间',
  `use_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '使用时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`rec_id`),
  KEY `form_id` (`form_id`(191)),
  KEY `user_id` (`user_id`),
  KEY `open_id` (`open_id`(191))
)COMMENT='模板消息form_id表';



CREATE TABLE `b2c_mp_user_portrait` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` char(30) NOT NULL COMMENT '时间： 如： "20180313"',
  `visit_uv_new` longtext COMMENT '新用户',
  `visit_uv` longtext COMMENT '活跃用户',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:昨天，1：最近7天，2:30天',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间，ref_date前8个字符',
  PRIMARY KEY (`id`),
  KEY `type` (`type`),
  KEY `ref_date` (`ref_date`)
)COMMENT='用户画像';



CREATE TABLE `b2c_mp_visit_page` (
  `ref_date` char(8) NOT NULL COMMENT '时间，如："20170313"',
  `page_path` varchar(200) NOT NULL,
  `page_visit_pv` int(11) DEFAULT NULL COMMENT '访问次数',
  `page_visit_uv` int(11) DEFAULT NULL COMMENT '访问人数',
  `page_staytime_pv` float DEFAULT NULL COMMENT '人均停留时长 (浮点型，单位：秒)',
  `entrypage_pv` int(11) DEFAULT NULL COMMENT '进入页次数',
  `exitpage_pv` int(11) DEFAULT NULL COMMENT '退出页次数',
  `page_share_pv` int(11) DEFAULT NULL COMMENT '转发次数',
  `page_share_uv` int(11) DEFAULT NULL COMMENT '转发人数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`),
  KEY `page_path` (`page_path`(191)),
  KEY `page_visit_pv` (`page_visit_pv`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_mp_weekly_retain` (
  `ref_date` char(20) NOT NULL COMMENT '时间，如："20180306-20180312"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)COMMENT='周留存';



CREATE TABLE `b2c_mp_weekly_visit` (
  `ref_date` char(20) NOT NULL COMMENT '时间，如："20180306-20180312"',
  `session_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '打开次数',
  `visit_pv` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `visit_uv` int(11) NOT NULL DEFAULT '0' COMMENT '访问人数',
  `visit_uv_new` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `stay_time_uv` float NOT NULL DEFAULT '0' COMMENT '人均停留时长 (浮点型，单位：秒)',
  `stay_time_session` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 (浮点型，单位：秒)',
  `visit_depth` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 (浮点型)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)COMMENT='周趋势';



CREATE TABLE `b2c_mrking_strategy` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `act_name` varchar(120) NOT NULL DEFAULT '',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型,1每满减 2满件 3满折 4仅第X件打折',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `recommend_goods_id` text COMMENT '指定商品可用',
  `recommend_cat_id` text COMMENT '指定平台分类可用',
  `recommend_brand_id` text COMMENT '指定品牌可用',
  `act_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '活动类型，0-全部商品参与活动；1-选中商品参与活动（由商品、平台分类、商家分类、品牌等ID字符串指定）',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `strategy_priority` int(11) NOT NULL DEFAULT '0' COMMENT '促销活动优先级',
  `card_id` text COMMENT '专属会员卡',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1可用，0停用',
  `recommend_sort_id` text COMMENT '指定商家分类可用',
  PRIMARY KEY (`id`),
  KEY `mrking_strategy_delflag` (`del_flag`)
)COMMENT='满折满减活动列表';



CREATE TABLE `b2c_mrking_strategy_condition` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `strategy_id` int(11) NOT NULL DEFAULT '0',
  `full_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '满多少金额',
  `reduce_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '减多少钱',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '满几件或第几件（第X件打折）',
  `discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '打几折',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='满折满减优惠规则表';



CREATE TABLE `b2c_mrking_voucher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `act_code` varchar(50) NOT NULL DEFAULT 'voucher',
  `act_name` varchar(120) NOT NULL DEFAULT '',
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `denomination` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '面额',
  `total_amount` int(11) NOT NULL DEFAULT '0' COMMENT '发行量',
  `type` tinyint(1) DEFAULT '0' COMMENT '优惠券类型，0普通优惠券；1分裂优惠券',
  `surplus` int(11) NOT NULL DEFAULT '0',
  `use_consume_restrict` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用限制',
  `least_consume` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '满多少可用',
  `use_explain` varchar(256) NOT NULL DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `receive_per_person` smallint(3) NOT NULL DEFAULT '0' COMMENT '每人限领张数',
  `suit_goods` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:全店通用,1:指定店铺',
  `together_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否与其他优惠券同时使用',
  `permit_share` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许分享优惠券链接',
  `remind_owner` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否到期前提醒用户',
  `giveout_amount` smallint(4) NOT NULL DEFAULT '0' COMMENT '发放优惠券数量',
  `giveout_person` smallint(4) NOT NULL DEFAULT '0' COMMENT '发放优惠券人数',
  `receive_amount` smallint(4) NOT NULL DEFAULT '0' COMMENT '领取优惠券数量',
  `receive_person` smallint(4) NOT NULL DEFAULT '0' COMMENT '领取优惠券人数',
  `used_amount` smallint(4) NOT NULL DEFAULT '0' COMMENT '已使用优惠券数量',
  `alias_code` varchar(16) NOT NULL DEFAULT '' COMMENT '唯一活动代码',
  `validation_code` varchar(10) NOT NULL DEFAULT '' COMMENT '领取码',
  `recommend_goods_id` text COMMENT '指定商品可用',
  `recommend_cat_id` text COMMENT '指定平台可用',
  `recommend_sort_id` text COMMENT '指定商家分类可用',
  `validity` mediumint(11) NOT NULL DEFAULT '0' COMMENT '优惠券有效天数',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1为删除状态',
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1:系統创建 2：来自crm',
  `identity_id` varchar(50) DEFAULT NULL COMMENT '关联外部优惠券规则唯一码',
  `recommend_product_id` text COMMENT '关联商品规格',
  `use_score` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否可以积分兑换',
  `score_number` int(6) NOT NULL DEFAULT '0' COMMENT '需要积分数',
  `card_id` text COMMENT '专属会员卡',
  `validity_type` tinyint(1) DEFAULT '0' COMMENT '优惠券有效期类型标记，1领取后开始指定时间段内有效，0固定时间段有效',
  `validity_hour` mediumint(11) DEFAULT '0' COMMENT '优惠券有效小时数',
  `validity_minute` mediumint(11) DEFAULT '0' COMMENT '优惠券有效分钟数',
  `expiration_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'validity_type为1是的过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `limit_surplus_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否限制库存：0限制，1不限制',
  `random_min` decimal(10,2) DEFAULT '0.00' COMMENT '分裂优惠卷随机金额最低',
  `random_max` decimal(10,2) DEFAULT '0.00' COMMENT '分裂优惠卷随机金额最高',
  `receive_per_num` tinyint(1) DEFAULT '0' COMMENT '分裂优惠券领券人数是否限制 0不限制 1限制',
  `receive_num` int(11) NOT NULL DEFAULT '0' COMMENT '分裂优惠券可领券人数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `alias_code` (`alias_code`),
  KEY `act_name` (`act_name`)
)COMMENT='常乐  7月16日 重新设计优惠券表结构';



CREATE TABLE `b2c_order_action` (
  `action_id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `order_id` mediumint(8) NOT NULL DEFAULT '0',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `action_user` varchar(128) NOT NULL DEFAULT '',
  `user_id` mediumint(8) NOT NULL DEFAULT '0',
  `user_cid` varchar(40) NOT NULL DEFAULT '',
  `user_openid` varchar(128) NOT NULL DEFAULT '',
  `order_status` tinyint(1) NOT NULL DEFAULT '0',
  `action_note` varchar(191) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`action_id`),
  KEY `order_id` (`order_id`,`order_sn`)
)COMMENT='订单操作表 b2c_order_action';



CREATE TABLE `b2c_order_goods` (
  `rec_id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `main_rec_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '主订单rec_id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `order_id` mediumint(8) NOT NULL DEFAULT '0',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `goods_id` mediumint(8) NOT NULL DEFAULT '0',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `goods_sn` varchar(60) NOT NULL DEFAULT '',
  `product_id` mediumint(8) NOT NULL DEFAULT '0',
  `product_sn` varchar(65) NOT NULL DEFAULT '',
  `goods_number` smallint(5) NOT NULL DEFAULT '1',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_attr` text NOT NULL,
  `send_number` smallint(5) NOT NULL DEFAULT '0',
  `return_number` smallint(5) NOT NULL DEFAULT '0' COMMENT '退货/退款成功数量',
  `is_real` tinyint(1) NOT NULL DEFAULT '0',
  `goods_attr_id` varchar(191) NOT NULL DEFAULT '',
  `goods_img` varchar(191) NOT NULL DEFAULT '',
  `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家再次提交申请，5：退款退货成功，6是拒绝退款退货',
  `comment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未评论，1:已评论，2：已晒单',
  `stra_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品参与的促销活动id',
  `per_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '促销折扣均摊到每件商品的折扣',
  `is_gift` int(1) NOT NULL DEFAULT '0' COMMENT '是否是赠品',
  `r_goods` varchar(191) NOT NULL DEFAULT '' COMMENT '赠品的关联商品',
  `goods_score` int(11) NOT NULL DEFAULT '0' COMMENT '商品积分',
  `goods_growth` int(11) NOT NULL DEFAULT '0' COMMENT '商品成长值',
  `discounted_goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '折后单价',
  `discount_detail` text COMMENT '打折详情，json存储',
  `fanli_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:不返利商品，1：返利商品',
  `can_calculate_money` decimal(10,2) DEFAULT '0.00' COMMENT '单品可计算返利金额，刨除优惠券和其他折扣后的金额',
  `fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '单品返利金额',
  `discounted_total_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '折后总价',
  `total_fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '商品返利总金额',
  `fanli_strategy` varchar(666) DEFAULT NULL COMMENT '返利比例',
  `fanli_percent` decimal(10,2) DEFAULT '0.00' COMMENT '返利比例',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `is_card_exclusive` tinyint(1) DEFAULT '0' COMMENT '是否会员卡专属',
  `promote_info` varchar(500) DEFAULT NULL COMMENT '推广信息',
  `gift_id` int(11) DEFAULT '0' COMMENT '赠品ID',
  `is_can_return` tinyint(1) DEFAULT '1' COMMENT '是否可退款',
  `reduce_price_num` smallint(5) NOT NULL DEFAULT '0',
  `activity_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '营销活动种类',
  `activity_id` int(11) NOT NULL DEFAULT '0' COMMENT '营销活动id',
  `activity_rule` int(11) NOT NULL DEFAULT '0' COMMENT '营销活动id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`rec_id`),
  KEY `order_id` (`order_id`),
  KEY `order_sn` (`order_sn`),
  KEY `goods_id` (`goods_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单商品表  b2c_order_goods';



CREATE TABLE `b2c_order_goods_rebate` (
  `rebate_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(20) NOT NULL DEFAULT '0' COMMENT '订单uuid',
  `goods_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '商品id',
  `product_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '产品id',
  `rebate_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '返利级别：0自购，2二级',
  `rebate_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '返利用户id',
  `rebate_percent` decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '返利比例',
  `rebate_money` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '单商品返利金额',
  `total_rebate_money` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '总返利金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `real_rebate_money` decimal(10,2) DEFAULT '0.00' COMMENT '实际返利金额',
  PRIMARY KEY (`rebate_id`),
  KEY `order_sn` (`order_sn`,`product_id`),
  KEY `rebate_user_id` (`rebate_user_id`)
)COMMENT='订单商品返利表';



CREATE TABLE `b2c_order_info` (
  `order_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `main_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '主订单编号(拆单时用)',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `user_openid` varchar(191) NOT NULL DEFAULT '' COMMENT '用户openid',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_status_name` varchar(32) NOT NULL DEFAULT '' COMMENT '订单状态名称',
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收件人姓名',
  `address_id` int(11) NOT NULL DEFAULT '0' COMMENT '地址id',
  `country_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '国家编号',
  `country_name` varchar(50) NOT NULL DEFAULT '' COMMENT '国家名称',
  `province_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '省份编号',
  `province_name` varchar(50) NOT NULL DEFAULT '' COMMENT '省份名称',
  `city_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '城市编号',
  `city_name` varchar(120) NOT NULL DEFAULT '' COMMENT '城市名称',
  `district_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '区县编号',
  `district_name` varchar(120) NOT NULL DEFAULT '' COMMENT '区县名称',
  `address` varchar(191) NOT NULL DEFAULT '' COMMENT '更多详细地址',
  `complete_address` varchar(512) NOT NULL DEFAULT '' COMMENT '完整收件地址',
  `zipcode` varchar(60) NOT NULL DEFAULT '' COMMENT '邮编',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机号',
  `add_message` varchar(191) NOT NULL DEFAULT '' COMMENT '客户留言',
  `shipping_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '快递id',
  `shipping_name` varchar(120) NOT NULL DEFAULT '' COMMENT '快递名称',
  `pay_code` varchar(30) NOT NULL DEFAULT '' COMMENT '支付代号',
  `pay_name` varchar(120) NOT NULL DEFAULT '' COMMENT '支付名称',
  `pay_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `goods_amount` smallint(6) NOT NULL DEFAULT '0' COMMENT '订单商品数量',
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '快递费金额',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单应付金额',
  `shoper_reduce_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商家减价金额',
  `sub_order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '子订单总金额',
  `discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '券折扣金额',
  `score_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `use_account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户消费余额',
  `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `grade_percent` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '购买会员等级的折扣%',
  `discount_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '购买会员等级的折扣金额',
  `dapei_reduce_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '搭配减价',
  `package_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '一口价抵扣金额',
  `dapei_id` int(8) NOT NULL DEFAULT '0' COMMENT '搭配id来源',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `confirm_time` timestamp NULL DEFAULT NULL COMMENT '订单确收收货时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_time` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `closed_time` timestamp NULL DEFAULT NULL COMMENT '关闭时间',
  `cancelled_time` timestamp NULL DEFAULT NULL COMMENT '取消时间',
  `finished_time` timestamp NULL DEFAULT NULL COMMENT '订单完成时间',
  `return_time` timestamp NULL DEFAULT NULL COMMENT '订单申请退货时间',
  `return_finish_time` timestamp NULL DEFAULT NULL COMMENT '订单退货完成时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '订单申请退款时间',
  `refund_finish_time` timestamp NULL DEFAULT NULL COMMENT '订单退款完成时间',
  `shipping_no` varchar(191) NOT NULL DEFAULT '' COMMENT '快递单号',
  `shipping_type` varchar(60) NOT NULL DEFAULT '' COMMENT '快递类型',
  `is_cod` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否货到付款',
  `return_type_cfg` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否支持退换货：1支持 2不支持',
  `return_days_cfg` tinyint(1) NOT NULL DEFAULT '7' COMMENT '发货后自动确认收货时间,单位天',
  `order_timeout_days` smallint(3) NOT NULL DEFAULT '3' COMMENT '确认收货后自动订单完成时间,单位天',
  `seller_remark` varchar(512) NOT NULL DEFAULT '' COMMENT '卖家备注',
  `erpordercode` varchar(32) NOT NULL DEFAULT '' COMMENT 'erp中订单代码',
  `comment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未评论，1:已评论，2：已晒单',
  `fanli_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员id',
  `fanli_grade` varchar(64) NOT NULL DEFAULT '' COMMENT '返利等级',
  `fanli_percent` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返利百分比',
  `settlement_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票id',
  `invoice_content` int(11) NOT NULL DEFAULT '0' COMMENT '发票类型：0普通发票；1增值税专票',
  `invoice_title` text COMMENT '发票内容：json存储',
  `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家再次提交申请，5：退款退货成功，6是拒绝退款退货',
  `pay_order_sn` varchar(30) NOT NULL DEFAULT '' COMMENT '对账单号',
  `goods_type` varchar(50) NOT NULL DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购',
  `order_source` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单来源，0：小程序，1wap，2app',
  `fanli_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '返利类型，0：普通订单，1：分销返利订单，2：返利会员返利订单',
  `manual_refund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1代表手动退款，0代表非手动',
  `order_pay_way` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单付款方式，0全款 1定金 2好友代付',
  `bk_order_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '补款订单号 order_pay_way=1时有效',
  `bk_order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补款金额 order_pay_way=1时有效',
  `bk_order_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '补款金额是否支付 order_pay_way=1时有效，0未支付，1已支付',
  `pin_goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前拼团商品金额，阶梯团根据人数时会变化，补款也随之变化',
  `pin_yj_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团支付佣金金额',
  `activity_id` int(11) NOT NULL DEFAULT '0' COMMENT '营销活动id',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
  `source` varchar(30) NOT NULL DEFAULT '' COMMENT '订单来源，记录app，wap，pc来源',
  `part_ship_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:，1:部分发货',
  `promotion_id` int(11) NOT NULL DEFAULT '0' COMMENT '促销活动id',
  `promotion_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '促销活动折扣金额,满折满减',
  `push_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'yadu推送状态：0-暂无推送，1-推送失败，2-推送成功',
  `push_desc` varchar(100) NOT NULL DEFAULT '' COMMENT 'yadu推送失败原因',
  `pos_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '门店订单标志：0：商城，1：门店同步订单',
  `pos_shop_name` varchar(191) NOT NULL DEFAULT '' COMMENT 'pos店铺名称',
  `store_id` int(11) DEFAULT '0' COMMENT '门店id',
  `store_name` varchar(191) NOT NULL DEFAULT '' COMMENT '门店名称',
  `member_card_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员卡id',
  `member_card_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡优惠金额',
  `member_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡消费金额',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '订单支付过期时间',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `deliver_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '配送类型：0 快递 1 自提',
  `deliver_type_name` varchar(191) DEFAULT NULL COMMENT '配送类型名称',
  `pickupdate_time` varchar(30) DEFAULT NULL COMMENT '自提时间',
  `star_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
  `verify_code` varchar(191) NOT NULL DEFAULT '' COMMENT '核销自提码',
  `split` int(11) NOT NULL DEFAULT '0' COMMENT '分裂优惠券id',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `fanli_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单品返利金额',
  `true_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(32) NOT NULL DEFAULT '' COMMENT '身份证号',
  `ali_trade_no` varchar(60) NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
  `grouper_cheap_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '团长优惠金额',
  `bk_shipping_time` timestamp NULL DEFAULT NULL COMMENT '定金预计发货时间',
  `bk_return_type` tinyint(2) DEFAULT NULL COMMENT '定金退款状态',
  `bk_prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `pre_sale_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '定金膨胀优惠金额',
  `instead_pay_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '代付金额',
  `order_user_message` varchar(50) DEFAULT NULL COMMENT '发起人留言',
  `instead_pay` text COMMENT '好友代付规则',
  `instead_pay_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '代付人数',
  `is_promote` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是推广单',
  `verifier_id` int(11) NOT NULL DEFAULT '0' COMMENT '核销员id',
  `exchang` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1 兑换 0否',
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `free_ship` decimal(10,2) DEFAULT '0.00' COMMENT '运费抵扣',
  `free_detail` text COMMENT '运费抵扣规则',
  `sub_goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '子单金额',
  `is_refund_coupon` tinyint(1) DEFAULT '0' COMMENT '是否退优惠券',
  `is_finish_refund` tinyint(1) DEFAULT '0' COMMENT '子订单是否已处理退款',
  `is_view_comment` tinyint(1) DEFAULT '1' COMMENT '是否已查看评价',
  `pos_order_action` tinyint(1) DEFAULT '1' COMMENT '1:扫码购 2：仅自提',
  `order_remind` tinyint(1) DEFAULT '0' COMMENT '发货提醒次数',
  `order_remind_time` timestamp NULL DEFAULT NULL COMMENT '发货提醒时间',
  `extend_receive_action` tinyint(1) DEFAULT '0' COMMENT '延长收货操作人：1:商家 2:用户',
  `extend_receive_time` timestamp NULL DEFAULT NULL COMMENT '收货延长时间',
  `tk_order_type` tinyint(2) DEFAULT '0' COMMENT '淘客订单类型：0：普通订单，1：京东订单，2：淘宝订单',
  `pay_award_id` int(9) DEFAULT NULL COMMENT '支付有礼id',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_sn` (`order_sn`),
  KEY `main_order_sn` (`main_order_sn`),
  KEY `user_id` (`user_id`),
  KEY `user_openid` (`user_openid`),
  KEY `order_status` (`order_status`),
  KEY `shipping_id` (`shipping_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单表';



CREATE TABLE `b2c_order_must` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(20) NOT NULL COMMENT '订单号',
  `must_content` varchar(100) NOT NULL COMMENT '必填信息',
  `order_real_name` varchar(50) DEFAULT NULL COMMENT '下单人真实姓名',
  `order_cid` varchar(50) DEFAULT NULL COMMENT '下单人身份证号码',
  `consignee_real_name` varchar(50) DEFAULT NULL COMMENT '收货人真实姓名',
  `consignee_cid` varchar(50) DEFAULT NULL COMMENT '收货人身份证号码',
  `custom_title` varchar(50) DEFAULT NULL COMMENT '自定义信息标题',
  `custom` varchar(50) DEFAULT NULL COMMENT '自定义信息内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`)
)COMMENT='下单必填信息';



CREATE TABLE `b2c_order_refund_record` (
  `id` mediumint(12) NOT NULL AUTO_INCREMENT,
  `ret_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单退货请求id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `refund_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '退款流水号',
  `pay_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `apply_user` varchar(60) NOT NULL DEFAULT '',
  `pay_code` varchar(20) NOT NULL DEFAULT '' COMMENT '支付宝:alipay,微信：？，...',
  `refund_amount` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `mobile` varchar(32) NOT NULL DEFAULT '',
  `shop_name` varchar(50) DEFAULT '' COMMENT '店铺名称',
  `deal_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '处理状态，0:退款中，1：退款完成，2：退款失败',
  `op_deal_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '人工处理状态，0:待确认，1：已确认待退款，2：退款完成',
  `deal_status_name` varchar(64) NOT NULL DEFAULT '',
  `deal_remark` varchar(512) NOT NULL DEFAULT '',
  `trans_sn` varchar(64) NOT NULL DEFAULT '' COMMENT '转账流水号',
  `finished_time` timestamp NULL DEFAULT NULL COMMENT '出账操作时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `remark1` text COMMENT '自定义备注  建议用于存储原始数据',
  `is_offline` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否线下处理',
  PRIMARY KEY (`id`),
  UNIQUE KEY `refund_sn` (`refund_sn`),
  KEY `apply_user` (`apply_user`),
  KEY `pay_code` (`pay_code`),
  KEY `refund_amount` (`refund_amount`),
  KEY `shop_id` (`shop_id`),
  KEY `refund_time` (`refund_time`),
  KEY `ret_id` (`ret_id`)
)COMMENT='退款记录表';



CREATE TABLE `b2c_order_verifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` int(8) DEFAULT NULL COMMENT '门店id',
  `user_id` int(8) DEFAULT NULL COMMENT '用户id',
  `verify_orders` int(6) DEFAULT NULL COMMENT '核销订单数',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `store_id` (`store_id`,`user_id`)
)COMMENT='核销员表';



CREATE TABLE `b2c_package_goods_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `package_id` int(11) NOT NULL DEFAULT '0' COMMENT '一口价活动id',
  `group_id` tinyint(2) NOT NULL DEFAULT '1' COMMENT '商品组id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品id',
  `goods_number` int(11) NOT NULL DEFAULT '1' COMMENT '商品数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`,`package_id`,`group_id`)
)COMMENT='一口价已选商品';



CREATE TABLE `b2c_package_sale` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `package_name` varchar(120) DEFAULT NULL COMMENT '名称',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `total_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '结算总价格',
  `goods_group_1` tinyint(2) NOT NULL DEFAULT '0' COMMENT '分组一，1启用',
  `group_name_1` varchar(20) DEFAULT NULL COMMENT '分组名称',
  `goods_number_1` mediumint(11) NOT NULL DEFAULT '0' COMMENT '分组商品数',
  `goods_ids_1` text COMMENT '分组goodsids',
  `cat_ids_1` text COMMENT '分组平台分类id',
  `sort_ids_1` text COMMENT '分组商家分类id',
  `goods_group_2` tinyint(2) NOT NULL DEFAULT '0' COMMENT '分组二，1启用',
  `group_name_2` varchar(20) DEFAULT NULL COMMENT '分组名称',
  `goods_number_2` mediumint(11) NOT NULL DEFAULT '0' COMMENT '分组商品数',
  `goods_ids_2` text COMMENT '分组goodsids',
  `cat_ids_2` text COMMENT '分组平台分类id',
  `sort_ids_2` text COMMENT '分组商家分类id',
  `goods_group_3` tinyint(2) NOT NULL DEFAULT '0' COMMENT '分组三，1启用',
  `group_name_3` varchar(20) DEFAULT NULL COMMENT '分组名称',
  `goods_number_3` mediumint(11) NOT NULL DEFAULT '0' COMMENT '分组商品数',
  `goods_ids_3` text COMMENT '分组goodsids',
  `cat_ids_3` text COMMENT '分组平台分类id',
  `sort_ids_3` text COMMENT '分组商家分类id',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '活动状态1启用',
  `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='一口价活动';



CREATE TABLE `b2c_page_classification` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT '分类名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='页面分类';



CREATE TABLE `b2c_part_order_goods_ship` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `order_goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单商品表的id',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `batch_no` varchar(120) NOT NULL DEFAULT '',
  `goods_id` mediumint(8) NOT NULL DEFAULT '0',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `product_id` mediumint(8) NOT NULL DEFAULT '0',
  `send_number` smallint(5) NOT NULL DEFAULT '1',
  `goods_attr` text,
  `shipping_no` varchar(50) NOT NULL DEFAULT '' COMMENT '快递单号',
  `shipping_name` varchar(120) NOT NULL DEFAULT '' COMMENT '快递名称',
  `shipping_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '快递id',
  `shipping_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '部分发货时间',
  `confirm_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '订单确收收货时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`rec_id`),
  KEY `order_goods_id` (`order_goods_id`),
  KEY `order_sn` (`order_sn`),
  KEY `batch_no` (`batch_no`),
  KEY `goods_id` (`goods_id`)
)COMMENT='部分商品发货表';



CREATE TABLE `b2c_pay_award` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `activity_names` varchar(50) DEFAULT NULL COMMENT '活动名称',
  `time_type` tinyint(1) DEFAULT '0' COMMENT '时间类型0固时1永久',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束',
  `act_first` mediumint(5) DEFAULT '0' COMMENT '优先级',
  `goods_area_type` mediumint(5) DEFAULT '0' COMMENT '商品范围类型 0全部商品 1 部分商品',
  `goods_ids` text COMMENT '商品id',
  `goods_cat_ids` text COMMENT '商品平台分类',
  `goods_sort_ids` text COMMENT '商品商家分类',
  `min_pay_money` decimal(10,2) DEFAULT '0.00' COMMENT '最小金额',
  `limit_times` int(9) DEFAULT '0' COMMENT '每个用户可参加次数',
  `award_list` text COMMENT '奖品列表json',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态1停用',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '跟新时间',
  PRIMARY KEY (`id`)
)COMMENT='支付有礼 2019年10月28日 17:04:30';



CREATE TABLE `b2c_pay_award_prize` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `pay_award_id` int(9) NOT NULL DEFAULT '0' COMMENT '支付有礼活动id',
  `gift_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '奖品类型',
  `coupon_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '优惠卷',
  `score_number` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户金额',
  `lottery_id` int(11) NOT NULL DEFAULT '0' COMMENT '抽奖活动id',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格id',
  `keep_days` int(9) NOT NULL DEFAULT '0' COMMENT '赠品有效期',
  `custom_image` varchar(255) NOT NULL DEFAULT '' COMMENT '自定义图片',
  `custom_link` varchar(255) NOT NULL DEFAULT '' COMMENT '自定义链接',
  `award_number` int(11) NOT NULL DEFAULT '0' COMMENT '奖品数量',
  `send_num` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)COMMENT='支付有礼的奖品表';



CREATE TABLE `b2c_pay_award_record` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `user_id` int(9) DEFAULT NULL COMMENT '用户id',
  `award_id` int(9) DEFAULT NULL COMMENT '支付有礼活动id',
  `award_prize_id` int(9) DEFAULT NULL COMMENT '支付有礼的奖品Id',
  `order_sn` varchar(50) DEFAULT NULL COMMENT '订单号',
  `gift_type` tinyint(4) DEFAULT NULL COMMENT '礼物类型 0 无奖品 1普通优惠卷  2分裂优惠卷 3幸运大抽奖 4 余额 5 商品 6积分 7 自定义',
  `award_times` int(11) DEFAULT NULL,
  `award_data` varchar(599) DEFAULT NULL,
  `send_data` varchar(599) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `keep_days` mediumint(5) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='支付有礼记录';



CREATE TABLE `b2c_payment` (
  `id` tinyint(3) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `pay_name` varchar(80) NOT NULL DEFAULT '',
  `pay_code` varchar(20) NOT NULL DEFAULT '',
  `pay_fee` varchar(10) NOT NULL DEFAULT '0',
  `pay_desc` text,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `is_cod` tinyint(1) NOT NULL DEFAULT '0',
  `is_online_pay` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_code` (`pay_code`)
)COMMENT='支付配置表';



CREATE TABLE `b2c_payment_record` (
  `id` mediumint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `pay_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `pay_code` varchar(20) NOT NULL DEFAULT '' COMMENT '支付宝:alipay,微信：？，...',
  `pay_code_alias` varchar(20) NOT NULL DEFAULT '' COMMENT '支付宝:alipay,微信：？，...',
  `trade_no` varchar(32) NOT NULL DEFAULT '' COMMENT '各平台交易号',
  `trdae_status` tinyint(3) NOT NULL DEFAULT '-1' COMMENT '交易状态0:成功，其它失败',
  `trdae_origin_status` varchar(20) NOT NULL DEFAULT '' COMMENT '原始交易状态',
  `subject` varchar(256) NOT NULL DEFAULT '' COMMENT '商品名称',
  `quantity` mediumint(10) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `order_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '网站订单号',
  `main_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '网站主订单号',
  `total_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '交易金额',
  `buyer_id` varchar(256) NOT NULL DEFAULT '' COMMENT '买家支付用户号',
  `buyer_account` varchar(256) NOT NULL DEFAULT '' COMMENT '各平台买家支付账号',
  `seller_id` varchar(32) NOT NULL DEFAULT '' COMMENT '收款方用户号',
  `seller_account` varchar(100) NOT NULL DEFAULT '' COMMENT '各平台收款方支付账号',
  `gmt_create` timestamp NULL DEFAULT NULL COMMENT '支付交易创建时间',
  `notify_time` timestamp NULL DEFAULT NULL COMMENT '通知时间',
  `gmt_pay_time` timestamp NULL DEFAULT NULL COMMENT '交易付款时间',
  `gmt_close_time` timestamp NULL DEFAULT NULL COMMENT '交易关闭时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '插入时间',
  `remark1` text COMMENT '自定义备注  建议用于存储原始数据',
  `remark2` text COMMENT '自定义备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_sn` (`pay_sn`),
  KEY `pay_code` (`pay_code`),
  KEY `trade_no` (`trade_no`),
  KEY `order_sn` (`order_sn`),
  KEY `trdae_status` (`trdae_status`),
  KEY `seller_account` (`seller_account`)
)COMMENT='支付记录表';



CREATE TABLE `b2c_pictorial` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action` tinyint(1) NOT NULL COMMENT '海报类型： 1：普通 2 ：拼团 3：砍价 4：表单 5:拼团分享',
  `path` varchar(500) NOT NULL COMMENT '海报路径',
  `rule` text COMMENT '生成触发规则',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `identity_id` int(11) NOT NULL COMMENT '关联id： 例如：goods_id, page_id',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记： 1：删除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='海报表';



CREATE TABLE `b2c_pin_group_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `limit_amount` smallint(6) NOT NULL COMMENT '成团人数',
  `join_limit` smallint(6) NOT NULL COMMENT '参团限制',
  `open_limit` smallint(6) NOT NULL COMMENT '开团限制',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '默认成团',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `stock` smallint(6) NOT NULL DEFAULT '0' COMMENT '总库存',
  `sale_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '销量',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1：启用  0： 禁用',
  `del_time` int(11) NOT NULL DEFAULT '0',
  `activity_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '活动类型：1：普通拼团，2：老带新团',
  `is_grouper_cheap` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启团长优惠：0：不开启，1：开启',
  `reward_coupon_id` varchar(200) DEFAULT NULL COMMENT '拼团失败发放优惠券',
  `share_config` text COMMENT '分享设置',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_pin_group_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pin_activity_id` int(11) NOT NULL COMMENT '拼团活动定义id',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `group_id` int(11) NOT NULL DEFAULT '0' COMMENT '拼团id',
  `user_id` int(11) NOT NULL,
  `is_grouper` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为团长 1：是 0：否',
  `order_sn` varchar(20) NOT NULL COMMENT '订单编号',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 拼团中 1:拼团成功 2:拼团失败',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开团时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '成团时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_pin_group_product_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pin_activity_id` int(11) NOT NULL COMMENT '拼团定义id',
  `product_id` int(11) NOT NULL COMMENT '商品规格id',
  `pin_group_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团价',
  `stock` smallint(6) NOT NULL DEFAULT '0' COMMENT '库存',
  `sale_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '销量',
  `grouper_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '团长优惠价',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_pin_integration_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺id',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `inte_total` int(11) NOT NULL DEFAULT '0' COMMENT '总抽奖积分',
  `inte_group` int(11) NOT NULL DEFAULT '0' COMMENT '每个团总积分',
  `limit_amount` smallint(6) NOT NULL COMMENT '成团人数',
  `join_limit` smallint(6) NOT NULL COMMENT '参团限制',
  `divide_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '瓜分方式：0：按邀请好友数量瓜分，1：好友均分，2：随机瓜分',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态： 1：启用  0： 禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `inte_remain` int(11) NOT NULL DEFAULT '0' COMMENT '剩余积分',
  `is_day_divide` tinyint(1) NOT NULL COMMENT '是否开团24小时自动开奖',
  `param_n` float NOT NULL DEFAULT '0' COMMENT '常数n',
  `is_continue` tinyint(1) NOT NULL DEFAULT '1' COMMENT '继续： 1：继续  0： 结束',
  `advertise` varchar(100) NOT NULL COMMENT '活动宣传语',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_pin_integration_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inte_activity_id` int(11) NOT NULL COMMENT '瓜分积分活动定义id',
  `group_id` varchar(60) NOT NULL DEFAULT '' COMMENT '拼团id',
  `user_id` int(11) NOT NULL,
  `is_grouper` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为团长 1：是 0：否',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 拼团中 1:拼团成功 2:拼团失败',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '参团时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '成团时间',
  `integration` int(11) NOT NULL DEFAULT '0' COMMENT '瓜分到的积分',
  `invite_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '邀请人的数量',
  `invite_user` int(11) NOT NULL DEFAULT '0' COMMENT '邀请人（被谁邀请）',
  `is_new` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是新用户：0：不是，1：是',
  `is_look` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否看过开奖结果',
  `can_integration` int(11) NOT NULL DEFAULT '0' COMMENT '该团可瓜分积分池',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_pledge` (
  `id` int(9) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pledge_name` varchar(100) NOT NULL DEFAULT '' COMMENT '承诺名称',
  `pledge_logo` varchar(255) DEFAULT NULL COMMENT '服务承诺的图标',
  `pledge_content` varchar(500) NOT NULL DEFAULT '' COMMENT '服务承诺的说明',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '开启状态1:开启，0:关闭',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识：0未删除，1已删除',
  PRIMARY KEY (`id`)
)COMMENT='服务承诺表';



CREATE TABLE `b2c_presale` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动id',
  `presale_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '预售类型1：全款',
  `presale_name` varchar(32) NOT NULL COMMENT '预售活动名称',
  `pre_pay_step` tinyint(2) NOT NULL DEFAULT '1' COMMENT '定金期数1|2',
  `pre_start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '定金一期支付开始时间',
  `pre_end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '定金一期支付结束时间',
  `pre_start_time_2` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '定金二期支付开始时间',
  `pre_end_time_2` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '定金二期支付结束时间',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '尾款支付开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '尾款支付结束时间',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `deliver_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '发货时间模式1:deliver_time;2:deliver_days',
  `deliver_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发货日期',
  `deliver_days` int(6) NOT NULL DEFAULT '0' COMMENT '下单后几日发货',
  `discount_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '优惠策略1:可叠加0:不可叠加',
  `return_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '退定金模式1:自动退定金0:不退定金',
  `show_sale_number` int(6) NOT NULL DEFAULT '0' COMMENT '是否显示销量1:显示',
  `buy_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否支持原价1:支持',
  `buy_number` int(6) NOT NULL DEFAULT '0' COMMENT '单用户最多可购买数量',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态1:启用0:停用',
  `del_flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除状态1:删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `share_config` text COMMENT '分享设置',
  PRIMARY KEY (`id`)
)COMMENT='定金膨胀活动表';



CREATE TABLE `b2c_presale_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `presale_id` int(11) NOT NULL DEFAULT '0' COMMENT '预售id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品id',
  `presale_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预售价格',
  `presale_number` int(11) NOT NULL DEFAULT '0' COMMENT '预售商品数量',
  `sale_number` int(6) NOT NULL DEFAULT '0',
  `presale_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预售定金金额',
  `pre_discount_money_1` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预售一阶段定金抵扣金额',
  `pre_discount_money_2` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预售二阶段定金抵扣金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `presale_id` (`presale_id`,`goods_id`,`product_id`),
  KEY `presale_id_2` (`presale_id`,`product_id`)
)COMMENT='定金膨胀活动商品表';



CREATE TABLE `b2c_prize_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户编号',
  `activity_id` int(10) NOT NULL COMMENT '活动id',
  `record_id` int(10) NOT NULL COMMENT '活动记录id',
  `activity_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '奖品来源 0幸运大抽奖，1好友助力，2测评 3支付有礼',
  `prd_id` int(10) NOT NULL DEFAULT '0' COMMENT '商品（规格）编号',
  `order_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '关联订单',
  `prize_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '赠品状态:0.待领取，1：已领取，2.已过期',
  `expired_day` int(10) DEFAULT '7' COMMENT '赠品过期时间',
  `expired_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '赠品过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='我的奖品记录';



CREATE TABLE `b2c_promotion_language` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `title` varchar(32) NOT NULL COMMENT '推广语标题',
  `promotion_language` varchar(400) NOT NULL COMMENT '推广语',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `is_block` tinyint(1) DEFAULT '0' COMMENT '是否停用：0否，1是',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '是否停用：0否，1是',
  PRIMARY KEY (`id`)
)COMMENT='分销推广语';



CREATE TABLE `b2c_purchase_price_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '活动名称',
  `level` smallint(6) NOT NULL DEFAULT '0' COMMENT '优先级',
  `max_change_purchase` smallint(6) DEFAULT NULL COMMENT '最大换购数',
  `goods_id` text COMMENT '主商品',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 0: 启用 1:禁用',
  `del_flag` tinyint(4) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_purchase_price_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `purchase_price_id` int(11) NOT NULL COMMENT '加价购活动id',
  `full_price` decimal(10,2) DEFAULT NULL COMMENT '满多少钱',
  `purchase_price` decimal(10,2) DEFAULT NULL COMMENT '换购多少钱的商品',
  `product_id` text COMMENT '换购商品',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='加价购';



CREATE TABLE `b2c_rebate_price_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `data_sign` varchar(50) NOT NULL DEFAULT '' COMMENT 'md5(rebate_data)',
  `rebate_data` text COMMENT '分享内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`data_sign`)
)COMMENT='请求外部记录表';



CREATE TABLE `b2c_recommend_goods` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `recommend_name` varchar(120) NOT NULL COMMENT '推荐名称',
  `recommend_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0.全部商品1.部分商品',
  `recommend_goods_id` varchar(299) DEFAULT NULL COMMENT '推荐商品id',
  `recommend_cat_id` varchar(299) DEFAULT NULL COMMENT '推荐分类id',
  `recommend_use_page` varchar(299) NOT NULL DEFAULT '' COMMENT '推荐使用页面',
  `status` tinyint(2) DEFAULT '0' COMMENT '状态1停用',
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '1删除',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `recommend_sort_id` varchar(299) DEFAULT NULL COMMENT '推荐商家分类id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `choose_type` tinyint(2) DEFAULT '0' COMMENT '0普通推荐1智能推荐',
  `recommend_number` int(4) DEFAULT '0' COMMENT '智能推荐商品数',
  PRIMARY KEY (`id`)
)COMMENT='推荐商品';



CREATE TABLE `b2c_record_admin_action` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作员id',
  `account_id` int(11) NOT NULL DEFAULT '0' COMMENT 'sub操作员id',
  `action_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '操作类型',
  `template_id` varchar(100) NOT NULL COMMENT '模版id',
  `template_data` varchar(200) NOT NULL COMMENT '模版数据',
  `user_name` varchar(60) NOT NULL COMMENT '操作员名称',
  `mobile` varchar(32) NOT NULL COMMENT '操作员手机号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '助力时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `account_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '账户类型，默认1商家账户',
  PRIMARY KEY (`id`)
)COMMENT='操作记录表';



CREATE TABLE `b2c_reduce_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '活动名称',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束日期',
  `period_action` tinyint(1) NOT NULL DEFAULT '0' COMMENT '周期类型：0:不进行周期重复  1:每天 2:每月 3:每周',
  `point_time` varchar(20) DEFAULT NULL COMMENT '时间段',
  `extend_time` varchar(50) DEFAULT NULL COMMENT '每月第几日（单选）；每周第几天（多选，@符隔开）；',
  `batch_discount` tinyint(1) NOT NULL DEFAULT '0' COMMENT '批量打几折',
  `batch_reduce` decimal(10,2) DEFAULT NULL COMMENT '批量减多少',
  `batch_final_price` decimal(10,2) DEFAULT NULL COMMENT '批量折后价',
  `is_batch_integer` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否批量取整',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1：启用 0：禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `limit_amount` int(11) NOT NULL DEFAULT '0',
  `add_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '新建方式：0正常，1批量改价，2批量加价率',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `share_config` text COMMENT '分享设置',
  `limit_flag` tinyint(1) DEFAULT '0' COMMENT '超限购买设置标记，1禁止超限购买，0超限全部恢复原价',
  `first` tinyint(1) NOT NULL DEFAULT '1' COMMENT '优先级',
  PRIMARY KEY (`id`)
)COMMENT='限时减价活动';



CREATE TABLE `b2c_reduce_price_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reduce_price_id` int(11) NOT NULL COMMENT '限时减价活动id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `discount` decimal(10,2) DEFAULT NULL COMMENT '打几折',
  `reduce_price` decimal(10,2) DEFAULT NULL COMMENT '减多少钱',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '折后价格',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reduce_goods` (`reduce_price_id`,`goods_id`)
)COMMENT='限时减价活动商品';



CREATE TABLE `b2c_reduce_price_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reduce_price_id` int(11) NOT NULL COMMENT '限时减价活动id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `prd_id` int(11) NOT NULL COMMENT '规格id',
  `prd_price` decimal(10,2) DEFAULT NULL COMMENT '折后价格',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reduce_product` (`reduce_price_id`,`goods_id`,`prd_id`)
)COMMENT='限时减价活动商品规格';



CREATE TABLE `b2c_refund_amount_record` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(30) NOT NULL DEFAULT '',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `refund_field` varchar(20) DEFAULT NULL COMMENT '订单退款字段：member_card_balance,score_discount,money_paid,use_account',
  `refund_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `refund_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单退款时间',
  `ret_id` int(11) NOT NULL COMMENT 'b2c_return_order的ret_id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`rec_id`),
  KEY `order_sn` (`order_sn`),
  KEY `ret_id` (`ret_id`)
)COMMENT='退款日志记录，优先级卡余额、用户余额、积分抵扣、支付额';



CREATE TABLE `b2c_return_order` (
  `ret_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL DEFAULT '0',
  `order_sn` varchar(30) NOT NULL DEFAULT '',
  `return_order_sn` varchar(30) DEFAULT '' COMMENT '退款单号',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `refund_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家提交物流 或 仅退款申请，5：退款退货成功，6是拒绝退款退货,7 撤销退款、退货',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款商品金额',
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退运费金额',
  `return_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '退款类型,0:只退款，1:退货又退款',
  `reason_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '退款/退货原因类型，0：协商一致退款，1：未按约定时间发货，2：缺货，3：拍错/多拍/不想要，4：其他',
  `reason_desc` text COMMENT '退款/退货描述',
  `shipping_type` varchar(191) NOT NULL DEFAULT '' COMMENT '快递类型',
  `shipping_no` varchar(50) NOT NULL DEFAULT '' COMMENT '快递单号',
  `goods_images` text COMMENT '商品图片',
  `voucher_images` text COMMENT '凭证图片',
  `phone` varchar(12) NOT NULL DEFAULT '' COMMENT '电话号码',
  `apply_time` timestamp NULL DEFAULT NULL COMMENT '退货且退货提交审核时间，对应refund_status=1',
  `apply_pass_time` timestamp NULL DEFAULT NULL COMMENT '审核通过时间，对应refund_status=2',
  `apply_not_pass_time` timestamp NULL DEFAULT NULL COMMENT '审核未通过时间，对应refund_status=3',
  `shipping_or_refund_time` timestamp NULL DEFAULT NULL COMMENT '只退款时为退款申请时间，退货又退款时为提交物流信息时间，对应refund_status=4',
  `refund_success_time` timestamp NULL DEFAULT NULL COMMENT '退款成功时间，对应refund_status=5',
  `refund_refuse_time` timestamp NULL DEFAULT NULL COMMENT '退款拒绝时间，对应refund_status=6',
  `refund_cancel_time` timestamp NULL DEFAULT NULL COMMENT '退款撤销时间，对应refund_status=7',
  `apply_not_pass_reason` varchar(1000) DEFAULT NULL COMMENT '审核未通过原因',
  `refund_refuse_reason` varchar(1000) DEFAULT NULL COMMENT '退款拒绝原因',
  `return_address` varchar(1000) NOT NULL DEFAULT '' COMMENT '退货地址',
  `merchant_telephone` varchar(12) NOT NULL DEFAULT '' COMMENT '商家电话',
  `consignee` varchar(32) NOT NULL DEFAULT '' COMMENT '收货人',
  `zip_code` varchar(10) NOT NULL DEFAULT '' COMMENT '邮编',
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`ret_id`),
  KEY `order_sn` (`order_sn`)
)COMMENT='退回订单表';



CREATE TABLE `b2c_return_order_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `rec_id` int(11) DEFAULT NULL COMMENT '订单商品表的id',
  `ret_id` int(11) DEFAULT NULL COMMENT '退货记录表的id',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `goods_id` mediumint(8) NOT NULL DEFAULT '0',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `product_id` mediumint(8) NOT NULL DEFAULT '0',
  `goods_number` smallint(5) NOT NULL DEFAULT '1' COMMENT '退货商品数量',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_attr` text,
  `send_number` smallint(5) NOT NULL DEFAULT '0' COMMENT '发货商品数量',
  `return_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际退款金额',
  `discounted_goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际退款金额',
  `goods_img` varchar(191) NOT NULL DEFAULT '',
  `success` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0代表退货申请被拒绝，1代表正在退货中，2代表退货成功',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `rec_id` (`rec_id`),
  KEY `ret_id` (`ret_id`),
  KEY `order_sn` (`order_sn`),
  KEY `goods_id` (`goods_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='退货商品表';



CREATE TABLE `b2c_return_status_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ret_id` int(11) NOT NULL COMMENT '退货申请id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `type` tinyint(1) DEFAULT NULL COMMENT '0 商家触发   1是用户触发  2系统自动处理',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '更改状态',
  `order_sn` varchar(20) NOT NULL COMMENT '订单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `desc` text COMMENT '备注',
  PRIMARY KEY (`id`)
)COMMENT='退货退款/退款状态记录';



CREATE TABLE `b2c_search_history` (
  `user_id` int(11) NOT NULL,
  `hot_words` varchar(100) NOT NULL COMMENT '热词',
  `search_count` int(11) NOT NULL DEFAULT '1' COMMENT '搜索次数',
  `is_hot_words` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是热词搜索',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `user_id` (`user_id`),
  KEY `hot_words` (`hot_words`)
)COMMENT='搜索热词表';



CREATE TABLE `b2c_sec_kill_define` (
  `sk_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '秒杀活动id',
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `limit_amount` smallint(6) NOT NULL COMMENT '每人限购数量',
  `limit_paytime` smallint(6) NOT NULL COMMENT '规定的有效支付时间',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存',
  `sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `del_flag` tinyint(1) DEFAULT '0',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态： 1：启用  0： 禁用',
  `free_freight` tinyint(1) DEFAULT '1' COMMENT '是否免运费： 1：免运费  0： 原先商品的运费',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `card_id` text COMMENT '专属会员卡',
  `share_config` text COMMENT '分享配置',
  PRIMARY KEY (`sk_id`),
  KEY `goods_id` (`goods_id`)
)COMMENT='秒杀定义';



CREATE TABLE `b2c_sec_kill_list` (
  `sklog_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '秒杀活动商品购买记录id',
  `sk_id` int(11) NOT NULL COMMENT '秒杀活动定义id',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL COMMENT '参与秒杀活动用户id',
  `order_sn` varchar(20) NOT NULL COMMENT '订单编号',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`sklog_id`)
)COMMENT='参与秒杀活动记录';



CREATE TABLE `b2c_sec_kill_product_define` (
  `skpro_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品规格id',
  `sk_id` int(11) NOT NULL COMMENT '秒杀活动定义id',
  `product_id` int(11) NOT NULL COMMENT '商品规格id',
  `sec_kill_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '秒杀价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存',
  `sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `total_stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`skpro_id`),
  KEY `sk_id` (`sk_id`)
)COMMENT='参与秒杀规格商品价格';



CREATE TABLE `b2c_service_message_record` (
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `mobile` varchar(20) DEFAULT NULL COMMENT '用户手机号',
  `request_action` tinyint(4) NOT NULL DEFAULT '0' COMMENT '请求类型：100:短信平台',
  `identity_id` varchar(50) DEFAULT NULL COMMENT '关联其他表：如：外部请求requestid',
  `template_platform` tinyint(1) NOT NULL DEFAULT '1' COMMENT '模板平台：1： 小程序 2：公众号',
  `template_content` text COMMENT '模板内容',
  `response_code` varchar(20) DEFAULT NULL COMMENT '响应code 0:成功 >0 其他',
  `response_msg` varchar(500) DEFAULT NULL COMMENT '响应结果',
  `path` varchar(200) DEFAULT NULL COMMENT '小程序路径',
  `path_query` varchar(500) DEFAULT NULL COMMENT '小程序路径参数',
  `send_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1: 发送成功  0：未知',
  `is_visit` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已点击访问 1：是 0： 否',
  `visit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '访问时间',
  `template_type` tinyint(2) DEFAULT NULL COMMENT '模板类型 7：商家自定义',
  `link_identity` varchar(50) DEFAULT NULL COMMENT '模板消息关联id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `user_request` (`user_id`,`request_action`,`template_platform`)
)COMMENT='模板发送记录';



CREATE TABLE `b2c_service_order` (
  `order_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `store_id` int(11) NOT NULL COMMENT '门店id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 0待付款，1：待服务，2：已取消，3：已完成',
  `order_status_name` varchar(32) NOT NULL DEFAULT '' COMMENT '订单状态名称',
  `subscriber` varchar(60) NOT NULL DEFAULT '' COMMENT '预约人姓名',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机号',
  `service_id` int(11) NOT NULL DEFAULT '0' COMMENT '服务id',
  `technician_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '预约技师id',
  `technician_name` varchar(20) NOT NULL DEFAULT '' COMMENT '技师名称',
  `service_date` varchar(18) NOT NULL DEFAULT '' COMMENT '预约日期',
  `service_period` varchar(20) DEFAULT NULL COMMENT '预约时段',
  `add_message` varchar(191) DEFAULT NULL COMMENT '客户留言',
  `admin_message` varchar(191) DEFAULT NULL COMMENT '商家备注',
  `verify_code` varchar(191) NOT NULL DEFAULT '' COMMENT '核销自提码',
  `verify_admin` varchar(30) NOT NULL DEFAULT '' COMMENT '核销人',
  `pay_code` varchar(30) NOT NULL DEFAULT '' COMMENT '支付代号',
  `pay_name` varchar(120) NOT NULL DEFAULT '' COMMENT '支付名称',
  `pay_sn` varchar(32) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单应付金额',
  `discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '券抵扣金额',
  `coupon_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '优惠券id',
  `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `pay_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '支付时间',
  `cancelled_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '取消时间',
  `finished_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '订单完成时间',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `verify_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '核销方式 0是店家核销 1是用户',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '创建类型 0用户创建 1后台',
  `verify_pay` tinyint(1) NOT NULL DEFAULT '0' COMMENT '核销支付方式 0门店买单 1会员卡 2余额',
  `ali_trade_no` varchar(60) NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `member_card_no` varchar(32) DEFAULT '0' COMMENT '会员卡NO',
  `member_card_balance` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡消费金额',
  `use_account` decimal(10,2) DEFAULT '0.00' COMMENT '用户消费余额',
  PRIMARY KEY (`order_id`)
)COMMENT='服务订单表';



CREATE TABLE `b2c_service_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(50) DEFAULT NULL COMMENT '请求requestid',
  `service_name` varchar(50) NOT NULL COMMENT '服务名',
  `request_content` text COMMENT '请求内容',
  `request_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '请求时间',
  `response_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '响应时间',
  `response_content` text COMMENT '响应内容',
  `ip` varchar(100) DEFAULT NULL COMMENT '请求ip',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `request_id` (`request_id`)
)COMMENT='接口请求记录';



CREATE TABLE `b2c_service_schedule` (
  `schedule_id` tinyint(6) NOT NULL AUTO_INCREMENT COMMENT '排班id',
  `store_id` int(11) NOT NULL DEFAULT '0',
  `schedule_name` varchar(32) NOT NULL DEFAULT '' COMMENT '排班名称',
  `begcreate_time` varchar(10) NOT NULL DEFAULT '' COMMENT '开始时间',
  `end_time` varchar(10) NOT NULL DEFAULT '' COMMENT '结束时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常，1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`schedule_id`)
)COMMENT='服务技师班次表';



CREATE TABLE `b2c_service_technician` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '技师id',
  `store_id` int(11) NOT NULL DEFAULT '0',
  `technician_name` varchar(100) NOT NULL DEFAULT '' COMMENT '技师名称',
  `technician_mobile` varchar(32) NOT NULL DEFAULT '' COMMENT '技师电话',
  `bg_img_path` varchar(191) DEFAULT '' COMMENT '技师头像地址',
  `technician_introduce` varchar(200) DEFAULT '' COMMENT '技师简介',
  `group_id` int(11) DEFAULT '0' COMMENT '技师分组',
  `service_type` tinyint(2) DEFAULT '0' COMMENT '技师服务项目：0所有，1部分',
  `service_list` varchar(191) DEFAULT '[]' COMMENT '当type=1是服务项目id数组',
  `remarks` varchar(1024) NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '0正常，1删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='服务技师表';



CREATE TABLE `b2c_service_technician_group` (
  `group_id` smallint(6) NOT NULL AUTO_INCREMENT COMMENT '分组id',
  `group_name` varchar(90) NOT NULL DEFAULT '' COMMENT '分组名称',
  `store_id` mediumint(11) NOT NULL COMMENT '门店id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` smallint(1) NOT NULL DEFAULT '0' COMMENT '0使用，1删除',
  PRIMARY KEY (`group_id`)
)COMMENT='服务技师分组表';



CREATE TABLE `b2c_service_technician_schedule` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `store_id` int(11) NOT NULL DEFAULT '0',
  `technician_id` mediumint(8) NOT NULL COMMENT '技师id',
  `work_date` varchar(18) NOT NULL DEFAULT '' COMMENT '工作日期',
  `schedule_id` tinyint(6) NOT NULL DEFAULT '0' COMMENT '排班id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='服务技师排班表';



CREATE TABLE `b2c_share_award` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL COMMENT '活动名称',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `is_forever` tinyint(1) DEFAULT NULL COMMENT '是否永久，0否，1是',
  `priority` mediumint(4) DEFAULT '1' COMMENT '优先级',
  `condition` tinyint(1) NOT NULL COMMENT '触发条件：1.分享全部商品，2.分享指定商品，3.分享访问量较少商品',
  `goods_ids` varchar(200) DEFAULT NULL COMMENT '触发条件为2时：分享指定商品id列表，逗号分隔符',
  `goods_pv` int(8) DEFAULT NULL COMMENT '触发条件为3时：被分享商品访问量条件',
  `visit_first` tinyint(1) DEFAULT NULL COMMENT '仅邀请未访问过的用户有效；0否，1是',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：1停用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1删除',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `first_level_rule` varchar(500) DEFAULT NULL COMMENT '一级规则设置（json）：规则优先级从一到三依次增强，规则一满足后方可进行规则二',
  `second_level_rule` varchar(500) DEFAULT NULL COMMENT '二级规则设置（json）',
  `third_level_rule` varchar(500) DEFAULT NULL COMMENT '三级规则设置（json）',
  `first_award_num` int(10) DEFAULT '0' COMMENT '一级规则剩余奖品数',
  `second_award_num` int(10) DEFAULT '0' COMMENT '二级规则剩余奖品数',
  `third_award_num` int(10) DEFAULT '0' COMMENT '三级规则剩余奖品数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `share_name` (`name`,`del_flag`)
) ROW_FORMAT=COMPACT COMMENT='分享有礼活动记录表';



CREATE TABLE `b2c_share_award_receive` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(9) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `share_id` int(9) NOT NULL DEFAULT '0' COMMENT '活动ID',
  `goods_id` int(9) NOT NULL DEFAULT '0' COMMENT '商品ID',
  `award_level` tinyint(1) DEFAULT '0' COMMENT '领取的是几级奖励 1,2 3',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间/分享奖励领取时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `share_id` (`share_id`),
  KEY `user_share` (`user_id`,`share_id`,`goods_id`)
)COMMENT='用户领取分享奖励记录表';



CREATE TABLE `b2c_share_award_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(9) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `share_id` int(9) NOT NULL DEFAULT '0' COMMENT '活动ID',
  `goods_id` int(9) DEFAULT '0' COMMENT '商品ID',
  `user_number` int(6) DEFAULT '0' COMMENT '分享链接被查看用户数',
  `status` tinyint(1) DEFAULT '0' COMMENT '活动现在处于等级 1，2,3分别对应b2c_share_award表中的first_level_rule，second_level_rule，third_level_rule',
  `first_award` tinyint(1) DEFAULT '0' COMMENT '1级规则奖品状态 0进行中 1未领取 2已领取 3已过期',
  `second_award` tinyint(1) DEFAULT '0' COMMENT '2级规则奖品状态 0进行中 1未领取 2已领取 3已过期',
  `third_award` tinyint(1) DEFAULT '0' COMMENT '3级规则奖品状态 0进行中 1未领取 2已领取 3已过期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `share_id` (`share_id`),
  KEY `user_share` (`user_id`,`share_id`,`goods_id`)
) ROW_FORMAT=COMPACT COMMENT='用户分享记录表';



CREATE TABLE `b2c_share_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) DEFAULT NULL COMMENT '活动id或商品id',
  `user_id` int(11) DEFAULT NULL,
  `activity_type` int(11) DEFAULT NULL COMMENT '活动类型',
  `count` int(11) DEFAULT NULL COMMENT '次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='分享记录';



CREATE TABLE `b2c_share_split` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `user` mediumint(8) NOT NULL DEFAULT '0' COMMENT '分享的user_id',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '分享领取的user_id',
  `act_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '分裂优惠券id',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `share_limit` int(11) NOT NULL DEFAULT '0' COMMENT '可分享个数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
)COMMENT='分裂优惠券分享领取记录';



CREATE TABLE `b2c_shipping` (
  `shipping_id` tinyint(3) NOT NULL AUTO_INCREMENT,
  `shipping_code` varchar(20) NOT NULL DEFAULT '',
  `express100_code` varchar(20) NOT NULL DEFAULT '' COMMENT '快递100code',
  `shipping_name` varchar(120) NOT NULL DEFAULT '',
  `shipping_desc` varchar(191) NOT NULL DEFAULT '',
  `insure` varchar(10) NOT NULL DEFAULT '0',
  `support_cod` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `shipping_print` text,
  `print_model` tinyint(1) DEFAULT '0',
  `shipping_order` tinyint(3) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`shipping_id`),
  KEY `shipping_code` (`shipping_code`,`enabled`)
)COMMENT='快递配置表 `b2c_shipping`';



CREATE TABLE `b2c_shop_cfg` (
  `rec_id` smallint(5) NOT NULL AUTO_INCREMENT,
  `k` varchar(191) DEFAULT '',
  `v` text,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`rec_id`)
)COMMENT='配置信息表 店铺或平台的配置';



CREATE TABLE `b2c_sms_send_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(50) NOT NULL COMMENT '账号',
  `user_id` int(11) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `request_msg` text COMMENT '请求内容',
  `response_code` varchar(50) DEFAULT NULL COMMENT '响应码',
  `response_msg` text COMMENT '响应内容',
  `ext` varchar(20) DEFAULT NULL COMMENT '行业账号 默认:行业 market:营销,checkcode:验证码',
  `sms` varchar(20) DEFAULT NULL COMMENT '短信通道 默认短信策略:mxt',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='短信发送记录';



CREATE TABLE `b2c_sort` (
  `sort_id` int(11) NOT NULL AUTO_INCREMENT,
  `sort_name` varchar(90) NOT NULL DEFAULT '',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '分类父节点，0表示一级',
  `level` smallint(5) NOT NULL DEFAULT '0',
  `has_child` tinyint(1) NOT NULL DEFAULT '0',
  `sort_img` varchar(191) NOT NULL DEFAULT '' COMMENT '一级分类是头图 其他为分类图标',
  `img_link` varchar(191) NOT NULL DEFAULT '' COMMENT '图标或者头图链接',
  `first` smallint(2) NOT NULL DEFAULT '0' COMMENT '优先级',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0普通商家分类 1推荐分类',
  `sort_desc` varchar(191) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`sort_id`),
  KEY `parent_id` (`parent_id`)
)COMMENT='店铺自定义分类';



CREATE TABLE `b2c_spec` (
  `spec_id` int(11) NOT NULL AUTO_INCREMENT,
  `spec_name` varchar(60) NOT NULL DEFAULT '',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`spec_id`),
  UNIQUE KEY `unique_spec_name_goods_id` (`spec_name`,`goods_id`)
);



CREATE TABLE `b2c_spec_vals` (
  `spec_val_id` int(11) NOT NULL AUTO_INCREMENT,
  `spec_id` int(11) NOT NULL DEFAULT '0',
  `spec_val_name` varchar(60) NOT NULL DEFAULT '',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`spec_val_id`),
  UNIQUE KEY `unique_spec_id_spec_val_name` (`spec_id`,`spec_val_name`),
  KEY `spec_id` (`spec_id`)
)COMMENT='规格表 `b2c_spec`';



CREATE TABLE `b2c_store` (
  `store_id` int(11) NOT NULL AUTO_INCREMENT,
  `store_name` varchar(60) NOT NULL COMMENT '门店名称',
  `manager` varchar(30) NOT NULL COMMENT '负责人',
  `mobile` varchar(20) NOT NULL COMMENT '联系电话',
  `store_imgs` text COMMENT '图片',
  `business_state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '营业状态1:营业，0:关店',
  `business_type` tinyint(1) DEFAULT '1' COMMENT '营业时间1：每天，0：工作日',
  `opening_time` varchar(5) DEFAULT NULL COMMENT '开门时间',
  `close_time` varchar(5) DEFAULT NULL COMMENT '打烊时间',
  `province_code` varchar(6) DEFAULT NULL COMMENT '省',
  `city_code` varchar(6) DEFAULT NULL COMMENT '市',
  `district_code` varchar(6) DEFAULT NULL COMMENT '区',
  `latitude` varchar(16) DEFAULT NULL COMMENT '纬度',
  `longitude` varchar(16) DEFAULT NULL COMMENT '经度',
  `address` varchar(100) DEFAULT NULL COMMENT '详细地址',
  `group` int(11) DEFAULT NULL COMMENT '分组',
  `service` text COMMENT '服务',
  `content` mediumtext COMMENT '介绍',
  `pos_shop_id` int(11) NOT NULL DEFAULT '0' COMMENT 'pos店铺id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `auto_pick` smallint(1) DEFAULT '0' COMMENT '设定自提',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '1为删除状态',
  PRIMARY KEY (`store_id`)
)COMMENT='门店信息表';



CREATE TABLE `b2c_store_goods` (
  `store_id` int(11) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `prd_id` int(11) NOT NULL,
  `prd_sn` varchar(30) NOT NULL,
  `product_number` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `product_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `is_sync` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已同步',
  `is_on_sale` tinyint(1) NOT NULL DEFAULT '0' COMMENT '''是否在售，1在售，0下架''',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:初始化数据，0:无效数据',
  PRIMARY KEY (`store_id`,`goods_id`,`prd_id`,`is_on_sale`)
)COMMENT='门店商品管理';



CREATE TABLE `b2c_store_group` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(20) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `group_name` (`group_name`)
)COMMENT='门店分组';



CREATE TABLE `b2c_store_order` (
  `order_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '门店id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_status_name` varchar(32) DEFAULT NULL COMMENT '订单状态名称',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票id',
  `invoice_detail` text COMMENT '发票内容：json存储',
  `add_message` varchar(191) NOT NULL DEFAULT '' COMMENT '客户留言',
  `pay_code` varchar(30) DEFAULT NULL COMMENT '支付代号',
  `pay_name` varchar(120) DEFAULT NULL COMMENT '支付名称',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `pay_sn` varchar(32) DEFAULT NULL COMMENT '支付流水号',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单应付金额',
  `member_card_no` varchar(32) NOT NULL DEFAULT '0' COMMENT '会员卡no',
  `member_card_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡抵扣金额',
  `member_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡消费金额',
  `score_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `use_account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户消费余额',
  `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `seller_remark` varchar(512) NOT NULL DEFAULT '' COMMENT '卖家备注',
  `star_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `ali_trade_no` varchar(60) NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`order_id`)
)COMMENT='门店买单订单表';



CREATE TABLE `b2c_store_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` int(11) NOT NULL COMMENT '门店id',
  `service_name` varchar(120) NOT NULL DEFAULT '' COMMENT '服务名称',
  `service_sn` varchar(60) DEFAULT '' COMMENT '服务编码',
  `sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `service_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '服务价格',
  `service_subsist` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '预约订金',
  `cat_id` int(11) DEFAULT NULL COMMENT '服务分类',
  `service_shelf` tinyint(1) NOT NULL DEFAULT '1' COMMENT '上下架 1:上架，0:下架',
  `service_img` text COMMENT '分类主图',
  `start_date` date DEFAULT NULL COMMENT '可服务日期开始时间',
  `end_date` date DEFAULT NULL COMMENT '可服务日期结束时间',
  `start_period` varchar(10) DEFAULT NULL COMMENT '开始服务时段',
  `end_period` varchar(10) DEFAULT NULL COMMENT '结束服务时段',
  `service_duration` int(11) DEFAULT NULL COMMENT '服务时长',
  `service_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '服务类型:0无技师1有技师',
  `services_number` int(11) DEFAULT NULL COMMENT '服务数量',
  `tech_services_number` int(11) DEFAULT NULL COMMENT '技师单时段服务数量',
  `content` mediumtext COMMENT '服务描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `charge_resolve` varchar(255) DEFAULT NULL COMMENT '收费说明',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1为删除状态',
  PRIMARY KEY (`id`)
)COMMENT='门店服务信息';



CREATE TABLE `b2c_store_service_category` (
  `cat_id` smallint(5) NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(90) NOT NULL DEFAULT '',
  `store_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`cat_id`),
  UNIQUE KEY `cat_name` (`cat_name`,`store_id`)
)COMMENT='门店服务分类';



CREATE TABLE `b2c_sub_order_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sub_order_sn` varchar(20) NOT NULL COMMENT '代付订单号',
  `main_order_sn` varchar(20) NOT NULL COMMENT '主订单号',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '代付订单状态',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '付款金额',
  `refund_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `pay_code` varchar(20) DEFAULT NULL COMMENT '支付代号',
  `pay_name` varchar(100) DEFAULT NULL COMMENT '支付名称',
  `pay_sn` varchar(32) DEFAULT NULL COMMENT '支付流水号',
  `prepay_id` varchar(200) DEFAULT NULL COMMENT '微信支付id',
  `message` varchar(200) DEFAULT NULL COMMENT '留言',
  `pay_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '支付时间',
  `refund_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最近一次退款时间',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sub_order_sn` (`sub_order_sn`),
  KEY `main_order_sn` (`main_order_sn`),
  KEY `user_id` (`user_id`),
  KEY `order_status` (`order_status`)
)COMMENT='代付表';



CREATE TABLE `b2c_subscribe_message` (
  `rec_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `wx_openid` varchar(50) NOT NULL,
  `template_id` varchar(50) DEFAULT NULL COMMENT '模板ID',
  `template_no` varchar(50) DEFAULT NULL COMMENT '模板编号',
  `status` tinyint(1) DEFAULT '1' COMMENT '1 正常 0 取消授权',
  `can_use_num` int(11) DEFAULT '0' COMMENT '可使用数',
  `success_num` int(11) DEFAULT '0' COMMENT '发送成功数',
  `add_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rec_id`),
  KEY `user_id` (`user_id`),
  KEY `wx_openid` (`wx_openid`),
  KEY `template_id` (`template_id`),
  KEY `template_no` (`template_no`)
)COMMENT='小程序订阅消息';



CREATE TABLE `b2c_tag` (
  `tag_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(20) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
)COMMENT='会员标签';



CREATE TABLE `b2c_template_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '消息名称',
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '消息类型： 1： 业务处理通知 2： 商家活动通知 3： 活动加入成功提醒',
  `title` varchar(50) NOT NULL COMMENT '业务标题',
  `template_id` int(11) NOT NULL DEFAULT '0' COMMENT '选择的模板id',
  `content` text COMMENT '业务内容',
  `page_link` varchar(255) DEFAULT NULL COMMENT '页面链接',
  `send_condition` text COMMENT '筛选发送人条件',
  `to_user` text COMMENT '发送人，逗号分隔，* 代表全部',
  `send_action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发送方式： 1：立即发送  2： 持续发送  3：定时发送',
  `send_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发送完成 1： 完成',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发送起始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '发送终止时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='消息模板配置表';



CREATE TABLE `b2c_trades` (
  `ref_date` date DEFAULT NULL COMMENT '日期：例2018-09-05',
  `hour` tinyint(2) DEFAULT NULL COMMENT '小时：10时',
  `uv` int(11) DEFAULT NULL COMMENT '访客数',
  `pv` int(11) DEFAULT NULL COMMENT '访问量',
  `pay_user_num` int(11) DEFAULT NULL COMMENT '付款人数（包括货到付款发货后的状态）',
  `pay_order_num` int(11) DEFAULT NULL COMMENT '付款订单数',
  `pay_order_money` decimal(10,2) DEFAULT NULL COMMENT '付款金额',
  `pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `pct` decimal(10,2) DEFAULT NULL COMMENT '客单价',
  `uv_pay_ratio` decimal(4,2) DEFAULT NULL COMMENT '转化率',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  KEY `ref_date` (`ref_date`),
  KEY `ref_hour` (`ref_date`,`hour`)
)COMMENT='交易统计 每小时统计数据';



CREATE TABLE `b2c_trades_record` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '交易记录id',
  `trade_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易时间',
  `trade_num` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '交易额',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '交易用户id',
  `trade_content` tinyint(1) NOT NULL DEFAULT '0' COMMENT '交易内容：0：现金，1：积分',
  `trade_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '交易类型说明',
  `trade_flow` tinyint(1) NOT NULL DEFAULT '0' COMMENT '资金流向：0：收入，1：支出，2：待确认收入',
  `trade_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '交易状态：0：已入账，1：已到账',
  `trade_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '交易单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='资产变动记录';



CREATE TABLE `b2c_trades_record_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '2018-09-04',
  `type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '统计类型：1,7,30',
  `income_total_money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总现金收入',
  `outgo_money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '现金支出',
  `income_real_money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '净现金收入',
  `income_total_score` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总积分收入',
  `outgo_score` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '积分支出',
  `income_real_score` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '净积分收入',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_type` (`ref_date`,`type`)
)COMMENT='资产变动记录统计';



CREATE TABLE `b2c_uploaded_image` (
  `img_id` int(11) NOT NULL AUTO_INCREMENT,
  `img_type` varchar(60) NOT NULL DEFAULT '',
  `img_size` int(11) NOT NULL DEFAULT '0',
  `img_name` varchar(500) NOT NULL DEFAULT '' COMMENT '图片名称，用于前端显示',
  `img_orig_fname` varchar(500) NOT NULL DEFAULT '',
  `img_path` varchar(500) NOT NULL DEFAULT '',
  `img_url` varchar(500) NOT NULL DEFAULT '',
  `img_cat_id` int(11) NOT NULL DEFAULT '0' COMMENT '图片分类 默认0 用户上传为-1',
  `img_width` int(11) NOT NULL DEFAULT '0',
  `img_height` int(11) NOT NULL DEFAULT '0',
  `is_refer` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否引用',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `del_flag` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`img_id`),
  KEY `shop_id` (`shop_id`),
  KEY `img_orig_fname` (`img_orig_fname`(191))
)COMMENT='b2c_uploaded_image 上传图片表';



CREATE TABLE `b2c_uploaded_image_category` (
  `img_cat_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `img_cat_name` varchar(60) NOT NULL DEFAULT '',
  `img_cat_parent_id` int(10) NOT NULL DEFAULT '0',
  `cat_ids` varchar(191) NOT NULL DEFAULT '0' COMMENT '层级id串,逗号分隔',
  `level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '层级，0开始',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序优先级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`img_cat_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='上传图片分类';



CREATE TABLE `b2c_uploaded_video` (
  `video_id` int(11) NOT NULL AUTO_INCREMENT,
  `video_type` varchar(60) NOT NULL DEFAULT '',
  `video_size` int(11) NOT NULL DEFAULT '0',
  `video_name` varchar(191) NOT NULL DEFAULT '' COMMENT '视频名称，用于前端显示',
  `video_orig_fname` varchar(191) NOT NULL DEFAULT '',
  `video_path` varchar(500) NOT NULL DEFAULT '',
  `video_snap_path` varchar(500) NOT NULL DEFAULT '' COMMENT '视频截图',
  `video_url` varchar(500) NOT NULL DEFAULT '',
  `video_cat_id` int(11) NOT NULL DEFAULT '0' COMMENT '视频分类',
  `video_width` int(11) NOT NULL DEFAULT '0',
  `video_height` int(11) NOT NULL DEFAULT '0',
  `video_meta` varchar(500) NOT NULL DEFAULT '' COMMENT '视频信息,json',
  `is_refer` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否引用',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `duration` int(6) DEFAULT '0' COMMENT '视频时长',
  `user_id` int(11) DEFAULT '0' COMMENT '用户ID',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `upyun_del` tinyint(1) DEFAULT '0' COMMENT '又拍云是否删除',
  `video_duration` int(6) DEFAULT '0' COMMENT '视频时长',
  PRIMARY KEY (`video_id`),
  KEY `shop_id` (`shop_id`),
  KEY `video_orig_fname` (`video_orig_fname`)
)COMMENT='小视频';



CREATE TABLE `b2c_uploaded_video_category` (
  `video_cat_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `video_cat_name` varchar(60) NOT NULL DEFAULT '',
  `video_cat_parent_id` int(10) NOT NULL DEFAULT '0',
  `cat_ids` varchar(191) NOT NULL DEFAULT '0' COMMENT '层级ID串,逗号分隔',
  `level` tinyint(4) DEFAULT '0' COMMENT '层级，0开始',
  `sort` int(11) DEFAULT '1' COMMENT '排序优先级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`video_cat_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='小视频分类';



CREATE TABLE `b2c_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `user_pwd` varchar(60) NOT NULL DEFAULT '' COMMENT '密码',
  `user_cid` varchar(64) NOT NULL DEFAULT '',
  `mobile` varchar(100) DEFAULT NULL COMMENT '电话',
  `user_code` varchar(100) DEFAULT NULL COMMENT '会员卡号',
  `wx_openid` varchar(128) NOT NULL DEFAULT '',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `wechat` varchar(100) NOT NULL DEFAULT '' COMMENT '微信',
  `fanli_grade` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员级别',
  `user_grade` int(11) NOT NULL DEFAULT '1' COMMENT '会员级别',
  `invite` int(10) NOT NULL DEFAULT '0',
  `invite_source` varchar(32) DEFAULT NULL COMMENT '邀请来源:groupbuy.拼团,bargain.砍价,integral.积分,seckill.秒杀,lottery.抽奖',
  `invitation_code` varchar(32) DEFAULT NULL,
  `account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户余额',
  `discount` int(11) NOT NULL DEFAULT '0' COMMENT '折扣',
  `discount_grade` int(11) NOT NULL DEFAULT '0' COMMENT '会员折扣等级',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `growth` int(11) NOT NULL DEFAULT '0' COMMENT '成长值',
  `score` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `source` int(11) NOT NULL DEFAULT '-1' COMMENT '门店来源-1未录入0后台>0为门店',
  `invite_id` int(11) NOT NULL DEFAULT '0' COMMENT '邀请人id',
  `invite_expiry_date` date DEFAULT NULL COMMENT '邀请失效时间',
  `wx_union_id` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序unionid',
  `is_distributor` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是分销员',
  `invite_act_id` int(10) NOT NULL DEFAULT '0' COMMENT '邀请来源活动id',
  `distributor_level` tinyint(2) NOT NULL DEFAULT '1' COMMENT '用户等级',
  `ali_user_id` varchar(191) NOT NULL DEFAULT '' COMMENT '支付宝用户id',
  `device` varchar(50) DEFAULT NULL COMMENT '登录设备',
  `invite_protect_date` date DEFAULT NULL COMMENT '邀请保护时间',
  `look_collect_time` timestamp NULL DEFAULT NULL COMMENT '最近看见收藏有礼图标时间',
  `get_collect_gift` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否获得收藏好礼：0未获得，1已获得',
  `invite_group` int(6) NOT NULL DEFAULT '0' COMMENT '分销员分组',
  `unit_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '客单价',
  `invite_time` timestamp NULL DEFAULT NULL COMMENT '邀请时间',
  `scene` int(11) DEFAULT '-1' COMMENT '用户微信来源 -1搜索、公众号等入口（主动）进入，-2分享（被动）进入，-3扫码进入 -4未获取',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `mobile` (`mobile`,`shop_id`),
  KEY `user_code` (`user_code`),
  KEY `wx_union_id` (`wx_union_id`)
)COMMENT='用户';



CREATE TABLE `b2c_user_account` (
  `id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) NOT NULL DEFAULT '0',
  `admin_user` varchar(191) NOT NULL DEFAULT '0' COMMENT '操作员',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '分销订单结算产生返利',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `admin_note` varchar(191) NOT NULL DEFAULT '' COMMENT '操作员备注',
  `payment` varchar(90) NOT NULL COMMENT '支付方式',
  `is_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付类型，0：充值，1：消费',
  `remark_id` varchar(100) NOT NULL COMMENT '备注模板id',
  `remark_data` varchar(200) DEFAULT '' COMMENT '备注模板数据',
  `source` tinyint(1) DEFAULT '0' COMMENT '1:分销来源，0:充值',
  `withdraw_status` tinyint(1) DEFAULT '0' COMMENT '0未提现或部分统计1已统计',
  `settle_account` decimal(10,2) DEFAULT '0.00' COMMENT '更新后的余额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='用户余额';



CREATE TABLE `b2c_user_address` (
  `address_id` mediumint(8) NOT NULL AUTO_INCREMENT,
  `address_name` varchar(50) NOT NULL DEFAULT '',
  `user_id` mediumint(8) NOT NULL DEFAULT '0',
  `user_cid` varchar(40) NOT NULL DEFAULT '',
  `wx_openid` varchar(128) NOT NULL DEFAULT '',
  `consignee` varchar(60) NOT NULL DEFAULT '',
  `email` varchar(60) NOT NULL DEFAULT '',
  `country_code` mediumint(10) NOT NULL DEFAULT '0',
  `province_name` varchar(191) NOT NULL DEFAULT '',
  `province_code` mediumint(10) NOT NULL DEFAULT '0',
  `city_code` mediumint(10) NOT NULL DEFAULT '0',
  `city_name` varchar(191) NOT NULL DEFAULT '',
  `district_code` mediumint(10) NOT NULL DEFAULT '0',
  `district_name` varchar(191) NOT NULL DEFAULT '',
  `address` varchar(120) NOT NULL DEFAULT '',
  `complete_address` varchar(191) NOT NULL DEFAULT '',
  `zipcode` varchar(60) NOT NULL DEFAULT '',
  `tel` varchar(60) NOT NULL DEFAULT '',
  `mobile` varchar(60) NOT NULL DEFAULT '',
  `best_time` varchar(120) NOT NULL DEFAULT '',
  `is_default` tinyint(1) NOT NULL DEFAULT '0',
  `last_used_time` timestamp NULL DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `lat` varchar(20) DEFAULT NULL COMMENT '纬度',
  `lng` varchar(20) DEFAULT NULL COMMENT '经度',
  PRIMARY KEY (`address_id`),
  KEY `user_id` (`user_id`)
)COMMENT='用户地址';



CREATE TABLE `b2c_user_card` (
  `user_id` int(11) NOT NULL COMMENT '会员id',
  `card_id` int(11) NOT NULL COMMENT '会员卡id',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:正常，1:删除',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `expire_time` timestamp NULL DEFAULT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:默认会员卡',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '卡余额',
  `surplus` int(11) NOT NULL DEFAULT '0' COMMENT '卡剩余次数',
  `activation_time` timestamp NULL DEFAULT NULL COMMENT '激活时间',
  `exchang_surplus` int(11) NOT NULL DEFAULT '0' COMMENT '卡剩余兑换次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  UNIQUE KEY `card_no` (`card_no`)
)COMMENT='会员绑定的会员卡信息';



CREATE TABLE `b2c_user_cart_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `prd_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格id',
  `num` smallint(3) NOT NULL DEFAULT '1' COMMENT '件数',
  `del_flag` smallint(3) DEFAULT '0' COMMENT '0：添加，1：删除标记',
  `user_ip` varchar(64) DEFAULT NULL COMMENT '用户ip',
  `province_code` varchar(20) DEFAULT NULL COMMENT '省',
  `province` varchar(20) DEFAULT NULL COMMENT '省',
  `city_code` varchar(20) DEFAULT NULL COMMENT '市',
  `city` varchar(20) DEFAULT NULL COMMENT '市',
  `district_code` varchar(20) DEFAULT NULL COMMENT '区',
  `district` varchar(20) DEFAULT NULL COMMENT '区',
  `lat` varchar(64) DEFAULT NULL COMMENT '经度',
  `lng` varchar(64) DEFAULT NULL COMMENT '纬度',
  `count` smallint(3) DEFAULT '0' COMMENT '次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`),
  KEY `goods_create_time` (`goods_id`,`create_time`),
  KEY `user_create_time` (`user_id`,`create_time`)
)COMMENT='用户添加购物车商品记录表';



CREATE TABLE `b2c_user_collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `username` varchar(32) NOT NULL DEFAULT '' COMMENT '用户名',
  `collect_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '收藏时商品价格',
  PRIMARY KEY (`id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='用户收藏';



CREATE TABLE `b2c_user_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `username` varchar(100) DEFAULT NULL COMMENT '昵称',
  `sex` char(5) DEFAULT NULL COMMENT '性别：女f,男m',
  `birthday_year` int(4) DEFAULT NULL COMMENT '生日年份',
  `birthday_month` int(2) DEFAULT NULL,
  `birthday_day` int(2) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `province_code` mediumint(10) DEFAULT NULL COMMENT '所在地省编号',
  `city_code` mediumint(10) DEFAULT NULL COMMENT '所在地市编号',
  `district_code` mediumint(10) DEFAULT NULL COMMENT '所在地区编号',
  `address` varchar(120) DEFAULT NULL COMMENT '所在地',
  `marital_status` tinyint(1) DEFAULT NULL COMMENT '婚姻状况：1未婚，2已婚，3保密',
  `monthly_income` int(8) DEFAULT NULL,
  `cid` varchar(18) DEFAULT NULL COMMENT '身份证号码',
  `education` tinyint(1) DEFAULT NULL COMMENT '教育程度',
  `industry_info` tinyint(1) DEFAULT NULL COMMENT '所在行业',
  `big_image` varchar(191) DEFAULT NULL COMMENT '头像',
  `bank_user_name` varchar(100) DEFAULT NULL COMMENT '开户行姓名',
  `shop_bank` varchar(100) DEFAULT NULL COMMENT '开户行',
  `bank_no` varchar(32) DEFAULT NULL COMMENT '开户行卡号',
  `withdraw_passwd` varchar(64) DEFAULT NULL COMMENT '提现密码验证',
  `user_avatar` varchar(191) NOT NULL DEFAULT '/image/admin/head_icon.png' COMMENT '用户头像',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
)COMMENT='用户详情表';



CREATE TABLE `b2c_user_explain` (
  `id` smallint(5) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) DEFAULT '0' COMMENT '店铺id',
  `text` text,
  `type` tinyint(1) DEFAULT '0' COMMENT '1-余额，2-级别，3-成长值，4-积分',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`)
)COMMENT='个人中心说明管理';



CREATE TABLE `b2c_user_fanli_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `fanli_user_id` int(11) DEFAULT NULL COMMENT '邀请人id',
  `order_number` int(11) DEFAULT NULL COMMENT '累积订单数量',
  `total_can_fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计返利订单可计算返利总金额',
  `total_fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '用户累计返利佣金',
  `rebate_level` tinyint(2) DEFAULT '1' COMMENT '返利等级 0自购；1直接；2间接',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='每个子分销员数据汇总';



CREATE TABLE `b2c_user_goods_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `active_id` int(11) DEFAULT '0' COMMENT '活动id',
  `active_type` smallint(3) DEFAULT '0' COMMENT '活动类型',
  `user_ip` varchar(64) DEFAULT NULL COMMENT '用户ip',
  `province_code` varchar(20) DEFAULT NULL COMMENT '省',
  `province` varchar(20) DEFAULT NULL COMMENT '省',
  `city_code` varchar(20) DEFAULT NULL COMMENT '市',
  `city` varchar(20) DEFAULT NULL COMMENT '市',
  `district_code` varchar(20) DEFAULT NULL COMMENT '区',
  `district` varchar(20) DEFAULT NULL COMMENT '区',
  `lat` varchar(64) DEFAULT NULL COMMENT '经度',
  `lng` varchar(64) DEFAULT NULL COMMENT '纬度',
  `count` smallint(3) DEFAULT '0' COMMENT '次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`)
)COMMENT='用户访问商品记录表';



CREATE TABLE `b2c_user_import` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_id` varchar(100) DEFAULT NULL COMMENT '会员卡ID',
  `total_num` int(11) DEFAULT '0' COMMENT '总数',
  `success_num` int(11) DEFAULT '0' COMMENT '成功数',
  `tag_id` int(11) DEFAULT '0' COMMENT '标签ID',
  `group_id` int(11) DEFAULT '0' COMMENT '分销员分组ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(4) DEFAULT '0',
  `del_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='用户导入主表';



CREATE TABLE `b2c_user_import_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_id` int(11) NOT NULL COMMENT '主表ID',
  `mobile` varchar(15) DEFAULT NULL COMMENT '手机号',
  `user_action` tinyint(1) DEFAULT '1' COMMENT '1:系统导入用户 2:CRM同步用户',
  `wx_union_id` varchar(50) DEFAULT NULL,
  `nick_name` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(100) DEFAULT NULL COMMENT '用户头像',
  `user_grade` varchar(10) DEFAULT NULL COMMENT '用户等级',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `invite_user_mobile` varchar(15) DEFAULT NULL COMMENT '邀请人手机号',
  `score` int(11) DEFAULT NULL COMMENT '积分',
  `sex` char(5) DEFAULT NULL COMMENT '性别： 女f 男m',
  `birthday` varchar(15) DEFAULT NULL COMMENT '生日',
  `province` varchar(10) DEFAULT NULL COMMENT '省',
  `city` varchar(10) DEFAULT NULL COMMENT '市',
  `district` varchar(10) DEFAULT NULL COMMENT '区',
  `address` varchar(100) DEFAULT NULL COMMENT '地址',
  `id_number` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `education` varchar(50) DEFAULT NULL COMMENT '教育程度',
  `industry` varchar(50) DEFAULT NULL COMMENT '所在行业',
  `marriage` varchar(50) DEFAULT NULL COMMENT '婚姻状况',
  `income` decimal(10,2) DEFAULT NULL COMMENT '月收入',
  `error_msg` varchar(100) DEFAULT NULL COMMENT '错误内容',
  `card_id` varchar(100) DEFAULT NULL COMMENT '会员卡ID',
  `tag_id` int(11) DEFAULT '0' COMMENT '标签ID',
  `group_id` int(11) DEFAULT '0' COMMENT '分销员分组ID',
  `is_activate` tinyint(1) DEFAULT '0' COMMENT '是否已激活',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_distributor` tinyint(1) DEFAULT '0' COMMENT '是否是分销员',
  PRIMARY KEY (`id`),
  KEY `mobile` (`mobile`),
  KEY `batch_id` (`batch_id`)
)COMMENT='用户导入明细表';



CREATE TABLE `b2c_user_login_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '登陆用户id',
  `user_ip` varchar(64) DEFAULT NULL COMMENT '用户登录ip',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '每日登陆次数',
  `province_code` varchar(20) DEFAULT NULL COMMENT '省',
  `province` varchar(20) DEFAULT NULL COMMENT '省',
  `city_code` varchar(20) DEFAULT NULL COMMENT '市',
  `city` varchar(20) DEFAULT NULL COMMENT '市',
  `district_code` varchar(20) DEFAULT NULL COMMENT '区',
  `district` varchar(20) DEFAULT NULL COMMENT '区',
  `lat` varchar(64) DEFAULT NULL COMMENT '经度',
  `lng` varchar(64) DEFAULT NULL COMMENT '纬度',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`),
  KEY `district_create_time` (`create_time`,`district_code`)
)COMMENT='用户登录记录表,每小时存一条';



CREATE TABLE `b2c_user_promotion_language` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `lan_id` int(8) NOT NULL DEFAULT '0' COMMENT '推广语关联ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='用户默认分销推广语';



CREATE TABLE `b2c_user_rebate_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '产品id',
  `advice_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '分销价格',
  `expire_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
)COMMENT='分销改价价格表';



CREATE TABLE `b2c_user_rfm_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '统计日期，如2018-09-04，按照下单时间',
  `recency_type` tinyint(2) NOT NULL COMMENT '最近一次消费时间类型（小达顺序，左闭右开）：1最近5天内，2最近5到10天，3最近10到30天，4最近30到90天，5最近90到180天，6最近180到365天，7最近365天以上',
  `frequency_type` tinyint(2) NOT NULL COMMENT '最近时间范围内用户消费频次类型：1，2，3，4，5大于等于5次',
  `total_paid_money` decimal(10,2) DEFAULT NULL COMMENT '总成交金额',
  `pay_user_num` int(11) DEFAULT '0' COMMENT '下单人数（已付款订单人数）',
  `in_time` datetime DEFAULT NULL,
  `up_time` datetime DEFAULT NULL,
  `order_num` int(11) DEFAULT '0' COMMENT '订单数量（已付款订单数）',
  PRIMARY KEY (`id`),
  KEY `ref_date` (`ref_date`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_user_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未使用 1:已使用 2：已过期 3：已退款',
  `flow_no` varchar(20) DEFAULT NULL COMMENT '积分流水号',
  `usable_score` int(11) NOT NULL DEFAULT '0' COMMENT '可用积分',
  `identity_id` varchar(500) DEFAULT NULL COMMENT '关联其他属性：例如order_sn',
  `goods_id` int(11) NOT NULL DEFAULT '0',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `desc` varchar(191) NOT NULL DEFAULT '',
  `remark_id` varchar(100) NOT NULL COMMENT '备注模板id',
  `remark_data` varchar(200) DEFAULT '' COMMENT '备注模板数据',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_time` timestamp NULL DEFAULT NULL,
  `admin_user` varchar(191) NOT NULL DEFAULT '0' COMMENT '操作员',
  PRIMARY KEY (`id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='用户积分表';



CREATE TABLE `b2c_user_score_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `score_name` varchar(20) NOT NULL COMMENT '购买:buy,评价:comment,兑换:convert',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未启用,1:启用',
  `two_status` tinyint(1) NOT NULL DEFAULT '0',
  `set_val` varchar(10) NOT NULL DEFAULT '',
  `set_val2` varchar(10) NOT NULL DEFAULT '',
  `set_val3` text,
  `sign_val` int(11) NOT NULL DEFAULT '0' COMMENT '签到积分',
  `sign_date` tinyint(1) NOT NULL DEFAULT '0' COMMENT '签到天数',
  `desc` varchar(191) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `growth_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:不送成长值，1：送成长值',
  PRIMARY KEY (`id`)
)COMMENT='积分设置';



CREATE TABLE `b2c_user_summary_trend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '2018-09-04',
  `type` tinyint(2) NOT NULL COMMENT '1，7，30',
  `login_data` int(11) NOT NULL COMMENT '访问会员数据',
  `user_data` int(11) NOT NULL COMMENT '累积会员数',
  `coupon_data` int(11) NOT NULL COMMENT '领券会员数',
  `cart_data` int(11) NOT NULL COMMENT '加购会员数',
  `reg_user_data` int(11) NOT NULL COMMENT '注册数',
  `upgrade_user_data` int(11) NOT NULL COMMENT '升级会员数',
  `charge_user_data` int(11) NOT NULL COMMENT '储值会员数',
  `order_user_data` int(11) NOT NULL COMMENT '成交客户数',
  `new_order_user_data` int(11) NOT NULL COMMENT '成交新客户数',
  `old_order_user_data` int(11) NOT NULL COMMENT '成交老客户数',
  `total_paid_money` decimal(10,2) DEFAULT NULL COMMENT '总成交金额',
  `new_paid_money` decimal(10,2) DEFAULT NULL COMMENT '成交新客户支付金额',
  `old_paid_money` decimal(10,2) DEFAULT NULL COMMENT '成交老客户支付金额',
  `pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `new_pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `old_pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '统计时间',
  `pay_order_num` int(11) DEFAULT '0' COMMENT '成交订单数',
  `login_pv` int(11) DEFAULT '0' COMMENT '登录pv',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '下单笔数',
  `order_user_num` int(11) DEFAULT '0' COMMENT '下单人数(生成订单就算)',
  PRIMARY KEY (`id`),
  KEY `ref_type` (`ref_date`,`type`)
) ROW_FORMAT=COMPACT;



CREATE TABLE `b2c_user_tag` (
  `user_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_id`,`tag_id`),
  UNIQUE KEY `user_tag` (`user_id`,`tag_id`)
)COMMENT='会员关联标签表';



CREATE TABLE `b2c_user_total_fanli` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '会员id',
  `mobile` varchar(16) DEFAULT '' COMMENT '会员手机号',
  `sublayer_number` int(11) DEFAULT '0' COMMENT '子层分销员数量',
  `total_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计获得佣金数',
  `can_money` decimal(10,2) DEFAULT '0.00' COMMENT '可用佣金余额',
  `blocked` decimal(10,2) DEFAULT '0.00' COMMENT '冻结佣金余额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
)COMMENT='每个分销员统计信息';



CREATE TABLE `b2c_virtual_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_status_name` varchar(32) NOT NULL DEFAULT '' COMMENT '订单状态名称',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票id',
  `invoice_detail` text COMMENT '发票内容：json存储',
  `add_message` varchar(191) NOT NULL DEFAULT '' COMMENT '客户留言',
  `pay_code` varchar(30) DEFAULT NULL COMMENT '支付代号',
  `pay_name` varchar(120) DEFAULT NULL COMMENT '支付名称',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
  `pay_sn` varchar(32) DEFAULT NULL COMMENT '支付流水号',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户消费现金',
  `use_account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户消费余额',
  `use_score` int(11) DEFAULT '0' COMMENT '用户消费积分',
  `member_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡消费金额',
  `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `pay_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `seller_remark` varchar(512) NOT NULL DEFAULT '' COMMENT '卖家备注',
  `star_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
  `ali_trade_no` varchar(60) NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
  `return_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未申请退款，1：退款失败，2：退款成功',
  `return_score` int(11) DEFAULT '0' COMMENT '已退款积分',
  `return_account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款余额',
  `return_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款现金',
  `return_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款会员卡余额',
  `return_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `goods_type` tinyint(2) NOT NULL COMMENT '虚拟商品类别：0：会员卡，1：优惠券',
  `virtual_goods_id` int(11) NOT NULL COMMENT '虚拟商品id',
  `card_no` varchar(32) DEFAULT NULL COMMENT '下单使用的会员卡号',
  `still_send_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '优惠券礼包订单-退款后是否继续发放优惠劵，1：继续发放，0：停止发放',
  `access_mode` tinyint(1) DEFAULT '0' COMMENT '优惠券礼包订单-下单时的领取方式，0：现金购买，1：积分购买，2直接领取',
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  PRIMARY KEY (`order_id`),
  KEY `order_sn` (`order_sn`),
  KEY `user_id` (`user_id`)
)COMMENT='虚拟商品订单订单表';



CREATE TABLE `b2c_virtual_order_refund_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(30) NOT NULL DEFAULT '',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `use_score` int(11) NOT NULL DEFAULT '0' COMMENT '退款积分',
  `use_account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款余额',
  `money_paid` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款现金',
  `member_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款会员卡余额',
  `refund_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单退款时间',
  `is_success` tinyint(1) NOT NULL DEFAULT '0' COMMENT '处理状态，1：退款失败，2：退款成功',
  PRIMARY KEY (`id`),
  KEY `order_sn` (`order_sn`),
  KEY `user_id` (`user_id`)
)COMMENT='虚拟商品订单退款记录';



CREATE TABLE `b2c_wx_shopping_recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `order_sn` int(11) DEFAULT NULL COMMENT '订单ordersn',
  `click_num` int(11) NOT NULL DEFAULT '1' COMMENT '点击次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `goods_id` (`goods_id`)
)COMMENT='好物推荐';



CREATE TABLE `b2c_wxp_unlimit_code` (
  `code_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '小程序码id',
  `scene_id` varchar(32) NOT NULL COMMENT '对应b2c_wxp_unlimit_scene的scene_id',
  `code_page` varchar(191) DEFAULT NULL COMMENT '必须是已经发布的小程序存在的页面（否则报错），例如 pages/index/index, 根路径前不要填加 /,不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面',
  `code_url` varchar(191) DEFAULT NULL COMMENT '小程序码url',
  `code_path` varchar(191) DEFAULT NULL COMMENT '小程序码本地地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0',
  `del_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '删除时间',
  PRIMARY KEY (`code_id`),
  KEY `scene_id` (`scene_id`,`code_page`)
)COMMENT='unlimit小程序码';



CREATE TABLE `b2c_wxp_unlimit_scene` (
  `scene_id` int(10) NOT NULL AUTO_INCREMENT,
  `scene_value` varchar(1200) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`scene_id`),
  KEY `scene_value` (`scene_value`(191))
)COMMENT='unlimit小程序码scene值保存表';



CREATE TABLE `b2c_xcx_customer_page` (
  `page_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `page_name` varchar(60) NOT NULL DEFAULT '',
  `page_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为首页1为首页，0非首页',
  `page_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `page_tpl_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '模板类型:0自定义模板，1默认模板，2美女模板，3自定义首页',
  `page_content` longtext COMMENT '页面内容，json格式存储',
  `page_publish_content` longtext COMMENT '正式页面内容，json格式存储',
  `page_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0未发布，1已发布',
  `cat_id` int(11) NOT NULL DEFAULT '0' COMMENT '页面分类id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`page_id`)
)COMMENT='微信小程序自定义页面表';

