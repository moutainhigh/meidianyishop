

CREATE TABLE `b2c_activity_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '2018-09-18',
  `type` tinyint(2) NOT NULL COMMENT '1，7，30',
  `activity_type` tinyint(2) NOT NULL COMMENT '活动类型',
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `shop_version` varchar(11) NOT NULL COMMENT '店铺版本',
  `currently` int(11) DEFAULT NULL COMMENT '进行中的活动',
  `expired` int(11) DEFAULT NULL COMMENT '过期中的活动',
  `closed` int(11) DEFAULT NULL COMMENT '关闭中的活动（截止到统计时间）',
  `order_num` int(11) DEFAULT NULL COMMENT '活动订单',
  `order_money` decimal(10,2) DEFAULT NULL COMMENT '活动订单金额',
  `visited` int(11) DEFAULT NULL COMMENT '活动访问用户数',
  `visited_user` int(11) DEFAULT NULL COMMENT '访问用户数',
  `join_user` int(11) DEFAULT NULL COMMENT '参与用户数',
  `success_user` int(11) DEFAULT NULL COMMENT '成功用户数',
  `share_user` int(11) DEFAULT NULL COMMENT '分享用户数',
  `used_user` int(11) DEFAULT NULL COMMENT '使用用户数',
  `bargain_user_count` int(11) DEFAULT NULL COMMENT '砍价人次',
  `new_user` int(11) DEFAULT NULL COMMENT '拉新数',
  PRIMARY KEY (`id`)
)COMMENT='活动记录';



CREATE TABLE `b2c_app` (
  `app_id` varchar(20) NOT NULL,
  `app_name` varchar(60) NOT NULL DEFAULT '' COMMENT '应用名称',
  `app_secret` varchar(255) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `app_id` (`app_id`),
  UNIQUE KEY `app_name` (`app_name`)
)COMMENT='应用表';



CREATE TABLE `b2c_app_auth` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `sys_id` int(11) NOT NULL DEFAULT '0',
    `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
    `app_id` varchar(20) COMMENT '对接类型',
    `session_key` varchar(191) NOT NULL DEFAULT '' COMMENT '授权key,最后一个s字符后面的字符串表示店铺id',
    `request_location` varchar(512) COMMENT '对方接口请求地址',
    `request_protocol` tinyint(1) NOT NULL DEFAULT 1 COMMENT '对方请求协议 0 http，1 https',
    `status` tinyint(1) DEFAULT 1 COMMENT '1：启用 0：禁用',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `session_key` (`session_key`)
) COMMENT='应用授权表';

CREATE TABLE `b2c_article` (
  `article_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL DEFAULT '1' COMMENT '文章分类',
  `title` varchar(256) DEFAULT NULL,
  `author` varchar(50) DEFAULT NULL,
  `keyword` varchar(256) DEFAULT NULL COMMENT '标签',
  `desc` varchar(1024) DEFAULT NULL COMMENT '文章描述',
  `content` text,
  `is_recommend` tinyint(1) DEFAULT '0' COMMENT '1:推荐',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '1:置顶',
  `status` tinyint(1) DEFAULT '0' COMMENT '0未发布,1已发布',
  `pub_time` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_visit_time` datetime DEFAULT NULL,
  `pv` int(11) DEFAULT NULL,
  `head_pic` varchar(191) DEFAULT NULL COMMENT '头图',
  PRIMARY KEY (`article_id`),
  KEY `is_recommend` (`is_recommend`),
  KEY `is_top` (`is_top`)
)COMMENT='帮助中心文章';



CREATE TABLE `b2c_article_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(191) NOT NULL DEFAULT '',
  `use_footer_nav` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否用于底部导航',
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`category_id`)
)COMMENT='文章分类表';



CREATE TABLE `b2c_article_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '阅读文章id',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sys_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='文章阅读记录';



CREATE TABLE `b2c_back_process` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `process_id` int(11) NOT NULL DEFAULT '0' COMMENT '进程ID',
  `job_name` varchar(255) NOT NULL DEFAULT '' COMMENT '任务名称',
  `class_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类名称',
  `parameters` text COMMENT '任务执行时，所需参数数组,serialize存储',
  `state` tinyint(1) DEFAULT '0' COMMENT '进程状态，0初始，1执行中，2完成，3失败',
  `fail_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  `only_run_one` tinyint(1) DEFAULT '0' COMMENT '是否只允许一个进程存在，按照class_name，static_method联合查',
  `progress` smallint(4) DEFAULT '0' COMMENT '执行进度',
  `progress_info` varchar(255) NOT NULL DEFAULT '' COMMENT '当前进度信息',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '进程结束时间',
  `job_code` int(11) NOT NULL DEFAULT '0' COMMENT '执行结果码，成功0，其他非0',
  `job_message` varchar(255) NOT NULL DEFAULT '' COMMENT '错误信息',
  `job_result` text COMMENT '执行结果,serialize存储',
  `memo` text COMMENT '备注',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`rec_id`),
  KEY `created` (`created`),
  KEY `shop_class` (`shop_id`,`class_name`(191))
)COMMENT='后台进程执行信息表';



CREATE TABLE `b2c_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `k` varchar(100) DEFAULT '',
  `v` text,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `k` (`k`)
)COMMENT='key value缓存表';



CREATE TABLE `b2c_category` (
  `cat_id` int(11) NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(90) DEFAULT '' COMMENT '分类名称',
  `keywords` varchar(191) DEFAULT '' COMMENT '关键词',
  `cat_desc` varchar(191) DEFAULT '' COMMENT '分类描述',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '父ID',
  `level` smallint(5) NOT NULL DEFAULT '0' COMMENT '层级',
  `has_child` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是子节点',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `cat_img` varchar(191) NOT NULL DEFAULT '' COMMENT '分类图标',
  `first` smallint(2) NOT NULL DEFAULT '0' COMMENT '优先级',
  PRIMARY KEY (`cat_id`),
  KEY `parent_id` (`parent_id`)
)COMMENT='移动端只支持分类一级';



CREATE TABLE `b2c_charge_renew` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `apply_id` int(11) NOT NULL COMMENT '申请用户的ID',
  `apply_name` varchar(50) DEFAULT '' COMMENT '申请用户名称',
  `sys_id` int(11) NOT NULL COMMENT '所属账号的ID',
  `shop_name` varchar(50) DEFAULT '' COMMENT '店铺名称',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `apply_mod` varchar(50) DEFAULT '' COMMENT '点击模块',
  `apply_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '申请类型 0升级 1续费',
  `contact` tinyint(1) NOT NULL DEFAULT '0' COMMENT '申请类型 0未联系 1已联系',
  `desc` text COMMENT '备注',
  PRIMARY KEY (`id`)
)COMMENT='升级续费记录';



CREATE TABLE `b2c_cron_define` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `class_name` varchar(128) NOT NULL COMMENT '定时任务完整类名',
  `expression` varchar(32) NOT NULL COMMENT 'cron表达式',
  `description` varchar(64) NOT NULL DEFAULT '' COMMENT '任务描述',
  `result` tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行结果,0:待执行;1:执行中；2已完成；3:执行失败',
  `retries_num` tinyint(1) NOT NULL DEFAULT '0' COMMENT '失败重试次数,默认0不重试',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态,1:启用;0:停用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cron_key` (`class_name`)
) COMMENT='定时任务定义表';



CREATE TABLE `b2c_cron_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `cron_id` int(11) NOT NULL COMMENT '定时任务id',
  `execute_num` tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行次数（小于等于失败重试次数）',
  `failed_reason` varchar(512) NOT NULL DEFAULT '' COMMENT '最后一次执行失败原因',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id` (`cron_id`)
) COMMENT='定时任务执行结果记录表';



CREATE TABLE `b2c_db_option_record` (
  `record_id` int(9) NOT NULL AUTO_INCREMENT,
  `version` varchar(10) DEFAULT '' COMMENT '版本号',
  `option_rst` longtext COMMENT '执行结果',
  `db_type` varchar(32) DEFAULT '' COMMENT '更新类型：mian主库，shop从库',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID，单个店铺更新时使用',
  `in_time` timestamp NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `up_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `option_type` varchar(32) DEFAULT '' COMMENT '操作类型：command命令行操作，SQL语句操作',
  `total_num` int(6) DEFAULT '0' COMMENT '总更新条数',
  `success_num` int(6) DEFAULT '0' COMMENT '成功条数',
  PRIMARY KEY (`record_id`)
)COMMENT='数据库更新日志表';



CREATE TABLE `b2c_decoration_template` (
  `page_id` int(10) NOT NULL AUTO_INCREMENT,
  `page_name` varchar(60) NOT NULL DEFAULT '',
  `page_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `page_content` longtext COMMENT '页面内容，json格式存储',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `page_img` varchar(1000) DEFAULT '' COMMENT '装修页面封图',
  PRIMARY KEY (`page_id`)
)COMMENT='装修模板表';



CREATE TABLE `b2c_dict_city` (
  `city_id` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `province_id` int(11) NOT NULL,
  `postcode` varchar(20) NOT NULL DEFAULT '',
  `short_name` varchar(32) NOT NULL DEFAULT '',
  `pinyin` varchar(128) NOT NULL DEFAULT '',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`city_id`),
  KEY `IX_dict_city_modified` (`modified`),
  KEY `province_id` (`province_id`)
);



CREATE TABLE `b2c_dict_country` (
  `country_id` int(11) NOT NULL,
  `en_short_name` varchar(60) NOT NULL,
  `name` varchar(60) NOT NULL,
  `short_name` varchar(20) NOT NULL,
  `en_name` varchar(60) NOT NULL DEFAULT '',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`country_id`)
);



CREATE TABLE `b2c_dict_district` (
  `district_id` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  `city_id` int(11) NOT NULL,
  `postcode` varchar(20) NOT NULL DEFAULT '',
  `pinyin` varchar(60) NOT NULL DEFAULT '',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`district_id`),
  KEY `IX_dict_district_modified` (`modified`),
  KEY `city_id` (`city_id`)
);



CREATE TABLE `b2c_dict_province` (
  `province_id` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  `country_id` int(11) NOT NULL DEFAULT '0',
  `area_id` int(11) NOT NULL DEFAULT '0',
  `short_name` varchar(45) NOT NULL DEFAULT '',
  `pinyin` varchar(64) NOT NULL DEFAULT '',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`province_id`),
  KEY `IX_dict_province_modified` (`modified`),
  KEY `country_id` (`country_id`)
);

CREATE TABLE `b2c_external_request_history`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `app_id` varchar(20) NOT NULL COMMENT '对接类型',
    `shop_id` int(11) NOT NULL COMMENT '店铺id',
    `service_name` varchar(128) COMMENT '请求服务方法',
    `error_code` int(11) DEFAULT '0' COMMENT '请求结果状态码',
    `request_param` text COMMENT '',
    `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`)
) COMMENT='外部接口请求记录表';

CREATE TABLE `b2c_failed_jobs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)COMMENT='用户登录记录表';



