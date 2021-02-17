CREATE TABLE `b2c_doctor_login_log` (
	`id` INT ( 11 ) NOT NULL auto_increment,
	`doctor_id` INT ( 8 ) DEFAULT NULL COMMENT '医师ID',
	`user_id` INT ( 8 ) DEFAULT NULL COMMENT '用户ID',
	`ip` VARCHAR ( 64 ) DEFAULT NULL COMMENT '用户登录ip',
	`lat` VARCHAR ( 64 ) DEFAULT NULL COMMENT '经度',
	`lng` VARCHAR ( 64 ) DEFAULT NULL COMMENT '纬度',
	`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
PRIMARY KEY ( `id` )
) COMMENT = '医师登录记录';
