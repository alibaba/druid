package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;

public class MysqlKeyWordAliasParseTest extends TestCase {

    public void test_mysql_query() {
        String[] sqls = {
                "select a.da comment from (select a ad from a ) t",
                "select orderId, orderCode from ( select  pms_order.remark comment from pms_order) t_order",
                "select ada comment from (select a comment from a ) t",
                "select comment from (select a comment from a ) t",
                "select a TRUNCATE from  t",
                "select a view from  t",
                "select a SEQUENCE, (select 1) bb,(select id from tt limit 1) view from  (select a do from b) `truncate` ",
                "select a tablespace from  t",
                "select a do from  t",
                "select a any from  t limit 5",
                "select a close from  t limit 5",
                "select a , (select b SEQUENCE from demo limit 1) from t"
        };

        for (int i = 0; i < sqls.length; i++) {
            MySqlStatementParser parser = new MySqlStatementParser(sqls[i]);
            parser.parseStatementList();
        }

    }
}