CREATE TABLE `b2c_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `goods_id` int(8) NOT NULL,
  `shop_id` int(11) DEFAULT '0' COMMENT '店铺ID',
  `cat_id` int(5) NOT NULL DEFAULT '0',
  `goods_sn` varchar(60) NOT NULL DEFAULT '',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `brand_id` int(11) NOT NULL DEFAULT '0' COMMENT '品牌ID',
  `goods_ad` varchar(1024) DEFAULT '' COMMENT '广告词',
  `goods_number` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `goods_weight` decimal(10,3) DEFAULT '0.000',
  `market_price` decimal(10,2) DEFAULT '0.00',
  `shop_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `cost_price` decimal(10,2) DEFAULT '0.00' COMMENT '成本价',
  `goods_desc` text,
  `goods_img` varchar(191) DEFAULT '',
  `is_on_sale` tinyint(1) DEFAULT '1' COMMENT '是否在售，1在售，0下架',
  `is_delete` tinyint(1) DEFAULT '0',
  `goods_type` tinyint(2) DEFAULT '0' COMMENT '商品类型，0普通商品，1团购商品，2秒杀商品,3积分商品',
  `deliver_template_id` int(5) DEFAULT '0' COMMENT '运费模板ID',
  `goods_sale_num` int(8) DEFAULT '0' COMMENT '销售数量',
  `goods_collect_num` int(8) DEFAULT '0' COMMENT '收藏数量',
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `state` tinyint(1) DEFAULT '0' COMMENT '审核状态,0待审核 1 审核通过 2 违规下架',
  `reason` text COMMENT '违规下架原因',
  `sub_account_id` int(11) DEFAULT '0' COMMENT '子帐号ID，主要用于官方店铺',
  `sale_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `limit_buy_num` int(11) DEFAULT '0' COMMENT '最少起购数量，0不限购',
  `limit_max_num` int(11) DEFAULT '0' COMMENT '最多起购数量，0不限购',
  `unit` varchar(60) DEFAULT '' COMMENT '商品单位',
  `add_sale_num` int(11) DEFAULT '0' COMMENT '虚假销量',
  `sale_type` tinyint(1) DEFAULT '0' COMMENT '上架状态,0立即上架， 1审核通过 2 加入仓库',
  `sort_id` int(5) NOT NULL DEFAULT '0',
  `goods_video` varchar(191) DEFAULT '' COMMENT '视频',
  `goods_video_img` varchar(191) DEFAULT '' COMMENT '视频首图',
  `goods_video_size` int(11) DEFAULT NULL COMMENT '视频尺寸',
  `goods_video_id` int(11) DEFAULT NULL COMMENT '视频ID',
  `goods_page_id` int(11) NOT NULL DEFAULT '0' COMMENT '详情页装修模板ID',
  `is_page_up` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在文本区域上方',
  `is_card_exclusive` tinyint(1) DEFAULT '0' COMMENT '是否会员卡专属',
  `base_sale` int(8) DEFAULT '0' COMMENT '初始销量',
  `source` tinyint(1) DEFAULT '0' COMMENT '商品来源,0：店铺自带；1、2..等：不同类型店铺第三方抓取自带商品来源',
  `is_control_price` tinyint(1) DEFAULT '0' COMMENT '是否控价：0不控价，1控价（不可修改价格）',
  `can_rebate` tinyint(1) DEFAULT '0' COMMENT '是否分销改价',
  `deliver_flag` tinyint(1) DEFAULT '0' COMMENT '混批标记，0：默认不支持，1：支持',
  PRIMARY KEY (`id`),
  UNIQUE KEY `goods_id` (`goods_id`,`shop_id`),
  UNIQUE KEY `goods_sn` (`goods_sn`,`shop_id`),
  KEY `goods_id_2` (`goods_id`),
  KEY `shop_id` (`shop_id`),
  KEY `cat_id` (`cat_id`)
)COMMENT='商品主库';



CREATE TABLE `b2c_goods_brand` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(500) NOT NULL COMMENT '品牌名称',
  `e_name` varchar(500) NOT NULL DEFAULT '' COMMENT '品牌英文名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌Logo',
  `first` tinyint(3) NOT NULL DEFAULT '0' COMMENT '优先级',
  `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录修改时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0为未删除 1为删除',
  `desc` text COMMENT '品牌介绍',
  PRIMARY KEY (`id`)
)COMMENT='商品品牌';



CREATE TABLE `b2c_goods_img` (
  `img_id` int(8) NOT NULL AUTO_INCREMENT,
  `goods_id` int(8) NOT NULL DEFAULT '0',
  `img_url` varchar(191) NOT NULL DEFAULT '',
  `img_desc` varchar(191) NOT NULL DEFAULT '',
  PRIMARY KEY (`img_id`),
  KEY `goods_id` (`goods_id`)
)COMMENT='商品图片表 `b2c_goods_img`';



CREATE TABLE `b2c_goods_spec_product` (
  `prd_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `goods_id` int(10) NOT NULL DEFAULT '0',
  `prd_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `prd_market_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '市场价',
  `prd_cost_price` decimal(10,2) DEFAULT '0.00' COMMENT '成本价',
  `prd_number` int(11) NOT NULL DEFAULT '1' COMMENT '当前规格组合产品库存',
  `prd_sn` varchar(65) NOT NULL DEFAULT '' COMMENT '商家编码',
  `prd_codes` varchar(500) NOT NULL DEFAULT '' COMMENT '商品条码',
  `prd_specs` varchar(1024) NOT NULL DEFAULT '',
  `prd_desc` varchar(1024) NOT NULL DEFAULT '' COMMENT '规格描述，格式例子：颜色:红色 尺码:S',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `self_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:商家自己添加商品，其他没用',
  `low_shop_price` varchar(1024) NOT NULL DEFAULT '0.00' COMMENT '最低售出价格',
  `prd_img` varchar(1024) NOT NULL DEFAULT '' COMMENT '图片地址',
  PRIMARY KEY (`prd_id`),
  KEY `gsp_goods_id` (`goods_id`),
  KEY `gsp_goods_codes` (`prd_codes`(191)),
  KEY `gsp_prd_sn` (`prd_sn`)
)COMMENT='商品规格组合的产品表 `b2c_goods_spec_product`';



CREATE TABLE `b2c_grasp_goods` (
  `goods_id` int(8) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) DEFAULT '0' COMMENT '店铺ID',
  `cat_id` int(5) NOT NULL DEFAULT '0',
  `goods_sn` varchar(60) NOT NULL DEFAULT '',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `brand_id` int(11) NOT NULL DEFAULT '0' COMMENT '品牌ID',
  `goods_ad` varchar(1024) DEFAULT '' COMMENT '广告词',
  `goods_number` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `goods_weight` decimal(10,3) DEFAULT '0.000',
  `market_price` decimal(10,2) DEFAULT '0.00',
  `shop_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `cost_price` decimal(10,2) DEFAULT '0.00' COMMENT '成本价',
  `goods_desc` text,
  `goods_img` varchar(191) DEFAULT '',
  `is_on_sale` tinyint(1) DEFAULT '1' COMMENT '是否在售，1在售，0下架',
  `is_delete` tinyint(1) DEFAULT '0',
  `goods_type` tinyint(2) DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品',
  `deliver_template_id` int(5) DEFAULT '0' COMMENT '运费模板ID',
  `goods_sale_num` int(8) DEFAULT '0' COMMENT '销售数量',
  `goods_collect_num` int(8) DEFAULT '0' COMMENT '收藏数量',
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `state` tinyint(1) DEFAULT '0' COMMENT '审核状态,0待审核 1 审核通过 2 违规下架',
  `reason` text COMMENT '违规下架原因',
  `sub_account_id` int(11) DEFAULT '0' COMMENT '子帐号ID，主要用于官方店铺',
  `sale_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `limit_buy_num` int(11) DEFAULT '0' COMMENT '最少起购数量，0不限购',
  `unit` varchar(60) DEFAULT '' COMMENT '商品单位',
  `add_sale_num` int(11) DEFAULT '0' COMMENT '虚假销量',
  `limit_max_num` int(11) DEFAULT '0' COMMENT '最多起购数量，0不限购',
  `sale_type` tinyint(1) DEFAULT '0' COMMENT '上架状态,0立即上架， 1审核通过 2 加入仓库',
  `sort_id` int(11) DEFAULT '0',
  `goods_video` varchar(191) DEFAULT '' COMMENT '视频',
  `goods_video_img` varchar(191) DEFAULT '' COMMENT '视频首图',
  `goods_video_size` int(11) DEFAULT NULL COMMENT '视频尺寸',
  `goods_video_id` int(11) DEFAULT NULL COMMENT '视频ID',
  `goods_page_id` int(11) NOT NULL DEFAULT '0' COMMENT '详情页装修模板ID',
  `is_page_up` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在文本区域上方',
  `is_card_exclusive` tinyint(1) DEFAULT '0' COMMENT '是否会员卡专属',
  `base_sale` int(8) DEFAULT '0' COMMENT '初始销量',
  `on_sale_shop_id` text COMMENT '发布店铺',
  `source` tinyint(1) DEFAULT '0' COMMENT '商品来源,0：店铺自带；1、2..等：不同类型店铺第三方抓取自带商品来源',
  `is_control_price` tinyint(1) DEFAULT '0' COMMENT '是否控价,0：不控价；1：控价',
  `goods_flag` tinyint(1) DEFAULT '1' COMMENT '商品来源平台1：欧派；2：寺库',
  UNIQUE KEY `goods_id` (`goods_id`,`shop_id`),
  UNIQUE KEY `goods_sn` (`goods_sn`,`shop_id`),
  KEY `goods_id_2` (`goods_id`),
  KEY `shop_id` (`shop_id`),
  KEY `cat_id` (`cat_id`)
)COMMENT='第三方抓取商品表 `b2c_grasp_goods`';

CREATE TABLE `b2c_inquiry_order` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `order_id` int(11) NOT NULL COMMENT '订单id',
    `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
    `order_sn` varchar(20)  NOT NULL DEFAULT '' COMMENT '订单编号',
    `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
    `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态0待付款 1待接诊 2接诊中 3已完成 4已退款 5已取消 6待退款 7部分退款',
    `doctor_id` int(11) NOT NULL DEFAULT '0' COMMENT '医师id',
    `doctor_name` varchar(32)  NOT NULL DEFAULT '' COMMENT '医师名称',
    `department_id` int(11) NOT NULL DEFAULT '0' COMMENT '科室id',
    `department_name` varchar(32)  NOT NULL DEFAULT '' COMMENT '医师科室',
    `patient_id` int(11) NOT NULL DEFAULT '0' COMMENT '患者id',
    `patient_mobile` varchar(32) NOT NULL DEFAULT '' COMMENT '患者手机号码',
    `patient_name` varchar(32)  NOT NULL DEFAULT '' COMMENT '患者名称',
    `patient_sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别 0：未知 1：男 2：女',
    `patient_birthday` date DEFAULT NULL COMMENT '出生年月',
    `patient_identity_code` varchar(64)  NOT NULL DEFAULT '' COMMENT '证件号码',
    `patient_identity_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '证件类型: 1：身份证 2：军人证 3：护照 4：社保卡',
    `pay_code` varchar(30)  NOT NULL DEFAULT '' COMMENT '支付代号',
    `pay_name` varchar(128)  NOT NULL DEFAULT '' COMMENT '支付名称',
    `pay_sn` varchar(32)  NOT NULL DEFAULT '' COMMENT '支付流水号',
    `prepay_id` varchar(128)  DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
    `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
    `pay_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '支付时间',
    `refund_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款金额',
    `limit_time` timestamp  NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '医生接诊后会话截止时间点',
    `cancelled_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '取消时间',
    `finished_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '订单完成时间',
    `description_disease` varchar(512)  NOT NULL  DEFAULT '' COMMENT '病情描述',
    `image_url` text  COMMENT '病情描述image信息',
    `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    KEY `shop_id` (`shop_id`),
    KEY `order_sn` (`order_sn`)
) COMMENT='问诊订单表';

CREATE TABLE `b2c_jobs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `queue` varchar(191) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) NOT NULL,
  `reserved_at` int(10) DEFAULT NULL,
  `available_at` int(10) NOT NULL,
  `created_at` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `jobs_queue_index` (`queue`)
);



