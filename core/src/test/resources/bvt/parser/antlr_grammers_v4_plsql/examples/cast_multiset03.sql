 select
 1
 , cursor(select 1 from dual) c1
 , cursor(select 2, 3 from dual) as c2
 from
 table(select 1 from dual)

