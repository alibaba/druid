with
  org_chart (eid, emp_last, mgr_id, reportlevel, salary, job_id) as
  (
    select employee_id, last_name, manager_id, 0 reportlevel, salary, job_id
    from employees
    where manager_id is null
  union all
    select e.employee_id, e.last_name, e.manager_id,
           r.reportlevel+1 reportlevel, e.salary, e.job_id
    from org_chart r, employees e
    where r.eid = e.manager_id
  )
  search depth first by emp_last set order1
select lpad(' ',2*reportlevel)||emp_last emp_name, eid, mgr_id, salary, job_id
from org_chart
order by order1