CREATE TABLE `b2c_log_manage` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `error_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '错误等级：0正常，1debug，2info，3error',
  `error_code` varchar(8) NOT NULL DEFAULT '0' COMMENT '错误编码',
  `error_msg` varchar(128) NOT NULL DEFAULT '' COMMENT '错误说明',
  `error_content` text COMMENT '错误内容',
  `error_source` tinyint(2) NOT NULL DEFAULT '0' COMMENT '错误来源：0无，1erp，2crm，3pos，4寺库，5欧派，...',
  `deal_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '处理结果：0待处理，1已处理',
  `action` varchar(64) NOT NULL DEFAULT '' COMMENT '动作方法',
  `action_name` varchar(64) NOT NULL DEFAULT '' COMMENT '动作方法名称',
  `request_content` text COMMENT '请求内容 json串',
  `response_content` text COMMENT '响应内容 json串',
  `ip` varchar(100) DEFAULT NULL COMMENT '请求ip',
  `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
  `deal_person` varchar(32) DEFAULT NULL COMMENT '处理人员',
  `in_time` datetime DEFAULT NULL COMMENT '添加时间',
  `up_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(600) DEFAULT NULL COMMENT '备注',
  `identity_id` int(11) DEFAULT '0' COMMENT '关联外部表',
  PRIMARY KEY (`log_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='日志管理表';

-- 营销日历表
CREATE TABLE `b2c_market_calendar` (
    `id` INT ( 8 ) NOT NULL AUTO_INCREMENT,
    `event_name` VARCHAR ( 64 ) NOT NULL DEFAULT '' COMMENT '事件名称',
    `event_time` date DEFAULT NULL COMMENT '事件时间',
    `event_desc` text COMMENT '事件说明',
    `pub_flag` TINYINT ( 1 ) NOT NULL DEFAULT '0' COMMENT '发布状态：0未发布，1已发布',
    `del_flag` TINYINT ( 1 ) NOT NULL DEFAULT '0' COMMENT '是否已删除：0否，1是',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY ( `id` )
)COMMENT='营销日历表';


-- 营销日历表对应活动
CREATE TABLE `b2c_market_calendar_activity` (
    `id` INT ( 8 ) NOT NULL AUTO_INCREMENT,
    `calendar_id` INT ( 8 ) NOT NULL DEFAULT '0' COMMENT '营销日历Id',
    `activity_type` VARCHAR ( 16 ) NOT NULL DEFAULT '0' COMMENT '具体营销活动类型',
    `shop_use_num` INT ( 8 ) NOT NULL DEFAULT '0' COMMENT '店铺使用累计数量',
    `recommend_type` TINYINT ( 1 ) NOT NULL DEFAULT '0' COMMENT '推荐类型：0站内文本，1外部链接',
    `recommend_link` VARCHAR ( 100 ) NOT NULL DEFAULT '' COMMENT '推荐链接',
    `del_flag` TINYINT ( 1 ) NOT NULL DEFAULT '0' COMMENT '是否已删除：0否，1是',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `shop_ids` VARCHAR ( 500 ) NOT NULL DEFAULT '' COMMENT '使用该推荐活动的店铺id，逗号隔开',
    `recommend_title` VARCHAR ( 32 ) NOT NULL DEFAULT '' COMMENT '推荐标题',
    PRIMARY KEY ( `id` ),
    KEY `calendar_id` ( `calendar_id` )
)COMMENT='营销日历表对应活动';

CREATE TABLE `b2c_mp_auth_shop` (
  `app_id` varchar(191) NOT NULL COMMENT '授权小程序appId',
  `shop_id` int(11) NOT NULL,
  `nick_name` varchar(191) DEFAULT '' COMMENT '小程序昵称',
  `user_name` varchar(191) DEFAULT '' COMMENT '授权方小程序的原始ID',
  `alias` varchar(191) DEFAULT '' COMMENT '授权方小程序所设置的微信号，可为空',
  `verify_type_info` varchar(191) DEFAULT '' COMMENT '授权方认证类型，-1代表未认证，0代表微信认证',
  `head_img` varchar(191) DEFAULT '' COMMENT '头像URL',
  `func_info` text COMMENT '权限集',
  `is_auth_ok` tinyint(1) DEFAULT '1' COMMENT '是否授权成功,如果小程序后台取消授权，则为0',
  `last_auth_time` timestamp NULL DEFAULT NULL COMMENT '最后成功授权的时间',
  `last_cancel_auth_time` timestamp NULL DEFAULT NULL COMMENT '最后取消授权的时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `qrcode_url` varchar(191) DEFAULT NULL COMMENT '二维码图片的URL',
  `open_pay` tinyint(1) DEFAULT '0' COMMENT '是否开头微信支付',
  `open_card` tinyint(1) DEFAULT '0' COMMENT '是否开通微信卡券功能',
  `authorizer_info` text COMMENT '授权者信息,json存储',
  `authorization_info` text COMMENT '授权详情,json存储',
  `pay_mch_id` varchar(191) DEFAULT NULL COMMENT '支付商户号',
  `pay_key` varchar(191) DEFAULT NULL COMMENT '支付密钥',
  `pay_cert_content` text COMMENT '支付证书内容',
  `pay_key_content` text COMMENT '支付私钥内容',
  `is_modify_domain` tinyint(1) DEFAULT '0' COMMENT '是否修改开发和业务域名，0未修改，1已修改',
  `bind_template_id` int(11) DEFAULT '0' COMMENT '绑定小程序的模板ID',
  `upload_state` tinyint(1) DEFAULT '0' COMMENT '上传状态，0未上传，1已上传',
  `last_upload_time` timestamp NULL DEFAULT NULL COMMENT '上传代码时间',
  `audit_id` bigint(64) DEFAULT '0' COMMENT '最新的审核ID',
  `audit_state` tinyint(1) DEFAULT '0' COMMENT '审核状态，0未提交，1审核中，2审核成功 3审核失败',
  `submit_audit_time` timestamp NULL DEFAULT NULL COMMENT '提交代码审核时间',
  `audit_ok_time` timestamp NULL DEFAULT NULL COMMENT '审核通过时间',
  `audit_fail_reason` varchar(191) DEFAULT '' COMMENT '未通过审核原因',
  `publish_state` tinyint(1) DEFAULT '0' COMMENT '通过审核的小程序发布状态，0未发布，1已发布',
  `publish_time` timestamp NULL DEFAULT NULL COMMENT '程序发布时间',
  `tester` text COMMENT '小程序体验者列表,json存储',
  `test_qr_path` varchar(191) DEFAULT NULL COMMENT '小程序体验二维码图片路径',
  `category` text COMMENT '小程序可选类目,json存储',
  `page_cfg` text COMMENT '小程序页面配置,json存储',
  `principal_name` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序的主体名称',
  `bind_open_app_id` varchar(191) NOT NULL COMMENT '绑定开放平台appId',
  `link_official_app_id` VARCHAR(191) DEFAULT NULL COMMENT '关联公众号appId，用于发送模板消息',
  `is_sub_merchant` tinyint(1) NOT NULL DEFAULT '0' COMMENT '子商户模式,0：非子商户 1：微铺宝子商户 2：通联支付子商户',
  `union_pay_app_id` varchar(191) NOT NULL DEFAULT '' COMMENT '通联支付子商户appId',
  `union_pay_cus_id` varchar(191) NOT NULL DEFAULT '' COMMENT '通联支付子商户商户号',
  `union_pay_app_key` varchar(191) NOT NULL DEFAULT '' COMMENT '通联支付子商户密钥',
  `fee_type` varchar(191) DEFAULT 'CNY' COMMENT '标价币种，国际支付字段',
  `merchant_category_code` varchar(191) NOT NULL DEFAULT '' COMMENT 'MCC码，国际支付字段',
  `live_pack_status` TINYINT(1) NULL DEFAULT 0 COMMENT '直播包状态 1：通过 2：打包审核中',
  PRIMARY KEY (`app_id`),
  UNIQUE KEY `shop_id` (`shop_id`),
  KEY `principal_name` (`principal_name`),
  KEY `bind_template_id` (`bind_template_id`),
  KEY `audit_state` (`audit_state`),
  KEY `is_auth_ok` (`is_auth_ok`),
  KEY `link_official_app_id` (`link_official_app_id`)
)COMMENT='小程序授权信息';



CREATE TABLE `b2c_mp_daily_retain` (
  `ref_date` char(8) NOT NULL COMMENT '时间，如："20180313"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
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
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `session_cnt_middle` int(11) NOT NULL DEFAULT '0' COMMENT '打开次数',
  `visit_pv_middle` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `visit_uv_middle` int(11) NOT NULL DEFAULT '0' COMMENT '访问人数',
  `visit_uv_new_middle` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `stay_time_uv_middle` float NOT NULL DEFAULT '0' COMMENT '人均停留时长 (浮点型，单位：秒)',
  `stay_time_session_middle` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 (浮点型，单位：秒)',
  `visit_depth_middle` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 (浮点型)',
  `stay_time_uv_total` float NOT NULL DEFAULT '0' COMMENT '停留时长 总和(浮点型)',
  `stay_time_session_total` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 总和(浮点型)',
  `visit_depth_total` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 总和(浮点型)'
)COMMENT='访问趋势';



CREATE TABLE `b2c_mp_deploy_history` (
  `deploy_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `bind_template_id` int(11) NOT NULL COMMENT '小程序模板Id',
  `app_id` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序app_id',
  `deploy_log` text COMMENT '小程序模板部署日志',
  `deploy_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '小程序模板添加时间',
  `is_modify_domain` tinyint(1) DEFAULT '0' COMMENT '是否修改开发和业务域名，0未修改，1已修改',
  `upload_state` tinyint(1) DEFAULT '0' COMMENT '上传状态，0未上传，1已上传',
  `last_upload_time` timestamp NULL DEFAULT NULL COMMENT '上传代码时间',
  `audit_id` bigint(64) DEFAULT '0' COMMENT '最新的审核ID',
  `audit_state` tinyint(1) DEFAULT '0' COMMENT '审核状态，0未提交，1审核中，2审核成功 3审核失败',
  `submit_audit_time` timestamp NULL DEFAULT NULL COMMENT '提交代码审核时间',
  `audit_ok_time` timestamp NULL DEFAULT NULL COMMENT '审核通过时间',
  `audit_fail_reason` varchar(191) DEFAULT NULL COMMENT '未通过审核原因',
  `publish_state` tinyint(1) DEFAULT '0' COMMENT '通过审核的小程序发布状态，0未发布，1已发布',
  `publish_time` timestamp NULL DEFAULT NULL COMMENT '程序发布时间',
  `tester` text COMMENT '小程序体验者列表,json存储',
  `test_qr_path` varchar(191) DEFAULT NULL COMMENT '小程序体验二维码图片路径',
  `category` text COMMENT '小程序可选类目,json存储',
  `page_cfg` text COMMENT '小程序页面配置,json存储',
  PRIMARY KEY (`deploy_id`),
  UNIQUE KEY `bind_template_id` (`bind_template_id`,`app_id`)
)COMMENT='小程序部署历史';



CREATE TABLE `b2c_mp_distribution_visit` (
  `ref_date` char(8) NOT NULL COMMENT '时间，如："20180313"',
  `list` text COMMENT '存入所有类型的指标情况',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`)
)COMMENT='访问分布';



CREATE TABLE `b2c_mp_jump_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` varchar(64) NOT NULL,
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:申请中，1:已提交',
  `add_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='小程序跳转链接，申请发布新版本列表';



CREATE TABLE `b2c_mp_monthly_retain` (
  `ref_date` char(6) NOT NULL COMMENT '时间，如："201803"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
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
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
)COMMENT='月趋势';



CREATE TABLE `b2c_mp_official_account` (
  `app_id` varchar(191) NOT NULL COMMENT '授权公众号appId',
  `nick_name` varchar(191) DEFAULT '' COMMENT '小程序昵称',
  `user_name` varchar(191) DEFAULT '' COMMENT '授权方小程序的原始ID',
  `alias` varchar(191) DEFAULT '' COMMENT '授权方小程序所设置的微信号，可为空',
  `verify_type_info` varchar(191) DEFAULT '' COMMENT '授权方认证类型，-1代表未认证，0代表微信认证',
  `head_img` varchar(191) DEFAULT '' COMMENT '头像URL',
  `func_info` text COMMENT '权限集',
  `is_auth_ok` tinyint(1) DEFAULT '1' COMMENT '是否授权成功,如果公众号后台取消授权，则为0',
  `last_auth_time` timestamp NULL DEFAULT NULL COMMENT '最后成功授权的时间',
  `last_cancel_auth_time` timestamp NULL DEFAULT NULL COMMENT '最后取消授权的时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `open_pay` tinyint(1) DEFAULT '0' COMMENT '是否开头微信支付',
  `open_card` tinyint(1) DEFAULT '0' COMMENT '是否开通微信卡券功能',
  `authorizer_info` text COMMENT '授权者信息,json存储',
  `authorization_info` text COMMENT '授权详情,json存储',
  `pay_mch_id` varchar(191) DEFAULT NULL COMMENT '支付商户号',
  `pay_key` varchar(191) DEFAULT NULL COMMENT '支付密钥',
  `pay_cert_content` text COMMENT '支付证书内容',
  `pay_key_content` text COMMENT '支付私钥内容',
  `principal_name` varchar(191) DEFAULT '' COMMENT '公众号的主体名称',
  `account_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '公众号类型：0 订阅号, 1微信认证订阅号 2 服务号, 3微信认证服务号',
  `bind_open_app_id` varchar(191) DEFAULT NULL COMMENT '绑定开放平台appId',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '系统账户ID',
  `qrcode_url` varchar(500) DEFAULT NULL COMMENT '二维码图片的URL',
  PRIMARY KEY (`app_id`),
  KEY `principal_name` (`principal_name`),
  KEY `create_time` (`create_time`)
)COMMENT='公众号列表';



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



