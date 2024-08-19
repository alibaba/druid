package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLParseAssertUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/6102" >Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/create-table.html">mysql create table </a>
 */
public class Issue6102 {


    @Test
    public void test_parse_create() {
        for (DbType dbType : new DbType[]{DbType.mysql}) {
            for (String sql : new String[]{
                "CREATE TABLE `account_info` (\n"
                    + "  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键id',\n"
                    + "  `account_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账户编号',\n"
                    + "  `open_acct_agreement_info` json DEFAULT (_utf8mb4'{}') COMMENT '协议信息',\n"
                    + "  `ext_info` json DEFAULT (_utf8mb4'{}') COMMENT '扩展信息',\n"
                    + "  `last_push_time` datetime DEFAULT NULL COMMENT '账户推送时间',\n"
                    + "  `create_time` datetime NOT NULL COMMENT '创建时间',\n"
                    + "  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',\n"
                    + "  PRIMARY KEY (`id`) USING BTREE,\n"
                    + "  UNIQUE KEY `idx_account_num` (`account_num`) USING BTREE\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='账户信息表';",

            }) {
                SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
                List<SQLStatement> statementList = parser.parseStatementList();
                System.out.println(statementList);
                String sqlnew=statementList.toString();
                assertTrue(sqlnew.contains("DEFAULT (_utf8mb4 '{}')"));
                SQLParseAssertUtil.assertParseSql(sql, dbType);
            }
        }
    }


}
