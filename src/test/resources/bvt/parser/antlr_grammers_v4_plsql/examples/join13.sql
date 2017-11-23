select * from t1,
       ((((
       	t2 left outer join t3 using(dummy)
	))))
	