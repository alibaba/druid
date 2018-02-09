with 
   dept_costs as (
      select department_name, sum(salary) dept_total
         from employees e, departments d
         where e.department_id = d.department_id
      group by department_name),
   avg_cost as (
      select sum(dept_total)/count(*) avg
      from dept_costs)
select * from dept_costs
   where dept_total >
      (select avvg from avg_cost)
      order by department_name


