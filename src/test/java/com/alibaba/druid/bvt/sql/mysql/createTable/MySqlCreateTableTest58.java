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
package com.alibaba.druid.bvt.sql.mysql.createTable;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import org.junit.Assert;
import org.junit.Test;

public class MySqlCreateTableTest58 extends MysqlTest {

    @Test
    public void test_one() throws Exception {
        String sql = "CREATE TABLE `appservice_account` (" + "`id` integer AUTO_INCREMENT NOT NULL PRIMARY KEY,"
                     + "`password` varchar(128) NOT NULL," + "`last_login` datetime NOT NULL,"
                     + "`username` varchar(40) NOT NULL UNIQUE," + "`date_of_birth` date NOT NULL,"
                     + "`head` varchar(100) NOT NULL," + "`headThumb` varchar(100) NOT NULL,"
                     + "`name` varchar(50) NOT NULL," + "`gender` integer," + "`uploadVideoCount` integer NOT NULL,"
                     + "`fansCount` integer NOT NULL," + "`balance` numeric(19, 2) NOT NULL,"
                     + "`brick` integer NOT NULL," + "`reward` integer NOT NULL," + "`token` varchar(500) NOT NULL,"
                     + "`weiboUserid` varchar(50) NOT NULL," + "`weiboAccesstoken` varchar(500) NOT NULL,"
                     + "`qqUserid` varchar(50) NOT NULL," + "`qqAccesstoken` varchar(500) NOT NULL,"
                     + "`wechatUserid` varchar(50) NOT NULL," + "`wechatAccesstoken` varchar(500) NOT NULL,"
                     + "`is_active` bool NOT NULL," + "`is_admin` bool NOT NULL" + ")";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        {
            String output = SQLUtils.toMySqlString(stmt);
            Assert.assertEquals("CREATE TABLE `appservice_account` (" + //
                                "\n\t`id` integer NOT NULL PRIMARY KEY AUTO_INCREMENT," + //
                                "\n\t`password` varchar(128) NOT NULL," + //
                                "\n\t`last_login` datetime NOT NULL," + //
                                "\n\t`username` varchar(40) NOT NULL UNIQUE," + //
                                "\n\t`date_of_birth` date NOT NULL," + //
                                "\n\t`head` varchar(100) NOT NULL," + //
                                "\n\t`headThumb` varchar(100) NOT NULL," + //
                                "\n\t`name` varchar(50) NOT NULL," + //
                                "\n\t`gender` integer," + //
                                "\n\t`uploadVideoCount` integer NOT NULL," + //
                                "\n\t`fansCount` integer NOT NULL," + //
                                "\n\t`balance` numeric(19, 2) NOT NULL," + //
                                "\n\t`brick` integer NOT NULL," + //
                                "\n\t`reward` integer NOT NULL," + //
                                "\n\t`token` varchar(500) NOT NULL," + //
                                "\n\t`weiboUserid` varchar(50) NOT NULL," + //
                                "\n\t`weiboAccesstoken` varchar(500) NOT NULL," + //
                                "\n\t`qqUserid` varchar(50) NOT NULL," + //
                                "\n\t`qqAccesstoken` varchar(500) NOT NULL," + //
                                "\n\t`wechatUserid` varchar(50) NOT NULL," + //
                                "\n\t`wechatAccesstoken` varchar(500) NOT NULL," + //
                                "\n\t`is_active` bool NOT NULL," + //
                                "\n\t`is_admin` bool NOT NULL" + //
                                "\n)", output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            Assert.assertEquals("create table `appservice_account` (" + //
                                "\n\t`id` integer not null primary key auto_increment," + //
                                "\n\t`password` varchar(128) not null," + //
                                "\n\t`last_login` datetime not null," + //
                                "\n\t`username` varchar(40) not null unique," + //
                                "\n\t`date_of_birth` date not null," + //
                                "\n\t`head` varchar(100) not null," + //
                                "\n\t`headThumb` varchar(100) not null," + //
                                "\n\t`name` varchar(50) not null," + //
                                "\n\t`gender` integer," + //
                                "\n\t`uploadVideoCount` integer not null," + //
                                "\n\t`fansCount` integer not null," + //
                                "\n\t`balance` numeric(19, 2) not null," + //
                                "\n\t`brick` integer not null," + //
                                "\n\t`reward` integer not null," + //
                                "\n\t`token` varchar(500) not null," + //
                                "\n\t`weiboUserid` varchar(50) not null," + //
                                "\n\t`weiboAccesstoken` varchar(500) not null," + //
                                "\n\t`qqUserid` varchar(50) not null," + //
                                "\n\t`qqAccesstoken` varchar(500) not null," + //
                                "\n\t`wechatUserid` varchar(50) not null," + //
                                "\n\t`wechatAccesstoken` varchar(500) not null," + //
                                "\n\t`is_active` bool not null," + //
                                "\n\t`is_admin` bool not null" + //
                                "\n)", output);
        }
    }
}
