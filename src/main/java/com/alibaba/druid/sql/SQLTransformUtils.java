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
import com.alibaba.druid.util.FnvHash;

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
        final long nameHash = x.nameHashCode64();

        if (name == null) {
            return x;
        }
        List<SQLExpr> argumentns = x.getArguments();

        SQLDataType dataType;

        if (nameHash == FnvHash.Constants.UROWID) {
            int len = 4000;
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLIntegerExpr) {
                    len = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }
            }
            dataType = new SQLDataTypeImpl("varchar", len);
        } else if (nameHash == FnvHash.Constants.ROWID) {
            dataType = new SQLDataTypeImpl("char", 10);

        } else if (nameHash == FnvHash.Constants.BOOLEAN) {
            dataType = new SQLDataTypeImpl("tinyint");

        } else if (nameHash == FnvHash.Constants.INTEGER) {
            dataType = new SQLDataTypeImpl("int");

        } else if (nameHash == FnvHash.Constants.FLOAT
                || nameHash == FnvHash.Constants.BINARY_FLOAT) {
                dataType = new SQLDataTypeImpl("float");

        } else if (nameHash == FnvHash.Constants.REAL
                || nameHash == FnvHash.Constants.BINARY_DOUBLE
                || nameHash == FnvHash.Constants.DOUBLE_PRECISION) {
            dataType = new SQLDataTypeImpl("double");

        } else if (nameHash == FnvHash.Constants.NUMBER) {
            if (argumentns.size() == 0) {
                dataType = new SQLDataTypeImpl("decimal", 38);
            } else {
                SQLExpr arg0 = argumentns.get(0);

                int precision, scale = 0;
                if (arg0 instanceof SQLAllColumnExpr) {
                    precision = 9;
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
                    } else if (precision <= 20) {
                        dataType = new SQLDataTypeImpl("bigint");
                    } else {
                        dataType = new SQLDataTypeImpl("decimal", precision);
                    }
                } else {
                    dataType = new SQLDataTypeImpl("decimal", precision, scale);
                }
            }

        } else if (nameHash == FnvHash.Constants.DEC
                || nameHash == FnvHash.Constants.DECIMAL) {

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

        } else if (nameHash == FnvHash.Constants.RAW) {
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
        } else if (nameHash == FnvHash.Constants.CHAR
                || nameHash == FnvHash.Constants.CHARACTER) {
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

        } else if (nameHash == FnvHash.Constants.NCHAR) {
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

        } else if (nameHash == FnvHash.Constants.VARCHAR2) {
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

        } else if (nameHash == FnvHash.Constants.NVARCHAR2) {
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

        } else if (nameHash == FnvHash.Constants.BFILE) {
            dataType = new SQLCharacterDataType("varchar", 255);

        } else if (nameHash == FnvHash.Constants.DATE
                || nameHash == FnvHash.Constants.TIMESTAMP) {
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
        } else if (nameHash == FnvHash.Constants.BLOB
                || nameHash == FnvHash.Constants.LONG_RAW) {
            argumentns.clear();
            dataType = new SQLDataTypeImpl("LONGBLOB");

        } else if (nameHash == FnvHash.Constants.CLOB
                || nameHash == FnvHash.Constants.NCLOB
                || nameHash == FnvHash.Constants.LONG
                || nameHash == FnvHash.Constants.XMLTYPE) {
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
        final long nameHash = x.nameHashCode64();

        if (name == null) {
            return x;
        }
        List<SQLExpr> argumentns = x.getArguments();

        SQLDataType dataType;

        if (nameHash == FnvHash.Constants.UROWID) {
            int len = 4000;
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLIntegerExpr) {
                    len = ((SQLIntegerExpr) arg0).getNumber().intValue();
                }
            }
            dataType = new SQLDataTypeImpl("varchar", len);
        } else if (nameHash == FnvHash.Constants.ROWID) {
            dataType = new SQLDataTypeImpl("char", 10);

        } else if (nameHash == FnvHash.Constants.BOOLEAN) {
            dataType = new SQLDataTypeImpl("tinyint");

        } else if (nameHash == FnvHash.Constants.INTEGER
                || nameHash == FnvHash.Constants.INT) {
            dataType = new SQLDataTypeImpl("decimal", 38);

        } else if (nameHash == FnvHash.Constants.BINARY_FLOAT) {
            dataType = new SQLDataTypeImpl("real");

        } else if (nameHash == FnvHash.Constants.BINARY_DOUBLE
                || nameHash == FnvHash.Constants.FLOAT
                || nameHash == FnvHash.Constants.DOUBLE
                || nameHash == FnvHash.Constants.REAL
                || nameHash == FnvHash.Constants.DOUBLE_PRECISION) {
            dataType = new SQLDataTypeImpl("double precision");

        } else if (nameHash == FnvHash.Constants.NUMBER) {
            if (argumentns.size() == 0) {
                dataType = new SQLDataTypeImpl("decimal", 38);
            } else {
                SQLExpr arg0 = argumentns.get(0);

                int precision, scale = 0;
                if (arg0 instanceof SQLAllColumnExpr) {
                    precision = 19;
                    scale = 0;
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
                    if (precision < 5) {
                        dataType = new SQLDataTypeImpl("smallint");
                    } else if (precision < 9) {
                        dataType = new SQLDataTypeImpl("int");
                    } else if (precision <= 20) {
                        dataType = new SQLDataTypeImpl("bigint");
                    } else {
                        dataType = new SQLDataTypeImpl("decimal", precision);
                    }
                } else {
                    dataType = new SQLDataTypeImpl("decimal", precision, scale);
                }
            }

        } else if (nameHash == FnvHash.Constants.DEC
                || nameHash == FnvHash.Constants.DECIMAL) {
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

        } else if (nameHash == FnvHash.Constants.CHARACTER) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
                dataType = new SQLCharacterDataType("char", len);
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("char");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }
        } else if (nameHash == FnvHash.Constants.CHAR) {
            if (argumentns.size() == 1) {
                SQLExpr arg0 = argumentns.get(0);

                int len;
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 2000) {
                    dataType = x;
                } else {
                    dataType = new SQLCharacterDataType("text");
                }
            } else if (argumentns.size() == 0) {
                dataType = new SQLCharacterDataType("char");
            } else {
                throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
            }
        } else if (nameHash == FnvHash.Constants.NCHAR) {
            // no changed
            dataType = x;
        } else if (nameHash == FnvHash.Constants.VARCHAR
                || nameHash == FnvHash.Constants.VARCHAR2) {
            if (argumentns.size() > 0) {
                int len;
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }

                if (len <= 4000) {
                    dataType = new SQLCharacterDataType("varchar", len);
                } else {
                    dataType = new SQLCharacterDataType("text");
                }
            } else {
                dataType = new SQLCharacterDataType("varchar");
            }

        } else if (nameHash == FnvHash.Constants.NVARCHAR2
                || nameHash == FnvHash.Constants.NCHAR_VARYING) {
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

        } else if (nameHash == FnvHash.Constants.BFILE) {
            dataType = new SQLCharacterDataType("varchar", 255);

        } else if (nameHash == FnvHash.Constants.DATE
                || nameHash == FnvHash.Constants.DATETIME
                || nameHash == FnvHash.Constants.TIMESTAMP) {
            int len = -1;
            if (argumentns.size() > 0) {
                SQLExpr arg0 = argumentns.get(0);
                if (arg0 instanceof SQLNumericLiteralExpr) {
                    len = ((SQLNumericLiteralExpr) arg0).getNumber().intValue();
                } else {
                    throw new UnsupportedOperationException(SQLUtils.toOracleString(x));
                }
            }

            if (len > 0) {
                dataType = new SQLDataTypeImpl("timestamp", len);
            } else {
                dataType = new SQLDataTypeImpl("timestamp");
            }
        } else if (nameHash == FnvHash.Constants.BLOB
                || nameHash == FnvHash.Constants.LONG_RAW
                || nameHash == FnvHash.Constants.RAW) {
            argumentns.clear();
            dataType = new SQLDataTypeImpl("bytea");

        } else if (nameHash == FnvHash.Constants.CLOB
                || nameHash == FnvHash.Constants.NCLOB
                || nameHash == FnvHash.Constants.LONG) {
            argumentns.clear();
            dataType = new SQLCharacterDataType("text");

        } else if (nameHash == FnvHash.Constants.XMLTYPE) {
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
