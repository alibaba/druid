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
package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBlockStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatementImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

/**
 * 
 * @Description: MySql create procedure statement
 * @author zz email:455910092@qq.com
 * @version V1.0
 */
public class MySqlCreateProcedureStatement extends MySqlStatementImpl {
	private boolean               orReplace;
    private SQLName               name;
    private MySqlBlockStatement  block;
    private List<MySqlParameter> parameters = new ArrayList<MySqlParameter>();

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, parameters);
            acceptChild(visitor, block);
        }
        visitor.endVisit(this);
    }

    public List<MySqlParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<MySqlParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public MySqlBlockStatement getBlock() {
        return block;
    }

    public void setBlock(MySqlBlockStatement block) {
        this.block = block;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

}
