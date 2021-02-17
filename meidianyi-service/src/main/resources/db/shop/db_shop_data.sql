#支付方式
insert into `b2c_payment` (`id`, pay_name, pay_code, pay_fee, pay_desc, enabled, is_cod, is_online_pay)
values ('1', '微信支付', 'wxpay', '0', '基于微信账号的支付', '1', '0', '1'),
  ('2', '积分支付', 'score', '0', '积分支付', '1', '1', '0'),
  ('3', '余额支付', 'balance', '0', '使用帐户余额支付。只有会员才能使用，通过设置信用额度，可以透支。', '1', '0', '1'),
  ('4', '货到付款', 'cod', '0', '开通城市：×××\n货到付款区域：×××', '0', '1', '0'),
  ('5', '储值卡支付', 'membercard', '0', '储值卡支付', '0', '1', '0'),
  ('6', '支付宝支付', 'ali_mini_pay', '0', '基于支付宝账号的支付', '0', '0', '1');



## 快递数据
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (1, 'sto_express', 'shentong', '申通快递', '江、浙、沪地区首重为15元/KG，其他地区18元/KG， 续重均为5-6元/KG， 云南地区为8元', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (2, 'yto', 'yuantong', '圆通速递', '上海圆通物流（速递）有限公司经过多年的网络快速发展，在中国速递行业中一直处于领先地位。为了能更好的发展国际快件市场，加快与国际市场的接轨，强化圆通的整体实力，圆通已在东南亚、欧美、中东、北美洲、非洲等许多城市运作国际快件业务', '0', 1, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (3, 'yunda', 'yunda', '韵达快运', '韵达快递', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (4, 'zto_express', 'zhongtong', '中通快递', '中通快递', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (5, 'EMS', 'ems', 'EMS邮政特快专递', 'EMS邮政特快专递', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (6, 'sf_express', 'shunfeng', '顺丰速运', '顺丰', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (7, 'best_express', 'huitongkuaidi', '百世汇通', '汇通', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (8, 'ttk_epxress', 'tiantian', '天天快递', '天天', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (9, 'zjs_express', 'zhaijisong', '宅急送', '宅急送', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (10, 'china_post', 'youzhengguonei', '邮政包裹/平邮', '邮政包裹/平邮', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (11, 'debang_express', 'debangwuliu', '德邦物流', '德邦', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (12, 'quanfeng_express', 'quanfengkuaidi', '全峰快递', '全峰', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (13, 'post_express', '', '邮政小包/E邮宝', '小包', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (14, 'chengshi100', 'city100', '城市100', '城市100', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (15, 'yufeng_express', 'rufengda', '如风达', '凡客如风达', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (16, 'guotong_express', 'guotongkuaidi', '国通快递', '国通快递', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (17, 'jindong_express', 'jd', '京东快递', '京东快递', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (18, 'longbang_express', 'longbanwuliu', '龙邦快递', '龙邦', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (19, 'nengdao_express', 'ganzhongnengda', '能达速递', '能达', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (20, 'qianfeng_express', 'quanfengkuaidi', '全峰快递', '全峰', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (21, 'suer_express', 'sue', '速尔快递', '速尔', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (22, 'uc_express', 'youshuwuliu', '优速快递', '优速', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (23, 'fpd', '', '运费到付', '所购商品到达即付运费', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping`(`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (24, 'other_express', '', '其它快递', '其它', '0', 0, 1, '', 0, 0);
INSERT INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (25, 'DISTRIBUTOR_906984', '', '华东配-万象', '落地配 华东华北华南', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (26, 'DISTRIBUTOR_13363915', '', '安鲜达供应链', '落地配 全国', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (27, 'DISTRIBUTOR_901497', '',  '晟邦物流-生鲜仓配-华东区域', '落地配 华东、华北', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (28, 'DISTRIBUTOR_11113521', '',  '成都东骏-生鲜仓配', '落地配 西南', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (29, 'DISTRIBUTOR_13195952', '',  '菜鸟瞄鲜生', '顺丰 全国', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (30, 'DISTRIBUTOR_13174102', '',  'D速', '落地配 青岛仓', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (31, 'DISTRIBUTOR_1256888', '',  '芝麻开门落地配', '落地配 华东、福州仓', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (32, 'DISTRIBUTOR_13456083', '',  '芝麻开门-菜鸟生鲜-安鲜达', '快递 全国', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (33, 'DISTRIBUTOR_13494868', '',  '河北新智慧', '快递 河北区域', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (34, 'jieanda', 'jieanda',  '捷安达国际速递', '捷安达国际速递', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (35, 'PJ', 'pjbest',  '品骏物流', '品骏物流', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (36, 'topspeedex', 'topspeedex',  '中运全速', '中运全速', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (37, 'xdexpress', 'xdexpress',  '迅达速递', '迅达速递', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (38, 'ewe', 'ewe',  'EWE全球快递', 'EWE全球快递', '0', 0, 1, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (39, 'exfresh',  '', '中欧物流', '中欧物流', '0', 0, 0, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (40, 'aae', 'aae''',  'AAE全球专递', 'AAE全球专递', '0', 0, 0, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (41, 'annengwuliu', '',  '安鲜达单', '安鲜达单', '0', 0, 0, '', 0, 0);
INSERT  INTO `b2c_shipping` (`shipping_id`, `shipping_code`, `express100_code`, `shipping_name`, `shipping_desc`, `insure`, `support_cod`, `enabled`, `shipping_print`, `print_model`, `shipping_order`) VALUES (42, 'no_number_express', '',  '无单号物流', '无单号物流', '0', 0, 1, '', 0, 0);

#底部导航
INSERT INTO `b2c_shop_cfg`( k, v) VALUES ('bottom', '[{\"text\":\"首页\",\"btn\":0,\"normal\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_no_2.png\",\"hover\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_yes_2.png\",\"page\":\"pages/index/index\"},{\"text\":\"门店\",\"btn\":0,\"normal\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_no_1.png\",\"hover\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_yes_1.png\",\"page\":\"pages/store/store\"},{\"text\":\"购物车\",\"btn\":0,\"normal\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_no_3.png\",\"hover\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_yes_3.png\",\"page\":\"pages/cart/cart\"},{\"text\":\"个人中心\",\"btn\":0,\"normal\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_no_4.png\",\"hover\":\"http://jmpdevimg.weipubao.cn/image/admin/icon_mps/icon_yes_4.png\",\"page\":\"pages/usercenter/usercenter\"}]');

INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('express',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('fetch',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('drawback_type',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('drawback_days',3);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('order_timeout_days',7);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('page','0,1');
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('ship_is_free',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('ship_fee',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('start_ship_order_gmv',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_limit',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_protect',null);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_day',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_month',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_year',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_type',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('score_login',0);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('invoice',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('cancel_time',30);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('service_comment',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('bind_mobile',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('comment',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('store_buy',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('sales_number',1);
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('shop_style','{\"shopStyleId\":0,\"shopStyleValue\":\" rgb(255, 209, 209), rgb(255, 102, 102)\"}');
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('share_config','');
#个人中心
INSERT INTO `b2c_shop_cfg`(k, v) VALUES ( 'user_center', '[{\"module_name\":\"global\",\"page_style\":\"2\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\"},{\"module_name\":\"center_header\",\"is_show\":\"1\",\"bg_type\":\"1\",\"bg_img\":\"/upload/224462/image/20200706/fjpZM4icNO495xO1T3Ms.jpeg\"},{\"title\":\"我的资产\",\"content\":[{\"title\":null,\"icon_name\":\"account\",\"is_show\":\"1\"},{\"title\":null,\"icon_name\":\"score\",\"is_show\":\"1\"},{\"title\":null,\"icon_name\":\"coupon\",\"is_show\":\"1\"},{\"title\":null,\"icon_name\":\"card\",\"is_show\":\"1\"}],\"module_name\":\"account_money\",\"is_show\":\"0\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\"},{\"title\":\"我的订单\",\"content\":[{\"title\":null,\"icon\":\"/image/admin/uc_config/uc_order_icon1.png\",\"icon_name\":\"wait_pay\",\"is_show\":\"1\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/uc_order_icon2.png\",\"icon_name\":\"wait_confirm\",\"is_show\":\"1\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/uc_order_icon3.png\",\"icon_name\":\"wait_delivery\",\"is_show\":\"1\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/uc_order_icon4.png\",\"icon_name\":\"shipped\",\"is_show\":\"1\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/uc_order_icon5.png\",\"icon_name\":\"returning\",\"is_show\":\"1\"}],\"module_name\":\"order\",\"is_show\":\"1\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\",\"module_style\":\"1\"},{\"title\":\"我的预约\",\"module_name\":\"appointment\",\"is_show\":\"0\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\"},{\"title\":\"当前就诊人\",\"module_name\":\"current_patient\",\"is_show\":\"1\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\"},{\"title\":\"使用记录\",\"module_name\":\"use_record\",\"is_show\":\"0\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\",\"is_show_collect\":\"1\",\"is_show_buy_history\":\"1\",\"is_show_footprint\":\"1\"},{\"title\":\"我的服务\",\"content\":[{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_dis.png\",\"link\":\"\",\"icon_name\":\"distribution\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_bargain.png\",\"link\":\"\",\"icon_name\":\"bargain\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_award.png\",\"link\":\"\",\"icon_name\":\"award\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_comment.png\",\"link\":\"\",\"icon_name\":\"comment_list\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_store.png\",\"link\":\"\",\"icon_name\":\"store_list\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_member.png\",\"link\":\"\",\"icon_name\":\"user_activate\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_scan.png\",\"link\":\"\",\"icon_name\":\"order_verify\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":null,\"icon\":\"/image/admin/uc_config/icon_pre.png\",\"link\":\"\",\"icon_name\":\"present_list\",\"is_show\":\"0\",\"link_name\":\"\"},{\"title\":\"退款/售后\",\"icon\":\"/upload/224462/image/20200708/jAWsMBmvQzibZSfIn8wD.png\",\"link\":\"/pages/orderlist/orderlist\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"},{\"title\":\"我的病历\",\"icon\":\"/upload/224462/image/20200707/YeXrMlT0naRSUwkH9CWo.png\",\"link\":\"/pages1/medicalrecordlist/medicalrecordlist\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"},{\"title\":\"我的处方\",\"icon\":\"/upload/224462/image/20200706/HT4IJuuMG3xedGkZx5T1.png\",\"link\":\"/pages1/prescriptionlist/prescriptionlist\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"},{\"title\":\"我的处方药\",\"icon\":\"/upload/224462/image/20200707/JC4wrSIbevpQP0xFxGzJ.png\",\"link\":\"/pages/search/search?pageFrom=101\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"},{\"title\":\"我的家人\",\"icon\":\"/upload/224462/image/20200707/zMdLoqpMye2Gh8o7bGi1.png\",\"link\":\"/pages1/familylist/familylist\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"},{\"title\":\"我的消息\",\"icon\":\"/upload/224462/image/20200723/7hfk9555PCzEwaaB1Pyn.png\",\"link\":\"/pages1/messagelist/messagelist\",\"icon_name\":\"custom_icon\",\"is_show\":\"1\",\"link_name\":\"\"}],\"module_name\":\"service\",\"is_show\":\"1\",\"bg_img\":\"/upload/245547/image/20191017/W3sS1ZSsQPffNP3tDI4U.jpg\"}]');
#好友代付
INSERT INTO `b2c_shop_cfg`(k, v) VALUES ('instead_pay', '{\"status\":false,\"singlePay\":true,\"orderUserMessageSingle\":\"哎吆，不错哦！\",\"insteadPayMessageSingle\":\"哎吆，不错哦！帮你付款啦！\",\"multiplePay\":true,\"orderUserMessageMultiple\":\"谁能帮我\",\"insteadPayMessageMultiple\":\"哈哈，代付下\",\"payRatioText\":[\"意思意思\",\"情比金坚\",\"一掷千金\"],\"payRatioNumber\":[10,20],\"insteadPayWay\":0}');
#运费模板
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('deliver_template','{\"templateName\":0,\"feeLimit\":0,\"price\":0}');
#商品分词搜索配置
INSERT INTO `b2c_shop_cfg`(k,v) VALUES ('accurate_search',0);
#商品推荐分类配置
INSERT INTO `b2c_shop_cfg`(k,v) VALUES('recommend_sort','{\"recommendSortStatus\":0,\"recommendSortImg\":null,\"recommendSortImgPath\":null,\"recommendImgLink\":null}');
#模版消息
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2001, 0, 0);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2002, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2003, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2004, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2005, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2006, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2007, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2008, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2009, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2010, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2011, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (2012, 1, 1);
INSERT INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2013, 1, 1);
INSERT INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2014, 1, 1);
INSERT INTO `b2c_message_template_config` (`id`, `open_ma`, `open_mp`) VALUES (2015, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3001, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3002, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3003, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3004, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3005, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3006, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3007, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3008, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3009, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3010, 1, 1);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3011, 0, 0);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3012, 0, 0);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3013, 0, 0);
INSERT INTO `b2c_message_template_config`(`id`, `open_ma`, `open_mp`) VALUES (3014, 0, 0);
