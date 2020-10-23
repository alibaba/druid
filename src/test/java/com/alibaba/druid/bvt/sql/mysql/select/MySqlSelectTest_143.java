package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_143 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE OS_TEST_COMPARE_test008 AS\n" +
                "SELECT  *\n" +
                "FROM    (\n" +
                "            SELECT  MD5(\n" +
                "                        CONCAT(\n" +
                "                            COALESCE(CAST(`ORDER_ID` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`ITEM_ID` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`BUYER_ID` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`SELLER_ID` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`CATE_ID` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`ADJUST_AMT` AS STRING),'#NULL#')\n" +
                "                            ,COALESCE(CAST(`DIV_CREATE_AMT` AS STRING),'#NULL#')\n" +
                "                        )\n" +
                "                    ) AS compare_column_source\n" +
                "                    ,COUNT(1) AS compare_cnt_source\n" +
                "                    ,MAX(ORDER_ID) AS ORDER_ID_source\n" +
                "                    ,MAX(ITEM_ID) AS ITEM_ID_source\n" +
                "                    ,MAX(BUYER_ID) AS BUYER_ID_source\n" +
                "                    ,MAX(SELLER_ID) AS SELLER_ID_source\n" +
                "                    ,MAX(CATE_ID) AS CATE_ID_source\n" +
                "                    ,MAX(ADJUST_AMT) AS ADJUST_AMT_source\n" +
                "                    ,MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_source\n" +
                "            FROM    aliyun_cdm.OS_INTEGRATION_PHY_74\n" +
                "            GROUP BY MD5(\n" +
                "                         CONCAT(\n" +
                "                             COALESCE(CAST(`ORDER_ID` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`ITEM_ID` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`BUYER_ID` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`SELLER_ID` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`CATE_ID` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`ADJUST_AMT` AS STRING),'#NULL#')\n" +
                "                             ,COALESCE(CAST(`DIV_CREATE_AMT` AS STRING),'#NULL#')\n" +
                "                         )\n" +
                "                     )\n" +
                "        ) left_table\n" +
                "FULL OUTER JOIN (\n" +
                "                    SELECT  MD5(\n" +
                "                                CONCAT(\n" +
                "                                    COALESCE(CAST(`ORDER_ID` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`ITEM_ID` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`BUYER_ID` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`SELLER_ID` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`CATE_ID` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`ADJUST_AMT` AS STRING),'#NULL#')\n" +
                "                                    ,COALESCE(CAST(`DIV_CREATE_AMT` AS STRING),'#NULL#')\n" +
                "                                )\n" +
                "                            ) AS compare_column_target\n" +
                "                            ,COUNT(1) AS compare_cnt_target\n" +
                "                            ,MAX(ORDER_ID) AS ORDER_ID_target\n" +
                "                            ,MAX(ITEM_ID) AS ITEM_ID_target\n" +
                "                            ,MAX(BUYER_ID) AS BUYER_ID_target\n" +
                "                            ,MAX(SELLER_ID) AS SELLER_ID_target\n" +
                "                            ,MAX(CATE_ID) AS CATE_ID_target\n" +
                "                            ,MAX(ADJUST_AMT) AS ADJUST_AMT_target\n" +
                "                            ,MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_target\n" +
                "                    FROM    aliyun_cdm.OS_INTEGRATION_BM_2\n" +
                "                    GROUP BY MD5(\n" +
                "                                 CONCAT(\n" +
                "                                     COALESCE(CAST(`ORDER_ID` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`ITEM_ID` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`BUYER_ID` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`SELLER_ID` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`CATE_ID` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`ADJUST_AMT` AS STRING),'#NULL#')\n" +
                "                                     ,COALESCE(CAST(`DIV_CREATE_AMT` AS STRING),'#NULL#')\n" +
                "                                 )\n" +
                "                             )\n" +
                "                ) right_table\n" +
                "ON      left_table.compare_column_source = right_table.compare_column_target\n" +
                "WHERE   left_table.compare_column_source = NULL\n" +
                "OR      right_table.compare_column_target = NULL\n" +
                "OR      left_table.compare_cnt_source <> right_table.compare_cnt_target";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("CREATE TABLE OS_TEST_COMPARE_test008\n" +
                "AS\n" +
                "SELECT *\n" +
                "FROM (\n" +
                "\tSELECT MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ITEM_ID` AS STRING), '#NULL#'), COALESCE(CAST(`BUYER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`SELLER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`CATE_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ADJUST_AMT` AS STRING), '#NULL#'), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), '#NULL#'))) AS compare_column_source\n" +
                "\t\t, COUNT(1) AS compare_cnt_source, MAX(ORDER_ID) AS ORDER_ID_source\n" +
                "\t\t, MAX(ITEM_ID) AS ITEM_ID_source, MAX(BUYER_ID) AS BUYER_ID_source\n" +
                "\t\t, MAX(SELLER_ID) AS SELLER_ID_source, MAX(CATE_ID) AS CATE_ID_source\n" +
                "\t\t, MAX(ADJUST_AMT) AS ADJUST_AMT_source, MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_source\n" +
                "\tFROM aliyun_cdm.OS_INTEGRATION_PHY_74\n" +
                "\tGROUP BY MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ITEM_ID` AS STRING), '#NULL#'), COALESCE(CAST(`BUYER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`SELLER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`CATE_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ADJUST_AMT` AS STRING), '#NULL#'), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), '#NULL#')))\n" +
                ") left_table\n" +
                "\tFULL JOIN (\n" +
                "\t\tSELECT MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ITEM_ID` AS STRING), '#NULL#'), COALESCE(CAST(`BUYER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`SELLER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`CATE_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ADJUST_AMT` AS STRING), '#NULL#'), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), '#NULL#'))) AS compare_column_target\n" +
                "\t\t\t, COUNT(1) AS compare_cnt_target, MAX(ORDER_ID) AS ORDER_ID_target\n" +
                "\t\t\t, MAX(ITEM_ID) AS ITEM_ID_target, MAX(BUYER_ID) AS BUYER_ID_target\n" +
                "\t\t\t, MAX(SELLER_ID) AS SELLER_ID_target, MAX(CATE_ID) AS CATE_ID_target\n" +
                "\t\t\t, MAX(ADJUST_AMT) AS ADJUST_AMT_target, MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_target\n" +
                "\t\tFROM aliyun_cdm.OS_INTEGRATION_BM_2\n" +
                "\t\tGROUP BY MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ITEM_ID` AS STRING), '#NULL#'), COALESCE(CAST(`BUYER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`SELLER_ID` AS STRING), '#NULL#'), COALESCE(CAST(`CATE_ID` AS STRING), '#NULL#'), COALESCE(CAST(`ADJUST_AMT` AS STRING), '#NULL#'), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), '#NULL#')))\n" +
                "\t) right_table\n" +
                "\tON left_table.compare_column_source = right_table.compare_column_target\n" +
                "WHERE left_table.compare_column_source = NULL\n" +
                "\tOR right_table.compare_column_target = NULL\n" +
                "\tOR left_table.compare_cnt_source <> right_table.compare_cnt_target", stmt.toString());

        assertEquals("CREATE TABLE OS_TEST_COMPARE_test\n" +
                        "AS\n" +
                        "SELECT *\n" +
                        "FROM (\n" +
                        "\tSELECT MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), ?), COALESCE(CAST(`ITEM_ID` AS STRING), ?), COALESCE(CAST(`BUYER_ID` AS STRING), ?), COALESCE(CAST(`SELLER_ID` AS STRING), ?), COALESCE(CAST(`CATE_ID` AS STRING), ?), COALESCE(CAST(`ADJUST_AMT` AS STRING), ?), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), ?))) AS compare_column_source\n" +
                        "\t\t, COUNT(1) AS compare_cnt_source, MAX(ORDER_ID) AS ORDER_ID_source\n" +
                        "\t\t, MAX(ITEM_ID) AS ITEM_ID_source, MAX(BUYER_ID) AS BUYER_ID_source\n" +
                        "\t\t, MAX(SELLER_ID) AS SELLER_ID_source, MAX(CATE_ID) AS CATE_ID_source\n" +
                        "\t\t, MAX(ADJUST_AMT) AS ADJUST_AMT_source, MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_source\n" +
                        "\tFROM aliyun_cdm.OS_INTEGRATION_PHY\n" +
                        "\tGROUP BY MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), ?), COALESCE(CAST(`ITEM_ID` AS STRING), ?), COALESCE(CAST(`BUYER_ID` AS STRING), ?), COALESCE(CAST(`SELLER_ID` AS STRING), ?), COALESCE(CAST(`CATE_ID` AS STRING), ?), COALESCE(CAST(`ADJUST_AMT` AS STRING), ?), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), ?)))\n" +
                        ") left_table\n" +
                        "\tFULL JOIN (\n" +
                        "\t\tSELECT MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), ?), COALESCE(CAST(`ITEM_ID` AS STRING), ?), COALESCE(CAST(`BUYER_ID` AS STRING), ?), COALESCE(CAST(`SELLER_ID` AS STRING), ?), COALESCE(CAST(`CATE_ID` AS STRING), ?), COALESCE(CAST(`ADJUST_AMT` AS STRING), ?), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), ?))) AS compare_column_target\n" +
                        "\t\t\t, COUNT(1) AS compare_cnt_target, MAX(ORDER_ID) AS ORDER_ID_target\n" +
                        "\t\t\t, MAX(ITEM_ID) AS ITEM_ID_target, MAX(BUYER_ID) AS BUYER_ID_target\n" +
                        "\t\t\t, MAX(SELLER_ID) AS SELLER_ID_target, MAX(CATE_ID) AS CATE_ID_target\n" +
                        "\t\t\t, MAX(ADJUST_AMT) AS ADJUST_AMT_target, MAX(DIV_CREATE_AMT) AS DIV_CREATE_AMT_target\n" +
                        "\t\tFROM aliyun_cdm.OS_INTEGRATION_BM\n" +
                        "\t\tGROUP BY MD5(CONCAT(COALESCE(CAST(`ORDER_ID` AS STRING), ?), COALESCE(CAST(`ITEM_ID` AS STRING), ?), COALESCE(CAST(`BUYER_ID` AS STRING), ?), COALESCE(CAST(`SELLER_ID` AS STRING), ?), COALESCE(CAST(`CATE_ID` AS STRING), ?), COALESCE(CAST(`ADJUST_AMT` AS STRING), ?), COALESCE(CAST(`DIV_CREATE_AMT` AS STRING), ?)))\n" +
                        "\t) right_table\n" +
                        "\tON left_table.compare_column_source = right_table.compare_column_target\n" +
                        "WHERE left_table.compare_column_source = ?\n" +
                        "\tOR right_table.compare_column_target = ?\n" +
                        "\tOR left_table.compare_cnt_source <> right_table.compare_cnt_target"
                , ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql));
    }


}