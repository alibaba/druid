package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_173 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "/*+engine=MPP*/\n" +
                "        \n" +
                "        WITH\n" +
                "  year_total AS (\n" +
                "   SELECT\n" +
                "     c_customer_id customer_id\n" +
                "   , c_first_name customer_first_name\n" +
                "   , c_last_name customer_last_name\n" +
                "   , c_preferred_cust_flag customer_preferred_cust_flag\n" +
                "   , c_birth_country customer_birth_country\n" +
                "   , c_login customer_login\n" +
                "   , c_email_address customer_email_address\n" +
                "   , d_year dyear\n" +
                "   , sum((ss_ext_list_price - ss_ext_discount_amt)) year_total\n" +
                "   , 's' sale_type\n" +
                "   FROM\n" +
                "     customer\n" +
                "   , store_sales\n" +
                "   , date_dim\n" +
                "   WHERE (c_customer_sk = ss_customer_sk)\n" +
                "      AND (ss_sold_date_sk = d_date_sk)\n" +
                "   GROUP BY c_customer_id, c_first_name, c_last_name, c_preferred_cust_flag, c_birth_country, c_login, c_email_address, d_year\n" +
                "UNION ALL    SELECT\n" +
                "     c_customer_id customer_id\n" +
                "   , c_first_name customer_first_name\n" +
                "   , c_last_name customer_last_name\n" +
                "   , c_preferred_cust_flag customer_preferred_cust_flag\n" +
                "   , c_birth_country customer_birth_country\n" +
                "   , c_login customer_login\n" +
                "   , c_email_address customer_email_address\n" +
                "   , d_year dyear\n" +
                "   , sum((ws_ext_list_price - ws_ext_discount_amt)) year_total\n" +
                "   , 'w' sale_type\n" +
                "   FROM\n" +
                "     customer\n" +
                "   , web_sales\n" +
                "   , date_dim\n" +
                "   WHERE (c_customer_sk = ws_bill_customer_sk)\n" +
                "      AND (ws_sold_date_sk = d_date_sk)\n" +
                "   GROUP BY c_customer_id\n" +
                "            ,c_first_name\n" +
                "\t    ,c_last_name\n" +
                "\t    , c_preferred_cust_flag, c_birth_country, c_login, c_email_address, d_year\n" +
                ")\n" +
                "SELECT\n" +
                "  t_s_secyear.customer_id\n" +
                ", t_s_secyear.customer_first_name\n" +
                ", t_s_secyear.customer_last_name\n" +
                ", t_s_secyear.customer_preferred_cust_flag\n" +
                ", t_s_secyear.customer_birth_country\n" +
                ", t_s_secyear.customer_login\n" +
                "FROM\n" +
                "  year_total t_s_firstyear\n" +
                ", year_total t_s_secyear\n" +
                ", year_total t_w_firstyear\n" +
                ", year_total t_w_secyear\n" +
                "WHERE (t_s_secyear.customer_id = t_s_firstyear.customer_id)\n" +
                "   AND (t_s_firstyear.customer_id = t_w_secyear.customer_id)\n" +
                "   AND (t_s_firstyear.customer_id = t_w_firstyear.customer_id)\n" +
                "   AND (t_s_firstyear.sale_type = 's')\n" +
                "   AND (t_w_firstyear.sale_type = 'w')\n" +
                "   AND (t_s_secyear.sale_type = 's')\n" +
                "   AND (t_w_secyear.sale_type = 'w')\n" +
                "   AND (t_s_firstyear.dyear = 2001)\n" +
                "   AND (t_s_secyear.dyear = (2001 + 1))\n" +
                "   AND (t_w_firstyear.dyear = 2001)\n" +
                "   AND (t_w_secyear.dyear = (2001 + 1))\n" +
                "   AND (t_s_firstyear.year_total > 0)\n" +
                "   AND (t_w_firstyear.year_total > 0)\n" +
                "   AND ((CASE WHEN (t_w_firstyear.year_total > 0) THEN (t_w_secyear.year_total / t_w_firstyear.year_total) ELSE DECIMAL '0.0' END) > (CASE WHEN (t_s_firstyear.year_total > 0) THEN (t_s_secyear.year_total / t_s_firstyear.year_total) ELSE DECIMAL '0.0' END))\n" +
                "ORDER BY t_s_secyear.customer_id ASC, t_s_secyear.customer_first_name ASC, t_s_secyear.customer_last_name ASC, t_s_secyear.customer_preferred_cust_flag ASC\n" +
                "LIMIT 100\n" +
                "        \n" +
                "    \n";
