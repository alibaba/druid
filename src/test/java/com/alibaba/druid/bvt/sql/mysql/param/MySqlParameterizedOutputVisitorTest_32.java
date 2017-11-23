package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_32 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;


        String sql = "/* cds internal mark */select count(*) as count  from (" +
                "( select env_type from `tmall_miaoscm`.`miao_sale_ledger_0060` where `id` > 1 limit 136 )" +
                " union all ( select env_type from `tmall_miaoscm`.`miao_sale_ledger_0060` where `id` > 2331 limit 136 )" +
                ") as miao_sale_ledger_0060 where `miao_sale_ledger_0060`.`env_type` = 3";

        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        assertEquals("SELECT COUNT(*) AS count\n" +
                "FROM (\n" +
                "\t(SELECT env_type\n" +
                "\tFROM `tmall_miaoscm`.miao_sale_ledger\n" +
                "\tWHERE `id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT env_type\n" +
                "\tFROM `tmall_miaoscm`.miao_sale_ledger\n" +
                "\tWHERE `id` > ?\n" +
                "\tLIMIT ?)\n" +
                ") miao_sale_ledger_0060\n" +
                "WHERE `miao_sale_ledger_0060`.`env_type` = ?", psql);

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        visitor.setExportTables(true);
        /*visitor.setPrettyFormat(false);*/

        SQLStatement stmt = stmtList.get(0);
        stmt.accept(visitor);

        // System.out.println(parameters);
        assertEquals(5, parameters.size());

        //SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(psql, dbType);
        // List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement pstmt = SQLUtils.parseStatements(psql, dbType).get(0);

        StringBuilder buf = new StringBuilder();
        SQLASTOutputVisitor visitor1 = SQLUtils.createOutputVisitor(buf, dbType);
        visitor1.addTableMapping("udata", "udata_0888");
        visitor1.setInputParameters(visitor.getParameters());
        pstmt.accept(visitor1);

        assertEquals("SELECT COUNT(*) AS count\n" +
                "FROM (\n" +
                "\t(SELECT env_type\n" +
                "\tFROM `tmall_miaoscm`.miao_sale_ledger\n" +
                "\tWHERE `id` > 1\n" +
                "\tLIMIT 136)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT env_type\n" +
                "\tFROM `tmall_miaoscm`.miao_sale_ledger\n" +
                "\tWHERE `id` > 2331\n" +
                "\tLIMIT 136)\n" +
                ") miao_sale_ledger_0060\n" +
                "WHERE `miao_sale_ledger_0060`.`env_type` = 3", buf.toString());
    }
}
