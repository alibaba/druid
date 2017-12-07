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
public class MySqlParameterizedOutputVisitorTest_46 extends TestCase {
    public void test_for_parameterize() throws Exception {
        final String dbType = JdbcConstants.MYSQL;
        String sql = "SELECT COUNT(*) AS count, `ipv_uv_1d_001` AS col FROM ( (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) UNION ALL (SELECT ipv_uv_1d_001 FROM `cbu_da_dihu_16`.`ads_tb_sycm_eff_slr_itm_1d_s015_p033` WHERE `auto_seq_id` > ? LIMIT ?) ) ads_tb_sycm_eff_slr_itm_1d_s015_p033 GROUP BY col ORDER BY col DESC";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        statement.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);


        assertEquals("SELECT COUNT(*) AS count, `ipv_uv_1d_001` AS col\n" +
                "FROM (\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                "\tUNION ALL\n" +
                "\t(SELECT ipv_uv_1d_001\n" +
                "\tFROM cbu_da_dihu.ads_tb_sycm_eff_slr_itm_1d_s015_p\n" +
                "\tWHERE `auto_seq_id` > ?\n" +
                "\tLIMIT ?)\n" +
                ") ads_tb_sycm_eff_slr_itm_1d_s015_p033\n" +
                "GROUP BY col\n" +
                "ORDER BY col DESC", psql);
    }
}
