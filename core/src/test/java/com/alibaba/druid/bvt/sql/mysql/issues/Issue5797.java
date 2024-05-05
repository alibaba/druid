package com.alibaba.druid.bvt.sql.mysql.issues;

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
 * @see <a href="https://github.com/alibaba/druid/issues/5797">Issue来源</a>
 */
public class Issue5797 {

    @Test
    public void test_parse_create_table() {
        for (DbType dbType : new DbType[]{
            DbType.mysql,
            DbType.postgresql,
            DbType.oracle,
            DbType.mariadb,
            DbType.sqlserver,
            DbType.h2,
            DbType.hive,
            //DbType.gaussdb,
            //DbType.oscar,
//            DbType.db2,
//            DbType.dm, DbType.kingbase,

        }) {

            for (String sql : new String[]{
                "-- 给课程表增加类型字段\n"
                    + "alter table info_course drop column if exists course_type_id",

            }) {
                System.out.println(dbType + "原始的sql===" + sql);
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                String sqlGen = statementList.toString();
                System.out.println(dbType + "生成的sql===" + sqlGen);
                StringBuilder sb = new StringBuilder();
                for (SQLStatement statement : statementList) {
                    sb.append(statement.toString()).append(";");
                }
                sb.deleteCharAt(sb.length() - 1);
                parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
                List<SQLStatement> statementListNew = parser.parseStatementList();
                String sqlGenNew = statementList.toString();
                System.out.println(dbType + "重新解析再生成的sql===" + sqlGenNew);
                assertEquals(statementList.toString(), statementListNew.toString());
            }
        }
    }
}
