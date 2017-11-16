SELECT last_name, 
NVL(TO_CHAR(commission_pct), 'Not Applicable')  "COMMISSION" FROM employees
WHERE last_name LIKE 'B%'
ORDER BY last_name