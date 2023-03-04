with
  reports_to_101 (eid, emp_last, mgr_id, reportlevel) as
  (
     select employee_id, last_name, manager_id, 0 reportlevel
     from employees
     where employee_id = 101
     union all
     select e.employee_id, e.last_name, e.manager_id, reportlevel+1
     from reports_to_101 r, employees e
     where r.eid = e.manager_id
  )
select eid, emp_last, mgr_id, reportlevel
from reports_to_101 r, auto a
where r.c1 = a.c2
order by reportlevel, eid