CREATE TABLE `b2c_mp_operate_log` (
  `operate_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `app_id` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序app_id',
  `template_id` int(11) NOT NULL COMMENT '小程序模板Id',
  `operate_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作类型',
  `memo` text COMMENT '操作日志',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `memo_id` varchar(100) NOT NULL,
  `memo_list` varchar(100) NOT NULL,
  `operate_state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作状态:1成功 2失败',
  PRIMARY KEY (`operate_id`),
  KEY `app_id` (`app_id`),
  KEY `operate_type` (`operate_type`),
  KEY `template_id` (`template_id`)
)COMMENT='小程序操作日志';



CREATE TABLE `b2c_mp_summary_trend` (
  `ref_date` char(8) NOT NULL COMMENT '日期',
  `visit_total` int(11) NOT NULL DEFAULT '0' COMMENT '总访问量',
  `share_pv` int(11) NOT NULL DEFAULT '0' COMMENT '转发次数',
  `share_uv` int(11) NOT NULL DEFAULT '0' COMMENT '转发人数',
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `visit_total_middle` int(11) NOT NULL DEFAULT '0' COMMENT '总访问量 中位数',
  `share_pv_middle` int(11) NOT NULL DEFAULT '0' COMMENT '转发次数 中位数',
  `share_uv_middle` int(11) NOT NULL DEFAULT '0' COMMENT '转发人数 中位数'
)COMMENT='分享统计';



CREATE TABLE `b2c_mp_summary_trend_shop` (
  `ref_date` char(8) NOT NULL COMMENT '日期 如： "20180313"',
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `visit_total` int(11) NOT NULL DEFAULT '0' COMMENT '总访问量',
  `share_pv` int(11) NOT NULL DEFAULT '0' COMMENT '转发次数',
  `share_uv` int(11) NOT NULL DEFAULT '0' COMMENT '转发人数',
  `session_cnt` int(11) NOT NULL DEFAULT '0' COMMENT '打开次数',
  `visit_pv` int(11) NOT NULL DEFAULT '0' COMMENT '访问次数',
  `visit_uv` int(11) NOT NULL DEFAULT '0' COMMENT '访问人数',
  `visit_uv_new` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `stay_time_uv` float NOT NULL DEFAULT '0' COMMENT '人均停留时长 (浮点型，单位：秒)',
  `stay_time_session` float NOT NULL DEFAULT '0' COMMENT '次均停留时长 (浮点型，单位：秒)',
  `visit_depth` float NOT NULL DEFAULT '0' COMMENT '平均访问深度 (浮点型)',
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`)
)COMMENT='按店铺统计访问量';



CREATE TABLE `b2c_mp_user_portrait` (
  `shop_id` int(11) DEFAULT '0' COMMENT '店铺ID',
  `ref_date` char(30) NOT NULL COMMENT '时间： 如： "20180313"',
  `visit_uv_new` longtext COMMENT '新用户',
  `visit_uv` longtext COMMENT '活跃用户',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:昨天，1：最近7天，2:30天',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `type` (`type`),
  KEY `ref_date` (`ref_date`)
)COMMENT='平台店铺数据统计';



CREATE TABLE `b2c_mp_version` (
  `template_id` int(11) NOT NULL COMMENT '小程序模板Id',
  `user_version` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序模板版本号',
  `user_desc` varchar(191) DEFAULT '' COMMENT '小程序模板版本描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '小程序模板添加时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `current_in_use` tinyint(1) NOT NULL DEFAULT '0' COMMENT '当前使用的版本',
  `source_miniprogram_appid` varchar(191) DEFAULT '' COMMENT '小程序开发appid',
  `source_miniprogram` varchar(191) DEFAULT '' COMMENT '小程序开发名称',
  `developer` varchar(191) DEFAULT '' COMMENT '开发者',
  `package_version` tinyint(1) DEFAULT '1' COMMENT '包版本 1：正常 2：包含好物推荐',
  PRIMARY KEY (`template_id`)
)COMMENT='小程序模板版本信息';



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
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  KEY `ref_date` (`ref_date`),
  KEY `page_path` (`page_path`(191)),
  KEY `page_visit_pv` (`page_visit_pv`)
)COMMENT='访问页面';



CREATE TABLE `b2c_mp_weekly_retain` (
  `ref_date` char(20) NOT NULL COMMENT '时间，如："20180306-20180312"',
  `visit_uv_new` text COMMENT '新增用户留存',
  `visit_uv` text COMMENT '活跃用户留存',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
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
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间'
)COMMENT='周趋势';



