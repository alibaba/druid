package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class Issue2002 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "select id,sum(uv[1]) uv1,sum(uv[2]) uv2\n" +
                "from xxxxx where a in\n" +
                "                     (  \n" +
                "                        ?\n" +
                "                     ) \n" +
                " and ta->'taAge' ??|\n" +
                "                 \n" +
                "                         '{  \n" +
                "                            1\n" +
                "                         , \n" +
                "                            2\n" +
                "                         , \n" +
                "                            3\n" +
                "                         }' \n" +
                "group by id";

        StatFilter filter = new StatFilter();
        filter.setMergeSql(true);
        String psql = filter.mergeSql(sql, JdbcConstants.POSTGRESQL);
        System.out.println(psql);
    }
}
