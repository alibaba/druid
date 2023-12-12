package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.mysql.issues.Issue5421;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * 验证 Oracle CREATE OR REPLACE TYPE TYPE4 as ENUM 语句的解析
 *
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5267">Issue来源</a>
 * @see <a href="https://docs.oracle.com/en/database/oracle/oracle-database/19/lnpls/CREATE-TYPE-statement.html">Oracle  create type语法</a>
 */
public class Issue5267 {

    @Test
    public void test_create_type() throws Exception {
        for (DbType dbType : new DbType[]{DbType.oracle}) {
            for (String sql : new String[]{
                "CREATE OR REPLACE TYPE TYPE4 as ENUM( 'Lane', 'Junction', 'Area', 'TrafficLight' ) ;",
            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                SQLStatement statement = parser.parseStatement();
                System.out.println(dbType + "原始的sql===" + sql);
                System.out.println(dbType + "原始sql归一化===" + Issue5421.normalizeSql(sql));
                String newSql = statement.toString();
                System.out.println(dbType + "初次解析生成的sql归一化===" + Issue5421.normalizeSql(newSql));
                parser = SQLParserUtils.createSQLStatementParser(newSql, dbType);
                statement = parser.parseStatement();
                System.out.println(dbType + "重新解析sql归一化===" + Issue5421.normalizeSql(statement.toString()));
                assertTrue(Issue5421.normalizeSql(sql).equalsIgnoreCase(Issue5421.normalizeSql(statement.toString())));
            }
        }
    }
}
