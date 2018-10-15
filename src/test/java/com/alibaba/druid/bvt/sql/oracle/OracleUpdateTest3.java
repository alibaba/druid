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
package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import org.junit.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleUpdateTest3 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "update sys.col_usage$ " //
                     + "set equality_preds = equality_preds + decode(bitand(:flag,1),0,0,1)" //
                     + "    , equijoin_preds = equijoin_preds + decode(bitand(:flag,2),0,0,1)" //
                     + "    , nonequijoin_preds = nonequijoin_preds + decode(bitand(:flag,4),0,0,1)" //
                     + "    , range_preds = range_preds + decode(bitand(:flag,8),0,0,1)" //
                     + "    , like_preds = like_preds + decode(bitand(:flag,16),0,0,1)" //
                     + "    , null_preds = null_preds + decode(bitand(:flag,32),0,0,1), timestamp = :time " + //
                     "where obj# = :objn and intcol# = :coln"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sys.col_usage$")));

        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(9, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sys.col_usage$", "obj#")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("sys.col_usage$", "intcol#")));
    }

}
