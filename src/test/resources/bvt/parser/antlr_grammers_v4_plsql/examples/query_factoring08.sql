with -- qf
	x1 as ( select * from t1 ),
	x2 as ( select * from t2 join t3 on (t2.a2 = t3.a3))
select
	*
from
	x1
join	x2 on (x1.a1 = x2.a2)
join	t4 on (x1.a1 = t4.a4)

