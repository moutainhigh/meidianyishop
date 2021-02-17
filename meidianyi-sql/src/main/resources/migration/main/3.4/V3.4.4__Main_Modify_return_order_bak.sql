ALTER TABLE `b2c_return_order_bak`DROP PRIMARY KEY,ADD PRIMARY KEY (`ret_id`, `shop_id`) ;
ALTER TABLE `b2c_return_order_goods_bak` DROP PRIMARY KEY, ADD PRIMARY KEY (`id`, `shop_id`) ;
