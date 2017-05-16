/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.mysql.create;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat.Column;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest76 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE `log_info_20170516` (\n" +
                "  `toid` varchar(40) CHARACTER SET utf8 NOT NULL,\n" +
                "  `title` varchar(40) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `traceid` varchar(50) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `parentid` varchar(40) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `key1` varchar(100) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `key2` varchar(100) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `key3` varchar(100) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `type` varchar(50) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `createdate` varchar(30) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `classmethod` varchar(100) CHARACTER SET utf8 DEFAULT NULL,\n" +
                "  `linenum` int(11) DEFAULT NULL,\n" +
                "  `threadname` varchar(80) CHARACTER SET utf8 DEFAULT '',\n" +
                "  `content` text CHARACTER SET utf8,\n" +
                "  PRIMARY KEY (`toid`),\n" +
                "  KEY `traceid_index` (`traceid`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        Column column = visitor.getColumn("log_info_20170516", "toid");
        Assert.assertNotNull(column);
        Assert.assertEquals("varchar", column.getDataType());

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `log_info_20170516` (\n" +
                    "\t`toid` varchar(40) CHARACTER SET utf8 NOT NULL, \n" +
                    "\t`title` varchar(40) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`traceid` varchar(50) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`parentid` varchar(40) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`key1` varchar(100) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`key2` varchar(100) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`key3` varchar(100) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`type` varchar(50) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`createdate` varchar(30) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`classmethod` varchar(100) CHARACTER SET utf8 DEFAULT NULL, \n" +
                    "\t`linenum` int(11) DEFAULT NULL, \n" +
                    "\t`threadname` varchar(80) CHARACTER SET utf8 DEFAULT '', \n" +
                    "\t`content` text CHARACTER SET utf8, \n" +
                    "\tPRIMARY KEY (`toid`), \n" +
                    "\tKEY `traceid_index` (`traceid`)\n" +
                    ") ENGINE = InnoDB CHARSET = utf8mb4", output);
        }
        
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `log_info_20170516` (\n" +
                    "\t`toid` varchar(40) character set utf8 not null, \n" +
                    "\t`title` varchar(40) character set utf8 default null, \n" +
                    "\t`traceid` varchar(50) character set utf8 default null, \n" +
                    "\t`parentid` varchar(40) character set utf8 default null, \n" +
                    "\t`key1` varchar(100) character set utf8 default null, \n" +
                    "\t`key2` varchar(100) character set utf8 default null, \n" +
                    "\t`key3` varchar(100) character set utf8 default null, \n" +
                    "\t`type` varchar(50) character set utf8 default null, \n" +
                    "\t`createdate` varchar(30) character set utf8 default null, \n" +
                    "\t`classmethod` varchar(100) character set utf8 default null, \n" +
                    "\t`linenum` int(11) default null, \n" +
                    "\t`threadname` varchar(80) character set utf8 default '', \n" +
                    "\t`content` text character set utf8, \n" +
                    "\tprimary key (`toid`), \n" +
                    "\tkey `traceid_index` (`traceid`)\n" +
                    ") engine = InnoDB charset = utf8mb4", output);
        }
    }
}
