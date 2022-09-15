package com.alibaba.druid.bvt.sql.postgresql.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class PGSelectTest74 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT COUNTRY_CODE,AREA_CODE,CITY_CODE,PARK_CODE,PARK_CN,PARK_EN,\n" +
                "SUM(case when EVNT_BIG_TYPE_CODE = 'SAFETY_INCIDENT' then EVNT_NUM else 0 end) AS SAFE_INCIDENT,\n" +
                "SUM(case when EVNT_BIG_TYPE_CODE = 'FIRE_INCIDENT' then EVNT_NUM else 0 end) AS FIRE_INCIDENT \n" +
                "FROM dm.dm_ioc_event_type_h_sf \n" +
                "WHERE CALCULATE_TIME >= (CURRENT_DATE - INTERVAL '1' MONTH) \n" +
                "AND PARK_CODE = '101001001083' \n" +
                "GROUP BY COUNTRY_CODE,AREA_CODE,CITY_CODE,PARK_CODE,PARK_CN,PARK_EN;";

        final List<SQLStatement> statements = SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
        assertEquals(1, statements.size());
        final SQLStatement stmt = statements.get(0);

        assertEquals("SELECT COUNTRY_CODE, AREA_CODE, CITY_CODE, PARK_CODE, PARK_CN\n" +
                "\t, PARK_EN\n" +
                "\t, SUM(CASE \n" +
                "\t\tWHEN EVNT_BIG_TYPE_CODE = 'SAFETY_INCIDENT' THEN EVNT_NUM\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS SAFE_INCIDENT\n" +
                "\t, SUM(CASE \n" +
                "\t\tWHEN EVNT_BIG_TYPE_CODE = 'FIRE_INCIDENT' THEN EVNT_NUM\n" +
                "\t\tELSE 0\n" +
                "\tEND) AS FIRE_INCIDENT\n" +
                "FROM dm.dm_ioc_event_type_h_sf\n" +
                "WHERE CALCULATE_TIME >= CURRENT_DATE - INTERVAL '1' MONTH\n" +
                "\tAND PARK_CODE = '101001001083'\n" +
                "GROUP BY COUNTRY_CODE, AREA_CODE, CITY_CODE, PARK_CODE, PARK_CN, PARK_EN;", stmt.toString());

        assertEquals("select COUNTRY_CODE, AREA_CODE, CITY_CODE, PARK_CODE, PARK_CN\n" +
                "\t, PARK_EN\n" +
                "\t, sum(case \n" +
                "\t\twhen EVNT_BIG_TYPE_CODE = 'SAFETY_INCIDENT' then EVNT_NUM\n" +
                "\t\telse 0\n" +
                "\tend) as SAFE_INCIDENT\n" +
                "\t, sum(case \n" +
                "\t\twhen EVNT_BIG_TYPE_CODE = 'FIRE_INCIDENT' then EVNT_NUM\n" +
                "\t\telse 0\n" +
                "\tend) as FIRE_INCIDENT\n" +
                "from dm.dm_ioc_event_type_h_sf\n" +
                "where CALCULATE_TIME >= CURRENT_DATE - interval '1' month\n" +
                "\tand PARK_CODE = '101001001083'\n" +
                "group by COUNTRY_CODE, AREA_CODE, CITY_CODE, PARK_CODE, PARK_CN, PARK_EN;", stmt.toLowerCaseString());
    }
}
