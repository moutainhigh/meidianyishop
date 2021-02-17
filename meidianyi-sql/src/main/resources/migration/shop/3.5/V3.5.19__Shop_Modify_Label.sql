ALTER TABLE `b2c_goods_label` ADD COLUMN `is_chronic` TINYINT(1) DEFAULT 0 COMMENT '是否慢性病： 1：是  0： 否';

create table `b2c_goods_chronic_couple`(
    `id`   int(11)      not null auto_increment,
    `goods_common_name`  varchar(50) not null default '' comment '药品通用名',
    `goods_form` varchar(50) not null default '' comment '药品剂型',
    `label_name` varchar(50) not null default '' comment '慢性标签名称',
    primary key(`id`)
)comment ='慢性病药品关联信息';
