package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 */
public class SQLParseAssertUtil {

    /**
     * 将sql解析成语法树1，然后再将语法树1生成sql1，将sql1再解析成语法树2，然后再将语法树2生成sql2，比较两次解析生成的sql是否一致
     * 针对有些sql虽然正常解析，但是再次生成的sql不正确的场景，加强验证，确认其幂等性
     * @param sql
     * @param dbType
     */
    public static void assertParseSql(String sql, DbType dbType) {
        System.out.println(dbType + "最初的最原始的sql===[" + sql + "]");
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> statementList = parser.parseStatementList();
        String sqlGen = statementList.toString();
        System.out.println(dbType + "首次解析生成的sql===" + sqlGen);
        StringBuilder sb = new StringBuilder();
        for (SQLStatement statement : statementList) {
            sb.append(statement.toString()).append(";");
        }
        sb.deleteCharAt(sb.length() - 1);
        parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
        List<SQLStatement> statementListNew = parser.parseStatementList();
        String sqlGenNew = statementList.toString();
        System.out.println(dbType + "再次解析生成的sql===" + sqlGenNew);
        assertEquals(statementList.toString(), statementListNew.toString());
    }

}
