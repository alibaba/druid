select * from dual t1
left outer join (select * from dual) tt2 using(dummy)
left outer join (select * from dual) using(dummy)
left outer join (select * from dual) d on(d.dummy=tt3.dummy)
inner join (select * from dual) tt2 using(dummy)
inner join (select * from dual) using(dummy)
inner join (select * from dual) d on(d.dummy=t1.dummy)

