with timegrouped_rawdata
as
(
	select /*+ leading(sn di md sh ot) cardinality(ot 1000) */
	sh.metric_id as metric_id ,
	ot.bsln_guid as bsln_guid ,
	ot.timegroup as timegroup ,
	sh.value as obs_value
	from
	dba_hist_snapshot sn ,
	dba_hist_database_instance di ,
	sys.wrh$_sysmetric_history sh ,
	bsln_metric_defaults md ,
	table(:b1 ) ot
	where sn.dbid = :b6
	and sn.snap_id between :b5 and :b4
	and di.dbid = sn.dbid
	and di.instance_number = sn.instance_number
	and di.startup_time = sn.startup_time
	and di.instance_name = :b3
	and sh.snap_id = sn.snap_id
	and sh.dbid = sn.dbid
	and sh.instance_number = sn.instance_number
	and sh.group_id = 2
	and sh.metric_id = md.metric_id
	and md.status = :b2
	and ot.obs_time = trunc(sh.end_time, 'hh24')
)
(
select
	bsln_statistics_t
	(
		bsln_guid ,metric_id ,:b11 ,:b10 ,timegroup ,sample_count ,average ,minimum ,maximum ,sdev ,
		pctile_25 ,pctile_50 ,pctile_75 ,pctile_90 ,pctile_95 ,pctile_99 ,est_sample_count ,est_slope ,est_intercept ,
		case when est_slope = 0 then 0 else greatest(0,nvl(100-(25*power((1-est_mu1/est_slope), 2)*(est_sample_count-1) ),0)) end ,
		ln( 1000) * est_slope + est_intercept ,
		ln(10000) * est_slope + est_intercept
	)
	from
	(
		select metric_id ,bsln_guid ,timegroup ,est_mu as est_slope ,est_mu * ln(alpha) + x_m as est_intercept ,to_number(null) as est_fit_quality ,
		case when count_below_x_j > 0 then (sum_below_x_j + (n-m+1)*(x_j-x_m))/count_below_x_j - x_j else to_number(null) end as est_mu1 ,
		est_sample_count ,n as sample_count ,average ,minimum ,maximum ,sdev ,pctile_25 ,pctile_50 ,pctile_75 ,pctile_90 ,pctile_95 ,pctile_99
		from
		(
			select metric_id ,bsln_guid ,timegroup ,max(n) as n ,count(rrank) as est_sample_count ,
			case when count(rrank) > 3 then ( sum(obs_value) + ( max(n) - max(rrank) ) * max(obs_value) - (max(n) - min(rrank) + 1) * min(obs_value) ) / (count(rrank)-1)
			else to_number(null) end as est_mu ,
			(max(n) - min(rrank) + 1) / (max(n) + 1) as alpha ,
			min(obs_value) as x_m ,max(obs_value) as x_l ,max(rrank) as l ,min(rrank) as m ,max(mid_tail_value) as x_j ,
			sum(case when obs_value < mid_tail_value then obs_value else 0 end ) as sum_below_x_j ,
			sum(case when cume_dist < :b7 then 1 else 0 end ) as count_below_x_j ,
			max(max_val) as maximum ,max(min_val) as minimum ,max(avg_val) as average ,max(sdev_val) as sdev ,max(pctile_25) as pctile_25 ,max(pctile_50) as pctile_50 ,
			max(pctile_75) as pctile_75 ,max(pctile_90) as pctile_90 ,max(pctile_95) as pctile_95 ,max(pctile_99) as pctile_99
			from
			(
				select metric_id ,bsln_guid ,timegroup ,obs_value as obs_value,
				cume_dist () over (partition by metric_id, bsln_guid, timegroup order by obs_value ) as cume_dist ,
				count(1) over (partition by metric_id, bsln_guid, timegroup ) as n ,
				row_number () over (partition by metric_id, bsln_guid, timegroup order by obs_value) as rrank ,
				percentile_disc(:b7 ) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as mid_tail_value ,
				max(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as max_val ,
				min(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as min_val ,
				avg(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as avg_val ,
				stddev(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as sdev_val ,
				percentile_cont(0.25) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_25 ,
				percentile_cont(0.5) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_50 ,
				percentile_cont(0.75) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_75 ,
				percentile_cont(0.90) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_90 ,
				percentile_cont(0.95) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_95 ,
				percentile_cont(0.99) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_99
				from timegrouped_rawdata d
			) x
			where x.cume_dist >= :b9 and x.cume_dist <= :b8
			group by metric_id ,bsln_guid ,timegroup
		)
	)
)
