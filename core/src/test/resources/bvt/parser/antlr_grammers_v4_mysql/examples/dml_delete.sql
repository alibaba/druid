#begin
-- delete one-table syntax
delete from t1 where col1 = true and (col2 - col3 <= (select count(*) from t2) or maincol/2 > 100.2);
delete low_priority from mytable where value_col > 0 order by sort_col desc limit 10;
delete quick ignore from test.parenttable where id*2 + somecol < 10;
#end
#begin
-- delete multiple-table syntax
delete ignore t1.*, alias_t2 from t1 inner join t3 on t1.col1 = t3.somecol and t1.col2 > t3.col_for_compare left join t2 as alias_t2 on t1.col1 <= alias_t2.col1 and alias_t2.col_onecol + t3.col_for_compare <> t1.sum_col
where alias_t2.not_null_col is not null and t1.primary_key_column >= 100;
-- http://dev.mysql.com/doc/refman/5.6/en/delete.html
DELETE FROM t1, t2 USING t1 INNER JOIN t2 INNER JOIN t3 WHERE t1.id=t2.id AND t2.id=t3.id;
DELETE t1, t2 FROM t1 INNER JOIN t2 INNER JOIN t3 WHERE t1.id=t2.id AND t2.id=t3.id;
DELETE t1 FROM t1 LEFT JOIN t2 ON t1.id=t2.id WHERE t2.id IS NULL;
DELETE a1, a2 FROM t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id;
DELETE FROM a1, a2 USING t1 AS a1 INNER JOIN t2 AS a2 WHERE a1.id=a2.id;
#end
