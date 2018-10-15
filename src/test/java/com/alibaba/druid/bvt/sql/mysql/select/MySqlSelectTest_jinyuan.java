package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_jinyuan extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "SELECT CATE1,PAY_ORD_BYR_CNT_DTR_004,CATE2,DDD,CRT_ORD_BYR_CNT_DTR_010,PAY_ORD_BYR_CNT_DTR_010,CRT_ORD_BYR_CNT_DTR_001,CRT_ORD_BYR_CNT_DTR_004,PAY_ORD_BYR_CNT_DTR_011,PAY_ORD_BYR_CNT_DTR_001,BIZ,PAY_ORD_AMT_DTR_011,PAY_ORD_AMT_DTR_010,CRT_ORD_VLD_AMT_DTR_001,PAY_ORD_AMT_DTR_001,CRT_ORD_VLD_AMT_DTR_004,PAY_ORD_AMT_DTR_004,CRT_ORD_VLD_AMT_DTR_010 FROM DWI_PUB_HBD_CATE_DTR \n" +
                "WHERE cate1=? and ddd=? and biz=? and cate2=? and app=? LIMIT 0,1";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("SELECT CATE1, PAY_ORD_BYR_CNT_DTR_004, CATE2, DDD, CRT_ORD_BYR_CNT_DTR_010\n" +
                "\t, PAY_ORD_BYR_CNT_DTR_010, CRT_ORD_BYR_CNT_DTR_001, CRT_ORD_BYR_CNT_DTR_004, PAY_ORD_BYR_CNT_DTR_011, PAY_ORD_BYR_CNT_DTR_001\n" +
                "\t, BIZ, PAY_ORD_AMT_DTR_011, PAY_ORD_AMT_DTR_010, CRT_ORD_VLD_AMT_DTR_001, PAY_ORD_AMT_DTR_001\n" +
                "\t, CRT_ORD_VLD_AMT_DTR_004, PAY_ORD_AMT_DTR_004, CRT_ORD_VLD_AMT_DTR_010\n" +
                "FROM DWI_PUB_HBD_CATE_DTR\n" +
                "WHERE cate1 = ?\n" +
                "\tAND ddd = ?\n" +
                "\tAND biz = ?\n" +
                "\tAND cate2 = ?\n" +
                "\tAND app = ?\n" +
                "LIMIT 0, 1", stmt.toString());
    }
}