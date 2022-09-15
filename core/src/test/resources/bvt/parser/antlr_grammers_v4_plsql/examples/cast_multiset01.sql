select t1.department_id, t2.* 
   from hr_info t1, table(cast(multiset(
      select t3.last_name, t3.department_id, t3.salary 
         from people t3
      where t3.department_id = t1.department_id)
      as people_tab_typ)) t2

