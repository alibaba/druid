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
package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataIntervalExpr extends SQLExprImpl implements TeradataExpr {
	
	private SQLExpr              value;
	private TeradataIntervalUnit unit;

	public TeradataIntervalExpr() {
		
	}
	
	public SQLExpr getValue() {
		return this.value;
	}
	
	public void setValue(SQLExpr value) {
		this.value = value;
	}
	
	public TeradataIntervalUnit getUnit() {
		return unit;
	}
	
	public void setUnit(TeradataIntervalUnit unit) {
		this.unit = unit;
	}
	
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TeradataIntervalExpr other = (TeradataIntervalExpr) obj;
        if (unit != other.unit) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null)? 0 : unit.hashCode());
		result = prime * result + ((value == null)? 0 : value.hashCode());
		return result;
	}

	@Override
	protected void accept0(SQLASTVisitor visitor) {
		TeradataASTVisitor tdVisitor = (TeradataASTVisitor) visitor;
		if (tdVisitor.visit(this)) {
			acceptChild(visitor, this.value);
		}
		tdVisitor.endVisit(this);
	}


}
