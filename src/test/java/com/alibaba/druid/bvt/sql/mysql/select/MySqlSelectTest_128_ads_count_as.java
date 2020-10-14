package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_128_ads_count_as extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+qt=30000*/SELECT COUNT( DISTINCT brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.__aid AS __aid ) FROM brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump WHERE brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.label_list_crowd1 IN ('20324_35_0', '20324_35_1', '20324_35_2', '20324_35_3', '20324_35_4', '20324_35_5', '20324_35_6', '20324_35_7', '20324_35_8', '20324_35_9', '20324_35_10', '20324_35_11', '20324_35_12', '20324_35_13', '20324_35_14', '20324_35_15', '20324_35_16', '20324_35_17', '20324_35_18', '20324_35_19')";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+qt=30000*/\n" +
                "SELECT COUNT(DISTINCT brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.__aid)\n" +
                "FROM brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump\n" +
                "WHERE brand_crm_ship.alimama_ecpm_algo_brand_display_ad_label_out_dump.label_list_crowd1 IN (\n" +
                "\t'20324_35_0', \n" +
                "\t'20324_35_1', \n" +
                "\t'20324_35_2', \n" +
                "\t'20324_35_3', \n" +
                "\t'20324_35_4', \n" +
                "\t'20324_35_5', \n" +
                "\t'20324_35_6', \n" +
                "\t'20324_35_7', \n" +
                "\t'20324_35_8', \n" +
                "\t'20324_35_9', \n" +
                "\t'20324_35_10', \n" +
                "\t'20324_35_11', \n" +
                "\t'20324_35_12', \n" +
                "\t'20324_35_13', \n" +
                "\t'20324_35_14', \n" +
                "\t'20324_35_15', \n" +
                "\t'20324_35_16', \n" +
                "\t'20324_35_17', \n" +
                "\t'20324_35_18', \n" +
                "\t'20324_35_19'\n" +
                ")", stmt.toString());
    }


}