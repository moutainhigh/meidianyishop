-- 店铺增加 宣传图和文案
ALTER TABLE `b2c_shop` ADD COLUMN `publicity_img` varchar(512) NULL DEFAULT '' COMMENT '店铺宣传图' AFTER `store_clerk_privilege_list`;
ALTER TABLE `b2c_shop` ADD COLUMN `copywriting` text NULL COMMENT '店铺详情文案' AFTER `publicity_img`;