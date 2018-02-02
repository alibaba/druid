#begin
-- common conustructions tests
-- -- Literals
-- -- -- String literal
SELECT 'hello world';
select N'testing conflict on N - spec symbol and N - as identifier' as n;
select n'abc' as tstConstrN;
select N'abc' "bcd" 'asdfasdf' as tstConstNAndConcat;
select 'afdf' "erwhg" "ads" 'dgs' "rter" as tstDiffQuoteConcat;
select 'some string' COLLATE latin1_danish_ci as tstCollate;
select _latin1'some string' COLLATE latin1_danish_ci as tstCollate;
select '\'' as c1, '\"' as c2, '\b' as c3, '\n' as c4, '\r' as c5, '\t' as c6, '\Z' as c7, '\\' as c8, '\%' as c9, '\_' as c10;
#end
#begin
-- -- -- String literal spec symbols
-- bug: two symbols ' afer each other: ''
select '\'Quoted string\'' col1, 'backslash \\ ' ', two double quote "" ' ', two single quote ''' as col2;
select '\'Quoted string\' ' col1, 'backslash \\ ' ', two double quote "" ' ', two single quote ''' as col2;
select * from `select` where `varchar` = 'abc \' ' and `varchar2` = '\'bca';
#end
#begin
-- -- -- Number literal
SELECT 1;
select 1.e-3 as 123e;
select del1.e123 as c from del1;
select -1, 3e-2, 2.34E0;
SELECT -4.1234e-2, 0.2e-3 as c;
SELECT .1e10;
SELECT -.1e10;
select 15e3, .2e5 as col1;
select .2e3 c1, .2e-4 as c5;
#end
#begin
-- -- -- Number float collision test
select t1e2 as e1 from t;
# select 1e2t as col from t; -- not support
#end
#begin
-- -- -- Hexadecimal literal
select X'4D7953514C';
select x'4D7953514C';
select 0x636174;
select 0x636174 c1;
select x'4D7953514C' c1, 0x636174 c2;
select x'79' as `select`, 0x2930 cc, 0x313233 as c2;

#end
#begin
-- -- -- Null literal
SELECT null;
SELECT not null;
#select \N;
#select ((\N));
select not ((\N));
#end
#begin
-- -- -- mixed literals
select \N as c1, null as c2, N'string';
select 4e15 colum, 'hello, ' 'world', X'53514C';
select 'abc' ' bcd' ' \' \' ' as col, \N c2, -.1e-3;
#end

#begin
-- -- Variables
SELECT @myvar;
#end

#begin
-- select_column tests
select * from `select`;
select *, `select`.*, `select`.* from `select`;
select *, 'abc' from `select`;
select *, 1, \N, N'string' 'string2' from `select`;
#end

#begin
-- UNION tests
select 1 union select 2 limit 0,5;
select * from (select 1 union select 2 union select 0) as t order by 1 limit 0,10;
select col1 from t1 union select * from (select 1 as col2) as newt;
select col1 from t1 union (select * from (select 1 as col2) as newt);
select 1 as c1 union (((select 2)));
#end
#begin
-- -- -- subquery in UNION
select 1 union select * from (select 2 union select 3) as table1;
select 1 union (select * from (select 2 union select 3) as table1);
#end
#begin
-- subquery FROM
select * from (((((((select col1 from t1) as ttt))))));
select ship_power.gun_power, ship_info.*
FROM
	(
		select s.name as ship_name, sum(g.power) as gun_power, max(callibr) as max_callibr
		from
			ships s inner join ships_guns sg on s.id = sg.ship_id inner join guns g on g.id = sg.guns_id
		group by s.name
	) ship_power
	inner join
	(
		select s.name as ship_name, sc.class_name, sc.tonange, sc.max_length, sc.start_build, sc.max_guns_size
		from
			ships s inner join ship_class sc on s.class_id = sc.id
	) ship_info using (ship_name)
order by ship_power.ship_name;
#end
#begin
-- JOIN
-- -- -- join condition
select * from t1 inner join (t1 as tt1, t2 as tt2) on t1.col1 = tt1.col1;
select * from  (t1 as tt1, t2 as tt2) inner join t1 on t1.col1 = tt1.col1;
select * from  t1 as tt1, t2 as tt2 inner join t1 on true;
#end
#begin
-- where_condition test
select col1 from t1 inner join t2 on (t1.col1 = t2.col2);
#end
#begin
-- identifiers tests
select 1 as 123e;
#end
#begin
-- not latin1 literals
select CONVERT( LEFT( CONVERT( '自動下書き' USING binary ), 100 ) USING utf8 ) AS x_0;
select CONVERT( LEFT( CONVERT( '自動' USING binary ), 6 ) USING utf8 ) AS x_0;
select  t.*, tt.* FROM wptests_terms AS t  INNER JOIN wptests_term_taxonomy AS tt ON t.term_id = tt.term_id WHERE tt.taxonomy IN ('category') AND t.name IN ('远征手记') ORDER BY t.name ASC;
#end
