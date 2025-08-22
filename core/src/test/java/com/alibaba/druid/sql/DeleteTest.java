package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

/**
 * @author lingo
 * @date 2025/8/7 12:11
 * @description
 */
public class DeleteTest {
    
    @Test
    public void test() {
        String sql = "delete a from users a force index(a1) where id < 10";
        List<SQLStatement> stmt = SQLUtils.parseStatements(sql, DbType.mysql);
        System.out.println(stmt.get(0));
        System.out.println(SQLUtils.toSQLString(stmt.get(0), DbType.mysql));
    }
}
