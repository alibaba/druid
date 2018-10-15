/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class DateAndTimeFunctionsTest extends TestCase {

    public void test_0() throws Exception {
        String sql = "SELECT something FROM tbl_name WHERE DATE_SUB(CURDATE(),INTERVAL 30 DAY) <= date_col";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT something\nFROM tbl_name\nWHERE DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date_col",
                            text);
    }

    public void test_1() throws Exception {
        String sql = "SELECT DAYOFMONTH('2001-11-00'), MONTH('2005-00-00')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DAYOFMONTH('2001-11-00'), MONTH('2005-00-00')", text);
    }

    public void test_2() throws Exception {
        String sql = "SELECT DAYNAME('2006-05-00')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DAYNAME('2006-05-00')", text);
    }

    public void test_3() throws Exception {
        String sql = "SELECT DAYNAME('2006-05-00')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DAYNAME('2006-05-00')", text);
    }

    public void test_4() throws Exception {
        String sql = "SELECT DATE_ADD('2008-01-02', INTERVAL 31 DAY)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE_ADD('2008-01-02', INTERVAL 31 DAY)", text);
    }

    public void test_5() throws Exception {
        String sql = "SELECT ADDDATE('2008-01-02', INTERVAL 31 DAY)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ADDDATE('2008-01-02', INTERVAL 31 DAY)", text);
    }

    public void test_6() throws Exception {
        String sql = "SELECT ADDDATE('2008-01-02', 31)";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ADDDATE('2008-01-02', 31)", text);
    }

    public void test_7() throws Exception {
        String sql = "SELECT ADDTIME('2007-12-31 23:59:59.999999', '1 1:1:1.000002')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ADDTIME('2007-12-31 23:59:59.999999', '1 1:1:1.000002')", text);
    }

    public void test_8() throws Exception {
        String sql = "SELECT ADDTIME('01:00:00.999999', '02:00:00.999998')";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT ADDTIME('01:00:00.999999', '02:00:00.999998')", text);
    }

    public void test_9() throws Exception {
        String sql = "SELECT CONVERT_TZ('2004-01-01 12:00:00','GMT','MET');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONVERT_TZ('2004-01-01 12:00:00', 'GMT', 'MET');", text);
    }

    public void test_10() throws Exception {
        String sql = "SELECT CONVERT_TZ('2004-01-01 12:00:00','+00:00','+10:00');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CONVERT_TZ('2004-01-01 12:00:00', '+00:00', '+10:00');", text);
    }

    public void test_11() throws Exception {
        String sql = "SELECT CURDATE();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CURDATE();", text);
    }

    public void test_12() throws Exception {
        String sql = "SELECT CURDATE() + 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CURDATE() + 0;", text);
    }

    public void test_13() throws Exception {
        String sql = "SELECT CURTIME();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CURTIME();", text);
    }

    public void test_14() throws Exception {
        String sql = "SELECT CURDATE() + 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT CURDATE() + 0;", text);
    }

    public void test_15() throws Exception {
        String sql = "SELECT DATE('2003-12-31 01:02:03');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE('2003-12-31 01:02:03');", text);
    }

    public void test_16() throws Exception {
        String sql = "SELECT DATEDIFF('2007-12-31 23:59:59','2007-12-30');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATEDIFF('2007-12-31 23:59:59', '2007-12-30');", text);
    }

    public void test_17() throws Exception {
        String sql = "SELECT DATEDIFF('2010-11-30 23:59:59','2010-12-31');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATEDIFF('2010-11-30 23:59:59', '2010-12-31');", text);
    }

    public void test_18() throws Exception {
        String sql = "SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND;", text);
    }

    public void test_19() throws Exception {
        String sql = "SELECT INTERVAL 1 DAY + '2008-12-31';";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL 1 DAY + '2008-12-31';", text);
    }

    public void test_20() throws Exception {
        String sql = "SELECT '2005-01-01' - INTERVAL 1 SECOND;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2005-01-01' - INTERVAL 1 SECOND;", text);
    }

    public void test_21() throws Exception {
        String sql = "SELECT DATE_ADD('2000-12-31 23:59:59',INTERVAL 1 SECOND);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE_ADD('2000-12-31 23:59:59', INTERVAL 1 SECOND);", text);
    }

    public void test_22() throws Exception {
        String sql = "SELECT DATE_ADD('2009-01-01', INTERVAL 6/4 HOUR_MINUTE);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE_ADD('2009-01-01', INTERVAL 6 / 4 HOUR_MINUTE);", text);
    }

    public void test_23() throws Exception {
        String sql = "SELECT DATE_ADD('2009-01-01', INTERVAL 6/4 MINUTE_SECOND);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE_ADD('2009-01-01', INTERVAL 6 / 4 MINUTE_SECOND);", text);
    }

    public void test_24() throws Exception {
        String sql = "SELECT INTERVAL '-1 10' DAY_HOUR;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL '-1 10' DAY_HOUR;", text);
    }

    public void test_25() throws Exception {
        String sql = "SELECT INTERVAL '1 1:1:1' DAY_SECOND;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL '1 1:1:1' DAY_SECOND;", text);
    }

    public void test_26() throws Exception {
        String sql = "SELECT INTERVAL '-1 10' DAY_HOUR;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL '-1 10' DAY_HOUR;", text);
    }

    public void test_27() throws Exception {
        String sql = "SELECT INTERVAL 31 DAY;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL 31 DAY;", text);
    }

    public void test_28() throws Exception {
        String sql = "SELECT INTERVAL '1.999999' SECOND_MICROSECOND;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT INTERVAL '1.999999' SECOND_MICROSECOND;", text);
    }

    public void test_29() throws Exception {
        String sql = "SELECT '2005-03-32' + INTERVAL 1 MONTH;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT '2005-03-32' + INTERVAL 1 MONTH;", text);
    }

    public void test_30() throws Exception {
        String sql = "SELECT DATE_FORMAT('2009-10-04 22:23:00', '%W %M %Y');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT DATE_FORMAT('2009-10-04 22:23:00', '%W %M %Y');", text);
    }

    public void test_31() throws Exception {
        String sql = "SELECT EXTRACT(YEAR FROM '2009-07-02');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXTRACT(YEAR FROM '2009-07-02');", text);
    }

    public void test_32() throws Exception {
        String sql = "SELECT EXTRACT(YEAR_MONTH FROM '2009-07-02 01:02:03');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXTRACT(YEAR_MONTH FROM '2009-07-02 01:02:03');", text);
    }

    public void test_33() throws Exception {
        String sql = "SELECT EXTRACT(DAY_MINUTE FROM '2009-07-02 01:02:03');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXTRACT(DAY_MINUTE FROM '2009-07-02 01:02:03');", text);
    }

    public void test_34() throws Exception {
        String sql = "SELECT EXTRACT(MICROSECOND FROM '2003-01-02 10:30:00.000123');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT EXTRACT(MICROSECOND FROM '2003-01-02 10:30:00.000123');", text);
    }

    public void test_35() throws Exception {
        String sql = "SELECT FROM_UNIXTIME(1196440219);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FROM_UNIXTIME(1196440219);", text);
    }

    public void test_36() throws Exception {
        String sql = "SELECT FROM_UNIXTIME(1196440219) + 0;";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT FROM_UNIXTIME(1196440219) + 0;", text);
    }

    public void test_37() throws Exception {
        String sql = "SELECT HOUR('10:05:03');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT HOUR('10:05:03');", text);
    }

    public void test_38() throws Exception {
        String sql = "SELECT NOW(), SLEEP(2), NOW();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT NOW(), SLEEP(2), NOW();", text);
    }

    public void test_39() throws Exception {
        String sql = "SELECT SYSDATE(), SLEEP(2), SYSDATE();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SYSDATE(), SLEEP(2), SYSDATE();", text);
    }

    public void test_40() throws Exception {
        String sql = "SELECT SUBDATE('2008-01-02', INTERVAL 31 DAY);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SUBDATE('2008-01-02', INTERVAL 31 DAY);", text);
    }

    public void test_41() throws Exception {
        String sql = "SELECT TIME('2003-12-31 01:02:03.000123');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TIME('2003-12-31 01:02:03.000123');", text);
    }

    public void test_42() throws Exception {
        String sql = "SELECT SECOND('10:05:03');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT SECOND('10:05:03');", text);
    }

    public void test_43() throws Exception {
        String sql = "SELECT TIMESTAMPADD(MINUTE,1,'2003-01-02');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TIMESTAMPADD(MINUTE, 1, '2003-01-02');", text);
    }

    public void test_44() throws Exception {
        String sql = "SELECT TIMESTAMPADD(WEEK,1,'2003-01-02');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TIMESTAMPADD(WEEK, 1, '2003-01-02');", text);
    }

    public void test_45() throws Exception {
        String sql = "SELECT TIMESTAMPDIFF(MONTH,'2003-02-01','2003-05-01');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TIMESTAMPDIFF(MONTH, '2003-02-01', '2003-05-01');", text);
    }

    public void test_46() throws Exception {
        String sql = "SELECT TIMESTAMPDIFF(MINUTE,'2003-02-01','2003-05-01 12:05:55');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TIMESTAMPDIFF(MINUTE, '2003-02-01', '2003-05-01 12:05:55');", text);
    }

    public void test_47() throws Exception {
        String sql = "SELECT TO_DAYS('2007-10-07');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TO_DAYS('2007-10-07');", text);
    }

    public void test_48() throws Exception {
        String sql = "SELECT TO_DAYS(950501);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TO_DAYS(950501);", text);
    }

    public void test_49() throws Exception {
        String sql = "SELECT TO_DAYS('0000-01-01');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT TO_DAYS('0000-01-01');", text);
    }

    public void test_50() throws Exception {
        String sql = "SELECT UNIX_TIMESTAMP();";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT UNIX_TIMESTAMP();", text);
    }

    public void test_51() throws Exception {
        String sql = "SELECT WEEK('2008-02-20');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT WEEK('2008-02-20');", text);
    }

    public void test_52() throws Exception {
        String sql = "SELECT MID(YEARWEEK('2000-01-01'),5,2);";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT MID(YEARWEEK('2000-01-01'), 5, 2);", text);
    }

    public void test_53() throws Exception {
        String sql = "SELECT YEARWEEK('1987-01-01');";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT YEARWEEK('1987-01-01');", text);
    }
    
    
    public void test_54() throws Exception {
        String sql = "SELECT t.c1, DATE_ADD('2008-01-02', INTERVAL +t.c1 DAY) from t";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT t.c1, DATE_ADD('2008-01-02', INTERVAL +t.c1 DAY)\nFROM t", text);
    }
    
    public void test_55() throws Exception {
        String sql = "SELECT t.c1, DATE_ADD('2008-01-02', INTERVAL -t.c1 DAY) from t";

        SQLStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        String text = output(stmtList);

        Assert.assertEquals("SELECT t.c1, DATE_ADD('2008-01-02', INTERVAL -t.c1 DAY)\nFROM t", text);
    }

    private String output(List<SQLStatement> stmtList) {
        return SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);
    }
}
