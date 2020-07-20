package com.alibaba.druid.bvt.sql.impala;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class ImpalaInsertTest_1 extends TestCase {
    public void test_create() throws Exception {
        String sql = "INSERT INTO TABLE `tt`.`articles`\n" +
            "VALUES ('a''大1大2-- /**fsdaf**/撒发链接a', 'bb', '大大aa-- /**fsdaf**/撒发链接'), ('a''大1大2-- " +
            "/**fsdaf**/撒发链接a', 'bb', '大大aa-- /**fsdaf**/撒发链接'), ('a''大1大2-- /**fsdaf**/撒发链接a', " +
            "'bb', '大大aa-- /**fsdaf**/撒发链接');";//
        assertEquals("INSERT INTO TABLE `tt`.`articles`\n" +
            "VALUES ('a''大1大2-- /**fsdaf**/撒发链接a', 'bb', '大大aa-- /**fsdaf**/撒发链接'), ('a''大1大2-- " +
            "/**fsdaf**/撒发链接a', 'bb', '大大aa-- /**fsdaf**/撒发链接'), ('a''大1大2-- /**fsdaf**/撒发链接a', " +
            "'bb', '大大aa-- /**fsdaf**/撒发链接');", SQLUtils.formatImpala(sql));
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.IMPALA);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.IMPALA);
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());

        assertEquals(1, visitor.getTables().size());
        assertEquals(0, visitor.getColumns().size());
    }
}
