package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/9/23.
 */
public class MySqlParameterizedOutputVisitorTest_61 extends TestCase {
    public void test_for_parameterize() throws Exception {

        String sql = "insert ignore into ktv_ibx_1690 (id, msg_id, cid, openid, gmt_create, gmt_modified, read_status, reach_status, create_at, type, tag, sender_id, extension, domain, at_me) VALUES (399644333, 49099001633, \"543351210\", 306202, now(), now(), 2, 2, 1515051603113, 51, 0, 26659032, '', 'dingding', 0), (399644599334, 49399311994, \"543351210\", 306202, now(), now(), 2, 2, 1515045263923, 51, 0, 55235569, '', 'dingding', 0), (399644599335, 49352513493, \"543351210\", 306202, now(), now(), 2, 2, 1515032652532, 51, 0, 91605449, '', 'dingding', 0), (399644599336, 49333315435, \"543351210\", 306202, now(), now(), 2, 2, 1515029099196, 51, 0, 159661969, '', 'dingding', 0), (399644599333, 49253992490, \"543351210\", 306202, now(), now(), 2, 2, 1514969992945, 51, 0, 26659032, '', 'dingding', 0), (399644599339, 49553143033, \"543351210\", 306202, now(), now(), 2, 2, 1514969920339, 51, 0, 26659032, '', 'dingding', 0), (399644599339, 49191512129, \"543351210\", 306202, now(), now(), 2, 2, 1514943655456, 51, 0, 104293319, '', 'dingding', 0), (399644599340, 49465261219, \"543351210\", 306202, now(), now(), 2, 2, 1514943290229, 51, 0, 26659032, '', 'dingding', 0), (399644599341, 49169690919, \"543351210\", 306202, now(), now(), 2, 2, 1514946945243, 51, 0, 104293319, '', 'dingding', 0), (399644599342, 43096553593, \"543351210\", 306202, now(), now(), 2, 2, 1514343951224, 51, 0, 26659032, '', 'dingding', 0), (399644599343, 46939394349, \"543351210\", 306202, now(), now(), 2, 2, 1514230399590, 51, 0, 26659032, '', 'dingding', 0), (399644599344, 43121249113, \"543351210\", 306202, now(), now(), 2, 2, 1514256154905, 51, 0, 26659032, '', 'dingding', 0), (399644599345, 46996923949, \"543351210\", 306202, now(), now(), 2, 2, 1514256015609, 51, 0, 26659032, '', 'dingding', 0), (399644599346, 43506239192, \"543351210\", 306202, now(), now(), 2, 2, 1514253595163, 51, 0, 26659032, '', 'dingding', 0), (399644599343, 46914302251, \"543351210\", 306202, now(), now(), 2, 2, 1514193122944, 51, 0, 26659032, '', 'dingding', 0), (399644599349, 46901562353, \"543351210\", 306202, now(), now(), 2, 2, 1514193003304, 51, 0, 26659032, '', 'dingding', 0), (399644599349, 46993995514, \"543351210\", 306202, now(), now(), 2, 2, 1514192994193, 51, 0, 26659032, '', 'dingding', 0), (399644599350, 46906923503, \"543351210\", 306202, now(), now(), 2, 2, 1514191365460, 51, 0, 159661969, '', 'dingding', 0), (399644599351, 43015354139, \"543351210\", 306202, now(), now(), 2, 2, 1514191433515, 51, 0, 26659032, '', 'dingding', 0), (399644599352, 43410063334, \"543351210\", 306202, now(), now(), 2, 2, 1514191049930, 51, 0, 26659032, '', 'dingding', 0), (399644599353, 43410045959, \"543351210\", 306202, now(), now(), 2, 2, 1514190943523, 51, 0, 26659032, '', 'dingding', 0), (399644599354, 46919625433, \"543351210\", 306202, now(), now(), 2, 2, 1514130390293, 51, 0, 26659032, '', 'dingding', 0), (399644599355, 46336694953, \"543351210\", 306202, now(), now(), 2, 2, 1514130332624, 51, 0, 26659032, '', 'dingding', 0)";

        List<Object> params =  new ArrayList<Object>();
        String psql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL, params);
        assertEquals("INSERT IGNORE INTO ktv_ibx (id, msg_id, cid, openid, gmt_create\n" +
                "\t, gmt_modified, read_status, reach_status, create_at, type\n" +
                "\t, tag, sender_id, extension, domain, at_me)\n" +
                "VALUES (?, ?, ?, ?, now()\n" +
                "\t\t, now(), ?, ?, ?, ?\n" +
                "\t\t, ?, ?, ?, ?, ?)", psql);
        assertEquals(13, params.size());
        assertEquals(399644333, params.get(0));
        assertEquals(49099001633L, params.get(1));
        assertEquals("543351210", params.get(2));
    }

}
