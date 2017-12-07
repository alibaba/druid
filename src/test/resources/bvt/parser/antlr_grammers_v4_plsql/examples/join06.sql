select * from dual d1
join dual d2 on (d1.dummy = d2.dummy)
join dual d3 on(d1.dummy = d3.dummy)
join dual on(d1.dummy = dual.dummy)

