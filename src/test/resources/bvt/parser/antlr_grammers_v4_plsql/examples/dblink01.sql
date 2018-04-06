select last_name, department_name 
   from employees@remote, departments
   where employees.department_id = departments.department_id


