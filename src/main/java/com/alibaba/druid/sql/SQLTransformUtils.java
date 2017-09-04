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
package com.alibaba.druid.sql;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;

import java.util.List;

public class SQLTransformUtils {
    public static SQLExpr transformDecode(SQLMethodInvokeExpr x) {
        if (x == null) {
            return null;
        }

        if (!"decode".equalsIgnoreCase(x.getMethodName())) {
            throw new IllegalArgumentException(x.getMethodName());
        }

        List<SQLExpr> parameters = x.getParameters();
        SQLCaseExpr caseExpr = new SQLCaseExpr();
        caseExpr.setParent(x.getParent());
        caseExpr.setValueExpr(parameters.get(0));

        if (parameters.size() == 4) {
            SQLExpr param1 = parameters.get(1);

            x.setMethodName("if");

            SQLBinaryOpExpr condition;
            if (param1 instanceof SQLNullExpr) {
                condition = new SQLBinaryOpExpr(parameters.get(0), SQLBinaryOperator.Is, param1);
            } else {
                condition = new SQLBinaryOpExpr(parameters.get(0), SQLBinaryOperator.Equality, param1);
            }
            condition.setParent(x);
            parameters.set(0, condition);
            parameters.set(1, parameters.get(2));
            parameters.set(2, parameters.get(3));
            parameters.remove(3);
            return x;
        }

        for (int i = 1; i + 1 < parameters.size(); i += 2) {
            SQLCaseExpr.Item item = new SQLCaseExpr.Item();
            SQLExpr conditionExpr = parameters.get(i);

            item.setConditionExpr(conditionExpr);

            SQLExpr valueExpr = parameters.get(i + 1);

            if (valueExpr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) valueExpr;
                if ("decode".equalsIgnoreCase(methodInvokeExpr.getMethodName())) {
                    valueExpr = transformDecode(methodInvokeExpr);
                }
            }

            item.setValueExpr(valueExpr);
            caseExpr.addItem(item);
        }

        if (parameters.size() % 2 == 0) {
            SQLExpr defaultExpr = parameters.get(parameters.size() - 1);

            if (defaultExpr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) defaultExpr;
                if ("decode".equalsIgnoreCase(methodInvokeExpr.getMethodName())) {
                    defaultExpr = transformDecode(methodInvokeExpr);
                }
            }

