with
  reports_to_101 (eid, emp_last, mgr_id, reportlevel, mgr_list) 
  as
  (
     select employee_id, last_name, manager_id, 0 reportlevel
     , cast(manager_id as varchar2(2000))
     from employees
     where employee_id = 101
  union all
     select e.employee_id, e.last_name, e.manager_id, reportlevel+1
     , cast(mgr_list || ',' || manager_id as varchar2(2000))
     from reports_to_101 r, employees e
     where r.eid = e.manager_id
  )
select eid, emp_last, mgr_id, reportlevel, mgr_list
from reports_to_101
order by reportlevel, eid

