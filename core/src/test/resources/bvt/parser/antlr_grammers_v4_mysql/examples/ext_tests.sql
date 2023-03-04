#begin
-- Intersections
-- -- Binary: charset and datatype
select _binary 'hello' as c1;
create table t1(col1 binary(20));
create table t2(col varchar(10) binary character set cp1251);
create table t2(col varchar(10) binary character set binary);
#end
#begin
-- -- Keywords, which can be ID. Intersect that keywords and ID
#end
#begin
-- Expression test
select +-!1 as c;
select 0 in (20 = any (select col1 from t1)) is not null is not unknown as t;
select 0 in (20 = any (select col1 from t1)) is not unknown as t;
select 20 = any (select col1 from t1) is not unknown as t;
select 20 = any (select col1 from t1) as t;
-- select sqrt(20.5) not in (sqrt(20.5) not in (select col1 from t1), 1 in (1, 2, 3, 4)) as c;
select 20 in (10 in (5 in (1, 2, 3, 4, 5), 1, 1, 8), 8, 8, 8);
select (1 in (2, 3, 4)) in (0, 1, 2) as c;
select 1 and (5 between 1 and 10) as c;

select 1 = 16/4 between 3 and 5 as c;
select 1 = 16/4 between 5 and 6 as c;
#end
#begin
-- Functions test
select *, sqrt(a), lower(substring(str, 'a', length(str)/2)) as col3 from tab1 where a is not \N;
#end
#begin
-- Spatial data type tests
INSERT INTO geom VALUES (GeomFromWKB(0x0101000000000000000000F03F000000000000F03F));
select y(point(1.25, 3.47)) as y, x(point(1.25, 3.47)) as x;
#end
