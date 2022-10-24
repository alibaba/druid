select
	decode(pid, null, null, max(program)) program , decode(pid, null, null, max(background)) background , sum(pga_used_mem) pga_used_mem ,
	sum(pga_alloc_mem) pga_alloc_mem , sum(pga_freeable_mem) pga_freeable_mem , max(pga_alloc_mem) max_pga_alloc_mem , max(pga_max_mem) max_pga_max_mem ,
	decode(pid, null, avg(pga_alloc_mem), null) avg_pga_alloc_mem , decode(pid, null, stddev(pga_alloc_mem), null) stddev_pga_alloc_mem ,
	decode(pid, null, count(pid), null) num_processes
from v$process
where program != 'pseudo'
group by grouping sets ( (), ((pid+1), serial#) )


														