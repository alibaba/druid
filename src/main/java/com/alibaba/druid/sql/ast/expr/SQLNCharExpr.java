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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;

public class SQLNCharExpr extends SQLTextLiteralExpr {
    private static SQLDataType defaultDataType = new SQLCharacterDataType("nvarchar");

    public SQLNCharExpr(){

    }

    public SQLNCharExpr(String text){
        this.text = text;
    }
    public SQLNCharExpr(String text, SQLObject parent){
        this.text = text;
        this.parent = parent;
    }

    public void output(Appendable buf) {
        try {
            if ((this.text == null) || (this.text.length() == 0)) {
                buf.append("NULL");
                return;
            }

            buf.append("N'");
            buf.append(this.text.replaceAll("'", "''"));
            buf.append("'");
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public SQLNCharExpr clone() {
        return new SQLNCharExpr(text);
    }

    public SQLDataType computeDataType() {
        return defaultDataType;
    }
}
