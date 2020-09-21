package com.alibaba.druid.bvt.sql.transform.datatype;

import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;
import com.alibaba.druid.sql.SQLTransformUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.sql.Statement;
import java.util.List;

public class Oracle2Mysql_DataTypeTest extends TestCase {
    public void test_oracle2Mysql_create_0() throws Exception {
        /*String sql = "SELECT prod_id, LISTAGG(cust_first_name||' '||cust_last_name, '; ') \n" //
                + "  WITHIN GROUP (ORDER BY amount_sold DESC) cust_list\n" //
                + "FROM sales, customers\n" //
                + "WHERE sales.cust_id = customers.cust_id AND cust_gender = 'M' \n" //
                + "  AND cust_credit_limit = 15000 AND prod_id BETWEEN 15 AND 18 \n" //
                + "  AND channel_id = 2 AND time_id > '01-JAN-01'\n" //
                + "GROUP BY prod_id;";*/
        /*String sql = "SELECT customer_id, cust_address_ntab FROM customers_demo WHERE cust_address_ntab IS A SET;";

        SQLDataType dataType = SQLParserUtils.createExprParser(sql, JdbcConstants.ORACLE).parseDataType();
        SQLDataType pgDataType = SQLTransformUtils.transformOracleToMySql(dataType);
        */
        String sql = "select distinct b.report_name,b.REPORT_CODE, case when to_char(report_start_time, 'yyyymmdd') is null then to_char(report_end_time, 'yyyymmdd') when to_char(report_start_time, 'yyyymmdd') is not null then to_char(report_start_time, 'yyyymmdd') || '-' || to_char(report_end_time, 'yyyymmdd') end reporttime, PRODUCT_TYPE_NAME, executestatu, to_CHAR(enddate, 'yyyymmdd HH24:ss:mi') as executetime, taskId,b.REPORTAGENCY_NAME AS reportAgencyName\n" +
                " FROM TC_REP_ASYNXXB a\n" +
                " LEFT JOIN T_BASE_USERREPORT c\n" +
                " LEFT JOIN TC_REP_PARA d\n" +
                " ON c.FREPORT_ID=d.REPORT_ID\n" +
                " LEFT JOIN tc_rep_task b\n" +
                " ON b.report_name = d.report_name\n" +
                " ON a.taskid = b.id\n" +
                " WHERE funcid = '0' and a.userid = 'd101468acaf34ddabac9646d2d920113' and c.fuser_id = 'd101468acaf34ddabac9646d2d920113' order by b.REPORT_CODE";
        List<SQLStatement> list = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        for(SQLStatement sqlStatement : list){
            System.out.println(sqlStatement);
        }
        //assertEquals("bigint", pgDataType.toString());
    }
}