CREATE TABLE `b2c_order_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rec_id` mediumint(8) NOT NULL,
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
  `purchase_price_id` int(11) DEFAULT '0' COMMENT '加价购ID',
  `purchase_price_rule_id` int(11) DEFAULT '0' COMMENT '换购挡位ID',
  `reduce_price_id` int(11) DEFAULT '0' COMMENT '限时降价ID',
  `fanli_strategy` varchar(299) DEFAULT '' COMMENT '返利比例',
  `fanli_percent` decimal(10,2) DEFAULT '0.00' COMMENT '返利比例',
  `cost_price` decimal(10,2) DEFAULT '0.00' COMMENT '成本价',
  `is_card_exclusive` tinyint(1) DEFAULT '0' COMMENT '是否会员卡专属',
  `promote_info` varchar(500) DEFAULT NULL COMMENT '推广信息',
  `gift_id` int(11) DEFAULT '0' COMMENT '赠品ID',
  `is_can_return` tinyint(1) DEFAULT '1' COMMENT '是否可退款',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `order_sn` (`order_sn`),
  KEY `goods_id` (`goods_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单商品表  b2c_order_goods';



CREATE TABLE `b2c_order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` mediumint(8) NOT NULL COMMENT '订单ID',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `main_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '主订单编号(拆单时用)',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `user_openid` varchar(191) NOT NULL DEFAULT '' COMMENT '用户openid',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_status_name` varchar(32) NOT NULL DEFAULT '' COMMENT '订单状态名称',
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收件人姓名',
  `address_id` int(11) NOT NULL DEFAULT '0' COMMENT '地址ID',
  `country_code` mediumint(10) DEFAULT '0' COMMENT '国家编号',
  `country_name` varchar(50) NOT NULL DEFAULT '' COMMENT '国家名称',
  `province_code` mediumint(10) DEFAULT '0' COMMENT '省份编号',
  `province_name` varchar(50) NOT NULL DEFAULT '' COMMENT '省份名称',
  `city_code` mediumint(10) DEFAULT '0' COMMENT '城市编号',
  `city_name` varchar(120) NOT NULL DEFAULT '' COMMENT '城市名称',
  `district_code` mediumint(10) DEFAULT '0' COMMENT '区县编号',
  `district_name` varchar(120) NOT NULL DEFAULT '' COMMENT '区县名称',
  `address` varchar(191) NOT NULL DEFAULT '' COMMENT '更多详细地址',
  `complete_address` varchar(512) NOT NULL DEFAULT '' COMMENT '完整收件地址',
  `zipcode` varchar(60) NOT NULL DEFAULT '' COMMENT '邮编',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机号',
  `add_message` varchar(191) DEFAULT '' COMMENT '客户留言',
  `shipping_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '快递ID',
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
  `package_discount` decimal(10,2) DEFAULT '0.00' COMMENT '一口价抵扣金额',
  `dapei_id` int(8) NOT NULL DEFAULT '0' COMMENT '搭配ID来源',
  `add_time` timestamp NULL DEFAULT NULL COMMENT '订单提交时间',
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
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',
  `shipping_no` varchar(191) NOT NULL DEFAULT '' COMMENT '快递单号',
  `shipping_type` varchar(60) NOT NULL DEFAULT '' COMMENT '快递类型',
  `is_cod` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否货到付款',
  `return_type_cfg` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否支持退换货：1支持 2不支持',
  `return_days_cfg` tinyint(1) NOT NULL DEFAULT '7' COMMENT '发货后自动确认收货时间,单位天',
  `order_timeout_days` smallint(3) NOT NULL DEFAULT '3' COMMENT '确认收货后自动订单完成时间,单位天',
  `seller_remark` varchar(512) NOT NULL DEFAULT '' COMMENT '卖家备注',
  `erpordercode` varchar(32) NOT NULL DEFAULT '' COMMENT 'ERP中订单代码',
  `comment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未评论，1:已评论，2：已晒单',
  `fanli_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员id',
  `fanli_grade` varchar(64) NOT NULL DEFAULT '' COMMENT '返利等级',
  `fanli_percent` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返利百分比',
  `settlement_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票Id',
  `invoice_content` int(11) DEFAULT '0' COMMENT '发票类型：0普通发票；1增值税专票',
  `invoice_title` text COMMENT '发票内容：json存储',
  `refund_status` tinyint(1) DEFAULT '0' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家再次提交申请，5：退款退货成功，6是拒绝退款退货',
  `pay_order_sn` varchar(30) DEFAULT '' COMMENT '对账单号',
  `goods_type` varchar(50) DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购',
  `order_source` tinyint(2) DEFAULT NULL COMMENT '订单来源，0pc，1wap，2app',
  `fanli_type` tinyint(2) DEFAULT NULL COMMENT '返利类型，0普通订单，1三级分销返利订单，2返利会员返利订单',
  `manual_refund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1代表手动退款，0代表非手动',
  `order_pay_way` tinyint(2) DEFAULT '0' COMMENT '订单付款方式，0全款 1定金 2补款',
  `bk_order_sn` varchar(20) DEFAULT '' COMMENT '补款订单号 order_pay_way=1时有效',
  `bk_order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补款金额 order_pay_way=1时有效',
  `bk_order_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '补款金额是否支付 order_pay_way=1时有效，0未支付，1已支付',
  `pin_goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前拼团商品金额，阶梯团根据人数时会变化，补款也随之变化',
  `pin_yj_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团支付佣金金额',
  `pin_group_id` int(11) NOT NULL DEFAULT '0' COMMENT '拼团ID',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
  `source` varchar(30) DEFAULT '' COMMENT '订单来源，记录app，wap，pc来源',
  `part_ship_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:，1:部分发货',
  `promotion_id` int(11) NOT NULL DEFAULT '0' COMMENT '促销活动Id',
  `promotion_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '促销活动折扣金额,满折满减',
  `push_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'yadu推送状态：0-暂无推送，1-推送失败，2-推送成功',
  `push_desc` varchar(100) NOT NULL DEFAULT '' COMMENT 'yadu推送失败原因',
  `pos_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '门店订单标志：0：商城，1：门店同步订单',
  `pos_shop_name` varchar(191) NOT NULL DEFAULT '' COMMENT 'pos店铺名称',
  `store_id` int(11) DEFAULT '0' COMMENT '门店ID',
  `store_name` varchar(191) DEFAULT '' COMMENT '门店名称',
  `member_card_id` int(11) DEFAULT '0' COMMENT '会员卡ID',
  `member_card_reduce` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡优惠金额',
  `member_card_balance` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡消费金额',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '订单支付过期时间',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '订单删除时间',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付Id，用于发送模板消息',
  `deliver_type` tinyint(1) DEFAULT '0' COMMENT '配送类型：0 快递 1 自提',
  `deliver_type_name` varchar(191) DEFAULT NULL COMMENT '配送类型名称',
  `pickup_time` varchar(30) DEFAULT NULL COMMENT '自提时间',
  `star_flag` tinyint(1) DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
  `verify_code` varchar(191) DEFAULT '' COMMENT '核销自提码',
  `split` int(11) DEFAULT '0' COMMENT '分裂优惠券id',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '返利金额',
  `true_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(32) NOT NULL DEFAULT '' COMMENT '身份证号18位',
  `ali_trade_no` varchar(60) DEFAULT '' COMMENT '支付宝交易单号',
  `grouper_cheap_reduce` decimal(10,2) DEFAULT '0.00' COMMENT '团长优惠金额',
  `bk_shipping_time` timestamp NULL DEFAULT NULL COMMENT '定金预计发货时间',
  `bk_return_type` tinyint(2) DEFAULT NULL COMMENT '定金退款状态',
  `bk_prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付Id，用于发送模板消息',
  `pre_sale_discount` decimal(10,2) DEFAULT '0.00' COMMENT '定金膨胀优惠金额',
  `instead_pay_money` decimal(10,2) DEFAULT '0.00' COMMENT '代付金额',
  `order_user_message` varchar(50) DEFAULT NULL COMMENT '发起人留言',
  `instead_pay` text COMMENT '好友代付规则',
  `instead_pay_num` smallint(6) DEFAULT '0' COMMENT '代付人数',
  `is_promote` tinyint(1) DEFAULT '0' COMMENT '是否是推广单',
  `verifier_id` int(9) DEFAULT '0' COMMENT '核销员',
  `exchang` tinyint(2) DEFAULT '0' COMMENT '1 兑换 0否',
  `free_ship` decimal(10,2) DEFAULT '0.00' COMMENT '运费抵扣',
  `free_detail` text COMMENT '运费抵扣规则',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`),
  KEY `main_order_sn` (`main_order_sn`),
  KEY `user_id` (`user_id`),
  KEY `user_openid` (`user_openid`),
  KEY `order_status` (`order_status`),
  KEY `shipping_id` (`shipping_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单';

CREATE TABLE  `b2c_order_info_new` (
    `order_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '订单id',
    `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
    `order_sn` varchar(20)   NOT NULL DEFAULT '' COMMENT '订单编号',
    `main_order_sn` varchar(20)   NOT NULL DEFAULT '' COMMENT '主订单编号(拆单时用)',
    `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户id',
    `user_openid` varchar(191)   NOT NULL DEFAULT '' COMMENT '用户openid',
    `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
    `order_status_name` varchar(32)   NOT NULL DEFAULT '' COMMENT '订单状态名称',
    `consignee` varchar(60)   NOT NULL DEFAULT '' COMMENT '收件人姓名',
    `address_id` int(11) NOT NULL DEFAULT '0' COMMENT '地址id',
    `country_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '国家编号',
    `country_name` varchar(50)   NOT NULL DEFAULT '' COMMENT '国家名称',
    `province_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '省份编号',
    `province_name` varchar(50)   NOT NULL DEFAULT '' COMMENT '省份名称',
    `city_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '城市编号',
    `city_name` varchar(120)   NOT NULL DEFAULT '' COMMENT '城市名称',
    `district_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '区县编号',
    `district_name` varchar(120)   NOT NULL DEFAULT '' COMMENT '区县名称',
    `address` varchar(191)   NOT NULL DEFAULT '' COMMENT '更多详细地址',
    `complete_address` varchar(512)   NOT NULL DEFAULT '' COMMENT '完整收件地址',
    `zipcode` varchar(60)   NOT NULL DEFAULT '' COMMENT '邮编',
    `mobile` varchar(60)   NOT NULL DEFAULT '' COMMENT '手机号',
    `add_message` varchar(191)   NOT NULL DEFAULT '' COMMENT '客户留言',
    `shipping_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '快递id',
    `shipping_name` varchar(120)   NOT NULL DEFAULT '' COMMENT '快递名称',
    `pay_code` varchar(30)   NOT NULL DEFAULT '' COMMENT '支付代号',
    `pay_name` varchar(120)   NOT NULL DEFAULT '' COMMENT '支付名称',
    `pay_sn` varchar(32)   NOT NULL DEFAULT '' COMMENT '支付流水号',
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
    `shipping_no` varchar(191)   NOT NULL DEFAULT '' COMMENT '快递单号',
    `shipping_type` varchar(60)   NOT NULL DEFAULT '' COMMENT '快递类型',
    `is_cod` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否货到付款',
    `return_type_cfg` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否支持退换货：1支持 2不支持',
    `return_days_cfg` tinyint(1) NOT NULL DEFAULT '7' COMMENT '发货后自动确认收货时间,单位天',
    `order_timeout_days` smallint(3) NOT NULL DEFAULT '3' COMMENT '确认收货后自动订单完成时间,单位天',
    `seller_remark` varchar(512)   NOT NULL DEFAULT '' COMMENT '卖家备注',
    `erpordercode` varchar(32)   NOT NULL DEFAULT '' COMMENT 'erp中订单代码',
    `comment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未评论，1:已评论，2：已晒单',
    `fanli_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员id',
    `fanli_grade` varchar(64)   NOT NULL DEFAULT '' COMMENT '返利等级',
    `fanli_percent` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返利百分比',
    `settlement_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算',
    `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票id',
    `invoice_content` int(11) NOT NULL DEFAULT '0' COMMENT '发票类型：0普通发票；1增值税专票',
    `invoice_title` text   COMMENT '发票内容：json存储',
    `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家再次提交申请，5：退款退货成功，6是拒绝退款退货',
    `pay_order_sn` varchar(30)   NOT NULL DEFAULT '' COMMENT '对账单号',
    `goods_type` varchar(50)   NOT NULL DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购',
    `order_source` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单来源，0：小程序，1wap，2app',
    `fanli_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '返利类型，0：普通订单，1：分销返利订单，2：返利会员返利订单',
    `manual_refund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1代表手动退款，0代表非手动',
    `order_pay_way` tinyint(2) NOT NULL DEFAULT '0' COMMENT '订单付款方式，0全款 1定金 2好友代付',
    `bk_order_sn` varchar(32)   NOT NULL DEFAULT '' COMMENT '补款订单号 order_pay_way=1时有效',
    `bk_order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补款金额 order_pay_way=1时有效',
    `bk_order_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '补款金额是否支付 order_pay_way=1时有效，0未支付，1已支付',
    `pin_goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前拼团商品金额，阶梯团根据人数时会变化，补款也随之变化',
    `pin_yj_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团支付佣金金额',
    `activity_id` int(11) NOT NULL DEFAULT '0' COMMENT '营销活动id',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
    `source` varchar(30)   NOT NULL DEFAULT '' COMMENT '订单来源，记录app，wap，pc来源',
    `part_ship_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:，1:部分发货',
    `promotion_id` int(11) NOT NULL DEFAULT '0' COMMENT '促销活动id',
    `promotion_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '促销活动折扣金额,满折满减',
    `push_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'yadu推送状态：0-暂无推送，1-推送失败，2-推送成功',
    `push_desc` varchar(100)   NOT NULL DEFAULT '' COMMENT 'yadu推送失败原因',
    `pos_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '门店订单标志：0：商城，1：门店同步订单',
    `pos_shop_name` varchar(191)   NOT NULL DEFAULT '' COMMENT 'pos店铺名称',
    `store_id` int(11) DEFAULT '0' COMMENT '门店id',
    `store_name` varchar(191)   NOT NULL DEFAULT '' COMMENT '门店名称',
    `member_card_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员卡id',
    `member_card_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡优惠金额',
    `member_card_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '会员卡消费金额',
    `expire_time` timestamp NULL DEFAULT NULL COMMENT '订单支付过期时间',
    `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
    `prepay_id` varchar(191)   DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
    `deliver_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '配送类型：0 快递 1 自提',
    `deliver_type_name` varchar(191)   DEFAULT NULL COMMENT '配送类型名称',
    `pickupdate_time` varchar(30)   DEFAULT NULL COMMENT '自提时间',
    `star_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
    `verify_code` varchar(191)   NOT NULL DEFAULT '' COMMENT '核销自提码',
    `split` int(11) NOT NULL DEFAULT '0' COMMENT '分裂优惠券id',
    `card_no` varchar(32)   NOT NULL DEFAULT '' COMMENT '会员卡号',
    `fanli_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '单品返利金额',
    `true_name` varchar(32)   NOT NULL DEFAULT '' COMMENT '真实姓名',
    `id_card` varchar(32)   NOT NULL DEFAULT '' COMMENT '身份证号',
    `ali_trade_no` varchar(60)   NOT NULL DEFAULT '' COMMENT '支付宝交易单号',
    `grouper_cheap_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '团长优惠金额',
    `bk_shipping_time` timestamp NULL DEFAULT NULL COMMENT '定金预计发货时间',
    `bk_return_type` tinyint(2) DEFAULT NULL COMMENT '定金退款状态',
    `bk_prepay_id` varchar(191)   DEFAULT NULL COMMENT '微信支付id，用于发送模板消息',
    `pre_sale_discount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '定金膨胀优惠金额',
    `instead_pay_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '代付金额',
    `order_user_message` varchar(50)   DEFAULT NULL COMMENT '发起人留言',
    `instead_pay` text   COMMENT '好友代付规则',
    `instead_pay_num` smallint(6) NOT NULL DEFAULT '0' COMMENT '代付人数',
    `is_promote` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是推广单',
    `verifier_id` int(11) NOT NULL DEFAULT '0' COMMENT '核销员id',
    `exchang` tinyint(2) NOT NULL DEFAULT '0' COMMENT '1 兑换 0否',
    `currency` varchar(10)   NOT NULL DEFAULT 'CNY' COMMENT '币种',
    `free_ship` decimal(10,2) DEFAULT '0.00' COMMENT '运费抵扣',
    `free_detail` text   COMMENT '运费抵扣规则',
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
    `is_lock` tinyint(1) DEFAULT '0' COMMENT '是否锁库存，0否，1是',
    `score_proportion` int(9) DEFAULT '100' COMMENT '积分比例',
    `is_freeship_card` tinyint(1) DEFAULT '0' COMMENT '0否，1是',
    `room_id` int(11) DEFAULT '0' COMMENT '直播间ID',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `order_sn` (`order_sn`),
    KEY `boin_main_order_sn` (`main_order_sn`),
    KEY `boin_user_id` (`user_id`),
    KEY `boin_user_openid` (`user_openid`),
    KEY `boin_order_status` (`order_status`),
    KEY `boin_shipping_id` (`shipping_id`),
    KEY `boin_shop_id` (`shop_id`)
);

CREATE TABLE `b2c_qf_img` (
  `img_id` int(8) NOT NULL AUTO_INCREMENT,
  `question_feedback_id` int(8) NOT NULL DEFAULT '0',
  `img_url` varchar(191) NOT NULL DEFAULT '',
  `img_desc` varchar(191) NOT NULL DEFAULT '',
  PRIMARY KEY (`img_id`),
  KEY `question_feedback_id` (`question_feedback_id`)
)COMMENT='admin用户问题反馈记录图片';



CREATE TABLE `b2c_shop` (
  `shop_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `sys_id` int(11) NOT NULL,
  `mobile` varchar(32) NOT NULL DEFAULT '',
  `receive_mobile` varchar(32) DEFAULT '' COMMENT ' 接收通知手机号码',
  `shop_name` varchar(50) DEFAULT '' COMMENT '店铺名称',
  `shop_avatar` varchar(191) DEFAULT '' COMMENT '店铺头像',
  `shop_bg_path` varchar(191) DEFAULT '' COMMENT '店铺背景图片',
  `shop_phone` varchar(50) DEFAULT '' COMMENT '店铺客服电话',
  `shop_notice` varchar(191) DEFAULT '' COMMENT '店铺公告',
  `shop_wx` varchar(191) DEFAULT '' COMMENT '店铺客服微信',
  `shop_email` varchar(191) DEFAULT '',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `province_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '所在省',
  `province_name` varchar(50) NOT NULL DEFAULT '',
  `city_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '所在城市',
  `city_name` varchar(120) NOT NULL DEFAULT '',
  `district_code` mediumint(10) NOT NULL DEFAULT '0' COMMENT '所在区县',
  `district_name` varchar(120) NOT NULL DEFAULT '',
  `address` varchar(191) NOT NULL DEFAULT '' COMMENT '所在地址',
  `complete_address` varchar(512) NOT NULL DEFAULT '' COMMENT '所在完整地址',
  `shop_sell_type` int(11) NOT NULL DEFAULT '254' COMMENT '经营品类,254：其他',
  `shop_qq` varchar(20) DEFAULT '' COMMENT '店铺客服QQ',
  `last_login_ip` varchar(40) DEFAULT '' COMMENT '上次登录IP',
  `state` tinyint(1) DEFAULT '0' COMMENT '0 入驻申请，1审核通过，2审核不通过',
  `business_state` tinyint(1) DEFAULT '0' COMMENT '营业状态 0未营业 1营业',
  `manage_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台管理费',
  `surplus` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `db_config` text COMMENT 'db config,json format',
  `shop_type` varchar(20) NOT NULL DEFAULT 'v3' COMMENT '店铺类型',
  `version_config` text COMMENT '店铺功能',
  `shop_flag` tinyint(2) DEFAULT '0' COMMENT '店铺标志：0店家，1欧派，2嗨购',
  `member_key` varchar(64) DEFAULT NULL COMMENT '欧派店铺标识',
  `tenancy_name` varchar(64) DEFAULT NULL COMMENT '欧派创思大屏租户名称',
  `user_name` varchar(64) DEFAULT NULL COMMENT '欧派创思大屏用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '欧派创思大屏密码',
  `sms_account` varchar(50) DEFAULT NULL COMMENT '短信账号',
  `kuajinggou` tinyint(1) DEFAULT '0' COMMENT '跨境购',
  `order_real_name` tinyint(1) DEFAULT '0' COMMENT '下单实名制',
  `hid_bottom` tinyint(1) DEFAULT '0' COMMENT '是否隐藏底部 1是 ',
  `logo` varchar(200) DEFAULT NULL COMMENT '小程序logo',
  `currency` varchar(45) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `shop_language` varchar(45) NOT NULL DEFAULT 'zh-CN' COMMENT '语言',
  `expire_time` timestamp null DEFAULT NULL COMMENT '到期时间',
  `store_clerk_privilege_list` TEXT NULL DEFAULT NULL COMMENT '门店店员权限列表',
  `publicity_img` varchar(512) DEFAULT '' COMMENT '店铺宣传图',
  `copywriting` text COMMENT '店铺详情文案',
  PRIMARY KEY (`shop_id`),
  KEY `mobile` (`mobile`)
)COMMENT='店铺';


CREATE TABLE `b2c_shop_account` (
  `sys_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `user_name` varchar(64) NOT NULL DEFAULT '-- dd ##' COMMENT '用户名',
  `password` varchar(32) NOT NULL DEFAULT '' COMMENT '密码',
  `state` tinyint(1) DEFAULT '0' COMMENT '1 入驻申请，2审核通过，3审核不通过,4已禁用',
  `business_state` tinyint(1) DEFAULT '0' COMMENT '营业状态 0未营业 1营业',
  `shop_grade` tinyint(1) DEFAULT '0' COMMENT '店铺等级：4旗舰店、3精品店、2专营店、1普通店',
  `max_sku_num` int(11) DEFAULT '0' COMMENT '最大上传sku数量，不同等级不同数量，管理员可修改',
  `max_shop_num` int(11) DEFAULT '0' COMMENT '最多小程序数量，不同等级不同数量，管理员可修改',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `buy_time` timestamp NULL DEFAULT NULL COMMENT '首次续费时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '到期时间',
  `last_login_shop_id` int(11) DEFAULT '0' COMMENT '上次登录店铺ID',
  `mobile` varchar(32) DEFAULT '' COMMENT '店铺账户的手机号',
  `account_name` varchar(32) DEFAULT '' COMMENT '账户昵称',
  `shop_avatar` varchar(255) DEFAULT '/image/admin/head_icon.png' COMMENT '账户头像',
  `company` varchar(255) DEFAULT '' COMMENT '公司名称',
  `salesperson` varchar(32) DEFAULT '' COMMENT '销售员',
  `province_code` varchar(10) DEFAULT '' COMMENT '省',
  `city_code` varchar(10) DEFAULT '' COMMENT '市',
  `district_code` varchar(10) DEFAULT '' COMMENT '区',
  `address` varchar(200) DEFAULT '' COMMENT '详细地址',
  `base_sale` tinyint(1) DEFAULT '0' COMMENT '初始销量配置开关：0关闭，1开启',
  `backlog` text COMMENT '待办事项列表',
  `add_comment_switch` tinyint(1) DEFAULT '0' COMMENT '商户自己添加评论开关：0关闭，1开启',
  `official_open_id` varchar(128) DEFAULT NULL COMMENT '公众号openid',
  `is_bind` tinyint(1) DEFAULT '0' COMMENT '是否已绑定',
  PRIMARY KEY (`sys_id`)
)COMMENT='商家账户系统';



CREATE TABLE `b2c_shop_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_date` date NOT NULL COMMENT '2018-09-18',
  `type` tinyint(2) NOT NULL COMMENT '1，7，30',
  `activity_type` tinyint(2) NOT NULL COMMENT '活动类型',
  `shop_num` int(11) NOT NULL COMMENT '有权限的店铺数',
  `use` int(11) NOT NULL COMMENT '有权限已使用的店铺数',
  `nouse` int(11) NOT NULL COMMENT '有权限未使用的店铺数',
  `ingoing` int(11) NOT NULL COMMENT '有权限正在使用的店铺数',
  `num` int(11) NOT NULL COMMENT '进行中的活动',
  PRIMARY KEY (`id`)
)COMMENT='店铺活动记录';



CREATE TABLE `b2c_shop_child_account` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '主账户ID',
  `account_name` varchar(191) NOT NULL DEFAULT '' COMMENT '子账号用户名',
  `account_pwd` varchar(40) NOT NULL DEFAULT '' COMMENT '子账号密码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mobile` varchar(32) NOT NULL DEFAULT '0' COMMENT '手机号',
  `backlog` text COMMENT '待办事项列表',
  `official_open_id` varchar(128) DEFAULT NULL COMMENT '公众号openid',
  `is_bind` tinyint(1) DEFAULT '0' COMMENT '是否已绑定',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `sys_id` (`sys_id`,`account_name`),
  KEY `sys_id_2` (`sys_id`)
)COMMENT='商家子帐号';



CREATE TABLE `b2c_shop_child_role` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '主账户ID',
  `account_id` int(11) NOT NULL DEFAULT '0' COMMENT '子账户ID',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID',
  PRIMARY KEY (`rec_id`),
  UNIQUE KEY `account_id` (`account_id`,`shop_id`),
  KEY `bscr_sys_id` (`sys_id`)
)COMMENT='商家子帐号与店铺权限关联表';



