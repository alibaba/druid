select e1.last_name from employees e1
 where f( cursor(select e2.hire_date from employees e2 where e1.employee_id = e2.manager_id), e1.hire_date) = 1
order by last_name