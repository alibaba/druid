select * from v.e
where
	cid <> rid
	and  rid  not in
	(
		(select distinct  rid  from  v.s )
		union
		(select distinct  rid  from v.p )
	)
	and  "timestamp"  <= 1298505600000

