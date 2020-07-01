select * from
(
	(
		select * from dual
	)
	unpivot
	(
		value for value_type in (dummy)
	)
)

