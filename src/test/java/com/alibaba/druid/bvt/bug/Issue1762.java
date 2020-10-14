package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 25/06/2017.
 */
public class Issue1762 extends TestCase {
    private final DbType dbType = DbType.mysql;

    public void test_0() throws Exception {
        String sql = "-- table-name-bean-name:some --\n" +
                "CREATE TABLE `some_table` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键'\n" +
                ")";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);
        List<String> beforeComments = stmt.getBeforeCommentsDirect();
        assertNotNull(beforeComments);
        assertEquals(1, beforeComments.size());
        assertEquals("-- table-name-bean-name:some --", beforeComments.get(0));

        assertEquals("-- table-name-bean-name:some --\n" +
                "CREATE TABLE `some_table` (\n" +
                "\t`id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键'\n" +
                ")", SQLUtils.toMySqlString(stmt));

        assertEquals("-- table-name-bean-name:some --\n" +
                "create table `some_table` (\n" +
                "\t`id` bigint(20) unsigned not null auto_increment comment '主键'\n" +
                ")", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        assertEquals(1, stmtList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());

        assertEquals(1, visitor.getColumns().size());
        assertEquals(1, visitor.getTables().size());
    }
}
