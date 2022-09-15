package com.alibaba.druid.bvt.bug;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class Issue3952 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select\n" +
                "  d.id,\n" +
                "  b.contract_date as                              contractDate,\n" +
                "  d.contract_no,\n" +
                "  d.brand_code    as                              brandCode,\n" +
                "  d.num,\n" +
                "  (SELECT COALESCE(sum(num1), 0)  FROM tc_order_record  WHERE tc_order_record.contract_no = d.contract_no)  orderSumNum,\n" +
                "  (SELECT (COALESCE(d.num, 0) - COALESCE(sum(num1), 0))  FROM tc_order_record  WHERE tc_order_record.contract_no = d.contract_no) avai\n" +
                "from tc_contract_detail as d left join tc_contract_base  b on d.contract_no = b.contract_no\n" +
                "where b.data_transfer_flag = '3' and if('' != '', d.contract_no = '', 1 = 1)\n" +
                "      and if('' != '', d.brand_code = '', 1 = 1) and if(NULL != '', b.contract_date >= NULL, 1 = 1) and if(NULL != '',   b.contract_date <= NULL, 1 = 1)\n" +
                "order by b.contract_date desc, d.id desc\n" +
                "limit 10";

        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals("select d.id, b.contract_date as contractDate, d.contract_no, d.brand_code as brandCode, d.num\n" +
                "\t, (\n" +
                "\t\tselect COALESCE(sum(num1), 0)\n" +
                "\t\tfrom tc_order_record\n" +
                "\t\twhere tc_order_record.contract_no = d.contract_no\n" +
                "\t) as orderSumNum\n" +
                "\t, (\n" +
                "\t\tselect COALESCE(d.num, 0) - COALESCE(sum(num1), 0)\n" +
                "\t\tfrom tc_order_record\n" +
                "\t\twhere tc_order_record.contract_no = d.contract_no\n" +
                "\t) as avai\n" +
                "from tc_contract_detail d\n" +
                "\tleft join tc_contract_base b on d.contract_no = b.contract_no\n" +
                "where b.data_transfer_flag = '3'\n" +
                "\tand if('' != '', d.contract_no = '', 1 = 1)\n" +
                "\tand if('' != '', d.brand_code = '', 1 = 1)\n" +
                "\tand if(null != '', b.contract_date >= null, 1 = 1)\n" +
                "\tand if(null != '', b.contract_date <= null, 1 = 1)\n" +
                "order by b.contract_date desc, d.id desc\n" +
                "limit 10", stmt.toLowerCaseString());
    }
}
