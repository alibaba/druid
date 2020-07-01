/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql.mysql.createTable;

import org.junit.Assert;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class MySqlCreateTableTest52 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE `ins_ebay_auth` ("//
                                 + "`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',"//
                                 + "`usr_id` int(10) NOT NULL COMMENT '外键，用户表',"//
                                 + "`status` char(1) COLLATE utf8_bin NOT NULL COMMENT '状态 0.有效?1.无效',"//
                                 + "`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'eBay授权码',"//
                                 + "`ebay_name` varchar(50)  NOT NULL COMMENT 'eBay唯一名',"//
                                 + "`create_time` datetime NOT NULL COMMENT '授权时间',"//
                                 + "`invalid_time` datetime NOT NULL COMMENT '授权失效时间'," + "PRIMARY KEY (`auth_id`)"//
                                 + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='INS_EBAY_AUTH';";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
//        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(7, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("ins_ebay_auth")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE `ins_ebay_auth` ("//
    +"\n\t`auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键id'," //
    +"\n\t`usr_id` int(10) NOT NULL COMMENT '外键，用户表',"//
    +"\n\t`status` char(1) COLLATE utf8_bin NOT NULL COMMENT '状态 0.有效?1.无效',"//
    +"\n\t`ebay_token` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'eBay授权码',"//
    +"\n\t`ebay_name` varchar(50) NOT NULL COMMENT 'eBay唯一名',"//
    +"\n\t`create_time` datetime NOT NULL COMMENT '授权时间',"//
    +"\n\t`invalid_time` datetime NOT NULL COMMENT '授权失效时间',"//
    +"\n\tPRIMARY KEY (`auth_id`)"
    +"\n) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin COMMENT 'INS_EBAY_AUTH'", output);
    }
}
