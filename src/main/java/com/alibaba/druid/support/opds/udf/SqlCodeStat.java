package com.alibaba.druid.support.opds.udf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitorAdapter;
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
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.odps);

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
        public int groupByCount;

        @JSONField(ordinal = 5)
        public int orderByCount;

        @JSONField(ordinal = 6)
        public int fromCount;

        @JSONField(ordinal = 7)
        public int joinCount;

        @JSONField(ordinal = 50)
        public int insert;

        @JSONField(ordinal = 51)
        public int update;

        @JSONField(ordinal = 52)
        public int delete;

        @JSONField(ordinal = 53)
        public int create;

        @JSONField(ordinal = 54)
        public int drop;

        @JSONField(ordinal = 100)
        public int conditionCount;

        @JSONField(ordinal = 101)
        public int joinConditionCount;

        @JSONField(ordinal = 102)
        public int valueConditionCount;

        @JSONField(ordinal = 103)
        public int otherConditionCount;

        @JSONField(ordinal = 104)
        public int limit;

        @JSONField(ordinal = 200)
        public int aggregateCount;

        @JSONField(ordinal = 201)
        public int functionCallCount;

        @JSONField(ordinal = 202)
        public int having;
    }

    private static class CodeStatVisitor extends OdpsASTVisitorAdapter {
        SqlStat stat = new SqlStat();

        public void preVisit(SQLObject x) {
            if (x instanceof SQLStatement) {
                stat.statementCount++;
            }

            if (x instanceof SQLInsertStatement) {
                stat.insert++;
            } else  if (x instanceof SQLDropStatement) {
                stat.drop++;
            } else  if (x instanceof SQLCreateStatement) {
                stat.create++;
            } else  if (x instanceof SQLDeleteStatement) {
                stat.delete++;
            } else  if (x instanceof SQLUpdateStatement) {
                stat.update++;
            }
        }

        public boolean visit(SQLUnionQuery x) {
            stat.union++;
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
            stat.groupByCount++;
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
            stat.orderByCount++;
            return true;
        }

        public boolean visit(SQLExprTableSource x) {
            stat.fromCount++;
            return true;
        }

        public boolean visit(SQLSubqueryTableSource x) {
            stat.fromCount++;
            return true;
        }

        public boolean visit(SQLJoinTableSource x) {
            stat.joinCount++;
            return true;
        }

        public boolean visit(SQLMethodInvokeExpr x) {
            stat.functionCallCount++;
            return true;
        }

        public boolean visit(SQLAggregateExpr x) {
            stat.aggregateCount++;
            return true;
        }

        public boolean visit(SQLCastExpr x) {
            stat.functionCallCount++;
            return true;
        }

        public boolean visit(SQLInListExpr x) {
            stat.conditionCount++;
            return true;
        }

        public boolean visit(SQLBinaryOpExpr x) {
            if (x.getOperator() != null && x.getOperator().isRelational()) {
                stat.conditionCount++;

                SQLExpr left = x.getLeft();
                SQLExpr right = x.getRight();

                if (left instanceof SQLName && right instanceof SQLName) {
                    stat.joinConditionCount++;
                } else if ((left instanceof SQLName || right instanceof SQLName)
                        && (left instanceof SQLLiteralExpr || right instanceof SQLLiteralExpr)
                ) {
                    stat.valueConditionCount++;
                } else {
                    stat.otherConditionCount++;
                }
            }
            return true;
        }

        public boolean visit(SQLCaseExpr x) {
            SQLExpr value = x.getValueExpr();
            if (value != null) {
                stat.conditionCount += x.getItems().size();
            }

            if (x.getElseExpr() != null) {
                stat.conditionCount++;
            }

            return true;
        }

        public boolean visit(SQLExistsExpr x) {
            stat.conditionCount++;
            return true;
        }

        public String toString() {
            return JSON.toJSONString(stat
                    , SerializerFeature.PrettyFormat
                    , SerializerFeature.NotWriteDefaultValue);
        }
    }
}
