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
package com.alibaba.druid.sql;

import java.util.List;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;


public class SQLUtils {

    private final static Log LOG = LogFactory.getLog(SQLUtils.class);

    public static String toSQLString(SQLObject sqlObject, String dbType) {
        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
            return toMySqlString(sqlObject);
        }

        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return toOracleString(sqlObject);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return toPGString(sqlObject);
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return toDB2String(sqlObject);
        }

        return toSQLServerString(sqlObject);
    }

    public static String toSQLString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new SQLASTOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toMySqlString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new MySqlOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static SQLExpr toMySqlExpr(String sql) {
        return toSQLExpr(sql, JdbcUtils.MYSQL);
    }

    public static String formatMySql(String sql) {
        return format(sql, JdbcUtils.MYSQL);
    }

    public static String formatOracle(String sql) {
        return format(sql, JdbcUtils.ORACLE);
    }

    public static String toOracleString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new OracleOutputVisitor(out, false));

        String sql = out.toString();
        return sql;
    }

    public static String toPGString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new PGOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }
    
    public static String toDB2String(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new DB2OutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String toSQLServerString(SQLObject sqlObject) {
        StringBuilder out = new StringBuilder();
        sqlObject.accept(new SQLServerOutputVisitor(out));

        String sql = out.toString();
        return sql;
    }

    public static String formatPGSql(String sql) {
        return format(sql, JdbcUtils.POSTGRESQL);
    }

    public static SQLExpr toSQLExpr(String sql, String dbType) {
        SQLExprParser parser = SQLParserUtils.createExprParser(sql, dbType);
        SQLExpr expr = parser.expr();

        if (parser.getLexer().token() != Token.EOF) {
            throw new ParserException("illegal sql expr : " + sql);
        }

        return expr;
    }

    public static List<SQLStatement> toStatementList(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        return parser.parseStatementList();
    }

    public static SQLExpr toSQLExpr(String sql) {
        return toSQLExpr(sql, null);
    }

    public static String format(String sql, String dbType) {
        return format(sql, dbType, null);
    }

    public static String format(String sql, String dbType, List<Object> parameters) {
        try {
            List<SQLStatement> statementList = toStatementList(sql, dbType);

            return toSQLString(statementList, dbType, parameters);
        } catch (ParserException ex) {
            LOG.warn("format error", ex);
            return sql;
        }
    }

    public static String toSQLString(List<SQLStatement> statementList, String dbType) {
        return toSQLString(statementList, dbType, null);
    }

    public static String toSQLString(List<SQLStatement> statementList, String dbType, List<Object> parameters) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = createFormatOutputVisitor(out, statementList, dbType);
        if (parameters != null) {
            visitor.setParameters(parameters);
        }

        for (SQLStatement stmt : statementList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static SQLASTOutputVisitor createFormatOutputVisitor(Appendable out, List<SQLStatement> statementList,
                                                                String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            if (statementList.size() == 1) {
                return new OracleOutputVisitor(out, false);
            } else {
                return new OracleOutputVisitor(out, true);
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
            return new MySqlOutputVisitor(out);
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGOutputVisitor(out);
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerOutputVisitor(out);
        }

        if (JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerOutputVisitor(out);
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2OutputVisitor(out);
        }

        return new SQLASTOutputVisitor(out);
    }

    public static SchemaStatVisitor createSchemaStatVisitor(List<SQLStatement> statementList, String dbType) {
        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            if (statementList.size() == 1) {
                return new OracleSchemaStatVisitor();
            } else {
                return new OracleSchemaStatVisitor();
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
            return new MySqlSchemaStatVisitor();
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGSchemaStatVisitor();
        }

        if (JdbcUtils.SQL_SERVER.equals(dbType)) {
            return new SQLServerSchemaStatVisitor();
        }

        if (JdbcUtils.JTDS.equals(dbType)) {
            return new SQLServerSchemaStatVisitor();
        }
        
        if (JdbcUtils.DB2.equals(dbType)) {
            return new DB2SchemaStatVisitor();
        }

        return new SchemaStatVisitor();
    }

    public static List<SQLStatement> parseStatements(String sql, String dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        if (parser.getLexer().token() != Token.EOF) {
            throw new DruidRuntimeException("syntax error : " + sql);
        }
        return stmtList;
    }
	
	 /**
     * @author owenludong.lud
     * @param  columnName
     * @param  tableAlias 
     * @param  pattern if pattern is null,it will be set {%Y-%m-%d %H:%i:%s} as mysql default value and set {yyyy-mm-dd hh24:mi:ss} as oracle default value
     * @param  dbType  {@link JdbcConstants} if dbType is null ,it will be set the mysql as a default value
     */
    public static String buildToDate(String columnName,String tableAlias,String pattern,String dbType){    	
     	StringBuilder sql = new StringBuilder();    	
     	if(StringUtils.isEmpty(columnName))
     		return "";  			
     	if(StringUtils.isEmpty(dbType))    dbType = JdbcConstants.MYSQL;   
     	String formatMethod = "";
     	if(JdbcConstants.MYSQL.equalsIgnoreCase(dbType)){
     		formatMethod = "STR_TO_DATE";
     		if(StringUtils.isEmpty(pattern)) pattern = "%Y-%m-%d %H:%i:%s";
     	}else if(JdbcConstants.ORACLE.equalsIgnoreCase(dbType)){
     		formatMethod = "TO_DATE";
     		if(StringUtils.isEmpty(pattern)) pattern = "yyyy-mm-dd hh24:mi:ss";
     	}else{     		
     		return "";
     		//expand date's handle method for other database 
     	}
     	sql.append(formatMethod).append("(");        	
 		if(!StringUtils.isEmpty(tableAlias))
 			sql.append(tableAlias).append("."); 
 		sql.append(columnName).append(",");
 		sql.append("'");
 		sql.append(pattern);
 		sql.append("')");     	   	
     	return sql.toString();
     }
}
