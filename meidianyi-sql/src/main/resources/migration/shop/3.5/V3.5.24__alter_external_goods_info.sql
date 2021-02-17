ALTER table b2c_goods_from_his add COLUMN is_match TINYINT(1) DEFAULT 0 AFTER state;
ALTER table b2c_goods_from_store add COLUMN is_match TINYINT(1) DEFAULT 0 AFTER state;

ALTER TABLE b2c_goods add COLUMN from_his_id int;
ALTER TABLE b2c_goods add COLUMN from_store_id int;
