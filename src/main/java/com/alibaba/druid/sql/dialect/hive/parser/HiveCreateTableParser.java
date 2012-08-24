/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveCreateTableStatement.PartitionedBy;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveCreateTableParser extends SQLCreateTableParser {

    public HiveCreateTableParser(String sql){
        super(new SQLExprParser(sql));
    }

    public HiveCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    protected HiveCreateTableStatement newCreateStatement() {
        return new HiveCreateTableStatement();
    }

    public SQLCreateTableStatement parseCrateTable(boolean acceptCreate) {
        HiveCreateTableStatement stmt = (HiveCreateTableStatement) super.parseCrateTable(acceptCreate);

        if (identifierEquals("PARTITIONED")) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);

            PartitionedBy partitionedBy = new PartitionedBy();
            partitionedBy.setName(exprParser.name().toString());
            partitionedBy.setType(exprParser.parseDataType());

            accept(Token.RPAREN);
            
            stmt.setPartitionedBy(partitionedBy);
        }

        return stmt;
    }
}
