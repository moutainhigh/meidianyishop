ALTER TABLE b2c_goods_medical_info drop COLUMN goods_composition;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_characters;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_function;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_use_method;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_adverse_reaction;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_taboos;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_notice_event;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_interaction;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_store_method;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_package_method;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_valid_time;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_limit_duty;
ALTER TABLE b2c_goods_medical_info drop COLUMN goods_limit_antibacterial;

ALTER TABLE b2c_goods_medical_info ADD COLUMN goods_medical_instruction TEXT AFTER goods_production_enterprise;