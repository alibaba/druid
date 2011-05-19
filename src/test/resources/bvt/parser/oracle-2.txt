SELECT employee_id FROM (SELECT * FROM employees)
   FOR UPDATE OF employee_id;