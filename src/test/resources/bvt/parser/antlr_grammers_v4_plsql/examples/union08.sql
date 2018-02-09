select * from dual where exists (
	select * from dual
	union all
	select * from dual
)
