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
package com.alibaba.druid.sql.visitor;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ExportParameterVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGExportParameterVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.MSSQLServerExportParameterVisitor;
import com.alibaba.druid.util.JdbcUtils;

public final class ExportParameterVisitorUtils {
    
    //private for util class not need new instance
    private ExportParameterVisitorUtils() {
        super();
    }

    public static ExportParameterVisitor createExportParameterVisitor(final  Appendable out ,final String dbType) {
        
        if (JdbcUtils.isMysqlDbType(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }

        if (JdbcUtils.isOracleDbType(dbType)) {
            return new OracleExportParameterVisitor(out);
        }

        if (JdbcUtils.isPgsqlDbType(dbType)) {
            return new PGExportParameterVisitor(out);
        }

        if (JdbcUtils.isSqlserverDbType(dbType)) {
            return new MSSQLServerExportParameterVisitor(out);
        }

        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2ExportParameterVisitor(out);
        }
        
        if (JdbcUtils.MARIADB.equals(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }
        
        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlExportParameterVisitor(out);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGExportParameterVisitor(out);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            return new MSSQLServerExportParameterVisitor(out);
        }
       return new ExportParameterizedOutputVisitor(out);
    }

    

    public static boolean exportParamterAndAccept(final List<Object> parameters, List<SQLExpr> list) {
        for (int i = 0, size = list.size(); i < size; ++i) {
            SQLExpr param = list.get(i);

            SQLExpr result = exportParameter(parameters, param);
            if (result != param) {
                list.set(i, result);
            }
        }

        return false;
    }

    public static SQLExpr exportParameter(final List<Object> parameters, final SQLExpr param) {
        Object value = null;
        boolean replace = false;

        if (param instanceof SQLCharExpr) {
            value = ((SQLCharExpr) param).getText();
            replace = true;
        } else if (param instanceof SQLBooleanExpr) {
            value = ((SQLBooleanExpr) param).getBooleanValue();
            replace = true;
        } else if (param instanceof SQLNumericLiteralExpr) {
            value = ((SQLNumericLiteralExpr) param).getNumber();
            replace = true;
        } else if (param instanceof SQLHexExpr) {
            value = ((SQLHexExpr) param).toBytes();
            replace = true;
        } else if (param instanceof SQLTimestampExpr || param instanceof SQLDateExpr) {
            value = ((SQLTimestampExpr) param).getValue();
            replace = true;
        } else if (param instanceof SQLListExpr) {
            SQLListExpr list = ((SQLListExpr) param);

            List<Object> listValues = new ArrayList<Object>();
            for (int i = 0; i < list.getItems().size(); i++) {
                SQLExpr listItem = list.getItems().get(i);

                if (listItem instanceof SQLCharExpr) {
                    Object listValue = ((SQLCharExpr) listItem).getText();
                    listValues.add(listValue);
                } else if (listItem instanceof SQLBooleanExpr) {
                    Object listValue = ((SQLBooleanExpr) listItem).getBooleanValue();
                    listValues.add(listValue);
                } else if (listItem instanceof SQLNumericLiteralExpr) {
                    Object listValue = ((SQLNumericLiteralExpr) listItem).getNumber();
                    listValues.add(listValue);
                } else if (param instanceof SQLHexExpr) {
                    Object listValue = ((SQLHexExpr) listItem).toBytes();
                    listValues.add(listValue);
                }
            }

            if (listValues.size() == list.getItems().size()) {
                value = listValues;
                replace = true;
            }
        }

        if (replace) {
            SQLObject parent = param.getParent();
            if (parent != null) {
                List<SQLObject> mergedList = null;
                if (parent instanceof SQLBinaryOpExpr) {
                    mergedList = ((SQLBinaryOpExpr) parent).getMergedList();
                }
                if (mergedList != null) {
                    List<Object> mergedListParams = new ArrayList<Object>(mergedList.size() + 1);
                    for (int i = 0; i < mergedList.size(); ++i) {
                        SQLObject item = mergedList.get(i);
                        if (item instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr binaryOpItem = (SQLBinaryOpExpr) item;
                            exportParameter(mergedListParams, binaryOpItem.getRight());
                        }
                    }
                    if (mergedListParams.size() > 0) {
                        mergedListParams.add(0, value);
                        value = mergedListParams;
                    }
                }
            }

            parameters.add(value);

            return new SQLVariantRefExpr("?");
        }

        return param;
    }

    public static void exportParameter(final List<Object> parameters, SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLLiteralExpr
                && x.getRight() instanceof SQLLiteralExpr
                && x.getOperator().isRelational()) {
            return;
        }

        {
            SQLExpr leftResult = ExportParameterVisitorUtils.exportParameter(parameters, x.getLeft());
            if (leftResult != x.getLeft()) {
                x.setLeft(leftResult);
            }
        }

        {
            SQLExpr rightResult = exportParameter(parameters, x.getRight());
            if (rightResult != x.getRight()) {
                x.setRight(rightResult);
            }
        }
    }

    public static void exportParameter(final List<Object> parameters, SQLBetweenExpr x) {
        {
            SQLExpr result = exportParameter(parameters, x.getBeginExpr());
            if (result != x.getBeginExpr()) {
                x.setBeginExpr(result);
            }
        }

        {
            SQLExpr result = exportParameter(parameters, x.getEndExpr());
            if (result != x.getBeginExpr()) {
                x.setEndExpr(result);
            }
        }

    }
}
