create table tt_xx (
	f1 string,
f2 string comment 'xxx',
	f3 string comment 'xxx',
	gmt_first datetime comment '首次时间',
gmt_last datetime comment '末次时间',
	pv_cnt bigint comment 'PV数量',
	pv_day_cnt bigint comment 'PV天数'
)
comment 'xx comment xxx' partitioned by (pt string comment '按天分区字段', seg string comment '时间跨度')
;
---------------------------
create table tt_xx (
	f1 string,
	f2 string comment 'xxx',
	f3 string comment 'xxx',
	gmt_first datetime comment '首次时间',
	gmt_last datetime comment '末次时间',
	pv_cnt bigint comment 'PV数量',
	pv_day_cnt bigint comment 'PV天数'
)
comment 'xx comment xxx'
partitioned by (pt string comment '按天分区字段', seg string comment '时间跨度')
;