CREATE TABLE `b2c_shop_free_experience` (
  `fe_id` int(11) NOT NULL AUTO_INCREMENT,
  `company` varchar(191) NOT NULL DEFAULT '' COMMENT '公司',
  `contact` varchar(191) NOT NULL DEFAULT '' COMMENT '联系人',
  `mobile` varchar(32) DEFAULT '' COMMENT '电话',
  `province_id` int(11) NOT NULL,
  `content` text COMMENT '留言',
  `ask_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `is_deal` tinyint(1) DEFAULT '0' COMMENT '1:已查看',
  `shop_id` int(11) DEFAULT NULL COMMENT '开通店铺ID',
  `desc` text COMMENT '备注',
  `source` varchar(191) NOT NULL DEFAULT '' COMMENT '来源shop_id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`fe_id`)
)COMMENT='官网免费申请';



CREATE TABLE `b2c_shop_grade` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_grade` varchar(64) NOT NULL COMMENT '店铺等级',
  `max_sku_num` int(11) NOT NULL COMMENT 'SKU数量',
  `max_shop_num` int(11) NOT NULL COMMENT '店铺数量',
  `manage_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台管理费百分比',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:正常，1:删除',
  `in_time` datetime DEFAULT NULL,
  `up_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='店铺等级';



CREATE TABLE `b2c_shop_grade_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL,
  `shop_grade` varchar(64) NOT NULL COMMENT '店铺等级',
  `max_sku_num` int(11) NOT NULL COMMENT 'SKU数量',
  `max_shop_num` int(11) NOT NULL COMMENT '店铺数量',
  `manage_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平台管理费百分比',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:正常，1:删除',
  `in_time` datetime DEFAULT NULL,
  `up_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='店铺平台管理费修改记录';



CREATE TABLE `b2c_shop_operation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '操作id',
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `operator_id` int(11) NOT NULL COMMENT '操作员id',
  `operator` varchar(50) NOT NULL COMMENT '操作员',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `desc` text COMMENT '操作描述',
  `ip` varchar(50) DEFAULT '' COMMENT '操作ip',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '记录类型 0 店铺 1 账号',
  PRIMARY KEY (`id`)
)COMMENT='操作日志记录';



CREATE TABLE `b2c_shop_question_feedback` (
  `question_feedback_id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '反馈店铺ID',
  `category_id` int(11) NOT NULL DEFAULT '1' COMMENT '反馈问题分类',
  `mobile` varchar(32) DEFAULT '' COMMENT '填写的手机号',
  `content` text,
  `is_look` tinyint(1) DEFAULT '0' COMMENT '1:已查看',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '反馈时间',
  `qf_img` varchar(191) NOT NULL DEFAULT '' COMMENT '图片',
  `version` varchar(50) not NULL default '' COMMENT '使用系统版本',
  `submit_user` varchar(128) not NULL default '' COMMENT '提交账号',
  `submit_user_phone` varchar(32) not NULL default '' COMMENT '提交账号绑定的手机号',
  PRIMARY KEY (`question_feedback_id`),
  KEY `is_look` (`is_look`)
)COMMENT='admin用户问题反馈记录';



CREATE TABLE `b2c_shop_renew` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `sys_id` int(11) NOT NULL,
  `mobile` varchar(32) DEFAULT '',
  `renew_money` decimal(12,2) DEFAULT NULL,
  `renew_date` date DEFAULT NULL COMMENT '店铺续费日期',
  `expire_time` date DEFAULT NULL COMMENT '到期时间',
  `operator` int(11) DEFAULT '0' COMMENT '操作员ID,主账号是0，子账号ID',
  `renew_desc` varchar(255) DEFAULT '' COMMENT '说明',
  `renew_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '续费类型：1续费，2试用，3赠送，4退款',
  `renew_duration` varchar(32) NOT NULL DEFAULT '0' COMMENT '时长：字符串逗号隔开',
  `send_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '赠送类型：0无，1时间，2功能',
  `send_content` varchar(32) NOT NULL DEFAULT '0' COMMENT '赠送内容：字符串逗号隔开',
  PRIMARY KEY (`id`)
)COMMENT='店铺续费表';



CREATE TABLE `b2c_shop_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '主账户ID',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `role_name` varchar(50) NOT NULL DEFAULT '' COMMENT '角色名称',
  `privilege_list` text COMMENT '权限列表，json数组存储',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `privilege_pass` text COMMENT '权限密码，json数组存储',
  `role_pass` varchar(50) DEFAULT NULL COMMENT '权限密码',
  PRIMARY KEY (`role_id`),
  KEY `shop_id` (`shop_id`),
  KEY `bsr_sys_id` (`sys_id`)
)COMMENT='店铺角色表';



CREATE TABLE `b2c_shop_uploaded_image` (
  `img_id` int(10) NOT NULL AUTO_INCREMENT,
  `img_type` varchar(60) NOT NULL DEFAULT '',
  `img_size` int(10) NOT NULL DEFAULT '0',
  `img_name` varchar(191) NOT NULL DEFAULT '' COMMENT '图片名称，用于前端显示',
  `img_orig_fname` varchar(191) NOT NULL DEFAULT '',
  `img_path` varchar(191) NOT NULL DEFAULT '',
  `img_url` varchar(191) NOT NULL DEFAULT '',
  `img_cat_id` int(10) DEFAULT '0' COMMENT '图片分类',
  `img_width` int(10) NOT NULL DEFAULT '0',
  `img_height` int(10) NOT NULL DEFAULT '0',
  `is_refer` tinyint(4) DEFAULT '0' COMMENT '是否引用',
  `upload_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '账户ID',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`img_id`),
  KEY `shop_id` (`shop_id`),
  KEY `img_orig_fname` (`img_orig_fname`)
)COMMENT='店铺主账号图片记录表';



CREATE TABLE `b2c_shop_uploaded_image_category` (
  `img_cat_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `img_cat_name` varchar(60) NOT NULL DEFAULT '',
  `img_cat_parent_id` int(10) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cat_ids` varchar(191) NOT NULL DEFAULT '0' COMMENT '层级ID串,逗号分隔',
  `level` tinyint(4) DEFAULT '0' COMMENT '层级，0开始',
  `sort` int(11) DEFAULT '1' COMMENT '排序优先级',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '账户ID',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`img_cat_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='店铺主账号图片分类';



CREATE TABLE `b2c_shop_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version_name` varchar(50) NOT NULL COMMENT '版本名称',
  `level` varchar(50) NOT NULL COMMENT '版本级别',
  `content` text COMMENT '包含功能',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `desc` text COMMENT '版本介绍',
  `flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0正常 1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `level` (`level`)
)COMMENT='店铺分版本';



CREATE TABLE `b2c_sms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(32) NOT NULL DEFAULT '',
  `sms_code` varchar(10) NOT NULL,
  `add_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ip` varchar(20) DEFAULT '',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0:发送失败 1：发送成功',
  `type` varchar(10) DEFAULT NULL,
  `content` text NOT NULL COMMENT '发送短信内容',
  PRIMARY KEY (`id`),
  KEY `mobile` (`mobile`)
)COMMENT='发送短信记录表';



CREATE TABLE `b2c_sort` (
  `sort_id` int(11) NOT NULL AUTO_INCREMENT,
  `sort_name` varchar(90) DEFAULT '',
  `parent_id` int(11) NOT NULL,
  `level` smallint(5) NOT NULL DEFAULT '0',
  `has_child` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录修改时间',
  `sort_img` varchar(191) NOT NULL DEFAULT '' COMMENT '一级分类是头图 其他为分类图标',
  `img_link` varchar(191) DEFAULT '' COMMENT '图标或者头图链接',
  `first` smallint(2) NOT NULL DEFAULT '0' COMMENT '优先级',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0普通商家分类 1推荐分类',
  `sort_desc` varchar(191) DEFAULT '',
  PRIMARY KEY (`sort_id`),
  KEY `parent_id` (`parent_id`)
)COMMENT='店铺自定义分类';



CREATE TABLE `b2c_spec` (
  `spec_id` int(10) NOT NULL AUTO_INCREMENT,
  `spec_name` varchar(60) NOT NULL DEFAULT '',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  PRIMARY KEY (`spec_id`)
);



CREATE TABLE `b2c_spec_vals` (
  `specvalid` int(10) NOT NULL AUTO_INCREMENT,
  `spec_id` int(10) NOT NULL DEFAULT '0',
  `specvalname` varchar(60) NOT NULL DEFAULT '',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  PRIMARY KEY (`specvalid`),
  KEY `spec_id` (`spec_id`)
)COMMENT='规格表 `b2c_spec`';



