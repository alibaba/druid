 select title 
 from
 table(select courses from department where name = 'history')
 where name like '%etruscan%'
		

