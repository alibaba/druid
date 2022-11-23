select *
 from employees x 
 where salary > (select avg(salary) from x)
 and 1 = 1
 and hiredate = sysdate
 and to_yminterval('01-00') < sysdate
 and to_yminterval('01-00') + x < sysdate

