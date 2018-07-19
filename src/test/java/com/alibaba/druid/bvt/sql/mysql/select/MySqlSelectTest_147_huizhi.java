package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_147_huizhi extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "INSERT INTO hz_dev_hb.tb_tmp_cda_opera_281c (target, appeartimes, source_id) SELECT\n" +
                "           VARCHAR20,\n" +
                "           count(1)          AS appeartimes,\n" +
                "           'resource_count1' as source_id\n" +
                "         FROM hz_dev_hb.tb_fxzx_large t1\n" +
                "         WHERE 1 = 1 AND VARCHAR20 IS NOT NULL AND\n" +
                "               MISSIONID =\n" +
                "               'd2051b6549d9a028e83a8a9ab2c2'\n" +
                "         GROUP BY VARCHAR20";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLInsertStatement stmt = (SQLInsertStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("INSERT INTO hz_dev_hb.tb_tmp_cda_opera_281c (target, appeartimes, source_id)\n" +
                "SELECT VARCHAR20, COUNT(1) AS appeartimes, 'resource_count1' AS source_id\n" +
                "FROM hz_dev_hb.tb_fxzx_large t1\n" +
                "WHERE 1 = 1\n" +
                "\tAND VARCHAR20 IS NOT NULL\n" +
                "\tAND MISSIONID = 'd2051b6549d9a028e83a8a9ab2c2'\n" +
                "GROUP BY VARCHAR20", stmt.toString());

        assertEquals("INSERT INTO hz_dev_hb.tb_tmp_cda_opera_281c(target, appeartimes, source_id)\n" +
                        "SELECT VARCHAR20, COUNT(1) AS appeartimes, ? AS source_id\n" +
                        "FROM hz_dev_hb.tb_fxzx_large t1\n" +
                        "WHERE 1 = 1\n" +
                        "\tAND VARCHAR20 IS NOT NULL\n" +
                        "\tAND MISSIONID = ?\n" +
                        "GROUP BY VARCHAR20"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL));

        SQLSelectQueryBlock queryBlock = stmt.getQuery().getQueryBlock();
        assertEquals(3, queryBlock.getSelectList().size());
        assertEquals(SQLCharExpr.class, queryBlock.getSelectList().get(2).getExpr().getClass());
    }

}