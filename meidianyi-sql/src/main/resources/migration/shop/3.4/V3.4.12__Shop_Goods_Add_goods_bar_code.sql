ALTER TABLE b2c_goods add COLUMN goods_bar_code VARCHAR(128) AFTER goods_sn;
-- 科室信息统计
create table `b2c_department_summary_trend` (
    `id`                   int(11)        not null auto_increment,
    `ref_date`             date           not null comment '2018-09-04',
    `type`                 tinyint(2)     not null comment '1,7,30,90',
    `department_id`             int(8)          default null comment '科室ID',
    `consultation_number`   int(11)        not null comment '接诊人数',
    `inquiry_money`     decimal(10, 2)      default null comment '咨询金额',
    `inquiry_number`        int(11) default 0  null comment '咨询单数',
    `prescription_money`     decimal(10, 2)      default null comment '处方金额',
    `prescription_num`       int(11) default 0  null comment '处方数',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (`id`),
    key `ref_type` (`ref_date`, `type`) using btree
)comment ='科室信息统计';