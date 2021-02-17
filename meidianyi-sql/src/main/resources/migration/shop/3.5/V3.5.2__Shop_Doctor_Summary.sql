create table `b2c_doctor_summary_trend` (
  `id`                   int(11)        not null auto_increment,
  `ref_date`             date           not null comment '2018-09-04',
  `type`                 tinyint(2)     not null comment '1,7,30,90',
  `doctor_id`             int(8)          default null comment '医师ID',
  `consultation_number`   int(11)        not null comment '接诊量',
  `inquiry_money`     decimal(10, 2)      default null comment '咨询金额',
  `inquiry_number`        int(11) default 0  null comment '咨询单数',
  `prescription_money`     decimal(10, 2)      default null comment '处方金额',
  `prescription_num`       int(11) default 0  null comment '处方数',
  `consume_money`     decimal(10, 2)      default null comment '总消费金额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  primary key (`id`),
  key `ref_type` (`ref_date`, `type`) using btree
)comment ='医师信息统计';
