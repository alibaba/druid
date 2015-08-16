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
package com.alibaba.druid.sql.dialect.mysql.visitor;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;

import java.security.AccessControlException;
import java.util.List;
import java.util.Properties;

public class MySqlParameterizedOutputVisitor extends MySqlOutputVisitor implements ParameterizedVisitor {

    private boolean shardingSupport = true;

    private int     replaceCount;

    public MySqlParameterizedOutputVisitor(){
        this(new StringBuilder());
    }

    public MySqlParameterizedOutputVisitor(Appendable appender){
        super(appender);

        try {
            configFromProperty(System.getProperties());
        } catch (AccessControlException e) {
        }
    }

    public void configFromProperty(Properties properties) {
        {
            String property = properties.getProperty("druid.parameterized.shardingSupport");
            if ("true".equals(property)) {
                this.setShardingSupport(true);
            } else if ("false".equals(property)) {
                this.setShardingSupport(false);
            }
        }
    }

    public boolean isShardingSupport() {
        return shardingSupport;
    }

    public void setShardingSupport(boolean shardingSupport) {
        this.shardingSupport = shardingSupport;
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
        boolean computeSharding = isShardingSupport();
        if (computeSharding) {
            SQLObject parent = x.getParent();
            computeSharding = parent instanceof SQLExprTableSource || parent instanceof SQLPropertyExpr;
        }

        if (computeSharding) {
            int pos = name.lastIndexOf('_');
            if (pos != -1 && pos != name.length() - 1) {
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
        x = ParameterizedOutputVisitorUtils.merge(this, x);

        return super.visit(x);
    }

    public boolean visit(SQLIntegerExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNumberExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLCharExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNCharExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    public boolean visit(SQLNullExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }
    
    public boolean visit(SQLHexExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }
    
    public boolean visit(MySqlCharExpr x) {
        if (!ParameterizedOutputVisitorUtils.checkParameterize(x)) {
            return super.visit(x);
        }

        return ParameterizedOutputVisitorUtils.visit(this, x);
    }

    protected void printValuesList(MySqlInsertStatement x) {
        List<ValuesClause> valuesList = x.getValuesList();
        print("VALUES ");
        incrementIndent();
        valuesList.get(0).accept(this);
        decrementIndent();
        if (valuesList.size() > 1) {
            this.incrementReplaceCunt();
        }
    }
}
