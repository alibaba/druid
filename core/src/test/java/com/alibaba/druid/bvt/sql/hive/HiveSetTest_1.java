package com.alibaba.druid.bvt.sql.hive;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import junit.framework.TestCase;

/**
 * @author lizongbo
 */
public class HiveSetTest_1 extends TestCase {

    static String sql1 = "set hivevar:exp_id = 8001\n" +
        ";";

    static String sql2 = "set hivevar:exp_id = \"8001\"\n" +
        ";";

    static String sql3 = "set hivevar.exp_id = 8001\n" +
        ";";

    static String sql4 = "set hivevar.exp_id = \"8001\"\n" +
        ";";
    public void test_setHiveVar1() throws Exception {
        {
            DbType dbType = JdbcConstants.ODPS;
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql1, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            //这里会多一个换行符， SQLExprParser的第 5259行针对ODPS类型，调用的是nextTokenForSet，而不是走nextToken，不敢随便改，先这样吧
            assertEquals("SET hivevar:exp_id = 8001\n;", stmt.toString());
        }
        {
            DbType dbType = JdbcConstants.HIVE;
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql1, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            assertEquals("SET hivevar:exp_id = 8001;", stmt.toString());
        }

    }

    public void test_setHiveVar2() throws Exception {
        for (DbType dbType : new DbType[]{JdbcConstants.ODPS, JdbcConstants.HIVE}) {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql2, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            assertEquals("SET hivevar:exp_id = '8001';", stmt.toString());

        }

    }


    public void test_setHiveVar3() throws Exception {
        {
            DbType dbType = JdbcConstants.ODPS;
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql3, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            //这里会多一个换行符， SQLExprParser的第 5259行针对ODPS类型，调用的是nextTokenForSet，而不是走nextToken，不敢随便改，先这样吧
            assertEquals("SET hivevar.exp_id = 8001\n;", stmt.toString());
        }
        {
            DbType dbType = JdbcConstants.HIVE;
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql3, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            assertEquals("SET hivevar.exp_id = 8001;", stmt.toString());
        }

    }


    public void test_setHiveVar4() throws Exception {
        for (DbType dbType : new DbType[]{JdbcConstants.ODPS, JdbcConstants.HIVE}) {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql4, dbType);
            SQLStatement stmt = statementList.get(0);
            //System.out.println(dbType + "==" + statementList);
            assertEquals(1, statementList.size());
            assertEquals("SET hivevar.exp_id = '8001';", stmt.toString());

        }

    }
}