CREATE TABLE `b2c_statistics_shop` (
  `id` mediumint(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `ref_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '统计数据时间',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '统计类型 1天 7周 30月 3季度',
  `week` tinyint(2) NOT NULL DEFAULT '0' COMMENT '第几周',
  `months` tinyint(2) NOT NULL DEFAULT '0' COMMENT '第几周',
  `quarter` tinyint(2) NOT NULL DEFAULT '0' COMMENT '第几季度',
  `new_user` mediumint(8) NOT NULL DEFAULT '0' COMMENT '注册用户',
  `order_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '订单数',
  `wx_order_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '微信支付订单数',
  `cod_order_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '货到付款订单数',
  `balance_order_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '余额订单',
  `score_order_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '积分订单',
  `wx_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '微信支付总额',
  `card_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '卡余额总额',
  `balance_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额总额',
  `score_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分总额',
  `order_user_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '下单用户',
  `wx_user_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '微信下单用户',
  `cod_user_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '货到付款用户',
  `balance_user_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '余额购买用户',
  `score_user_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '积分购买用户',
  `order_goods_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '订单商品数量',
  `wx_goods_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '微信支付商品',
  `cod_goods_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '货到付款商品',
  `balance_goods_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '余额购买商品',
  `score_goods_num` mediumint(8) NOT NULL DEFAULT '0' COMMENT '积分购买商品',
  `wx_order_pay` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '微信金额',
  `cod_order_pay` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '货到付款金额',
  `balance_order_pay` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额金额',
  `score_order_pay` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分金额',
  `wx_order_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '微信支付余额',
  `cod_order_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '货到付款支付余额',
  `balance_order_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额支付余额',
  `score_order_balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分支付余额',
  `wx_order_card` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '微信支付卡余额',
  `cod_order_card` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '货到付款支付卡余额',
  `balance_order_card` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额支付卡余额',
  `score_order_card` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分支付卡余额',
  `wx_order_score` mediumint(8) NOT NULL DEFAULT '0' COMMENT '微信支付积分',
  `cod_order_score` mediumint(8) NOT NULL DEFAULT '0' COMMENT '货到付款支付积分',
  `balance_order_score` mediumint(8) NOT NULL DEFAULT '0' COMMENT '余额支付积分',
  `score_order_score` mediumint(8) NOT NULL DEFAULT '0' COMMENT '积分支付卡积分',
  PRIMARY KEY (`id`),
  KEY `ref_date` (`ref_date`),
  KEY `shop_id` (`shop_id`)
)COMMENT='店铺数据统计';

CREATE TABLE `b2c_store_account` (
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
    `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联用户Id',
    `official_open_id` varchar(128) DEFAULT NULL COMMENT '公众号openid',
    `is_bind` tinyint(1) DEFAULT '0' COMMENT '是否已绑定',
    PRIMARY KEY (`account_id`),
    KEY `mobile` (`mobile`),
    KEY `account_name` (`account_name`)
) comment '门店账户表';


CREATE TABLE `b2c_system_cfg` (
  `rec_id` smallint(5) NOT NULL AUTO_INCREMENT,
  `sys_id` int(11) NOT NULL,
  `k` varchar(191) DEFAULT '',
  `v` text,
  PRIMARY KEY (`rec_id`),
  KEY `bsc_sys_id` (`sys_id`)
)COMMENT='微信公众号用户表';



CREATE TABLE `b2c_system_child_account` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `account_name` varchar(191) NOT NULL DEFAULT '' COMMENT '子账号用户名',
  `account_pwd` varchar(40) NOT NULL DEFAULT '' COMMENT '子账号密码',
  `role_id` int(11) NOT NULL DEFAULT '0' COMMENT '角色ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mobile` varchar(32) NOT NULL DEFAULT '0' COMMENT '手机号',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `account_name` (`account_name`),
  KEY `system_user_id` (`system_user_id`)
)COMMENT='平台子帐号';



CREATE TABLE `b2c_system_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `system_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '平台账号ID',
  `role_name` varchar(50) NOT NULL DEFAULT '' COMMENT '角色名称',
  `privilege_list` text COMMENT '权限列表，json数组存储',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  KEY `system_user_id` (`system_user_id`)
)COMMENT='平台角色表';



CREATE TABLE `b2c_system_user` (
  `system_user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(60) NOT NULL DEFAULT '',
  `password` varchar(32) NOT NULL DEFAULT '',
  `mobile` varchar(32) NOT NULL DEFAULT '',
  `receive_mobile` varchar(32) DEFAULT '' COMMENT ' 接收通知手机号码',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否可用',
  `last_login_ip` varchar(40) DEFAULT '' COMMENT '上次登录IP',
  PRIMARY KEY (`system_user_id`)
)COMMENT='平台账号';



CREATE TABLE `b2c_task_job_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` MEDIUMTEXT COMMENT '消息内容',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识：0未删除，1已删除',
  PRIMARY KEY (`id`)
)COMMENT='第三方服务提醒';



CREATE TABLE `b2c_task_job_main` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) DEFAULT '0' COMMENT '店铺ID',
  `content_id` int(11) DEFAULT '0' COMMENT 'MQ消息内容ID',
  `status` tinyint(3) DEFAULT '0' COMMENT '任务状态：0待执行,1执行中,2已完成',
  `progress` tinyint(3) DEFAULT '0' COMMENT '任务进度：0-100',
  `class_name` varchar(100) DEFAULT '' COMMENT '反序列化类名（全称）',
  `execution_type` int(8) DEFAULT '0' COMMENT '执行类型:任务类型标识',
  `cycle` int(11) DEFAULT '0' COMMENT '轮循间隔(单位:秒)',
  `type` tinyint(3) DEFAULT '0' COMMENT 'task任务类型(立刻执行；定时执行；循环执行)',
  `next_execute_time` timestamp NULL DEFAULT NULL COMMENT '下次执行开始日期',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '周期开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '周期结束时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识：0未删除，1已删除',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_third_party_services` (
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `account_action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账号类型 1:主账号 2：子账号',
  `account_id` int(11) NOT NULL COMMENT '账号ID',
  `service_action` tinyint(4) DEFAULT '1' COMMENT '服务类型 1:订单提醒',
  `service_detail` text COMMENT '服务明细',
  `add_time` datetime DEFAULT NULL COMMENT '操作时间',
  KEY `shop_id` (`shop_id`),
  KEY `account_type` (`account_action`,`account_id`,`service_action`),
  KEY `account_id` (`account_id`)
);



CREATE TABLE `b2c_upload_uyun_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `file_size` int(64) NOT NULL COMMENT '文件大小',
  `file_url` varchar(500) NOT NULL DEFAULT '' COMMENT '文件url',
  `update_timestamp` varchar(500) NOT NULL DEFAULT '' COMMENT '文件更新时间戳',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '文件更新日期',
  `upload_status` tinyint(2) DEFAULT '0' COMMENT '文件上传状态 0：上传成功；1：上传失败（或开关关闭后未上传的文件）',
  `upload_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件上传时间',
  `fail_reason` text NOT NULL COMMENT '失败原因',
  `fail_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '失败时间',
  PRIMARY KEY (`record_id`),
  KEY `file_url` (`file_url`(191)),
  KEY `shop_id` (`shop_id`)
)COMMENT='上传uyun记录';



CREATE TABLE `b2c_uploaded_image` (
  `img_id` int(10) NOT NULL AUTO_INCREMENT,
  `img_type` varchar(60) NOT NULL DEFAULT '',
  `img_size` int(10) NOT NULL DEFAULT '0',
  `img_name` varchar(500) NOT NULL DEFAULT '' COMMENT '图片名称，用于前端显示',
  `img_orig_fname` varchar(500) NOT NULL DEFAULT '',
  `img_path` varchar(500) NOT NULL DEFAULT '',
  `img_url` varchar(500) NOT NULL DEFAULT '',
  `img_cat_id` int(10) DEFAULT '0' COMMENT '图片分类',
  `img_width` int(10) NOT NULL DEFAULT '0',
  `img_height` int(10) NOT NULL DEFAULT '0',
  `is_refer` tinyint(4) DEFAULT '0' COMMENT '是否引用',
  `upload_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '账户ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`img_id`),
  KEY `shop_id` (`shop_id`),
  KEY `img_orig_fname` (`img_orig_fname`(191))
)COMMENT='uploaded_image 上传图片表';



CREATE TABLE `b2c_uploaded_image_category` (
  `img_cat_id` int(10) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `img_cat_name` varchar(60) NOT NULL DEFAULT '',
  `img_cat_parent_id` int(10) NOT NULL DEFAULT '0',
  `cat_ids` varchar(191) NOT NULL DEFAULT '0' COMMENT '层级ID串,逗号分隔',
  `level` tinyint(4) DEFAULT '0' COMMENT '层级，0开始',
  `sort` int(11) DEFAULT '1' COMMENT '排序优先级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`img_cat_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='上传图片分类';



CREATE TABLE `b2c_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `user_pwd` varchar(60) NOT NULL DEFAULT '' COMMENT '密码',
  `user_cid` varchar(64) NOT NULL DEFAULT '',
  `mobile` varchar(100) DEFAULT NULL COMMENT '电话',
  `user_code` varchar(100) DEFAULT NULL COMMENT '会员卡号',
  `wx_openid` varchar(128) NOT NULL DEFAULT '',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `wechat` varchar(100) NOT NULL DEFAULT '' COMMENT '微信',
  `fanli_grade` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员级别',
  `user_grade` int(11) NOT NULL DEFAULT '1' COMMENT '会员级别',
  `invite` int(11) NOT NULL DEFAULT '0',
  `invite_source` varchar(32) DEFAULT NULL COMMENT '邀请来源:groupbuy.拼团,bargain.砍价,integral.积分,seckill.秒杀,lottery.抽奖',
  `invitation_code` int(8) NOT NULL DEFAULT '0' COMMENT '邀请码',
  `account` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户余额',
  `discount` int(11) NOT NULL DEFAULT '0' COMMENT '折扣',
  `discount_grade` int(11) NOT NULL DEFAULT '0' COMMENT '会员折扣等级',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `growth` int(11) DEFAULT '0' COMMENT '成长值',
  `score` int(11) DEFAULT '0' COMMENT '积分',
  `source` int(11) DEFAULT '-1' COMMENT '门店来源-1未录入0后台>0为门店',
  `invite_id` int(11) DEFAULT '0' COMMENT '邀请人ID',
  `invite_expiry_date` date DEFAULT NULL COMMENT '邀请失效时间',
  `wx_union_id` varchar(191) NOT NULL DEFAULT '' COMMENT '小程序union_id',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `is_distributor` tinyint(2) DEFAULT '0' COMMENT '是否是分销员',
  `invite_act_id` int(10) DEFAULT '0' COMMENT '邀请来源活动ID',
  `distributor_level` tinyint(2) DEFAULT '1' COMMENT '用户等级',
  `ali_user_id` varchar(191) NOT NULL DEFAULT '' COMMENT '支付宝用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_user_id` (`user_id`,`shop_id`),
  KEY `user_id` (`user_id`),
  KEY `wx_openid` (`wx_openid`)
)COMMENT='用户';



CREATE TABLE `b2c_user_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
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
  `monthly_income` tinyint(1) DEFAULT NULL COMMENT '月收入',
  `cid` varchar(18) DEFAULT NULL COMMENT '身份证号码',
  `education` tinyint(1) DEFAULT NULL COMMENT '教育程度',
  `industry_info` tinyint(1) DEFAULT NULL COMMENT '所在行业',
  `big_image` varchar(191) DEFAULT NULL COMMENT '头像',
  `bank_user_name` varchar(100) DEFAULT NULL COMMENT '开户行姓名',
  `shop_bank` varchar(100) DEFAULT NULL COMMENT '开户行',
  `bank_no` varchar(32) DEFAULT NULL COMMENT '开户行卡号',
  `withdraw_passwd` varchar(64) DEFAULT NULL COMMENT '提现密码验证',
  `user_avatar` varchar(191) DEFAULT '/image/admin/head_icon.png' COMMENT '用户头像',
  PRIMARY KEY (`id`)
)COMMENT='用户详情';



CREATE TABLE `b2c_user_login_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `shop_name` varchar(100) NOT NULL DEFAULT '' COMMENT '店铺名称',
  `sys_id` int(11) NOT NULL DEFAULT '0' COMMENT '主账户ID',
  `user_id` smallint(3) NOT NULL DEFAULT '0' COMMENT '登陆用户id',
  `user_name` varchar(64) NOT NULL DEFAULT '' COMMENT '登陆用户名',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '每日登陆时间',
  `user_ip` varchar(64) DEFAULT NULL COMMENT '用户登录ip',
  `count` smallint(3) DEFAULT '0' COMMENT '每日登陆次数',
  `account_type` tinyint(1) DEFAULT NULL COMMENT '登录日志账户类型：0店铺登录日志，1系统账号登录日志',
  PRIMARY KEY (`id`)
);



