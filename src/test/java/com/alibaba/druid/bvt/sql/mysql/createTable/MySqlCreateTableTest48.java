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
import com.alibaba.druid.stat.TableStat;
import org.junit.Assert;

public class MySqlCreateTableTest48 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "CREATE TABLE AO_E8B6CC_REPO_TO_CHANGESET (" //
                     + "    CHANGESET_ID INTEGER,"//
                     + "    ID INTEGER AUTO_INCREMENT NOT NULL,"//
                     + "    REPOSITORY_ID INTEGER,"//
                     + "    CONSTRAINT fk_ao_e8b6cc_repo_to_changeset_repository_id FOREIGN KEY (REPOSITORY_ID) REFERENCES AO_E8B6CC_REPOSITORY_MAPPING(ID),"//
                     + "    CONSTRAINT fk_ao_e8b6cc_repo_to_changeset_changeset_id FOREIGN KEY (CHANGESET_ID) REFERENCES AO_E8B6CC_CHANGESET_MAPPING(ID),"//
                     + "    PRIMARY KEY(ID)"//
                     + ") ENGINE=InnoDB"; //

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseCreateTable();

        System.out.println(stmt);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(3, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("AO_E8B6CC_REPO_TO_CHANGESET")));

        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals("CREATE TABLE AO_E8B6CC_REPO_TO_CHANGESET ("//
                                    + "\n\tCHANGESET_ID INTEGER,"//
                                    + "\n\tID INTEGER NOT NULL AUTO_INCREMENT,"//
                                    + "\n\tREPOSITORY_ID INTEGER,"//
                                    + "\n\tCONSTRAINT fk_ao_e8b6cc_repo_to_changeset_repository_id FOREIGN KEY (REPOSITORY_ID) REFERENCES AO_E8B6CC_REPOSITORY_MAPPING (ID),"//
                                    + "\n\tCONSTRAINT fk_ao_e8b6cc_repo_to_changeset_changeset_id FOREIGN KEY (CHANGESET_ID) REFERENCES AO_E8B6CC_CHANGESET_MAPPING (ID),"//
                                    + "\n\tPRIMARY KEY (ID)"//
                                    + "\n) ENGINE = InnoDB",//
                            output);

    }
}
