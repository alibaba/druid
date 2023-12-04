package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_0 extends TestCase {
    public void test_select() throws Exception {
        String sql = "CREATE EXTERNAL TABLE students (name VARCHAR(64), age INT, gpa DECIMAL(3, 2))\n" +
                "  CLUSTERED BY (age) INTO 2 BUCKETS STORED AS ORC;";//
        assertEquals("CREATE EXTERNAL TABLE students (\n" +
                "\tname VARCHAR(64),\n" +
                "\tage INT,\n" +
                "\tgpa DECIMAL(3, 2)\n" +
                ")\n" +
                "CLUSTERED BY (age)\n" +
                "INTO 2 BUCKETS\n" +
                "STORED AS ORC;", SQLUtils.formatHive(sql));
        assertEquals("create external table students (\n" +
                "\tname VARCHAR(64),\n" +
                "\tage INT,\n" +
                "\tgpa DECIMAL(3, 2)\n" +
                ")\n" +
                "clustered by (age)\n" +
                "into 2 buckets\n" +
                "stored as ORC;", SQLUtils.formatHive(sql, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//      System.out.println("coditions : " + visitor.getConditions());
//      System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(3, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());

        assertTrue(visitor.containsColumn("students", "name"));
        assertTrue(visitor.containsColumn("students", "age"));
        assertTrue(visitor.containsColumn("students", "gpa"));
    }

    public void test_create_table_without_as_1() throws Exception {
        //https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-CreateTable
        String sql = "CREATE TABLE new_key_value_store\n"
            + "   ROW FORMAT SERDE \"org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe\"\n"
            + "   STORED AS RCFile\n"
            + "   AS\n"
            + "SELECT (key % 1024) new_key, concat(key, value) key_value_pair\n"
            + "FROM key_value_store\n"
            + "SORT BY new_key, key_value_pair;";

        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        System.out.println(statement);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
    }

    public void test_create_table_without_as_2() throws Exception {
        //https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-CreateTable
        String sql = "CREATE TABLE new_key_value_store\n"
            + "   ROW FORMAT SERDE \"org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe\"\n"
            + "   STORED AS RCFile\n"
            + "SELECT (key % 1024) new_key, concat(key, value) key_value_pair\n"
            + "FROM key_value_store\n"
            + "SORT BY new_key, key_value_pair;";

        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statement = statementList.get(0);
        System.out.println(statement);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("orderBy : " + visitor.getOrderByColumns());
    }
}
