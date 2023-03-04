package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsReadStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.aliyun.odps.udf.UDF;

import java.util.List;

public class SqlCodeStat extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) {
        return evaluate(sql, dbTypeName, false);
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) {
        DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);

        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(
                    sql,
                    DbType.odps,
                    SQLParserFeature.EnableMultiUnion,
                    SQLParserFeature.EnableSQLBinaryOpExprGroup
            );

            CodeStatVisitor v = new CodeStatVisitor();
            for (SQLStatement stmt : stmtList) {
                stmt.accept(v);
            }

            return v.toString();
        } catch (Exception ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            return null;
        }
    }

    public static class SqlStat {
        @JSONField(ordinal = 0)
        public int statementCount;

        @JSONField(ordinal = 1)
        public int cte;

        @JSONField(ordinal = 2)
        public int union;

        @JSONField(ordinal = 3)
        public int select;

        @JSONField(ordinal = 4)
        public int groupBy;

        @JSONField(ordinal = 5)
        public int orderBy;

        @JSONField(ordinal = 6)
        public int from;

        @JSONField(ordinal = 7)
        public int join;

        @JSONField(ordinal = 8)
        public int over;

        @JSONField(ordinal = 9)
        public int subQuery;

        @JSONField(ordinal = 10)
        public int lateralView;

        @JSONField(ordinal = 40)
        public int insert;

        @JSONField(ordinal = 41)
        public int insertInto;

        @JSONField(ordinal = 42)
        public int insertOverwrite;

        @JSONField(ordinal = 43)
        public int insertSelect;

        @JSONField(ordinal = 44)
        public int insertMulti;

        @JSONField(ordinal = 51)
        public int update;

        @JSONField(ordinal = 52)
        public int delete;

        @JSONField(ordinal = 60)
        public int create;

        @JSONField(ordinal = 61)
        public int createTable;

        @JSONField(ordinal = 62)
        public int createView;

        @JSONField(ordinal = 70)
        public int drop;

        @JSONField(ordinal = 71)
        public int dropTable;

        @JSONField(ordinal = 72)
        public int dropView;

        @JSONField(ordinal = 80)
        public int set;

        @JSONField(ordinal = 90)
        public int alter;

        @JSONField(ordinal = 100)
        public int read;

        @JSONField(ordinal = 200)
        public int condition;

        @JSONField(ordinal = 201)
        public int joinCondition;

        @JSONField(ordinal = 202)
        public int valueCondition;

        @JSONField(ordinal = 203)
        public int otherCondition;

        @JSONField(ordinal = 204)
        public int limit;

        @JSONField(ordinal = 300)
        public int aggregate;

        @JSONField(ordinal = 201)
        public int functionCall;

        @JSONField(ordinal = 202)
        public int having;
    }

    static class CodeStatVisitor extends OdpsASTVisitorAdapter {
        SqlStat stat = new SqlStat();

        public void preVisit(SQLObject x) {
            if (x instanceof SQLStatement) {
                stat.statementCount++;

                if (x instanceof SQLInsertStatement) {
                    stat.insert++;

                    SQLInsertStatement insert = (SQLInsertStatement) x;

                    if (insert.getQuery() != null) {
                        stat.insertSelect++;
                    }

                    if (insert.isOverwrite()) {
                        stat.insertOverwrite++;
                    } else {
                        stat.insertInto++;
                    }
                } else if (x instanceof HiveMultiInsertStatement) {
                    stat.insert++;
                    stat.insertMulti++;
                    stat.insertSelect++;
                    for (HiveInsert item : ((HiveMultiInsertStatement) x).getItems()) {
                        if (item.isOverwrite()) {
                            stat.insertOverwrite++;
                        } else {
                            stat.insertInto++;
                        }
                    }
                } else if (x instanceof SQLDropStatement) {
                    stat.drop++;
                    if (x instanceof SQLDropTableStatement) {
                        stat.dropTable++;
                    } else if (x instanceof SQLDropViewStatement) {
                        stat.dropView++;
                    }
                } else if (x instanceof SQLCreateStatement) {
                    stat.create++;
                    if (x instanceof SQLCreateTableStatement) {
                        stat.createTable++;
                    } else if (x instanceof SQLCreateViewStatement) {
                        stat.createView++;
                    }
                } else if (x instanceof SQLDeleteStatement) {
                    stat.delete++;
                } else if (x instanceof SQLUpdateStatement) {
                    stat.update++;
                } else if (x instanceof SQLSetStatement) {
                    stat.set++;
                } else if (x instanceof SQLAlterStatement) {
                    stat.alter++;
                } else if (x instanceof OdpsReadStatement) {
                    stat.read++;
                }
            }
        }

        public boolean visit(SQLUnionQuery x) {
            stat.union++;
            return true;
        }

        public boolean visit(SQLLateralViewTableSource x) {
            stat.lateralView++;
            return true;
        }

        public boolean visit(OdpsSelectQueryBlock x) {
            return visit((SQLSelectQueryBlock) x);
        }

        public boolean visit(SQLSelectQueryBlock x) {
            stat.select++;
            return true;
        }

        public boolean visit(SQLSelectGroupByClause x) {
            stat.groupBy++;
            if (x.getHaving() != null) {
                stat.having++;
            }
            return true;
        }

        public boolean visit(SQLLimit x) {
            stat.limit++;
            return false;
        }

        public boolean visit(SQLWithSubqueryClause.Entry x) {
            stat.cte++;
            return true;
        }

        public boolean visit(SQLOrderBy x) {
            stat.orderBy++;
            return true;
        }

        public boolean visit(SQLExprTableSource x) {
            stat.from++;
            return true;
        }

        public boolean visit(SQLSubqueryTableSource x) {
            stat.from++;
            stat.subQuery++;
            return true;
        }

        public boolean visit(SQLJoinTableSource x) {
            if (!(x.getParent() instanceof SQLJoinTableSource)) {
                stat.from++;
            }
            stat.join++;
            return true;
        }

        public boolean visit(SQLMethodInvokeExpr x) {
            stat.functionCall++;
            return true;
        }

        public boolean visit(SQLAggregateExpr x) {
            stat.aggregate++;
            return true;
        }

        public boolean visit(SQLOver x) {
            stat.over++;
            return true;
        }

        public boolean visit(SQLCastExpr x) {
            stat.functionCall++;
            return true;
        }

        public boolean visit(SQLInListExpr x) {
            stat.condition++;
            return true;
        }

        public boolean visit(SQLBinaryOpExpr x) {
            if (x.getOperator() != null && x.getOperator().isRelational()) {
                stat.condition++;

                SQLExpr left = x.getLeft();
                SQLExpr right = x.getRight();

                if (left instanceof SQLName && right instanceof SQLName) {
                    stat.joinCondition++;
                } else if ((left instanceof SQLName || right instanceof SQLName)
                        && (left instanceof SQLLiteralExpr || right instanceof SQLLiteralExpr)
                ) {
                    stat.valueCondition++;
                } else {
                    stat.otherCondition++;
                }
            }
            return true;
        }

        public boolean visit(SQLCaseExpr x) {
            SQLExpr value = x.getValueExpr();
            if (value != null) {
                stat.condition += x.getItems().size();
            }

            if (x.getElseExpr() != null) {
                stat.condition++;
            }

            return true;
        }

        public boolean visit(SQLExistsExpr x) {
            stat.condition++;
            return true;
        }

        public String toString() {
            return JSON.toJSONString(
                    stat,
                    JSONWriter.Feature.PrettyFormat,
                    JSONWriter.Feature.NotWriteDefaultValue
            );
        }

        public java.util.Map toMap() {
            return (java.util.Map) JSON.toJSON(stat);
        }
    }
}
