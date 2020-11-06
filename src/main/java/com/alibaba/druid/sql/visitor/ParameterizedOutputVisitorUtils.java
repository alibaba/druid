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
package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLExprUtils;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.dialect.h2.visitor.H2OutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlParameterizedVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTParameterizedVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.dialect.phoenix.visitor.PhoenixOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.FnvHash;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ParameterizedOutputVisitorUtils {
    private final static SQLParserFeature[] defaultFeatures = {
            SQLParserFeature.EnableSQLBinaryOpExprGroup,
            SQLParserFeature.UseInsertColumnsCache,
            SQLParserFeature.OptimizedForParameterized,
    };

    private final static SQLParserFeature[] defaultFeatures2 = {
            SQLParserFeature.EnableSQLBinaryOpExprGroup,
            SQLParserFeature.UseInsertColumnsCache,
            SQLParserFeature.OptimizedForParameterized,
            SQLParserFeature.OptimizedForForParameterizedSkipValue,
    };

    private final static SQLParserFeature[] defaultFeatures_tddl = {
            SQLParserFeature.EnableSQLBinaryOpExprGroup,
            SQLParserFeature.UseInsertColumnsCache,
            SQLParserFeature.OptimizedForParameterized,
            SQLParserFeature.TDDLHint,
    };

    private final static SQLParserFeature[] defaultFeatures2_tddl = {
            SQLParserFeature.EnableSQLBinaryOpExprGroup,
            SQLParserFeature.UseInsertColumnsCache,
            SQLParserFeature.OptimizedForParameterized,
            SQLParserFeature.OptimizedForForParameterizedSkipValue,
            SQLParserFeature.TDDLHint,
    };

    public static String parameterize(String sql, DbType dbType) {
        return parameterize(sql, dbType, null, null);
    }

    public static String parameterize(String sql, DbType dbType, VisitorFeature ...features) {
        return parameterize(sql, dbType, null, features);
    }

    public static String parameterize(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache) {
        return parameterize(sql, dbType, selectListCache, null);
    }

    public static String parameterize(String sql
            , DbType dbType
            , List<Object> outParameters) {
        return parameterize(sql, dbType, null, outParameters);
    }

    public static String parameterize(String sql
            , DbType dbType
            , List<Object> outParameters, VisitorFeature ...features) {
        return parameterize(sql, dbType, null, outParameters, features);
    }

    public static String parameterizeForTDDL(String sql
            , DbType dbType
            , List<Object> outParameters, VisitorFeature ...features) {
        return parameterizeForTDDL(sql, dbType, null, outParameters, features);
    }

    private static void configVisitorFeatures(ParameterizedVisitor visitor, VisitorFeature ...features) {
        if(features != null) {
            for (int i = 0; i < features.length; i++) {
                visitor.config(features[i], true);
            }
        }
    }

    public static String parameterize(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache
            , List<Object> outParameters
            , VisitorFeature ...visitorFeatures) {
        final SQLParserFeature[] features = outParameters == null
                ? defaultFeatures2
                : defaultFeatures;

        return parameterize(sql, dbType, selectListCache, outParameters, features, visitorFeatures);
    }

    public static String parameterizeForTDDL(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache
            , List<Object> outParameters
            , VisitorFeature ...visitorFeatures) {
        final SQLParserFeature[] features = outParameters == null
                ? defaultFeatures2_tddl
                : defaultFeatures_tddl;

        return parameterize(sql, dbType, selectListCache, outParameters, features, visitorFeatures);
    }

    public static String parameterize(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache
            , List<Object> outParameters
            , SQLParserFeature[] features
            , VisitorFeature ...visitorFeatures) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);

        if (selectListCache != null) {
            parser.setSelectListCache(selectListCache);
        }

        List<SQLStatement> statementList = parser.parseStatementList();
        if (statementList.size() == 0) {
            return sql;
        }

        StringBuilder out = new StringBuilder(sql.length());
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);
        if (outParameters != null) {
            visitor.setOutputParameters(outParameters);
        }

        configVisitorFeatures(visitor, visitorFeatures);

        for (int i = 0; i < statementList.size(); i++) {
            SQLStatement stmt = statementList.get(i);

            if (i > 0) {
                SQLStatement preStmt = statementList.get(i - 1);

                if (preStmt.getClass() == stmt.getClass()) {
                    StringBuilder buf = new StringBuilder();
                    ParameterizedVisitor v1 = createParameterizedOutputVisitor(buf, dbType);
                    preStmt.accept(v1);
                    if (out.toString().equals(buf.toString())) {
                        continue;
                    }
                }

                if (!preStmt.isAfterSemi()) {
                    out.append(";\n");
                } else {
                    out.append('\n');
                }
            }

            if (stmt.hasBeforeComment()) {
                stmt.getBeforeCommentsDirect().clear();
            }

            Class<?> stmtClass = stmt.getClass();
            if (stmtClass == SQLSelectStatement.class) { // only for performance
                SQLSelectStatement selectStatement = (SQLSelectStatement) stmt;
                visitor.visit(selectStatement);
                visitor.postVisit(selectStatement);
            } else {
                stmt.accept(visitor);
            }
        }

        if (visitor.getReplaceCount() == 0
                && parser.getLexer().getCommentCount() == 0
                && sql.charAt(0) != '/') {

            boolean notUseOriginalSql = false;
            if (visitorFeatures != null) {
                for (VisitorFeature visitorFeature : visitorFeatures) {
                    if (visitorFeature == VisitorFeature.OutputParameterizedZeroReplaceNotUseOriginalSql) {
                        notUseOriginalSql = true;
                    }
                }
            }
            if (!notUseOriginalSql) {
                int ddlStmtCount = 0;
                for (SQLStatement stmt : statementList) {
                    if (stmt instanceof SQLDDLStatement) {
                        ddlStmtCount++;
                    }
                }
                if (ddlStmtCount == statementList.size()) {
                    notUseOriginalSql = true;
                }
            }

            if (!notUseOriginalSql) {
                return sql;
            }
        }

        return out.toString();
    }

    public static long parameterizeHash(String sql
            , DbType dbType
            , List<Object> outParameters) {
        return parameterizeHash(sql, dbType, null, outParameters, null);
    }

    public static long parameterizeHash(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache
            , List<Object> outParameters) {
        return parameterizeHash(sql, dbType, selectListCache, outParameters, null);
    }

    public static long parameterizeHash(String sql
            , DbType dbType
            , SQLSelectListCache selectListCache
            , List<Object> outParameters, VisitorFeature ...visitorFeatures) {

        final SQLParserFeature[] features = outParameters == null
                ? defaultFeatures2
                : defaultFeatures;

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);

        if (selectListCache != null) {
            parser.setSelectListCache(selectListCache);
        }

        List<SQLStatement> statementList = parser.parseStatementList();
        final int stmtSize = statementList.size();
        if (stmtSize == 0) {
            return 0L;
        }

        StringBuilder out = new StringBuilder(sql.length());
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);
        if (outParameters != null) {
            visitor.setOutputParameters(outParameters);
        }
        configVisitorFeatures(visitor, visitorFeatures);

        if (stmtSize == 1) {
            SQLStatement stmt = statementList.get(0);
            if (stmt.getClass() == SQLSelectStatement.class) {
                SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;

                if (selectListCache != null) {
                    SQLSelectQueryBlock queryBlock = selectStmt.getSelect().getQueryBlock();
                    if (queryBlock != null) {
                        String cachedSelectList = queryBlock.getCachedSelectList();
                        long cachedSelectListHash = queryBlock.getCachedSelectListHash();
                        if (cachedSelectList != null) {
                            visitor.config(VisitorFeature.OutputSkipSelectListCacheString, true);
                        }

                        visitor.visit(selectStmt);
                        return FnvHash.fnv1a_64_lower(cachedSelectListHash, out);
                    }
                }

                visitor.visit(selectStmt);
            } else if (stmt.getClass() == MySqlInsertStatement.class) {
                MySqlInsertStatement insertStmt = (MySqlInsertStatement) stmt;
                String columnsString = insertStmt.getColumnsString();
                if (columnsString != null) {
                    long columnsStringHash = insertStmt.getColumnsStringHash();
                    visitor.config(VisitorFeature.OutputSkipInsertColumnsString, true);

                    ((MySqlASTVisitor) visitor).visit(insertStmt);
                    return FnvHash.fnv1a_64_lower(columnsStringHash, out);
                }
            } else {
                stmt.accept(visitor);
            }

            return FnvHash.fnv1a_64_lower(out);
        }

        for (int i = 0; i < statementList.size(); i++) {
            if (i > 0) {
                out.append(";\n");
            }
            SQLStatement stmt = statementList.get(i);

            if (stmt.hasBeforeComment()) {
                stmt.getBeforeCommentsDirect().clear();
            }

            Class<?> stmtClass = stmt.getClass();
            if (stmtClass == SQLSelectStatement.class) { // only for performance
                SQLSelectStatement selectStatement = (SQLSelectStatement) stmt;
                visitor.visit(selectStatement);
                visitor.postVisit(selectStatement);
            } else {
                stmt.accept(visitor);
            }
        }

        return FnvHash.fnv1a_64_lower(out);
    }

    public static String parameterize(List<SQLStatement> statementList, DbType dbType) {
        StringBuilder out = new StringBuilder();
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);

        for (int i = 0; i < statementList.size(); i++) {
            if (i > 0) {
                out.append(";\n");
            }
            SQLStatement stmt = statementList.get(i);

            if (stmt.hasBeforeComment()) {
                stmt.getBeforeCommentsDirect().clear();
            }
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String parameterize(SQLStatement stmt, DbType dbType) {
        StringBuilder out = new StringBuilder();
        ParameterizedVisitor visitor = createParameterizedOutputVisitor(out, dbType);

        if (stmt.hasBeforeComment()) {
            stmt.getBeforeCommentsDirect().clear();
        }
        stmt.accept(visitor);

        return out.toString();
    }

    public static SQLStatement parameterizeOf(String sql, DbType dbType) {
        return parameterizeOf(sql, null, dbType);
    }

    public static SQLStatement parameterizeOf(String sql, List<Object> outParameters, DbType dbType) {
        if (dbType == DbType.mysql) {
            SQLStatement stmt = SQLUtils.parseSingleMysqlStatement(sql);
            MySqlParameterizedVisitor visitor = new MySqlParameterizedVisitor(outParameters);
            stmt.accept(visitor);
            return stmt;
        } else if (dbType == DbType.oracle) {
            SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.oracle);
            OracleASTParameterizedVisitor visitor = new OracleASTParameterizedVisitor(outParameters);
            stmt.accept(visitor);
            return stmt;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static ParameterizedVisitor createParameterizedOutputVisitor(Appendable out, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                return new OracleParameterizedOutputVisitor(out);
            case mysql:
            case mariadb:
            case elastic_search:
                return new MySqlOutputVisitor(out, true);
            case h2:
                return new H2OutputVisitor(out, true);
            case postgresql:
            case edb:
                return new PGOutputVisitor(out, true);
            case sqlserver:
            case jtds:
                return new SQLServerOutputVisitor(out, true);
            case db2:
                return new DB2OutputVisitor(out, true);
            case phoenix:
                return new PhoenixOutputVisitor(out, true);
            default:
                return new SQLASTOutputVisitor(out, true);
        }
    }

    public static String restore(String sql, DbType dbType, List<Object> parameters) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        visitor.setInputParameters(parameters);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String restore(String sql, DbType dbType, Map<String, Object> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return sql;
        }

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        RestoreVisitor v = new RestoreVisitor(parameters);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(v);
        }

        StringBuilder out = new StringBuilder(sql.length());
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    private static class RestoreVisitor extends SQLASTVisitorAdapter {
        Map<String, Object> parameters;
        TimeZone timeZone;

        public RestoreVisitor(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public boolean visit(SQLVariantRefExpr x) {
            String name = x.getName();
            if (name.length() > 3) {
                char c0 = name.charAt(0);
                char c1 = name.charAt(1);
                char c1x = name.charAt(name.length() - 1);

                if (c0 == '#' && c1 == '{' && c1x == '}') {
                    String key = name.substring(2, name.length() - 1);
                    Object value = parameters.get(x);
                    SQLExpr expr = SQLExprUtils.fromJavaObject(value, timeZone);
                    SQLUtils.replaceInParent(x, expr);
                }
            }
            return true;
        }
    }
}
