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
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SQLIntervalExpr extends SQLExprImpl implements SQLReplaceable {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl("interval");

    private SQLExpr           value;
    private SQLIntervalUnit   unit;

    public SQLIntervalExpr(){
    }

    public SQLIntervalExpr(SQLExpr value, SQLIntervalUnit unit){
        setValue(value);
        this.unit = unit;
    }

    public SQLIntervalExpr clone() {
        SQLIntervalExpr x = new SQLIntervalExpr();
        if (value != null) {
            x.setValue(value.clone());
        }
        x.unit = unit;
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.value == expr) {
            setValue(target);
            return true;
        }
        return false;
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.value = x;
    }

    public SQLIntervalUnit getUnit() {
        return unit;
    }

    public void setUnit(SQLIntervalUnit unit) {
        this.unit = unit;
    }

    @Override
    public void output(Appendable buf) {
        try {
            buf.append("INTERVAL ");
            value.output(buf);
            if (unit != null) {
                buf.append(' ');
                buf.append(unit.name());
            }
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.value != null) {
                this.value.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
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
        SQLIntervalExpr other = (SQLIntervalExpr) obj;
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


    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

}
