select lpad(' ',2*(level-1)) || last_name org_chart, 
        employee_id, manager_id, job_id
    from employees
    where job_id != 'fi_mgr'
    start with job_id = 'ad_vp' 
    connect by prior employee_id = manager_id

