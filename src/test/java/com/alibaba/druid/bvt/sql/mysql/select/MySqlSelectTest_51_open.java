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
package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class MySqlSelectTest_51_open extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "select id as id,    gmt_create as gmtCreate,    gmt_modified as gmtModified,    name as name,    owner as owner,    type as type,    statement as statement,    datasource as datasource,    meta as meta,    param_file as paramFile,    sharable as sharable,    data_type as dataType,    status as status,    config as config,    project_id as projectId,    plugins as plugins,    field_compare as fieldCompare,    field_ext as fieldExt,    open as open   from tb_001     where id = 12569434";


        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL, true);
        SQLStatement stmt = statementList.get(0);

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        stmt.accept(visitor);

//        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
//        System.out.println("coditions : " + visitor.getConditions());
//        System.out.println("orderBy : " + visitor.getOrderByColumns());
        
//        assertEquals(1, visitor.getTables().size());
//        assertEquals(1, visitor.getColumns().size());
//        assertEquals(0, visitor.getConditions().size());
//        assertEquals(0, visitor.getOrderByColumns().size());
        
        {
            String output = SQLUtils.toMySqlString(stmt);
            assertEquals("SELECT id AS id, gmt_create AS gmtCreate, gmt_modified AS gmtModified, name AS name, owner AS owner\n" +
                            "\t, type AS type, statement AS statement, datasource AS datasource, meta AS meta, param_file AS paramFile\n" +
                            "\t, sharable AS sharable, data_type AS dataType, status AS status, config AS config, project_id AS projectId\n" +
                            "\t, plugins AS plugins, field_compare AS fieldCompare, field_ext AS fieldExt, open AS open\n" +
                            "FROM tb_001\n" +
                            "WHERE id = 12569434", //
                                output);
        }
        {
            String output = SQLUtils.toMySqlString(stmt, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
            assertEquals("select id as id, gmt_create as gmtCreate, gmt_modified as gmtModified, name as name, owner as owner\n" +
                            "\t, type as type, statement as statement, datasource as datasource, meta as meta, param_file as paramFile\n" +
                            "\t, sharable as sharable, data_type as dataType, status as status, config as config, project_id as projectId\n" +
                            "\t, plugins as plugins, field_compare as fieldCompare, field_ext as fieldExt, open as open\n" +
                            "from tb_001\n" +
                            "where id = 12569434", //
                                output);
        }

        {
            String output = SQLUtils.toMySqlString(stmt, new SQLUtils.FormatOption(true, true, true));
            assertEquals("SELECT id AS id, gmt_create AS gmtCreate, gmt_modified AS gmtModified, name AS name, owner AS owner\n" +
                            "\t, type AS type, statement AS statement, datasource AS datasource, meta AS meta, param_file AS paramFile\n" +
                            "\t, sharable AS sharable, data_type AS dataType, status AS status, config AS config, project_id AS projectId\n" +
                            "\t, plugins AS plugins, field_compare AS fieldCompare, field_ext AS fieldExt, open AS open\n" +
                            "FROM tb\n" +
                            "WHERE id = ?", //
                    output);
        }
    }
}
