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
package com.alibaba.druid.bvt.sql.mysql.alter;

import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class MySqlAlterTableAddPrimaryKey extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "ALTER TABLE `test`.`tb1` CHANGE COLUMN `fid` `fid` INT(11) NOT NULL DEFAULT NULL, ADD PRIMARY KEY (`fid`) ;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        
        Assert.assertEquals("ALTER TABLE `test`.`tb1`" + //
                            "\n\tCHANGE COLUMN `fid` `fid` INT(11) NOT NULL DEFAULT NULL,\n\t" + //
                            "ADD PRIMARY KEY (`fid`);", SQLUtils.toMySqlString(stmt));
        
        Assert.assertEquals("alter table `test`.`tb1`" + //
                "\n\tchange column `fid` `fid` INT(11) not null default null,\n\t" + //
                "add primary key (`fid`);", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));

        SchemaStatVisitor visitor = new SQLUtils().createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);
        TableStat tableStat = visitor.getTableStat("test.tb1");
        assertNotNull(tableStat);
        assertEquals(1, tableStat.getAlterCount());
        assertEquals(1, tableStat.getCreateIndexCount());
    }

}
