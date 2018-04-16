package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class SchemaStatTest16 extends TestCase {

    public void test_schemaStat() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);

        String sql = "SELECT\n" +
                "\tid AS id,\n" +
                "\tactivity_code AS activityCode,\n" +
                "\tactivity_name AS activityName,\n" +
                "\tstart_time AS startTime,\n" +
                "\tend_time AS endTime,\n" +
                "\t`state`,\n" +
                "\tbonus_type AS bonusType,\n" +
                "\tsend_num AS sendNum,\n" +
                "\tpoints,\n" +
                "\texchange_flag AS exchangeFlag,\n" +
                "\texchange_points AS exchangePoints,\n" +
                "\tauth_code_key AS authCodeKey,\n" +
                "\tauth_code_key_repeat AS authCodeKeyRepeat,\n" +
                "\t`type`,\n" +
                "\tmobile_flag AS mobileFlag,\n" +
                "\tactivity_code_list AS activityCodeList,\n" +
                "\tregister_product_id AS registerProductId,\n" +
                "\tadd_user AS addUser,\n" +
                "\tadd_time AS addTime,\n" +
                "\tmod_user AS modUser,\n" +
                "\tdelete_flag AS deleteFlag\n" +
                "FROM\n" +
                "\ttb_activity\n" +
                "WHERE\n" +
                "\tid != 1\n" +
                "AND id NOT IN (1, 2)\n" +
                "and DATE_FORMAT(add_time, '%Y-%m-%d') = '2018-08-08';";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = parser.parseStatementList().get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(statVisitor);

        System.out.println("Tables : " + statVisitor.getTables());
        System.out.println(statVisitor.getColumns());
//        System.out.println(statVisitor.getGroupByColumns()); // group by
        System.out.println("relationships : " + statVisitor.getRelationships()); // group by
        System.out.println(statVisitor.getConditions());

        assertEquals(21, statVisitor.getColumns().size());
        assertEquals(3, statVisitor.getConditions().size());
        assertEquals(1, statVisitor.getFunctions().size());

        assertTrue(statVisitor.containsTable("tb_activity"));
        assertTrue(statVisitor.containsColumn("tb_activity", "id"));
    }
}
