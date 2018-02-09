#begin
replace into t1 values (default, 1, '2', abs(-10 * col1) + sqrt(col2/col3));
replace table1(col1, col2, col3) value (1, 2, 3), (4, 5, 6), (7, 8, 9);
replace into t2(str1, str2) values (null, 'abc'), ('some' ' string' ' to replace', @someval);
replace into new_t select * from old_t;
#end
#begin
-- http://dev.mysql.com/doc/refman/5.6/en/replace.html
REPLACE INTO test VALUES (1, 'Old', '2014-08-20 18:47:00');
REPLACE INTO test VALUES (1, 'New', '2014-08-20 18:47:42');
REPLACE INTO T SELECT * FROM T;
REPLACE LOW_PRIORITY INTO `online_users` SET `session_id`='3580cc4e61117c0785372c426eddd11c', `user_id` = 'XXX', `page` = '/', `lastview` = NOW();
#end
