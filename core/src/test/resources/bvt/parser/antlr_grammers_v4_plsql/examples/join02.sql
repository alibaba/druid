select department_id as d_e_dept_id, e.last_name
   from departments d full outer join employees e
   using (department_id)
   order by department_id, e.last_name


