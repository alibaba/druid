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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.sql.Types;
import java.util.List;

public class SQLCharacterDataType extends SQLDataTypeImpl {

    private String             charSetName;
    private String             collate;

    private String             charType;
    private boolean            hasBinary;

    public List<SQLCommentHint> hints;

    public final static String CHAR_TYPE_BYTE = "BYTE";
    public final static String CHAR_TYPE_CHAR = "CHAR";

    public SQLCharacterDataType(String name){
        super(name);
    }

    public SQLCharacterDataType(String name, int precision){
        super(name, precision);
    }

    public String getCharSetName() {
        return charSetName;
    }

    public void setCharSetName(String charSetName) {
        this.charSetName = charSetName;
    }
    
    public boolean isHasBinary() {
        return hasBinary;
    }

    public void setHasBinary(boolean hasBinary) {
        this.hasBinary = hasBinary;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public String getCharType() {
        return charType;
    }

    public void setCharType(String charType) {
        this.charType = charType;
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public int getLength() {
        if (this.arguments.size() == 1) {
            SQLExpr arg = this.arguments.get(0);
            if (arg instanceof SQLIntegerExpr) {
                return ((SQLIntegerExpr) arg).getNumber().intValue();
            }
        }

        return -1;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < arguments.size(); i++) {
                SQLExpr arg = arguments.get(i);
                if (arg != null) {
                    arg.accept(visitor);
                }
            }
        }

        visitor.endVisit(this);
    }


    public SQLCharacterDataType clone() {
        SQLCharacterDataType x = new SQLCharacterDataType(getName());

        super.cloneTo(x);

        x.charSetName = charSetName;
        x.collate = collate;
        x.charType = charType;
        x.hasBinary = hasBinary;

        return x;
    }

    @Override
    public String toString() {
        return SQLUtils.toSQLString(this);
    }

    public int jdbcType() {
        long nameNash = nameHashCode64();

        if (nameNash == FnvHash.Constants.NCHAR) {
            return Types.NCHAR;
        }

        if (nameNash == FnvHash.Constants.CHAR || nameNash == FnvHash.Constants.JSON) {
            return Types.CHAR;
        }

        if (nameNash == FnvHash.Constants.VARCHAR
                || nameNash == FnvHash.Constants.VARCHAR2
                || nameNash == FnvHash.Constants.STRING) {
            return Types.VARCHAR;
        }

        if (nameNash == FnvHash.Constants.NVARCHAR || nameNash == FnvHash.Constants.NVARCHAR2) {
            return Types.NVARCHAR;
        }

        return Types.OTHER;
    }
}
