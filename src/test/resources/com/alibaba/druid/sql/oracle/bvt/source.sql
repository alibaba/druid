SELECT CAST(manager_id AS VARCHAR2(2000)), department_name, SUM(salary) dept_total
         FROM employees e, departments d
         WHERE e.department_id = d.department_id
      GROUP BY department_name;
      
      
 SELECT employee_id, last_name, manager_id, 0 reportLevel,
        CAST(manager_id AS VARCHAR2(2000))
 FROM employees
 WHERE employee_id = 101
  UNION ALL
 SELECT e.employee_id, e.last_name, e.manager_id, reportLevel+1,
        CAST(mgr_list || ',' || manager_id AS VARCHAR2(2000))
 FROM reports_to_101 r, employees e
 WHERE r.eid = e.manager_id;
     
 SELECT * 
   FROM employees
   WHERE job_id = 'PU_CLERK' 
   ORDER BY salary DESC; 
 
SELECT last_name, department_id, salary
   FROM employees
   ORDER BY department_id ASC, salary DESC, last_name; 
 
 SELECT last_name, department_id, salary 
   FROM employees 
   ORDER BY 2 ASC, 3 DESC, 1; 
   
SELECT e.employee_id, e.salary, e.commission_pct
   FROM employees e, departments d
   WHERE job_id = 'SA_REP'
   AND e.department_id = d.department_id
   AND location_id = 2500
   FOR UPDATE
   ORDER BY e.employee_id;
   
SELECT last_name, department_id FROM employees
   WHERE department_id =
     (SELECT department_id FROM employees
      WHERE last_name = 'Lorentz')
   ORDER BY last_name, department_id; 
   
UPDATE employees 
    SET salary = salary * 1.1
    WHERE employee_id IN (SELECT employee_id FROM job_history);
    
SELECT e1.last_name||' works for '||e2.last_name 
   "Employees and Their Managers"

   FROM employees e1, employees e2 
   WHERE e1.manager_id = e2.employee_id
      AND e1.last_name LIKE 'R%'
   ORDER BY e1.last_name;

SELECT d.department_id, e.last_name
   FROM departments d LEFT OUTER JOIN employees e
   ON d.department_id = e.department_id
   ORDER BY d.department_id, e.last_name;
   
SELECT d.department_id, e.last_name
   FROM departments d, employees e
   WHERE d.department_id = e.department_id(+)
   ORDER BY d.department_id, e.last_name;

SELECT d.department_id, e.last_name
   FROM departments d RIGHT OUTER JOIN employees e
   ON d.department_id = e.department_id
   ORDER BY d.department_id, e.last_name;

SELECT d.department_id as d_dept_id, e.department_id as e_dept_id,
      e.last_name
   FROM departments d FULL OUTER JOIN employees e
   ON d.department_id = e.department_id
   ORDER BY d.department_id, e.last_name;

SELECT department_id AS d_e_dept_id, e.last_name
   FROM departments d FULL OUTER JOIN employees e
   USING (department_id)
   ORDER BY department_id, e.last_name;

INSERT INTO inventory VALUES (TO_DATE('01/04/01', 'DD/MM/YY'), 'bottle', 10);
INSERT INTO inventory VALUES (TO_DATE('06/04/01', 'DD/MM/YY'), 'bottle', 10);
INSERT INTO inventory VALUES (TO_DATE('01/04/01', 'DD/MM/YY'), 'can', 10);
INSERT INTO inventory VALUES (TO_DATE('04/04/01', 'DD/MM/YY'), 'can', 10);

SELECT times.time_id, product, quantity FROM inventory 
   RIGHT OUTER JOIN times ON (times.time_id = inventory.time_id) 
   WHERE times.time_id BETWEEN TO_DATE('01/04/01', 'DD/MM/YY') 
      AND TO_DATE('06/04/01', 'DD/MM/YY') 
   ORDER BY  2,1; 

SELECT * FROM employees 
   WHERE department_id NOT IN 
   (SELECT department_id FROM departments 
       WHERE location_id = 1700)
   ORDER BY last_name;

SELECT LPAD(' ',2*(LEVEL-1)) || last_name org_chart, 
        employee_id, manager_id, job_id
    FROM employees
    START WITH job_id = 'AD_VP' 
    CONNECT BY PRIOR employee_id = manager_id; 

SELECT LPAD(' ',2*(LEVEL-1)) || last_name org_chart, 
employee_id, manager_id, job_id 
    FROM employees
    START WITH job_id = 'AD_PRES' 
    CONNECT BY PRIOR employee_id = manager_id AND LEVEL <= 2; 

SELECT department_id, last_name, salary 
   FROM employees x 
   WHERE salary > (SELECT AVG(salary) 
      FROM employees 
      WHERE x.department_id = department_id) 
   ORDER BY department_id; 

SELECT SYSDATE FROM DUAL; 

SELECT employees_seq.nextval 
    FROM DUAL; 
SELECT employees_seq.currval 
    FROM DUAL; 
    