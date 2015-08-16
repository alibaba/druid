/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class DB2ExprParser extends SQLExprParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER", "ROWNUMBER" };

    public DB2ExprParser(String sql){
        this(new DB2Lexer(sql));
        this.lexer.nextToken();
    }

    public DB2ExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
    }

}
