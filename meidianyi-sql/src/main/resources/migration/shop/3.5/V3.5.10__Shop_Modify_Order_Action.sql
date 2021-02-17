ALTER TABLE `b2c_order_action` ADD COLUMN `account_id`  int(11) NOT NULL DEFAULT '0' COMMENT '操作人门店账户id' AFTER `action_note`;
