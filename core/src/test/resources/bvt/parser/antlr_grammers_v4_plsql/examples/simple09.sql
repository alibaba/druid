select a||last_name,
        employee_id
    from employees
    start with job_id = 'ad_vp' 
    connect by prior employee_id = manager_id


