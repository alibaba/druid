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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;

import java.util.List;

public abstract class SQLStatementImpl extends SQLObjectImpl implements SQLStatement {
    protected DbType               dbType;
    protected boolean              afterSemi;
    protected List<SQLCommentHint> headHints;

    public SQLStatementImpl(){

    }
    
    public SQLStatementImpl(DbType dbType){
        this.dbType = dbType;
    }
    
    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }


    public String toString(VisitorFeature... features) {
        return SQLUtils.toSQLString(this, dbType, null, features);
    }

    public String toLowerCaseString() {
        return SQLUtils.toSQLString(this, dbType, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
    }

    public String toUnformattedString() {
        return SQLUtils.toSQLString(this, dbType, new SQLUtils.FormatOption(true, false));
    }

    public String toParameterizedString() {
        return ParameterizedOutputVisitorUtils.parameterize(this, dbType);
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public List<SQLObject> getChildren() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public boolean isAfterSemi() {
        return afterSemi;
    }

    public void setAfterSemi(boolean afterSemi) {
        this.afterSemi = afterSemi;
    }

    public SQLStatement clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public List<SQLCommentHint> getHeadHintsDirect() {
        return headHints;
    }

    public void setHeadHints(List<SQLCommentHint> headHints) {
        this.headHints = headHints;
    }
}
