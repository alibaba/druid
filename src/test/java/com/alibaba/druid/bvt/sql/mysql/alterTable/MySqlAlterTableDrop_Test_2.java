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
package com.alibaba.druid.bvt.sql.mysql.alterTable;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.Token;
import junit.framework.TestCase;
import org.junit.Assert;

public class MySqlAlterTableDrop_Test_2 extends TestCase {

    public void test_alter_first() throws Exception {
        String sql = "ALTER TABLE test_ddl \n" +
                "DROP COLUMN display_name, \n" +
                "MODIFY COLUMN id bigint(20) UNSIGNED NOT NULL ,\n" +
                "CHANGE COLUMN content content VARCHAR(3000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL DEFAULT '未填写' COMMENT '默认' \n";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(Token.EOF);
        
        Assert.assertEquals("ALTER TABLE test_ddl\n" +
                "\tDROP COLUMN display_name,\n" +
                "\tMODIFY COLUMN id bigint(20) UNSIGNED NOT NULL,\n" +
                "\tCHANGE COLUMN content content VARCHAR(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未填写' COMMENT '默认'", SQLUtils.toMySqlString(stmt));
        
        Assert.assertEquals("alter table test_ddl\n" +
                "\tdrop column display_name,\n" +
                "\tmodify column id bigint(20) unsigned not null,\n" +
                "\tchange column content content VARCHAR(3000) character set utf8mb4 collate utf8mb4_unicode_ci not null default '未填写' comment '默认'", SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION));
    }

}
