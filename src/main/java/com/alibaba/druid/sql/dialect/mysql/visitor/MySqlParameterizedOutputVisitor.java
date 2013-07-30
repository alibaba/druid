/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;

public class MySqlParameterizedOutputVisitor extends MySqlOutputVisitor implements ParameterizedVisitor {

    private int replaceCount;

    public MySqlParameterizedOutputVisitor(){
        this(new StringBuilder());
    }

    public MySqlParameterizedOutputVisitor(Appendable appender){
        super(appender);
    }

    public int getReplaceCount() {
        return this.replaceCount;
    }

    public void incrementReplaceCunt() {
        replaceCount++;
    }

    public boolean visit(SQLInListExpr x) {
        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLIdentifierExpr x) {
        final String name = x.getName();
        if (x.getParent() instanceof SQLExprTableSource || x.getParent() instanceof SQLPropertyExpr) {
            int pos = name.lastIndexOf('_');
            if (pos != -1 && pos != name.length()) {
                boolean isNumber = true;
                for (int i = pos + 1; i < name.length(); ++i) {
                    char ch = name.charAt(i);
                    if (ch < '0' || ch > '9') {
                        isNumber = false;
                        break;
                    }
                }
                if (isNumber) {
                    String realName = name.substring(0, pos);
                    print(realName);
                    incrementReplaceCunt();
                    return false;
                }
            }

            int numberCount = 0;
            for (int i = name.length() - 1; i >= 0; --i) {
                char ch = name.charAt(i);
                if (ch < '0' || ch > '9') {
                    break;
                } else {
                    numberCount++;
                }
            }

            if (numberCount > 1) {
                int numPos = name.length() - numberCount;
                String realName = name.substring(0, numPos);
                print(realName);
                incrementReplaceCunt();
                return false;
            }
        }
        print(name);
        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        x = ParameterizedOutputVisitorUtils.merge(x);

        return super.visit(x);
    }

    public boolean visit(SQLNullExpr x) {
        print('?');
        incrementReplaceCunt();
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        incrementReplaceCunt();
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        incrementReplaceCunt();
        return false;
    }

    public boolean visit(SQLCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        incrementReplaceCunt();
        return false;
    }

    public boolean visit(SQLNCharExpr x) {
        if (Boolean.TRUE.equals(x.getAttribute(ParameterizedOutputVisitorUtils.ATTR_PARAMS_SKIP))) {
            return super.visit(x);
        }

        print('?');
        incrementReplaceCunt();
        return false;
    }

    protected void printValuesList(MySqlInsertStatement x) {
        print("VALUES ");
        incrementIndent();
        x.getValuesList().get(0).accept(this);
        decrementIndent();
    }
}