            caseExpr.setElseExpr(defaultExpr);
        }

        caseExpr.setParent(x.getParent());

        return caseExpr;
    }

    public static SQLDataType transformOracleToMySql(SQLDataType x) {
        final String name = x.getName();

        if (name == null) {
            return x;
        }
        List<SQLExpr> argumentns = x.getArguments();

        String name_lower = name.toLowerCase();

        SQLDataType dataType;

        if ("urowid".equalsIgnoreCase(name_lower)) {
            int len = 4000;
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLIntegerExpr) {
                    len = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }
            }
            dataType = new SQLDataTypeImpl("varchar", len);
        } else if ("rowid".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("char", 10);

        } else if ("boolean".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("tinyint");

        } else if ("integer".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("int");

        } else if ("float".equals(name_lower) || "binary_float".equals(name_lower)) {
                dataType = new SQLDataTypeImpl("float");

        } else if ("real".equals(name_lower)
                || "double precision".equals(name_lower)
                || "binary_double".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("double");

        } else if ("number".equals(name_lower)) {
            if (argumentns.size() == 0) {
                dataType = new SQLDataTypeImpl("decimal", 38);
            } else {
                SQLExpr arg0 = argumentns.get(0);

                int precision, scale = 0;
                if (arg0 instanceof SQLAllColumnExpr) {
                    precision = 9;
                    scale = 1;
                } else {
                    precision = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }

                if (argumentns.size() > 1) {
                    scale = ((SQLIntegerExpr) argumentns.get(1)).getNumber().intValue();
                }

                if (scale > precision) {
                    if (arg0 instanceof SQLAllColumnExpr) {
                        precision = 19;
                        if (scale > precision) {
                            precision = scale;
                        }
                    } else {
                        precision = scale;
                    }
                }

                if (scale == 0) {
                    if (precision < 3) {
                        dataType = new SQLDataTypeImpl("tinyint");
                    } else if (precision < 5) {
                        dataType = new SQLDataTypeImpl("smallint");
                    } else if (precision < 9) {
                        dataType = new SQLDataTypeImpl("int");
                    } else if (precision < 19) {
                        dataType = new SQLDataTypeImpl("bigint");
                    } else {
                        dataType = new SQLDataTypeImpl("decimal", precision);
                    }
                } else {
                    dataType = new SQLDataTypeImpl("decimal", precision, scale);
                }
            }

        } else if ("dec".equals(name_lower) || "decimal".equals(name_lower)) {
            dataType = x.clone();
            dataType.setName("decimal");

            int precision = 0;
            if (argumentns.size() > 0) {
                precision = ((SQLIntegerExpr) argumentns.get(0)).getNumber().intValue();
            }

            int scale = 0;
            if (argumentns.size() > 1) {
                scale = ((SQLIntegerExpr) argumentns.get(1)).getNumber().intValue();
                if (precision < scale) {
                    ((SQLIntegerExpr) dataType.getArguments().get(1)).setNumber(precision);
                }
            }

//            if (precision == 38 && scale == 0 && x.getParent() instanceof SQLCastExpr) {
//                dataType.getArguments().clear();
//                dataType.setName("int");
//            }
            /////////////////////////////////

        } else if ("raw".equals(name_lower)) {
            int len;

            if (argumentns.size() == 0) {
                len = -1;
            } else if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }

            if (len == -1) {
                dataType = new SQLDataTypeImpl("binary");
            } else if (len <= 255) {
                dataType = new SQLDataTypeImpl("binary", len);
            } else {
                dataType = new SQLDataTypeImpl("varbinary", len);
            }
        } else if ("char".equals(name_lower)
                || "character".equals(name_lower)) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 255) {
                    dataType = new SQLCharacterDataType("char", len);
                } else {
                    dataType = new SQLCharacterDataType("varchar", len);
                }
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("char");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }

        } else if ("nchar".equals(name_lower)) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 255) {
                    dataType = new SQLCharacterDataType("nchar", len);
                } else {
                    dataType = new SQLCharacterDataType("nvarchar", len);
                }
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("nchar");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }

        } else if ("varchar2".equals(name_lower)) {
            if (argumentns.size() > 0) {
                int len;
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
                dataType = new SQLCharacterDataType("varchar", len);
            } else {
                dataType = new SQLCharacterDataType("varchar");
            }

        } else if ("nvarchar2".equals(name_lower)) {
            if (argumentns.size() > 0) {
                int len;
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
                dataType = new SQLCharacterDataType("nvarchar", len);
            } else {
                dataType = new SQLCharacterDataType("nvarchar");
            }

        } else if ("bfile".equals(name_lower)) {
            dataType = new SQLCharacterDataType("varchar", 255);

        } else if ("date".equals(name_lower)
                || "timestamp".equals(name_lower)) {
            int len = -1;
            if (argumentns.size() > 0) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
            }

            if (len >= 0) {
                if (len > 6) {
                    len = 6;
                }
                dataType = new SQLDataTypeImpl("datetime", len);
            } else {
                dataType = new SQLDataTypeImpl("datetime");
            }
        } else if ("blob".equals(name_lower) || "long raw".equals(name_lower)) {
            argumentns.clear();
            dataType = new SQLDataTypeImpl("LONGBLOB");

        } else if ("clob".equals(name_lower)
                || "nclob".equals(name_lower)
                || "long".equals(name_lower)
                || "xmltype".equals(name_lower)) {
            argumentns.clear();
            dataType = new SQLCharacterDataType("LONGTEXT");

        } else {
            dataType = x;
        }

        if (dataType != x) {
            dataType.setParent(x.getParent());
        }

        return dataType;
    }

    public static SQLDataType transformOracleToAliyunAds(SQLDataType x) {
        final String dataTypeName = x.getName().toLowerCase();
        SQLDataType dataType;

        if (dataTypeName.equals("varchar2")
                || dataTypeName.equals("varchar")
                || dataTypeName.equals("char")
                || dataTypeName.equals("nchar")
                || dataTypeName.equals("nvarchar")
                || dataTypeName.equals("nvarchar2")
                || dataTypeName.equals("clob")
                || dataTypeName.equals("nclob")
                || dataTypeName.equals("blob")
                || dataTypeName.equals("long")
                || dataTypeName.equals("long raw")
                || dataTypeName.equals("raw")
                ) {
            dataType = new SQLCharacterDataType("varchar");
        } else if (dataTypeName.equals("number")
                || dataTypeName.equals("decimal")
                || dataTypeName.equals("dec")
                || dataTypeName.equals("numeric")) {
            int scale = 0;
            if (x.getArguments().size() > 1) {
                scale = ((SQLIntegerExpr) x.getArguments().get(1)).getNumber().intValue();
            }
            if (scale == 0) {
                dataType = new SQLDataTypeImpl("bigint");
            } else {
                dataType = new SQLDataTypeImpl("double");
            }
        } else if (dataTypeName.equals("date")
                || dataTypeName.equals("datetime")
                || dataTypeName.equals("timestamp")) {
            dataType = new SQLDataTypeImpl("timestamp");
        } else if (dataTypeName.equals("float")
                || dataTypeName.equals("binary_float")) {
            dataType = new SQLDataTypeImpl("float");
        } else if (dataTypeName.equals("double")
                || dataTypeName.equals("binary_double")) {
            dataType = new SQLDataTypeImpl("double");
        } else {
            dataType = x;
        }

        if (dataType != x) {
            dataType.setParent(x.getParent());
        }

        return dataType;
    }

    public static SQLDataType transformOracleToPostgresql(SQLDataType x) {
        final String name = x.getName();

        if (name == null) {
            return x;
        }
        List<SQLExpr> argumentns = x.getArguments();

        String name_lower = name.toLowerCase();

        SQLDataType dataType;

        if ("urowid".equalsIgnoreCase(name_lower)) {
            int len = 4000;
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLIntegerExpr) {
                    len = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }
            }
            dataType = new SQLDataTypeImpl("varchar", len);
        } else if ("rowid".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("char", 10);

        } else if ("boolean".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("tinyint");

        } else if ("integer".equals(name_lower) || "int".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("decimal", 38);

        } else if ("binary_float".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("real");

        } else if ("binary_double".equals(name_lower)
                || "float".equals(name_lower)
                || "real".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("double precision");

        } else if ("number".equals(name_lower)) {
            if (argumentns.size() == 0) {
                dataType = new SQLDataTypeImpl("decimal", 38);
            } else {
                SQLExpr arg0 = argumentns.get(0);

                int precision, scale = 0;
                if (arg0 instanceof SQLAllColumnExpr) {
                    precision = 9;
                    scale = 1;
                } else {
                    precision = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }

                if (argumentns.size() > 1) {
                    scale = ((SQLIntegerExpr) argumentns.get(1)).getNumber().intValue();
                }

                if (scale > precision) {
                    if (arg0 instanceof SQLAllColumnExpr) {
                        precision = 19;
                        if (scale > precision) {
                            precision = scale;
                        }
                    } else {
                        precision = scale;
                    }
                }

                if (scale == 0) {
                    if (precision < 3) {
                        dataType = new SQLDataTypeImpl("tinyint");
                    } else if (precision < 5) {
                        dataType = new SQLDataTypeImpl("smallint");
                    } else if (precision < 9) {
                        dataType = new SQLDataTypeImpl("int");
                    } else if (precision < 19) {
                        dataType = new SQLDataTypeImpl("bigint");
                    } else {
                        dataType = new SQLDataTypeImpl("decimal", precision);
                    }
                } else {
                    dataType = new SQLDataTypeImpl("decimal", precision, scale);
                }
            }

        } else if ("dec".equals(name_lower) || "decimal".equals(name_lower)) {
            dataType = x.clone();
            dataType.setName("decimal");

            int precision = 0;
            if (argumentns.size() > 0) {
                precision = ((SQLIntegerExpr) argumentns.get(0)).getNumber().intValue();
            }

            int scale = 0;
            if (argumentns.size() > 1) {
                scale = ((SQLIntegerExpr) argumentns.get(1)).getNumber().intValue();
                if (precision < scale) {
                    ((SQLIntegerExpr) dataType.getArguments().get(1)).setNumber(precision);
                }
            }

        } else if ("char".equals(name_lower)
                || "character".equals(name_lower)) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 255) {
                    dataType = new SQLCharacterDataType("char", len);
                } else {
                    dataType = new SQLCharacterDataType("varchar", len);
                }
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("char");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }

        } else if ("nchar".equals(name_lower)) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 255) {
                    dataType = new SQLCharacterDataType("char", len);
                } else {
                    dataType = new SQLCharacterDataType("varchar", len);
                }
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("char");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }

        } else if ("varchar2".equals(name_lower)) {
            if (argumentns.size() > 0) {
                int len;
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
                dataType = new SQLCharacterDataType("varchar", len);
            } else {
                dataType = new SQLCharacterDataType("varchar");
            }

        } else if ("nvarchar2".equals(name_lower) || "nchar varying".equals(name_lower)) {
            if (argumentns.size() > 0) {
                int len;
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
                dataType = new SQLCharacterDataType("varchar", len);
            } else {
                dataType = new SQLCharacterDataType("varchar");
            }

        } else if ("bfile".equals(name_lower)) {
            dataType = new SQLCharacterDataType("varchar", 255);

        } else if ("date".equals(name_lower)
                || "timestamp".equals(name_lower)) {
            int len = -1;
            if (argumentns.size() > 0) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
            }

            dataType = new SQLDataTypeImpl("timestamp", len);
        } else if ("blob".equals(name_lower) || "long raw".equals(name_lower) || "raw".equals(name_lower)) {
            argumentns.clear();
            dataType = new SQLDataTypeImpl("bytea");

        } else if ("clob".equals(name_lower)
                || "nclob".equals(name_lower)
                || "long".equals(name_lower)) {
            argumentns.clear();
            dataType = new SQLCharacterDataType("TEXT");

        } else if ("xmltype".equals(name_lower)) {
            dataType = new SQLDataTypeImpl("xml");
        } else {
            dataType = x;
        }

        if (dataType != x) {
            dataType.setParent(x.getParent());
        }

        return dataType;
    }
}
