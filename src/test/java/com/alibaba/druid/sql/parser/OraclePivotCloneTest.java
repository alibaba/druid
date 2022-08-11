package com.alibaba.druid.sql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import junit.framework.TestCase;

/**
 * @author gfChris
 * @version 1.0
 * @Description
 * @date 2022/8/11 下午4:46
 */
public class OraclePivotCloneTest extends TestCase {

    public void testCreateCharset() {

        String sql = "Select \n" +
                "DEPT_ID,\n" +
                "M01,M02,M03,M04,M05,M06,M07,M08,M09,M10,M11,M12\n" +
                "from \n" +
                "(\n" +
                "Select \n" +
                "DEPT_ID,CMONTH,\n" +
                "SUM(SO_TAXMONEY) AS SO_TAXMONEY\n" +
                "FROM DW_SCM_DIM_SALEDATA\n" +
                "GROUP BY \n" +
                "DEPT_ID,CMONTH\n" +
                ")  \n" +
                "pivot(\n" +
                "    sum(SO_TAXMONEY) for CMONTH in (    \n" +
                "        '01' as M01,                \n" +
                "        '02' as M02,                \n" +
                "        '03' as M03,\n" +
                "        '04' as M04,\n" +
                "        '05' as M05,\n" +
                "        '06' as M06,\n" +
                "        '07' as M07,\n" +
                "        '08' as M08,\n" +
                "        '09' as M09,\n" +
                "        '10' as M10,\n" +
                "        '11' as M11,\n" +
                "        '12' as M12\n" +
                "    )\n" +
                "  );";

        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, DbType.oracle);

        SQLStatement sqlStatement1 = sqlStatement.clone();

        System.out.println(sqlStatement1.toString());
    }

}
