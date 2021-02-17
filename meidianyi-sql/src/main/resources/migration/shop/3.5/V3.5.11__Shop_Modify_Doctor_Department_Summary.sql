ALTER TABLE `b2c_doctor_summary_trend` ADD COLUMN `consultation_score` decimal(10, 2) default '0.00' comment '接诊分数' AFTER `consume_money`;
ALTER TABLE `b2c_doctor_summary_trend` ADD COLUMN `inquiry_score` decimal(10, 2) default '0.00' comment '咨询分数' AFTER `consultation_score`;
ALTER TABLE `b2c_department_summary_trend` ADD COLUMN `consultation_score` decimal(10, 2) default '0.00' comment '接诊分数' AFTER `prescription_num`;
ALTER TABLE `b2c_department_summary_trend` ADD COLUMN `inquiry_score` decimal(10, 2) default '0.00' comment '咨询分数' AFTER `consultation_score`;