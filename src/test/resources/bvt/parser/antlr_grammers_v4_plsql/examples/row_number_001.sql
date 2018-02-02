SELECT department_id, last_name, employee_id, 
ROW_NUMBER()
OVER (PARTITION BY department_id ORDER BY employee_id) AS emp_id
FROM employees
