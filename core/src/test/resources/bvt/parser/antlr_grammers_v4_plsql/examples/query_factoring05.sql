with 
x1 as (select max(y1) from klm1),
x2 as (select max(y2) from klm2),
x3 as (select max(y3) from klm3),
x4 as (select max(y4) from klm4)
select
 distinct
 -1,
 +1,
 a + b * (a * d) as aaa,
 t1.region_name,
 t2.division_name,
 t1.region_name as a,
 t2.division_name as aaaa,
 a.*,
 sum(t3.amount),
 sum(count(1)) + count(*)
 , sum(1) + (select count(1) from ddd) a
from dual, fff
where a is null 
or b is not null 
and ( a like 'd')
and 1 = 0
and a.b is a set
union
select a from dual

