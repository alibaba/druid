package com.alibaba.druid.bvt.sql.odps.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5791">Issue来源</a>
 */
public class Issue5791 {

    @Test
    public void test_parse_comment() {
        for (DbType dbType : new DbType[]{
            DbType.odps,
            DbType.mysql,
            DbType.postgresql,
            DbType.oracle,
            //DbType.sqlserver,
            //DbType.oscar,
//            DbType.db2,  DbType.h2,
//            DbType.hive, DbType.dm, DbType.kingbase, DbType.gaussdb

        }) {

            for (String sql : new String[]{
                "select a -- C1\n"
                    + "from  -- C2\n"
                    + "-- C2-2 \n"
                    + "  t -- C3\n"
                    + "  join s on t.n = s.n -- C4\n"
                    + "where -- C5\n"
                    + "  t.x = 1  -- C6;",

            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                String sqlGen = statementList.toString();
                System.out.println(dbType + "生成的sql===" + sqlGen);
                assertTrue(sqlGen.contains(" C2-2"));
                assertTrue(sqlGen.contains(" C4"));
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                String sqlGenNew = statementList.toString();
                System.out.println(dbType + "重新解析再生成的sql===" + sqlGenNew);
                assertTrue(sqlGenNew.contains(" C2-2"));
                assertTrue(sqlGenNew.contains(" C4"));
                assertEquals(statementList.toString(), statementListNew.toString());
            }
        }
    }
}
