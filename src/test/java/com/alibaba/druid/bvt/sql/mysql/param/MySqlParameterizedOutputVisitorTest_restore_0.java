package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_restore_0 extends TestCase {
    public void test_for_parameterize() throws Exception {
        String sqlTemplate = "SELECT id, name, x, y, city_code FROM `gpo_abi_raw_data` `gpo_abi_raw_data` WHERE 1 = 1 AND `id` > ? ORDER BY `id`";
        String params = "[[1200000,1250000]]";
        params = params.replaceAll("''", "'");
        sqlTemplate = SQLUtils.formatMySql(sqlTemplate);
        String table = "[\"`gpo_abi_raw_data`\"]";
        String formattedSql = ParseUtil.restore(sqlTemplate, table, params);
        assertEquals("SELECT id, name, x, y, city_code\n" +
                "FROM `gpo_abi_raw_data` `gpo_abi_raw_data`\n" +
                "WHERE 1 = 1\n" +
                "\tAND (`id` > 1200000 OR `id` > 1250000)\n" +
                "ORDER BY `id`", formattedSql);
    }
}
