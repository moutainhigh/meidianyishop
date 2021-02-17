ALTER TABLE b2c_store_goods DROP PRIMARY KEY;
ALTER TABLE b2c_store_goods add COLUMN id int not null PRIMARY KEY auto_increment first;
ALTER TABLE b2c_store_goods add INDEX(store_id,goods_id);