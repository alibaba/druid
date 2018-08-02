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
package com.alibaba.druid.test;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import junit.framework.TestCase;

public class OdpsSelectTest6 extends TestCase {

    public void test_distribute_by() throws Exception {
        File file = new File("/Users/wenshao/Downloads/datasafe_base_dev.udf_test.txt");
        String sql = FileUtils.readFileToString(file, "UTF-8");
        
        
        SQLStatementParser parser = new OdpsStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);

        Assert.assertEquals(1, statementList.size());

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
//        Assert.assertEquals("SELECT *"
//                + "\nFROM t"
//                + "\nWHERE ds = '20160303'"
//                + "\n\tAND hour IN ('18')", SQLUtils.formatOdps(sql));
    }
    
}
