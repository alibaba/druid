(
	select distinct job_id from hr.jobs
)
union all
(
	select distinct job_id from hr.job_history
	union all
	((((
		select distinct job_id from hr.job_history
		union all
		(
			select distinct job_id from hr.job_history
		)
	)))
	union all
		select distinct job_id from hr.job_history	
	)	
)
union all
(
	select distinct job_id from hr.job_history
	union all
	(
		select distinct job_id from hr.job_history
		union all
		(
			select distinct job_id from hr.job_history
		)
	)
)
union all
(
	select distinct job_id from hr.job_history
	union all
	select distinct job_id from hr.job_history
)
union all
	select distinct job_id from hr.job_history
union all
	select distinct job_id from hr.job_history
union all
	select distinct job_id from hr.job_history
union all
	select distinct job_id from hr.job_history	

