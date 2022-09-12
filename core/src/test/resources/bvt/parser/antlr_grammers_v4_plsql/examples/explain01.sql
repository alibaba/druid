explain plan 
    set statement_id = 'raise in tokyo' 
    into plan_table 
    for update employees 
        set salary = salary * 1.10 
        where department_id =  
           (select department_id from departments
               where location_id = 1700)
