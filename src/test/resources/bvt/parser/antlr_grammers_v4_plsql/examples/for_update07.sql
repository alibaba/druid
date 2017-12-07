select employee_id from (select employee_id+1 as employee_id from employees)
   for update of a, b.c, d skip locked

