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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.ads.visitor.AdsOutputVisitor;
import com.alibaba.druid.sql.dialect.antspark.visitor.AntsparkOutputVisitor;
import com.alibaba.druid.sql.dialect.antspark.visitor.AntsparkSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.blink.vsitor.BlinkOutputVisitor;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickhouseOutputVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.h2.visitor.H2OutputVisitor;
import com.alibaba.druid.sql.dialect.h2.visitor.H2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.ast.clause.MySqlSelectIntoStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsOutputVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleToMySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.*;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class SQLUtils {
    public final static Charset UTF8                             = Charset.forName("UTF-8");

    private final static SQLParserFeature[] FORMAT_DEFAULT_FEATURES = {
            SQLParserFeature.KeepComments,
            SQLParserFeature.EnableSQLBinaryOpExprGroup
    };

    public static FormatOption DEFAULT_FORMAT_OPTION = new FormatOption(true, true);
    public static FormatOption DEFAULT_LCASE_FORMAT_OPTION
            = new FormatOption(false, true);

    private final static Log LOG = LogFactory.getLog(SQLUtils.class);

    public static String toSQLString(SQLObject sqlObject, String dbType) {
        return toSQLString(sqlObject, DbType.valueOf(dbType));
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType) {
        return toSQLString(sqlObject, dbType, null, null);
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType, FormatOption option) {
        return toSQLString(sqlObject, dbType, option, null);
    }

    public static String toSQLString(SQLObject sqlObject, DbType dbType, FormatOption option, VisitorFeature... features) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createOutputVisitor(out, dbType);

        if (option == null) {
            option = DEFAULT_FORMAT_OPTION;
        }

        visitor.setUppCase(option.isUppCase());
        visitor.setPrettyFormat(option.isPrettyFormat());
        visitor.setParameterized(option.isParameterized());

        int featuresValue = option.features;
        if (features != null) {
            for (VisitorFeature feature : features) {
                visitor.config(feature, true);
                featuresValue |= feature.mask;
            }
        }

        visitor.setFeatures(featuresValue);

        sqlObject.accept(visitor);

        String sql = out.toString();
        return sql;
    }

    public static String toSQLString(SQLObject obj) {
        if (obj instanceof SQLStatement) {
            SQLStatement stmt = (SQLStatement) obj;
            return toSQLString(stmt, stmt.getDbType());
        }

        if (obj instanceof MySqlObject) {
            return toMySqlString(obj);
        }

        StringBuilder out = new StringBuilder();
        obj.accept(new SQLASTOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toOdpsString(SQLObject sqlObject) {
        return toOdpsString(sqlObject, null);
    }

    public static String toHiveString(SQLObject sqlObject) {
        return toSQLString(sqlObject, DbType.odps);
    }

    public static String toOdpsString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.odps, option);
    }

    public static String toAntsparkString(SQLObject sqlObject){
        return toAntsparkString(sqlObject,null);
    }

    public static String toAntsparkString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.antspark, option);
    }
    public static String toMySqlString(SQLObject sqlObject) {
        return toMySqlString(sqlObject, (FormatOption) null);
    }

    public static String toMySqlStringIfNotNull(SQLObject sqlObject, String defaultStr) {
        if (sqlObject == null) {
            return defaultStr;
        }

        return toMySqlString(sqlObject, (FormatOption) null);
    }

    public static String toMySqlString(SQLObject sqlObject, VisitorFeature... features) {
        return toMySqlString(sqlObject, new FormatOption(features));
    }

    public static String toNormalizeMysqlString(SQLObject sqlObject) {
        if (sqlObject != null) {
            return SQLUtils.normalize(toSQLString(sqlObject, DbType.mysql));
        }
        return null;
    }

    public static String toMySqlString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.mysql, option);
    }

    public static SQLExpr toMySqlExpr(String sql) {
        return toSQLExpr(sql, DbType.mysql);
    }

    public static String formatMySql(String sql) {
        return format(sql, DbType.mysql);
    }

    public static String formatMySql(String sql, FormatOption option) {
        return format(sql, DbType.mysql, option);
    }

    public static String formatOracle(String sql) {
        return format(sql, DbType.oracle);
    }

    public static String formatOracle(String sql, FormatOption option) {
        return format(sql, DbType.oracle, option);
    }

    public static String formatOdps(String sql) {
        return format(sql, DbType.odps);
    }

    public static String formatPresto(String sql) {
        return formatPresto(sql, null);
    }

    public static String formatPresto(String sql, FormatOption option) {
        SQLParserFeature[] features = { SQLParserFeature.KeepComments,
                                                       SQLParserFeature.EnableSQLBinaryOpExprGroup,
                                                       SQLParserFeature.KeepNameQuotes};
        return format(sql, DbType.mysql, null, option, features);
    }

    public static String formatHive(String sql) {
        return format(sql, DbType.hive);
    }

    public static String formatOdps(String sql, FormatOption option) {
        return format(sql, DbType.odps, option);
    }

    public static String formatHive(String sql, FormatOption option) {
        return format(sql, DbType.hive, option);
    }

    public static String formatSQLServer(String sql) {
        return format(sql, DbType.sqlserver);
    }

    public static String toOracleString(SQLObject sqlObject) {
        return toOracleString(sqlObject, null);
    }

    public static String toOracleString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.oracle, option);
    }

    public static String toPGString(SQLObject sqlObject) {
        return toPGString(sqlObject, null);
    }

    public static String toPGString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.postgresql, option);
    }

    public static String toDB2String(SQLObject sqlObject) {
        return toDB2String(sqlObject, null);
    }

    public static String toDB2String(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.db2, option);
    }

    public static String toSQLServerString(SQLObject sqlObject) {
        return toSQLServerString(sqlObject, null);
    }

    public static String toSQLServerString(SQLObject sqlObject, FormatOption option) {
        return toSQLString(sqlObject, DbType.sqlserver, option);
    }

    public static String formatPGSql(String sql, FormatOption option) {
        return format(sql, DbType.postgresql, option);
    }

    public static SQLExpr toSQLExpr(String sql, DbType dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLExpr expr = parser.expr();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql +", " +parser.getLexer().info());
        }

        return expr;
    }

    public static SQLSelectOrderByItem toOrderByItem(String sql, DbType dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLSelectOrderByItem orderByItem = parser.parseSelectOrderByItem();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql + ", " + parser.getLexer().info());
        }

        return orderByItem;
    }

    public static SQLUpdateSetItem toUpdateSetItem(String sql, DbType dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLUpdateSetItem updateSetItem = parser.parseUpdateSetItem();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql + ", " + parser.getLexer().info());
        }

        return updateSetItem;
    }

    public static SQLSelectItem toSelectItem(String sql, DbType dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLSelectItem selectItem = parser.parseSelectItem();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql + ", " + parser.getLexer().info());
        }

        return selectItem;
    }

    public static List<SQLStatement> toStatementList(String sql, DbType dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        return parser.parseStatementList();
    }

    public static SQLExpr toSQLExpr(String sql) {
        return toSQLExpr(sql, null);
    }

    public static String format(String sql, String dbType) {
        return format(sql, DbType.of(dbType));
    }

    public static String format(String sql, DbType dbType) {
        return format(sql, dbType, null, null);
    }

    public static String format(String sql, DbType dbType, FormatOption option) {
        return format(sql, dbType, null, option);
    }

    public static String format(String sql, DbType dbType, List<Object> parameters) {
        return format(sql, dbType, parameters, null);
    }

    public static String format(String sql, DbType dbType, List<Object> parameters, FormatOption option) {
        return format(sql, dbType, parameters, option, FORMAT_DEFAULT_FEATURES);
    }

    public static String format(String sql, DbType dbType, List<Object> parameters, FormatOption option,
                                SQLParserFeature[] features) {
        try {
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);
            List<SQLStatement> statementList = parser.parseStatementList();
            return toSQLString(statementList, dbType, parameters, option);
        } catch (ParserException ex) {
            LOG.warn("rowFormat error", ex);
            return sql;
        }
    }

    public static String toSQLString(List<SQLStatement> statementList, DbType dbType) {
        return toSQLString(statementList, dbType, (List<Object>) null);
    }

    public static String toSQLString(List<SQLStatement> statementList, DbType dbType, FormatOption option) {
        return toSQLString(statementList, dbType, null, option);
    }

    public static String toSQLString(List<SQLStatement> statementList, DbType dbType, List<Object> parameters) {
        return toSQLString(statementList, dbType, parameters, null, null);
    }

    public static String toSQLString(List<SQLStatement> statementList, DbType dbType, List<Object> parameters, FormatOption option) {
        return toSQLString(statementList, dbType, parameters, option, null);
    }

    public static String toSQLString(List<SQLStatement> statementList
            , DbType dbType
            , List<Object> parameters
            , FormatOption option
            , Map<String, String> tableMapping) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createFormatOutputVisitor(out, statementList, dbType);
        if (parameters != null) {
            visitor.setInputParameters(parameters);
        }

        if (option == null) {
            option = DEFAULT_FORMAT_OPTION;
        }
        visitor.setFeatures(option.features);

        if (tableMapping != null) {
            visitor.setTableMapping(tableMapping);
        }

        boolean printStmtSeperator;
        if (DbType.sqlserver == dbType) {
            printStmtSeperator = false;
        } else {
            printStmtSeperator = DbType.oracle != dbType;
        }

        for (int i = 0, size = statementList.size(); i < size; i++) {
            SQLStatement stmt = statementList.get(i);

            if (i > 0) {
                SQLStatement preStmt = statementList.get(i - 1);
                if (printStmtSeperator && !preStmt.isAfterSemi()) {
                    visitor.print(";");
                }

                List<String> comments = preStmt.getAfterCommentsDirect();
                if (comments != null){
                    for (int j = 0; j < comments.size(); ++j) {
                        String comment = comments.get(j);
                        if (j != 0) {
                            visitor.println();
                        }
                        visitor.printComment(comment);
                    }
                }

                if (printStmtSeperator) {
                    visitor.println();
                }

                if (!(stmt instanceof SQLSetStatement)) {
                    visitor.println();
                }
            }

//            {
//                List<String> comments = stmt.getBeforeCommentsDirect();
//                if (comments != null){
//                    for(String comment : comments) {
//                        visitor.printComment(comment);
//                        visitor.println();
//                    }
//                }
//            }
            stmt.accept(visitor);

            if (i == size - 1) {
                List<String> comments = stmt.getAfterCommentsDirect();
                if (comments != null){
                    for (int j = 0; j < comments.size(); ++j) {
                        String comment = comments.get(j);
                        if (j != 0) {
                            visitor.println();
                        }
                        visitor.printComment(comment);
                    }
                }
            }
        }

        return out.toString();
    }

    public static SQLASTOutputVisitor createOutputVisitor(Appendable out, DbType dbType) {
        return createFormatOutputVisitor(out, null, dbType);
    }

    public static SQLASTOutputVisitor createFormatOutputVisitor(Appendable out, //
                                                                List<SQLStatement> statementList, //
                                                                DbType dbType) {
        if (dbType == null) {
            if (statementList != null && statementList.size() > 0) {
                dbType = statementList.get(0).getDbType();
            }

            if (dbType == null) {
                dbType = DbType.other;
            }
        }

        switch (dbType) {
            case oracle:
            case oceanbase_oracle:
                if (statementList == null || statementList.size() == 1) {
                    return new OracleOutputVisitor(out, false);
                } else {
                    return new OracleOutputVisitor(out, true);
                }
            case mysql:
            case mariadb:
                return new MySqlOutputVisitor(out);
            case postgresql:
                return new PGOutputVisitor(out);
            case sqlserver:
            case jtds:
                return new SQLServerOutputVisitor(out);
            case db2:
                return new DB2OutputVisitor(out);
            case odps:
                return new OdpsOutputVisitor(out);
            case h2:
                return new H2OutputVisitor(out);
            case hive:
                return new HiveOutputVisitor(out);
            case ads:
                return new AdsOutputVisitor(out);
            case blink:
                return new BlinkOutputVisitor(out);
            case antspark:
                return new AntsparkOutputVisitor(out);
            case clickhouse:
                return new ClickhouseOutputVisitor(out);
            default:
                return new SQLASTOutputVisitor(out, dbType);
        }
    }

    @Deprecated
    public static SchemaStatVisitor createSchemaStatVisitor(List<SQLStatement> statementList, DbType dbType) {
        return createSchemaStatVisitor(dbType);
    }

    public static SchemaStatVisitor createSchemaStatVisitor(DbType dbType) {
        return createSchemaStatVisitor((SchemaRepository) null, dbType);
    }

    public static SchemaStatVisitor createSchemaStatVisitor(SchemaRepository repository) {
        return createSchemaStatVisitor(repository, repository.getDbType());
    }

    public static SchemaStatVisitor createSchemaStatVisitor(SchemaRepository repository, DbType dbType) {
        if (repository == null) {
            repository = new SchemaRepository(dbType);
        }

        if (dbType == null) {
            return new SchemaStatVisitor(repository);
        }

        switch (dbType) {
            case oracle:
                return new OracleSchemaStatVisitor(repository);
            case mysql:
            case mariadb:
            case elastic_search:
                return new MySqlSchemaStatVisitor(repository);
            case postgresql:
                return new PGSchemaStatVisitor(repository);
            case sqlserver:
            case jtds:
                return new SQLServerSchemaStatVisitor(repository);
            case db2:
                return new DB2SchemaStatVisitor(repository);
            case odps:
                return new OdpsSchemaStatVisitor(repository);
            case h2:
                return new H2SchemaStatVisitor(repository);
            case hive:
                return new HiveSchemaStatVisitor(repository);
            case antspark:
                return new AntsparkSchemaStatVisitor(repository);
            case clickhouse:
                return new ClickSchemaStatVisitor(repository);
            default:
                return new SchemaStatVisitor(repository);
        }
    }

    public static List<SQLStatement> parseStatements(String sql, String dbType, SQLParserFeature... features) {
        return parseStatements(sql, DbType.of(dbType), features);
    }

    public static List<SQLStatement> parseStatements(String sql, DbType dbType, SQLParserFeature... features) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);
        List<SQLStatement> stmtList = new ArrayList<SQLStatement>();
        parser.parseStatementList(stmtList, -1, null);
        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("syntax error : " + sql);
        }
        return stmtList;
    }

    public static List<SQLStatement> parseStatements(String sql, DbType dbType, boolean keepComments) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, keepComments);
        List<SQLStatement> stmtList = parser.parseStatementList();
        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("syntax error. " + sql);
        }
        return stmtList;
    }

    public static List<SQLStatement> parseStatements(String sql, String dbType) {
        return parseStatements(sql, dbType, new SQLParserFeature[0]);
    }


    public static List<SQLStatement> parseStatements(String sql, DbType dbType) {
        return parseStatements(sql, dbType, new SQLParserFeature[0]);
    }

    public static SQLStatement parseSingleStatement(String sql, DbType dbType, boolean keepComments) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, keepComments);
        List<SQLStatement> stmtList = parser.parseStatementList();

        if (stmtList.size() > 1) {
            throw new ParserException("multi-statement be found.");
        }

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("syntax error. " + sql);
        }
        return stmtList.get(0);
    }

    public static SQLStatement parseSingleStatement(String sql, String dbType, SQLParserFeature... features) {
        return parseSingleStatement(sql, DbType.of(dbType), features);
    }

    public static SQLStatement parseSingleStatement(String sql, DbType dbType, SQLParserFeature... features) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);
        List<SQLStatement> stmtList = parser.parseStatementList();

        if (stmtList.size() > 1) {
            throw new ParserException("multi-statement be found.");
        }

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("syntax error. " + sql);
        }
        return stmtList.get(0);
    }

    public static SQLStatement parseSingleMysqlStatement(String sql) {
        return parseSingleStatement(sql, DbType.mysql, false);
    }

    /**
     * @author owenludong.lud
     * @param columnName
     * @param tableAlias
     * @param pattern if pattern is null,it will be set {%Y-%m-%d %H:%i:%s} as mysql default value and set {yyyy-mm-dd
     * hh24:mi:ss} as oracle default value
     * @param dbType {@link DbType} if dbType is null ,it will be set the mysql as a default value
     */
    public static String buildToDate(String columnName, String tableAlias, String pattern, DbType dbType) {
        StringBuilder sql = new StringBuilder();
        if (StringUtils.isEmpty(columnName)) return "";
        if (dbType == null) {
            dbType = DbType.mysql;
        }
        String formatMethod = "";
        if (DbType.mysql == dbType) {
            formatMethod = "STR_TO_DATE";
            if (StringUtils.isEmpty(pattern)) pattern = "%Y-%m-%d %H:%i:%s";
        } else if (DbType.oracle == dbType) {
            formatMethod = "TO_DATE";
            if (StringUtils.isEmpty(pattern)) pattern = "yyyy-mm-dd hh24:mi:ss";
        } else {
            return "";
            // expand date's handle method for other database
        }
        sql.append(formatMethod).append("(");
        if (!StringUtils.isEmpty(tableAlias)) sql.append(tableAlias).append(".");
        sql.append(columnName).append(",");
        sql.append("'");
        sql.append(pattern);
        sql.append("')");
        return sql.toString();
    }

    public static List<SQLExpr> split(SQLBinaryOpExpr x) {
        return SQLBinaryOpExpr.split(x);
    }

    public static String translateOracleToMySql(String sql) {
        List<SQLStatement> stmtList = toStatementList(sql, DbType.oracle);

        StringBuilder out = new StringBuilder();
        OracleToMySqlOutputVisitor visitor = new OracleToMySqlOutputVisitor(out, false);
        for (int i = 0; i < stmtList.size(); ++i) {
            stmtList.get(i).accept(visitor);
        }

        String mysqlSql = out.toString();
        return mysqlSql;

    }

    public static String addCondition(String sql, String condition, DbType dbType) {
        String result = addCondition(sql, condition, SQLBinaryOperator.BooleanAnd, false, dbType);
        return result;
    }

    public static String addCondition(String sql, String condition, SQLBinaryOperator op, boolean left, DbType dbType) {
        if (sql == null) {
            throw new IllegalArgumentException("sql is null");
        }

        if (condition == null) {
            return sql;
        }

        if (op == null) {
            op = SQLBinaryOperator.BooleanAnd;
        }

        if (op != SQLBinaryOperator.BooleanAnd //
                && op != SQLBinaryOperator.BooleanOr) {
            throw new IllegalArgumentException("add condition not support : " + op);
        }

        List<SQLStatement> stmtList = parseStatements(sql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + sql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + sql);
        }

        SQLStatement stmt = stmtList.get(0);

        SQLExpr conditionExpr = toSQLExpr(condition, dbType);

        addCondition(stmt, op, conditionExpr, left);

        return toSQLString(stmt, dbType);
    }

    public static void addCondition(SQLStatement stmt, SQLBinaryOperator op, SQLExpr condition, boolean left) {
        if (stmt instanceof SQLSelectStatement) {
            SQLSelectQuery query = ((SQLSelectStatement) stmt).getSelect().getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                SQLExpr newCondition = buildCondition(op, condition, left, queryBlock.getWhere());
                queryBlock.setWhere(newCondition);
            } else {
                throw new IllegalArgumentException("add condition not support " + stmt.getClass().getName());
            }

            return;
        }

        if (stmt instanceof SQLDeleteStatement) {
            SQLDeleteStatement delete = (SQLDeleteStatement) stmt;

            SQLExpr newCondition = buildCondition(op, condition, left, delete.getWhere());
            delete.setWhere(newCondition);

            return;
        }

        if (stmt instanceof SQLUpdateStatement) {
            SQLUpdateStatement update = (SQLUpdateStatement) stmt;

            SQLExpr newCondition = buildCondition(op, condition, left, update.getWhere());
            update.setWhere(newCondition);

            return;
        }

        throw new IllegalArgumentException("add condition not support " + stmt.getClass().getName());
    }

    public static SQLExpr buildCondition(SQLBinaryOperator op, SQLExpr condition, boolean left, SQLExpr where) {
        if (where == null) {
            return condition;
        }

        SQLBinaryOpExpr newCondition;
        if (left) {
            newCondition = new SQLBinaryOpExpr(condition, op, where);
        } else {
            newCondition = new SQLBinaryOpExpr(where, op, condition);
        }
        return newCondition;
    }

    public static String addSelectItem(String selectSql, String expr, String alias, DbType dbType) {
        return addSelectItem(selectSql, expr, alias, false, dbType);
    }

    public static String addSelectItem(String selectSql, String expr, String alias, boolean first, DbType dbType) {
        List<SQLStatement> stmtList = parseStatements(selectSql, dbType);

        if (stmtList.size() == 0) {
            throw new IllegalArgumentException("not support empty-statement :" + selectSql);
        }

        if (stmtList.size() > 1) {
            throw new IllegalArgumentException("not support multi-statement :" + selectSql);
        }

        SQLStatement stmt = stmtList.get(0);

        SQLExpr columnExpr = toSQLExpr(expr, dbType);

        addSelectItem(stmt, columnExpr, alias, first);

        return toSQLString(stmt, dbType);
    }

    public static void addSelectItem(SQLStatement stmt, SQLExpr expr, String alias, boolean first) {
        if (expr == null) {
            return;
        }

        if (stmt instanceof SQLSelectStatement) {
            SQLSelectQuery query = ((SQLSelectStatement) stmt).getSelect().getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                addSelectItem(queryBlock, expr, alias, first);
            } else {
                throw new IllegalArgumentException("add condition not support " + stmt.getClass().getName());
            }

            return;
        }

        throw new IllegalArgumentException("add selectItem not support " + stmt.getClass().getName());
    }

    public static void addSelectItem(SQLSelectQueryBlock queryBlock, SQLExpr expr, String alias, boolean first) {
        SQLSelectItem selectItem = new SQLSelectItem(expr, alias);
        queryBlock.getSelectList().add(selectItem);
        selectItem.setParent(selectItem);
    }

    public static class FormatOption {
        private int features = VisitorFeature.of(VisitorFeature.OutputUCase
                , VisitorFeature.OutputPrettyFormat);

        public FormatOption() {

        }

        public FormatOption(VisitorFeature... features) {
            this.features = VisitorFeature.of(features);
        }

        public FormatOption(boolean ucase) {
            this(ucase, true);
        }

        public FormatOption(boolean ucase, boolean prettyFormat) {
            this(ucase, prettyFormat, false);
        }

        public FormatOption(boolean ucase, boolean prettyFormat, boolean parameterized) {
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputUCase, ucase);
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputPrettyFormat, prettyFormat);
            this.features = VisitorFeature.config(this.features, VisitorFeature.OutputParameterized, parameterized);
        }

        public boolean isDesensitize() {
            return isEnabled(VisitorFeature.OutputDesensitize);
        }

        public void setDesensitize(boolean val) {
            config(VisitorFeature.OutputDesensitize, val);
        }

        public boolean isUppCase() {
            return isEnabled(VisitorFeature.OutputUCase);
        }

        public void setUppCase(boolean val) {
            config(VisitorFeature.OutputUCase, val);
        }

        public boolean isPrettyFormat() {
            return isEnabled(VisitorFeature.OutputPrettyFormat);
        }

        public void setPrettyFormat(boolean prettyFormat) {
            config(VisitorFeature.OutputPrettyFormat, prettyFormat);
        }

        public boolean isParameterized() {
            return isEnabled(VisitorFeature.OutputParameterized);
        }

        public void setParameterized(boolean parameterized) {
            config(VisitorFeature.OutputParameterized, parameterized);
        }

        public void config(VisitorFeature feature, boolean state) {
            features = VisitorFeature.config(features, feature, state);
        }

        public final boolean isEnabled(VisitorFeature feature) {
            return VisitorFeature.isEnabled(this.features, feature);
        }
    }

    public static String refactor(String sql, DbType dbType, Map<String, String> tableMapping) {
        List<SQLStatement> stmtList = parseStatements(sql, dbType);
        return SQLUtils.toSQLString(stmtList, dbType, null, null, tableMapping);
    }

    public static long hash(String sql, DbType dbType) {
        Lexer lexer = SQLParserUtils.createLexer(sql, dbType);

        StringBuilder buf = new StringBuilder(sql.length());

        for (;;) {
            lexer.nextToken();

            Token token = lexer.token();
            if (token == Token.EOF) {
                break;
            }

            if (token == Token.ERROR) {
                return Utils.fnv_64(sql);
            }

            if (buf.length() != 0) {

            }
        }

        return buf.hashCode();
    }

    public static SQLExpr not(SQLExpr expr) {
        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
            SQLBinaryOperator op = binaryOpExpr.getOperator();

            SQLBinaryOperator notOp = null;

            switch (op){
                case Equality:
                    notOp = SQLBinaryOperator.LessThanOrGreater;
                    break;
                case LessThanOrEqualOrGreaterThan:
                    notOp = SQLBinaryOperator.Equality;
                    break;
                case LessThan:
                    notOp = SQLBinaryOperator.GreaterThanOrEqual;
                    break;
                case LessThanOrEqual:
                    notOp = SQLBinaryOperator.GreaterThan;
                    break;
                case GreaterThan:
                    notOp = SQLBinaryOperator.LessThanOrEqual;
                    break;
                case GreaterThanOrEqual:
                    notOp = SQLBinaryOperator.LessThan;
                    break;
                case Is:
                    notOp = SQLBinaryOperator.IsNot;
                    break;
                case IsNot:
                    notOp = SQLBinaryOperator.Is;
                    break;
                default:
                    break;
            }


            if (notOp != null) {
                return new SQLBinaryOpExpr(binaryOpExpr.getLeft(), notOp, binaryOpExpr.getRight());
            }
        }

        if (expr instanceof SQLInListExpr) {
            SQLInListExpr inListExpr = (SQLInListExpr) expr;

            SQLInListExpr newInListExpr = new SQLInListExpr(inListExpr);
            newInListExpr.getTargetList().addAll(inListExpr.getTargetList());
            newInListExpr.setNot(!inListExpr.isNot());
            return newInListExpr;
        }

        return new SQLUnaryExpr(SQLUnaryOperator.Not, expr);
    }

    public static String normalize(String name) {
        return normalize(name, null);
    }

    public static String normalize(String name, boolean isTrimmed) {
        return _normalize(name, null, false, isTrimmed);
    }

    public static String normalize(String name, DbType dbType) {
        return _normalize(name, dbType, false);
    }

    private static String _normalize(String name, DbType dbType, boolean isForced) {
        return _normalize(name, dbType, isForced, true);
    }

    private static String _normalize(String name, DbType dbType, boolean isForced, boolean isTrimmed) {
        if (name == null) {
            return null;
        }

        if (name.length() > 2) {
            char c0 = name.charAt(0);
            char x0 = name.charAt(name.length() - 1);
            if ((c0 == '"' && x0 == '"') || (c0 == '`' && x0 == '`') || (c0 == '\'' && x0 == '\'')) {
                String normalizeName = name.substring(1, name.length() - 1);

                if (isTrimmed) {
                    normalizeName = normalizeName.trim();
                }

                int dotIndex = normalizeName.indexOf('.');
                if (dotIndex > 0) {
                    if (c0 == '`') {
                        normalizeName = normalizeName.replaceAll("`\\.`", ".");
                    }
                }

                if(!isForced) {
                    if (DbType.oracle == dbType) {
                        if (OracleUtils.isKeyword(normalizeName)) {
                            return name;
                        }
                    } else if (DbType.mysql == dbType) {
                        if (MySqlUtils.isKeyword(normalizeName)) {
                            return name;
                        }
                    } else if (DbType.postgresql == dbType || DbType.db2 == dbType) {
                        if (PGUtils.isKeyword(normalizeName)) {
                            return name;
                        }
                    }
                }

                return normalizeName;
            }
        }

        return name;
    }

    public static String forcedNormalize(String name, DbType dbType) {
        return _normalize(name, dbType, true);
    }

    public static boolean nameEquals(SQLName a, SQLName b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.nameHashCode64() == b.nameHashCode64();
    }

    public static boolean nameEquals(String a, String b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.equalsIgnoreCase(b)) {
            return true;
        }

        String normalize_a = normalize(a);
        String normalize_b = normalize(b);

        return normalize_a.equalsIgnoreCase(normalize_b);
    }

    public static boolean isValue(SQLExpr expr) {
        if (expr instanceof SQLLiteralExpr) {
            return true;
        }

        if (expr instanceof SQLVariantRefExpr) {
            return true;
        }

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
            SQLBinaryOperator op = binaryOpExpr.getOperator();
            if (op == SQLBinaryOperator.Add
                    || op == SQLBinaryOperator.Subtract
                    || op == SQLBinaryOperator.Multiply) {
                return isValue(binaryOpExpr.getLeft())
                        && isValue(binaryOpExpr.getRight());
            }
        }

        return false;
    }

    public static boolean replaceInParent(SQLExpr expr, SQLExpr target) {
        if (expr == null) {
            return false;
        }

        SQLObject parent = expr.getParent();

        if (parent instanceof SQLReplaceable) {
            return ((SQLReplaceable) parent).replace(expr, target);
        }

        return false;
    }

    public static boolean replaceInParent(SQLTableSource cmp, SQLTableSource dest) {
        if (cmp == null) {
            return false;
        }

        SQLObject parent = cmp.getParent();

        if (parent instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) parent;
            if (queryBlock.getFrom() == cmp) {
                queryBlock.setFrom(dest);
                return true;
            }
        }

        if (parent instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) parent;
            return join.replace(cmp, dest);
        }

        return false;
    }

    public static boolean replaceInParent(SQLSelectQuery cmp, SQLSelectQuery dest) {
        if (cmp == null) {
            return false;
        }

        SQLObject parent = cmp.getParent();
        if (parent == null) {
            return false;
        }


        if (parent instanceof SQLUnionQuery) {
            return ((SQLUnionQuery) parent).replace(cmp, dest);
        }

        if (parent instanceof SQLSelect) {
            return ((SQLSelect) parent).replace(cmp, dest);
        }
        return false;
    }

    public static String desensitizeTable(String tableName) {
        if (tableName == null) {
            return null;
        }

        tableName = normalize(tableName);
        long hash = FnvHash.hashCode64(tableName);
        return Utils.hex_t(hash);
    }

    /**
     * 重新排序建表语句，解决建表语句的依赖关系
     * @param sql
     * @param dbType
     */
    public static String sort(String sql, DbType dbType) {
        List stmtList = SQLUtils.parseStatements(sql, DbType.oracle);
        SQLCreateTableStatement.sort(stmtList);
        return SQLUtils.toSQLString(stmtList, dbType);
    }

    /**
     *
     * @param query
     * @param dbType
     * @return  0：sql.toString, 1:
     */
    public static Object[] clearLimit(String query, DbType dbType) {
        List stmtList = SQLUtils.parseStatements(query, dbType);

        SQLLimit limit = null;

        SQLStatement statement = (SQLStatement) stmtList.get(0);

        if (statement instanceof SQLSelectStatement) {
            SQLSelectStatement selectStatement = (SQLSelectStatement) statement;

            if (selectStatement.getSelect().getQuery() instanceof SQLSelectQueryBlock) {
                limit = clearLimit(selectStatement.getSelect().getQueryBlock());
            }
        }

        if (statement instanceof SQLDumpStatement) {
            SQLDumpStatement dumpStatement = (SQLDumpStatement) statement;

            if (dumpStatement.getSelect().getQuery() instanceof SQLSelectQueryBlock) {
                limit = clearLimit(dumpStatement.getSelect().getQueryBlock());
            }
        }

        if (statement instanceof MySqlSelectIntoStatement) {
            MySqlSelectIntoStatement sqlSelectIntoStatement = (MySqlSelectIntoStatement) statement;
            limit = clearLimit(sqlSelectIntoStatement.getSelect().getQueryBlock());
        }

        if (statement instanceof MySqlInsertStatement) {
            MySqlInsertStatement insertStatement = (MySqlInsertStatement) statement;
            limit = clearLimit(insertStatement.getQuery().getQueryBlock());
        }

        String sql = SQLUtils.toSQLString(stmtList, dbType);
        return new Object[]{ sql, limit};
    }

    private static SQLLimit clearLimit(SQLSelectQueryBlock queryBlock) {
        if (queryBlock == null) {
            return null;
        }

        SQLLimit limit = queryBlock.getLimit();
        queryBlock.setLimit(null);
        return limit;
    }

    public static SQLLimit getLimit(SQLStatement statement, DbType dbType) {
        if (statement instanceof SQLSelectStatement) {
            SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) statement).getSelect().getQueryBlock();
            return queryBlock == null ? null : queryBlock.getLimit();
        } else if (statement instanceof SQLDumpStatement) {
            SQLSelectQueryBlock queryBlock = ((SQLDumpStatement) statement).getSelect().getQueryBlock();
            return queryBlock == null ? null : queryBlock.getLimit();
        } else if (statement instanceof MySqlSelectIntoStatement) {
            SQLSelectQueryBlock queryBlock = ((MySqlSelectIntoStatement) statement).getSelect().getQueryBlock();
            return queryBlock == null ? null : queryBlock.getLimit();
        } else if (statement instanceof MySqlInsertStatement) {
            SQLSelect select = ((MySqlInsertStatement) statement).getQuery();

            if (select == null) {
                return null;
            }

            if (select.getQuery() instanceof SQLUnionQuery) {
                return ((SQLUnionQuery) select.getQuery()).getLimit();
            } else {
                return select.getQueryBlock().getLimit();
            }

        } else {
            return null;
        }
    }

    public static SQLLimit getLimit(String query, DbType dbType) {
        List stmtList = SQLUtils.parseStatements(query, dbType);
        SQLStatement statement = (SQLStatement) stmtList.get(0);
        return getLimit(statement, dbType);
    }

    public static String convertTimeZone(String sql, TimeZone from, TimeZone to) {
        SQLStatement statement = parseSingleMysqlStatement(sql);
        statement.accept(new TimeZoneVisitor(from, to));
        return statement.toString();
    }

    public static SQLStatement convertTimeZone(SQLStatement stmt, TimeZone from, TimeZone to) {
        stmt.accept(new TimeZoneVisitor(from, to));
        return stmt;
    }

    public static List<SQLInsertStatement> splitInsertValues(DbType dbType, String insertSql, int size) {
        SQLStatement statement = SQLUtils.parseStatements(insertSql, dbType, false).get(0);
        if (!(statement instanceof SQLInsertStatement)) {
            throw new IllegalArgumentException("The SQL must be insert statement.");
        }

        List<SQLInsertStatement> insertLists = new ArrayList<SQLInsertStatement>();

        SQLInsertStatement insertStatement = (SQLInsertStatement) statement;

        List<SQLInsertStatement.ValuesClause> valuesList = insertStatement.getValuesList();

        int totalSize = valuesList.size();
        if (totalSize <= size) {
            insertLists.add(insertStatement);
        } else {
            SQLInsertStatement insertTemplate = new SQLInsertStatement();

            insertStatement.cloneTo(insertTemplate);
            insertTemplate.getValuesList().clear();

            int batchCount = 0;
            if( totalSize % size == 0) {
                batchCount = totalSize / size;
            } else {
                batchCount = (totalSize / size) + 1;
            }
            for (int i = 0; i < batchCount; i++){
                SQLInsertStatement subInsertStatement = new SQLInsertStatement();
                insertTemplate.cloneTo(subInsertStatement);

                int fromIndex = i * size;
                int toIndex = (fromIndex + size) > totalSize ? totalSize : fromIndex + size;
                List<SQLInsertStatement.ValuesClause> subValuesList = valuesList.subList(fromIndex, toIndex);
                subInsertStatement.getValuesList().addAll(subValuesList);

                insertLists.add(subInsertStatement);
            }
        }

        return insertLists;
    }

    static class TimeZoneVisitor extends SQLASTVisitorAdapter {

        private TimeZone from;
        private TimeZone to;

        public TimeZoneVisitor(TimeZone from, TimeZone to) {
            this.from = from;
            this.to = to;
        }

        public boolean visit(SQLTimestampExpr x) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String newTime = format.format(x.getDate(from));

            x.setLiteral(newTime);

            return true;
        }

    }
}

