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

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLUnionQueryTableSource extends SQLTableSourceImpl {
    private SQLUnionQuery union;
    protected List<SQLName> columns = new ArrayList<SQLName>();

    public SQLUnionQueryTableSource() {
    }

    public SQLUnionQueryTableSource(String alias) {
        super(alias);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union, String alias) {
        super(alias);
        this.setUnion(union);
    }

    public SQLUnionQueryTableSource(SQLUnionQuery union) {
        this.setUnion(union);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (union != null) {
                union.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public void output(StringBuilder buf) {
        buf.append("(");
        this.union.output(buf);
        buf.append(")");
    }

    public SQLUnionQuery getUnion() {
        return union;
    }

    public void setUnion(SQLUnionQuery union) {
        if (union != null) {
            union.setParent(this);
        }
        this.union = union;
    }

    @Override
    public SQLUnionQueryTableSource clone() {
        SQLUnionQueryTableSource x = new SQLUnionQueryTableSource(this.union.clone(), alias);

        if (this.flashback != null) {
            x.setFlashback(this.flashback.clone());
        }

        if (this.hints != null) {
            for (SQLHint e : this.hints) {
                SQLHint e2 = e.clone();
                e2.setParent(x);
                x.getHints().add(e2);
            }
        }

        return x;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SQLUnionQueryTableSource that = (SQLUnionQueryTableSource) o;

        if (union != null ? !union.equals(that.union) : that.union != null) {
            return false;
        }
        return columns != null ? columns.equals(that.columns) : that.columns == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (union != null ? union.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        return result;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash, String columnName, int option) {
        if (union == null) {
            return null;
        }

        final SQLSelectQueryBlock firstQueryBlock = union.getFirstQueryBlock();
        if (firstQueryBlock != null) {
            final SQLSelectItem selectItem = firstQueryBlock.findSelectItem(columnNameHash);
            if (selectItem != null) {
                return this;
            }
        }
        return null;
    }

}
