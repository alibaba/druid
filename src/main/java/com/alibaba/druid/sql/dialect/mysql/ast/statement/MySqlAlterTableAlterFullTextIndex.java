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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.dialect.mysql.ast.AnalyzerIndexType;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlAlterTableAlterFullTextIndex extends MySqlObjectImpl implements SQLAlterTableItem {

    private SQLName indexName;

    private AnalyzerIndexType analyzerType;
    private SQLName           analyzerName;


    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexName);
            acceptChild(visitor, analyzerName);
        }
        visitor.endVisit(this);
    }

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    public SQLName getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(SQLName analyzerName) {
        this.analyzerName = analyzerName;
    }

    public AnalyzerIndexType getAnalyzerType() {
        return analyzerType;
    }

    public void setAnalyzerType(AnalyzerIndexType analyzerType) {
        this.analyzerType = analyzerType;
    }
}