CREATE TABLE `b2c_user_summary_trend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
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
  `total_paid_money` int(11) NOT NULL COMMENT '总成交金额',
  `new_paid_money` decimal(10,4) DEFAULT NULL COMMENT '成交新客户支付金额',
  `old_paid_money` decimal(10,0) DEFAULT NULL COMMENT '成交老客户支付金额',
  `pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `new_pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `old_pay_goods_number` int(11) DEFAULT NULL COMMENT '付款件数',
  `pay_order_num` int(11) DEFAULT '0' COMMENT '成交订单数',
  `login_pv` int(11) DEFAULT '0' COMMENT '登录pv',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '下单笔数',
  `order_user_num` int(11) DEFAULT '0' COMMENT '下单人数(生成订单就算)',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '统计时间',
  PRIMARY KEY (`id`),
  KEY `ref_type` (`ref_date`,`type`)
);

-- 店铺短信配置表
create table `b2c_shop_sms_config` (
    `id` int(11) not null auto_increment,
    `shop_id` int(11) not null comment '店铺id',
    `user_check_code_num` int(11) not null default 15 comment '每个用户可发送验证码数量',
    `patient_check_code_num` int(11) not null default 15 comment '每个患者可发送验证码数量',
    `marketing_num` int(11) not null default 2000 comment '每天可发送营销短信数量',
    `industry_num` int(11) not null default 2000 comment '每天可发送行业短信数量',
    `is_delete`     tinyint(1)   not null default '0',
    `create_time`   timestamp    not null default current_timestamp,
    `update_time`   timestamp    not null default current_timestamp on update current_timestamp comment '最后修改时间',
    primary key (`id`)
)comment = '店铺短信配置表';


-- 订单商品备份
CREATE TABLE `b2c_order_goods_bak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rec_id` mediumint(8) NOT NULL ,
  `main_rec_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '主订单rec_id',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `order_id` mediumint(8) NOT NULL DEFAULT '0',
  `order_sn` varchar(20) NOT NULL DEFAULT '',
  `goods_id` mediumint(8) NOT NULL DEFAULT '0',
  `goods_name` varchar(120) NOT NULL DEFAULT '',
  `goods_sn` varchar(60) NOT NULL DEFAULT '',
  `product_id` mediumint(8) NOT NULL DEFAULT '0',
  `product_sn` varchar(65) NOT NULL DEFAULT '',
  `goods_number` INT(11)     NOT NULL DEFAULT '1',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `goods_attr` text NOT NULL,
  `send_number` INT(11)      NOT NULL DEFAULT '0',
  `return_number` INT(11)      NOT NULL DEFAULT '0' COMMENT '退货/退款成功数量',
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
  `fanli_strategy` VARCHAR (2999) DEFAULT '' COMMENT '返利配置详情',
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
  `purchase_id` int(11) NOT NULL DEFAULT 0 COMMENT '加价购活动id',
  `prescription_old_code` varchar(64)   NOT NULL DEFAULT '' COMMENT '老处方项目明细号码（可根据此字段反查批次号）',
  `prescription_code` varchar(64)   NOT NULL DEFAULT '' COMMENT '处方项目明细号码（可根据此字段反查批次号）',
  `medical_audit_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '药品审核类型, 0不审核,1审核,2开方,3根据处方下单',
  `medical_audit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '药品审核状态 0未审核 1审核通过 2审核不通过',
  `audit_time`timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '药品审核时间',
  PRIMARY KEY (`id`),
  KEY `rec_id` (`rec_id`),
  KEY `order_id` (`order_id`),
  KEY `order_sn` (`order_sn`),
  KEY `goods_id` (`goods_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单商品表  b2c_order_goods';

-- 订单详情备份
CREATE TABLE `b2c_order_info_bak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` mediumint(8) NOT NULL COMMENT '订单ID',
  `shop_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `main_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '主订单编号(拆单时用)',
  `user_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `user_openid` varchar(191) NOT NULL DEFAULT '' COMMENT '用户openid',
  `order_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_status_name` varchar(32) NOT NULL DEFAULT '' COMMENT '订单状态名称',
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收件人姓名',
  `address_id` int(11) NOT NULL DEFAULT '0' COMMENT '地址ID',
  `country_code` mediumint(10) DEFAULT '0' COMMENT '国家编号',
  `country_name` varchar(50) NOT NULL DEFAULT '' COMMENT '国家名称',
  `province_code` mediumint(10) DEFAULT '0' COMMENT '省份编号',
  `province_name` varchar(50) NOT NULL DEFAULT '' COMMENT '省份名称',
  `city_code` mediumint(10) DEFAULT '0' COMMENT '城市编号',
  `city_name` varchar(120) NOT NULL DEFAULT '' COMMENT '城市名称',
  `district_code` mediumint(10) DEFAULT '0' COMMENT '区县编号',
  `district_name` varchar(120) NOT NULL DEFAULT '' COMMENT '区县名称',
  `address` varchar(191) NOT NULL DEFAULT '' COMMENT '更多详细地址',
  `complete_address` varchar(512) NOT NULL DEFAULT '' COMMENT '完整收件地址',
  `zipcode` varchar(60) NOT NULL DEFAULT '' COMMENT '邮编',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机号',
  `add_message` varchar(191) DEFAULT '' COMMENT '客户留言',
  `shipping_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '快递ID',
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
  `package_discount` decimal(10,2) DEFAULT '0.00' COMMENT '一口价抵扣金额',
  `dapei_id` int(8) NOT NULL DEFAULT '0' COMMENT '搭配ID来源',
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
  `erpordercode` varchar(32) NOT NULL DEFAULT '' COMMENT 'ERP中订单代码',
  `comment_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未评论，1:已评论，2：已晒单',
  `fanli_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '返利会员id',
  `fanli_grade` varchar(64) NOT NULL DEFAULT '' COMMENT '返利等级',
  `fanli_percent` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '返利百分比',
  `settlement_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '结算标志：0：未结算，1：已结算',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票Id',
  `invoice_content` int(11) DEFAULT '0' COMMENT '发票类型：0普通发票；1增值税专票',
  `invoice_title` text COMMENT '发票内容：json存储',
  `refund_status` tinyint(1) DEFAULT '0' COMMENT '1是审核中，2是通过审核，3退货没通过审核，4买家再次提交申请，5：退款退货成功，6是拒绝退款退货',
  `pay_order_sn` varchar(30) DEFAULT '' COMMENT '对账单号',
  `goods_type` varchar(50) DEFAULT '0' COMMENT '商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品 6限时降价 7加价购',
  `order_source` tinyint(2) DEFAULT NULL COMMENT '订单来源，0pc，1wap，2app',
  `fanli_type` tinyint(2) DEFAULT NULL COMMENT '返利类型，0普通订单，1三级分销返利订单，2返利会员返利订单',
  `manual_refund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1代表手动退款，0代表非手动',
  `order_pay_way` tinyint(2) DEFAULT '0' COMMENT '订单付款方式，0全款 1定金 2补款',
  `bk_order_sn` varchar(20) DEFAULT '' COMMENT '补款订单号 order_pay_way=1时有效',
  `bk_order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补款金额 order_pay_way=1时有效',
  `bk_order_paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '补款金额是否支付 order_pay_way=1时有效，0未支付，1已支付',
  `pin_goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前拼团商品金额，阶梯团根据人数时会变化，补款也随之变化',
  `pin_yj_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '拼团支付佣金金额',
  `activity_id` int(11) NOT NULL DEFAULT '0' COMMENT '营销活动id',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
  `source` varchar(30) DEFAULT '' COMMENT '订单来源，记录app，wap，pc来源',
  `part_ship_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:，1:部分发货',
  `promotion_id` int(11) NOT NULL DEFAULT '0' COMMENT '促销活动Id',
  `promotion_reduce` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '促销活动折扣金额,满折满减',
  `push_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'yadu推送状态：0-暂无推送，1-推送失败，2-推送成功',
  `push_desc` varchar(100) NOT NULL DEFAULT '' COMMENT 'yadu推送失败原因',
  `pos_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '门店订单标志：0：商城，1：门店同步订单',
  `pos_shop_name` varchar(191) NOT NULL DEFAULT '' COMMENT 'pos店铺名称',
  `store_id` int(11) DEFAULT '0' COMMENT '门店ID',
  `store_name` varchar(191) DEFAULT '' COMMENT '门店名称',
  `member_card_id` int(11) DEFAULT '0' COMMENT '会员卡ID',
  `member_card_reduce` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡优惠金额',
  `member_card_balance` decimal(10,2) DEFAULT '0.00' COMMENT '会员卡消费金额',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '订单支付过期时间',
  `del_time` timestamp NULL DEFAULT NULL COMMENT '订单删除时间',
  `prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付Id，用于发送模板消息',
  `deliver_type` tinyint(1) DEFAULT '0' COMMENT '配送类型：0 快递 1 自提',
  `deliver_type_name` varchar(191) DEFAULT NULL COMMENT '配送类型名称',
  `pickup_time` varchar(30) DEFAULT NULL COMMENT '自提时间',
  `star_flag` tinyint(1) DEFAULT '0' COMMENT '标星订单：0 未标星 1 标星',
  `verify_code` varchar(191) DEFAULT '' COMMENT '核销自提码',
  `split` int(11) DEFAULT '0' COMMENT '分裂优惠券id',
  `card_no` varchar(32) NOT NULL DEFAULT '' COMMENT '会员卡号',
  `fanli_money` decimal(10,2) DEFAULT '0.00' COMMENT '返利金额',
  `true_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `id_card` varchar(32) NOT NULL DEFAULT '' COMMENT '身份证号18位',
  `ali_trade_no` varchar(60) DEFAULT '' COMMENT '支付宝交易单号',
  `grouper_cheap_reduce` decimal(10,2) DEFAULT '0.00' COMMENT '团长优惠金额',
  `bk_shipping_time` timestamp NULL DEFAULT NULL COMMENT '定金预计发货时间',
  `bk_return_type` tinyint(2) DEFAULT NULL COMMENT '定金退款状态',
  `bk_prepay_id` varchar(191) DEFAULT NULL COMMENT '微信支付Id，用于发送模板消息',
  `pre_sale_discount` decimal(10,2) DEFAULT '0.00' COMMENT '定金膨胀优惠金额',
  `instead_pay_money` decimal(10,2) DEFAULT '0.00' COMMENT '代付金额',
  `order_user_message` varchar(50) DEFAULT NULL COMMENT '发起人留言',
  `instead_pay` text COMMENT '好友代付规则',
  `instead_pay_num` smallint(6) DEFAULT '0' COMMENT '代付人数',
  `is_promote` tinyint(1) DEFAULT '0' COMMENT '是否是推广单',
  `verifier_id` int(9) DEFAULT '0' COMMENT '核销员',
  `exchang` tinyint(2) DEFAULT '0' COMMENT '1 兑换 0否',
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
  `is_lock` tinyint(1) DEFAULT '0' COMMENT '是否锁库存，0否，1是',
  `score_proportion` int(9) DEFAULT '100' COMMENT '积分比例',
  `is_freeship_card` tinyint(1) DEFAULT '0' COMMENT '0否，1是',
  `room_id` int(11) DEFAULT '0' COMMENT '直播间ID',
  `order_medical_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单药品类型 0普通 1处方药',
  `order_audit_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单审核类型, 0不审核,1审核,2开方,3根据处方下单',
  `order_audit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单审核状态 0未审核 1审核通过 2审核不通过 ',
  `prescription_code_list` varchar(350)  NOT NULL DEFAULT '' COMMENT '处方号外键',
  `audit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '药品审核时间',
  `patient_id` int(11) NOT NULL DEFAULT '0' COMMENT '患者id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_sn` (`order_sn`),
  KEY `main_order_sn` (`main_order_sn`),
  KEY `user_id` (`user_id`),
  KEY `user_openid` (`user_openid`),
  KEY `order_status` (`order_status`),
  KEY `shipping_id` (`shipping_id`),
  KEY `shop_id` (`shop_id`)
)COMMENT='订单';


-- 退款订单
CREATE TABLE   `b2c_return_order_bak` (
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
  `is_auto_return` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0否；1是',
  `return_source` tinyint(1) DEFAULT '1' COMMENT '售后发起来源：0商家手动发起，1用户主动申请，2订单异常系统自动发起',
  `return_source_type` tinyint(1) DEFAULT '0' COMMENT '售后发起来源类型：0改价失败自动售后，1微信支付失败，2活动自动售后',
  PRIMARY KEY (`ret_id`),
  KEY `order_sn` (`order_sn`)
)COMMENT='退回订单表';


--退款订单商品
CREATE TABLE  `b2c_return_order_goods_bak` (
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

-- 店铺物流信息表
CREATE TABLE `b2c_shop_logistics` (
    `id` int(8) NOT NULL AUTO_INCREMENT,
    `shop_id` int(8) NOT NULL DEFAULT '0' COMMENT '店铺Id',
    `logistic_name` varchar(30) NOT NULL DEFAULT '' COMMENT '快递公司名称',
    `logistic_type` varchar(20) NOT NULL DEFAULT '' COMMENT '快递100type字段',
    `shipping_code` varchar(20) NOT NULL DEFAULT '',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未删除，1:已删除',
    PRIMARY KEY (`id`)
) COMMENT='店铺物流信息表';

