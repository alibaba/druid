select d.department_id, e.last_name
   from departments d, employees e
   where d.department_id = e.department_id(+)
   order by d.department_id, e.last_name


