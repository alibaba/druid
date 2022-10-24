select employee_id from (select employee_id+1 as employee_id from employees)
   for update of employee_id skip locked

