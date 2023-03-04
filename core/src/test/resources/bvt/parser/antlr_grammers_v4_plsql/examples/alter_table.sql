alter table hr.employees
  add constraint emp_fk foreign key (department_id) 
  references hr.departments (department_id) 
  on delete cascade;

alter table hr.employees
  add foreign key (department_id) 
  references hr.departments (department_id) 
  on delete set null;

alter table hr.employees
  add constraint emp_fk foreign key (department_id, employee_id) 
  references hr.some_table (department_id, employee_id);

alter table employees
  add primary key (employee_id);

alter table hr.employees
  add constraint emp_complex_pk primary key (employee_id, department_id);

alter table hr.employees
  add constraint emp_uq unique (employee_id, email);

alter table employees
  add unique (employee_id);

ALTER TABLE suppliers
ADD CONSTRAINT check_supplier_name
  CHECK (supplier_name IN ('IBM', 'Microsoft', 'NVIDIA'));
  
ALTER TABLE suppliers
  DROP CONSTRAINT check_supplier_id;

ALTER TABLE suppliers
  ENABLE CONSTRAINT check_supplier_id;

ALTER TABLE suppliers
  DISABLE CONSTRAINT check_supplier_id;