//
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("/*+engine=MPP*/\n" +
                "WITH year_total AS (\n" +
                "\t\tSELECT c_customer_id AS customer_id, c_first_name AS customer_first_name, c_last_name AS customer_last_name, c_preferred_cust_flag AS customer_preferred_cust_flag, c_birth_country AS customer_birth_country\n" +
                "\t\t\t, c_login AS customer_login, c_email_address AS customer_email_address, d_year AS dyear\n" +
                "\t\t\t, sum(ss_ext_list_price - ss_ext_discount_amt) AS year_total, 's' AS sale_type\n" +
                "\t\tFROM customer, store_sales, date_dim\n" +
                "\t\tWHERE c_customer_sk = ss_customer_sk\n" +
                "\t\t\tAND ss_sold_date_sk = d_date_sk\n" +
                "\t\tGROUP BY c_customer_id, c_first_name, c_last_name, c_preferred_cust_flag, c_birth_country, c_login, c_email_address, d_year\n" +
                "\t\tUNION ALL\n" +
                "\t\tSELECT c_customer_id AS customer_id, c_first_name AS customer_first_name, c_last_name AS customer_last_name, c_preferred_cust_flag AS customer_preferred_cust_flag, c_birth_country AS customer_birth_country\n" +
                "\t\t\t, c_login AS customer_login, c_email_address AS customer_email_address, d_year AS dyear\n" +
                "\t\t\t, sum(ws_ext_list_price - ws_ext_discount_amt) AS year_total, 'w' AS sale_type\n" +
                "\t\tFROM customer, web_sales, date_dim\n" +
                "\t\tWHERE c_customer_sk = ws_bill_customer_sk\n" +
                "\t\t\tAND ws_sold_date_sk = d_date_sk\n" +
                "\t\tGROUP BY c_customer_id, c_first_name, c_last_name, c_preferred_cust_flag, c_birth_country, c_login, c_email_address, d_year\n" +
                "\t)\n" +
                "SELECT t_s_secyear.customer_id, t_s_secyear.customer_first_name, t_s_secyear.customer_last_name, t_s_secyear.customer_preferred_cust_flag, t_s_secyear.customer_birth_country\n" +
                "\t, t_s_secyear.customer_login\n" +
                "FROM year_total t_s_firstyear, year_total t_s_secyear, year_total t_w_firstyear, year_total t_w_secyear\n" +
                "WHERE t_s_secyear.customer_id = t_s_firstyear.customer_id\n" +
                "\tAND t_s_firstyear.customer_id = t_w_secyear.customer_id\n" +
                "\tAND t_s_firstyear.customer_id = t_w_firstyear.customer_id\n" +
                "\tAND t_s_firstyear.sale_type = 's'\n" +
                "\tAND t_w_firstyear.sale_type = 'w'\n" +
                "\tAND t_s_secyear.sale_type = 's'\n" +
                "\tAND t_w_secyear.sale_type = 'w'\n" +
                "\tAND t_s_firstyear.dyear = 2001\n" +
                "\tAND t_s_secyear.dyear = 2001 + 1\n" +
                "\tAND t_w_firstyear.dyear = 2001\n" +
                "\tAND t_w_secyear.dyear = 2001 + 1\n" +
                "\tAND t_s_firstyear.year_total > 0\n" +
                "\tAND t_w_firstyear.year_total > 0\n" +
                "\tAND CASE \n" +
                "\t\tWHEN t_w_firstyear.year_total > 0 THEN t_w_secyear.year_total / t_w_firstyear.year_total\n" +
                "\t\tELSE DECIMAL '0.0'\n" +
                "\tEND > CASE \n" +
                "\t\tWHEN t_s_firstyear.year_total > 0 THEN t_s_secyear.year_total / t_s_firstyear.year_total\n" +
                "\t\tELSE DECIMAL '0.0'\n" +
                "\tEND\n" +
                "ORDER BY t_s_secyear.customer_id ASC, t_s_secyear.customer_first_name ASC, t_s_secyear.customer_last_name ASC, t_s_secyear.customer_preferred_cust_flag ASC\n" +
                "LIMIT 100", stmt.toString());


    }

}