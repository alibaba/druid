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
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlCreateTable_showColumns_test_2 extends MysqlTest {

    public void test_0() throws Exception {
        SchemaRepository repository = new SchemaRepository(JdbcConstants.MYSQL);

        String sql = "create table yushitai_test.card_record ( id bigint auto_increment) auto_increment=256 "
                + "; alter table yushitai_test.card_record add column customization_id bigint unsigned NOT NULL COMMENT 'TEST' ;"
                + "; alter table yushitai_test.card_record add column customization_id bigint unsigned NOT NULL COMMENT 'TEST' ;";
        repository.console(sql);

        repository.setDefaultSchema("yushitai_test");
        SchemaObject table = repository.findTable("card_record");
        assertEquals("CREATE TABLE card_record (\n" +
                "\tid bigint AUTO_INCREMENT,\n" +
                "\tcustomization_id bigint UNSIGNED NOT NULL COMMENT 'TEST'\n" +
                ") AUTO_INCREMENT = 256", table.getStatement().toString());
    }
}
