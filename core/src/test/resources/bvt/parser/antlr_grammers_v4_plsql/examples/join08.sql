select * from dual t1
join (select * from dual) tt2 using(dummy)
join (select * from dual) using(dummy)
join (select * from dual) d on(d.dummy=tt3.dummy)
inner join (select * from dual) tt2 using(dummy)
inner join (select * from dual) using(dummy)
inner join (select * from dual) d on(d.dummy=t1.dummy)

