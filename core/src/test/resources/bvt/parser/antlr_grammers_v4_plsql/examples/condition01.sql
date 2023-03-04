select department_id, last_name, salary 
 from employees x 
 where salary > (select avg(salary) 
 from employees 
 where x.department_id = department_id) 
 order by department_id

