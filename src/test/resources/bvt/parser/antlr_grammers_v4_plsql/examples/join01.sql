select d.department_id as d_dept_id, e.department_id as e_dept_id, e.last_name
   from departments d full outer join employees e
   on d.department_id = e.department_id
   order by d.department_id, e.last_name

