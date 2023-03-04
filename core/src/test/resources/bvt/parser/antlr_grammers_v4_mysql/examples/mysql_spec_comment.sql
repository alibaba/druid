#begin
-- Mysql spec comment
select 1 /*!, ' hello' */, 2 /*! union select 5, ' world', 10 */;
select * from t /*! where col = somefunc(col2) order by sortcol */; insert into mytable /*!(col2, col3, col1) */ values (load_file('sompath'), 'str1', 2);
insert into tbl values ('a', 1, 'b'), ('c', 2, 'd'), ('e', 3, 'f') /*! on duplicate key update notsecret_col = secret_col */;
select clientname, email from users where clientname='Petrov'/*! UNION SELECT 1,load_file('/etc/passwd')*/;#
#end
#begin
-- Duplicate query with ordinal comment
select 1 /*, ' hello' */, 2 /*! union select 5, ' world', 10 */;
select * from t /* where col = somefunc(col2) order by sortcol */; insert into mytable /*(col2, col3, col1) */ values (load_file('sompath'), 'str1', 2);
insert into tbl values ('a', 1, 'b'), ('c', 2, 'd'), ('e', 3, 'f') /* on duplicate key update notsecret_col = secret_col */;
select clientname, email from users where clientname='Petrov'/* UNION SELECT 1,load_file('/etc/passwd')*/;#
#end

#begin
-- Empty line comment
--
--
#end