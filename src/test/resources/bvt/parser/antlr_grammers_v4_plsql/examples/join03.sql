select d.department_id, e.last_name
   from m.departments d right outer join n.employees e
   on d.department_id = e.department_id
   order by d.department_id, e.last_name


