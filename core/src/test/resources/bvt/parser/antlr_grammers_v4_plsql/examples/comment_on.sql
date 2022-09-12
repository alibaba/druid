-- column

comment on column employees.employee_id is 'Primary key of employees table.';

comment on column hr.employees.employee_id is 'Multiline
comment on column.';

comment on column "hr"."employees"."employee_id" is 'Primary key of employees table.';

-- table

comment on table employees is 'employees table. Contains 107 rows.';

comment on table hr.employees is 'employees table. Contains 107 rows.';

comment on table "hr"."employees" is 'employees table. Contains 107 rows.';

comment on table "my schema"."my table" is 'Some demo table with space in its name
and a multiline comment.';