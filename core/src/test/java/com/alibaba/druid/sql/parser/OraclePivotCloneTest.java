package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * @author gfChris
 * @version 1.0
 * @Description
 * @date 2022/8/11 下午4:46
 */
public class OraclePivotCloneTest extends TestCase {

    public void testCreateCharset() {

        String sql = "SELECT DEPT_ID, M01, M02, M03, M04\n" +
                "\t, M05, M06, M07, M08, M09\n" +
                "\t, M10, M11, M12\n" +
                "FROM (\n" +
                "\tSELECT DEPT_ID, CMONTH, SUM(SO_TAXMONEY) AS SO_TAXMONEY\n" +
                "\tFROM DW_SCM_DIM_SALEDATA\n" +
                "\tGROUP BY DEPT_ID, CMONTH\n" +
                ")\n" +
                "PIVOT (sum(SO_TAXMONEY) FOR CMONTH IN ('01' AS M01, '02' AS M02, '03' AS M03, '04' AS M04, '05' AS M05, '06' AS M06, '07' AS M07, '08' AS M08, '09' AS M09, '10' AS M10, '11' AS M11, '12' AS M12));";

        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, DbType.oracle);

        SQLStatement sqlStatement1 = sqlStatement.clone();

        System.out.println(sqlStatement1.toString());


        Assert.assertTrue(sqlStatement1.toString().equals(sql));

    }

}
