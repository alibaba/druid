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
package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObjectImpl;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatementImpl;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerExecStatement extends SQLServerStatementImpl implements SQLServerStatement {

    private SQLName       returnStatus;
    private SQLName       moduleName;
    private List<SQLServerParameter> parameters = new ArrayList<SQLServerParameter>();

    public SQLName getModuleName() {
        return moduleName;
    }

    public void setModuleName(SQLName moduleName) {
        this.moduleName = moduleName;
    }

    public List<SQLServerParameter> getParameters() {
        return parameters;
    }

    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.returnStatus);
            acceptChild(visitor, this.moduleName);
            acceptChild(visitor, this.parameters);
        }
        visitor.endVisit(this);
    }

    public SQLName getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(SQLName returnStatus) {
        this.returnStatus = returnStatus;
    }

    /**
     * 
     * @author zz [455910092@qq.com]
     */
    public static class SQLServerParameter extends SQLServerObjectImpl
    {
    	private SQLExpr expr;
    	private boolean type;//sql server 支持参数只有input 和 output 两种
		public SQLExpr getExpr() {
			return expr;
		}
		public void setExpr(SQLExpr expr) {
			this.expr = expr;
		}
		public boolean getType() {
			return type;
		}
		public void setType(boolean type) {
			this.type = type;
		}
		@Override
		public void accept0(SQLServerASTVisitor visitor) {
			if (visitor.visit(this)) {
	            acceptChild(visitor, expr);
	        }
	        visitor.endVisit(this);
			
		}
    }
}
