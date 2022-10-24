#begin
-- update one-table syntax
update t set col = 100 where id = 101;
update ignore t1 set `column_name` = default, `one-more-column` = (to_seconds(now()) mod 33);
#end
#begin
-- update multiple-table syntax
update t1, t2, t3 inner join t4 using (col_name1, col_name2)
set t1.value_col = t3.new_value_col, t4.`some-col*` = `t2`.`***` * 2
where  t1.pk = t2.fk_t1_pk and t2.id = t4.fk_id_entity;
#end
#begin
-- http://dev.mysql.com/doc/refman/5.6/en/update.html
UPDATE t1 SET col1 = col1 + 1;
UPDATE t1 SET col1 = col1 + 1, col2 = col1;
UPDATE t SET id = id + 1 ORDER BY id DESC;
UPDATE items,month SET items.price=month.price WHERE items.id=month.id;
UPDATE `Table A`,`Table B` SET `Table A`.`text`=concat_ws('',`Table A`.`text`,`Table B`.`B-num`," from ",`Table B`.`date`,'/')
WHERE `Table A`.`A-num` = `Table B`.`A-num`;
UPDATE TABLE_1 LEFT JOIN TABLE_2 ON TABLE_1.COLUMN_1= TABLE_2.COLUMN_2 SET TABLE_1.`COLUMN` = EXPR WHERE TABLE_2.COLUMN2 IS NULL;
UPDATE Groups LEFT JOIN (SELECT GroupId, MIN(ValWithinGroup) AS baseVal FROM Groups GROUP BY GroupId) AS GrpSum USING (GroupId) SET ValWithinGroup=ValWithinGroup-baseVal;
update Table1 t1 join Table2 t2 on t1.ID=t2.t1ID join Table3 t3 on t2.ID=t3.t2ID set t1.Value=12345 where t3.ID=54321;
#end
