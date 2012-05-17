package com.alibaba.druid.bvt.sql.cobar;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class DMLSelectParserTest extends TestCase {

    public void test_union_0() throws Exception {
        String sql = "(select id from t1) union all (select id from t2) union all (select id from t3) ordeR By d desC limit 1 offset ?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT id\n" + //
                            "FROM t1\n" + //
                            "UNION ALL\n" + //
                            "SELECT id\n" + //
                            "FROM t2\n" + //
                            "UNION ALL\n" + //
                            "(SELECT id\n" + //
                            "FROM t3)\n" + //
                            "ORDER BY d DESC\n" + //
                            "LIMIT ?, 1", output);
    }

    public void test_union_1() throws Exception {
        String sql = "(select id from t1) union  select id from t2 order by id union aLl (select id from t3) ordeR By d asC";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT id\n" + //
                            "FROM t1\n" + //
                            "UNION\n" + //
                            "(SELECT id\n" + //
                            "FROM t2\n" + //
                            "ORDER BY id)\n" + //
                            "UNION ALL\n" + //
                            "(SELECT id\n" + //
                            "FROM t3)\n" + //
                            "ORDER BY d ASC", output);
    }

    public void test_union_2() throws Exception {
        String sql = "(select id from t1) union distInct (select id from t2) union  select id from t3";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT id\nFROM t1\nUNION DISTINCT\nSELECT id\nFROM t2\nUNION\nSELECT id\nFROM t3", output);
    }

    public void test_select_0() throws Exception {
        String sql = "SELect t1.id , t2.* from t1, test.t2 where test.t1.id=1 and t1.id=test.t2.id";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT t1.id, t2.*\n" + //
                            "FROM t1, test.t2\n" + //
                            "WHERE test.t1.id = 1\n" + //
                            "\tAND t1.id = test.t2.id", output);
    }

    public void test_select_1() throws Exception {
        String sql = "select * from  offer  a  straight_join wp_image b use key for join(t1,t2) on a.member_id=b.member_id inner join product_visit c where a.member_id=c.member_id and c.member_id='abc' ";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("SELECT *\n"
                                    + //
                                    "FROM offer a STRAIGHT_JOIN wp_image b USE INDEX FOR JOIN (t1, t2) ON a.member_id = b.member_id INNER JOIN product_visit c\n"
                                    + //
                                    "WHERE a.member_id = c.member_id\n" + //
                                    "\tAND c.member_id = 'abc'", output);
    }
}
