select * from
(
select * from a 
	unpivot
	(
		value for value_type in (dummy)
	)
)

