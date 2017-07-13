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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 23/05/2017.
 */
public class SQLCreateFunctionStatement extends SQLStatementImpl {
    private SQLName definer;

    private boolean            create     = true;
    private boolean            orReplace;
    private SQLName            name;
    private SQLStatement block;
    private List<SQLParameter> parameters = new ArrayList<SQLParameter>();

    // for oracle
    private String             javaCallSpec;

    private SQLName            authid;

    SQLDataType                returnDataType;

    // for mysql

    private String             comment;

    private boolean            deterministic  = false;

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, definer);
            acceptChild(visitor, name);
            acceptChild(visitor, parameters);
            acceptChild(visitor, block);
        }
        visitor.endVisit(this);
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SQLParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLStatement getBlock() {
        return block;
    }

    public void setBlock(SQLStatement block) {
        if (block != null) {
            block.setParent(this);
        }
        this.block = block;
    }

    public SQLName getAuthid() {
        return authid;
    }

    public void setAuthid(SQLName authid) {
        if (authid != null) {
            authid.setParent(this);
        }
        this.authid = authid;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public SQLName getDefiner() {
        return definer;
    }

    public void setDefiner(SQLName definer) {
        this.definer = definer;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public String getJavaCallSpec() {
        return javaCallSpec;
    }

    public void setJavaCallSpec(String javaCallSpec) {
        this.javaCallSpec = javaCallSpec;
    }

    public SQLDataType getReturnDataType() {
        return returnDataType;
    }

    public void setReturnDataType(SQLDataType returnDataType) {
        if (returnDataType != null) {
            returnDataType.setParent(this);
        }
        this.returnDataType = returnDataType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }
}
