package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.visitor.VisitorFeature;


public class MySqlCreateTableTest163_distributed_by_hash_group extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "CREATE TABLE `test_user` ( `id` int(11) NOT NULL AUTO_INCREMENT ) DISTRIBUTE BY HASH(id,name);";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals(sql, SQLUtils.toSQLString(stmt, DbType.mysql, new SQLUtils.FormatOption(VisitorFeature.OutputUCase)));
    }

    public void test_1() throws Exception {
        String sql = "CREATE TABLE `test_user` ( `id` int(11) NOT NULL AUTO_INCREMENT ) DISTRIBUTED BY HASH(col1,col2)(g1,g2,g3);";

        MySqlCreateTableStatement stmt = (MySqlCreateTableStatement) SQLUtils.parseSingleMysqlStatement(sql);

        assertEquals(sql, SQLUtils.toSQLString(stmt, DbType.mysql, new SQLUtils.FormatOption(
                VisitorFeature.OutputDistributedLiteralInCreateTableStmt, VisitorFeature.OutputUCase)));
    }

}