package com.alibaba.druid.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.Test;

import java.util.List;
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
