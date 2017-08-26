package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by kaiwang.ckw on 15/05/2017.
 */
public class SqlHolder {
    private String text;
    private String dialect;

    private boolean parsed;
    private SQLStatement ast;

    public static SqlHolder of(String sql) {
        return new SqlHolder(sql);
    }

    SqlHolder(String text) {
        this(text, JdbcConstants.MYSQL);
    }

    SqlHolder(String text, String dialect) {
        if (!"mysql".equalsIgnoreCase(dialect)) {
            throw new IllegalArgumentException("only mysql is");
        }

        this.text = text;
        this.dialect = dialect;
    }

    public String format() {
        try {
            return SQLUtils.format(text, dialect);
        } catch (ParserException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void ensureParsed() {
        if (parsed) {
            return;
        }
        // ast = SQLUtils.parseStatements(text, dialect).get(0);
        try {
            ast = new MySqlStatementParser(text, SQLParserFeature.EnableSQLBinaryOpExprGroup).parseStatement();
        } catch (ParserException e) {
            throw new UnsupportedOperationException(e);
        }
        parsed = true;
    }

    // returns rewritten sql, or original string object if not rewritten
    public String select() {
        ensureParsed();

        SQLStatement stmt = ast;
        if (stmt instanceof SQLSelectStatement) {
            boolean rewritten = StatementConverter.rewriteSelect((SQLSelectStatement) stmt);
            if (rewritten) {
                return SQLUtils.toMySqlString(stmt);
            } else {
                return text;
            }
        } else {
            SQLSelectStatement selectStatement = StatementConverter.rewrite(stmt);
            if (stmt == selectStatement) {
                return text;
            } else {
                return SQLUtils.toMySqlString(selectStatement);
            }
        }
    }

    public String parameterize() {
        return parameterize(null, null);
    }

    public String parameterize(Set<String> physicalNames) {
        ensureParsed();
        return Templates.parameterize(ast, physicalNames, null);
    }

    public String parameterize(Set<String> physicalNames, List<Object> params) {
        ensureParsed();
        return Templates.parameterize(ast, physicalNames, params);
    }


    public String getParams() {
        ensureParsed();
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        ast.accept(visitor);
        String params = JSONArray.toJSONString(parameters, SerializerFeature.WriteClassName);
        params = StringUtils.replace(params, "\"", "\\\"");
        return params;
    }
}
