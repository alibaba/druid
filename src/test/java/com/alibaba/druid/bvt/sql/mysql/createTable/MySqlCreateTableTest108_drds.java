package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlCreateTableTest108_drds extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "create table ARCHIVE_ERR_RECORD\n" +
                "(\n" +
                "SUBS_ORDER_ID numeric(18,0) not null comment '订单编号',\n" +
                "ERR_MSG text comment '失败的消息结构',\n" +
                "ERR_REASON varchar(255) comment '失败原因',\n" +
                "PART_ID integer not null comment '分区标识（取订单编号中的月份）'\n" +
                ")\n" +
                "DBPARTITION BY HASH(SUBS_ORDER_ID)\n" +
                "TBPARTITION BY UNI_HASH(PART_ID) TBPARTITIONS 12;";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ALIYUN_DRDS);
        SQLCreateTableStatement stmt = (SQLCreateTableStatement) statementList.get(0);

        assertEquals(1, statementList.size());
        assertEquals(4, stmt.getTableElementList().size());

        assertEquals("CREATE TABLE ARCHIVE_ERR_RECORD (\n" +
                "\tSUBS_ORDER_ID numeric(18, 0) NOT NULL COMMENT '订单编号',\n" +
                "\tERR_MSG text COMMENT '失败的消息结构',\n" +
                "\tERR_REASON varchar(255) COMMENT '失败原因',\n" +
                "\tPART_ID integer NOT NULL COMMENT '分区标识（取订单编号中的月份）'\n" +
                ")\n" +
                "DBPARTITION BY HASH (SUBS_ORDER_ID)\n" +
                "TBPARTITION BY HASH (PART_ID)\n" +
                "TBPARTITIONS 12;", stmt.toString());
    }
}