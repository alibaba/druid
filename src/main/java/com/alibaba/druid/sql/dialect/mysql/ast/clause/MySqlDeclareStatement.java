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
package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * @author zz [455910092@qq.com]
 */
public class MySqlDeclareStatement extends MySqlStatementImpl {

    private List<SQLDeclareItem> varList = new ArrayList<SQLDeclareItem>();

    public List<SQLDeclareItem> getVarList() {
        return varList;
    }

    public void addVar(SQLDeclareItem expr) {
        varList.add(expr);
    }

    public void setVarList(List<SQLDeclareItem> varList) {
        this.varList = varList;
    }

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, varList);
        }
        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return varList;
    }
}
