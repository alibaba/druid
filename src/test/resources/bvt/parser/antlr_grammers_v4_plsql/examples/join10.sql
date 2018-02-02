select * from dual t1,
       	      (
		dual left outer join (select * from dual) tt2 using(dummy)
		)

