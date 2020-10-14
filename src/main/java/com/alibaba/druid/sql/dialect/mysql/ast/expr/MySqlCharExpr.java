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
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;

public class MySqlCharExpr extends SQLCharExpr implements MySqlExpr {
    private String charset;
    private String collate;

    private String type;

    public MySqlCharExpr(){

    }

    public MySqlCharExpr(String text){
        super(text);
    }

    public MySqlCharExpr(String text, String charset){
        super(text);
        this.charset = charset;
    }

    public MySqlCharExpr(String text, String charset, String collate){
        super(text);
        this.charset = charset;
        this.collate = collate;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void output(Appendable buf) {
        try {
            if (charset != null) {
                buf.append(charset);
                buf.append(' ');
            }
            if (super.text != null) {
                super.output(buf);
            }

            if (collate != null) {
                buf.append(" COLLATE ");
                buf.append(collate);
            }
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            visitor.visit(this);
            visitor.endVisit(this);
        }
    }

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        output(buf);
        return buf.toString();
    }


    public MySqlCharExpr clone() {
        MySqlCharExpr x = new MySqlCharExpr(text);
        x.collate = collate;
        x.charset = charset;
        x.type = type;

        return x;
    }
}
