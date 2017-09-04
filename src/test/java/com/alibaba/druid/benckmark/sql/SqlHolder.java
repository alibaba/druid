package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
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
    public SQLStatement ast;

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

    public String getSqlItems(String db) {
        Map<String, LinkedHashSet<String>> itemMap = new HashMap<String, LinkedHashSet<String>>();
        try {
            SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
            ast.accept(schemaStatVisitor);
            List<TableStat.Condition> conditionList = schemaStatVisitor.getConditions();
            for (TableStat.Condition condition : conditionList) {
                String tableName = condition.getColumn().getTable();
                LinkedHashSet<String> condLinkedSet = itemMap.get(tableName);
                if (condLinkedSet == null) {
                    condLinkedSet = new LinkedHashSet<String>();
                    itemMap.put(tableName, condLinkedSet);
                }
                TableStat.Column column = condition.getColumn();
                String upperClnName = column.toString().toUpperCase();
                String finalClnName = filterChar(db, upperClnName);
                condLinkedSet.add(finalClnName);
            }
        } catch (Exception ex) {
        }
        if (itemMap.isEmpty())
            return null;
        StringBuilder resSb = new StringBuilder();
        for (Map.Entry<String, LinkedHashSet<String>> entry : itemMap.entrySet()) {
            resSb.append(StringUtils.join(entry.getValue(), ",")).append(",");
        }
        return resSb.substring(0, resSb.length() - 1);
    }

    public static String filterChar(String db, String name) {
        String resName;
        if (StringUtils.isNotBlank(name)) {
            String[] names = name.split("\\.");
            StringBuilder nameSb = new StringBuilder();
            boolean isFirst = true;
            int size = names.length;
            int k = 0;
            for (String n : names) {
                String tempN = n;
                if (n.startsWith("`") && n.endsWith("`")) {
                    tempN = n.substring(1, n.length() - 1);
                }
                if (k == size - 1) {
                    nameSb.append(tempN).append(".");
                } else if (isFirst) {
                    if (!n.startsWith(db)) {
                        nameSb.append(convert(tempN)).append(".");
                    }
                    isFirst = false;
                } else {
                    nameSb.append(convert(tempN)).append(".");
                }
                k++;
            }
            resName = nameSb.substring(0, nameSb.length() - 1);
        } else {
            resName = name;
        }
        return db + "." + resName;
    }

    public static String convert(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return tableName;
        }
        int len = tableName.length();
        int k = -1;
        int min = Math.min(4, len);
        for (int i = 1; i <= min; i++) {
            String ch = String.valueOf(tableName.charAt(len - i));
            boolean isNum = StringUtils.isNumeric(ch);
            if (isNum) {
                k = i;
            } else {
                break;
            }
        }

        if (k != -1) {
            tableName = tableName.substring(0, len - k);
            if (tableName.endsWith("_")) {
                tableName = tableName.substring(0, tableName.length() - 1);
            }
        }
        int idx = tableName.lastIndexOf("_");
        if (idx == -1 || (tableName.length() - 1 == idx)) {
            return tableName;
        }
        String num = tableName.substring(idx + 1);
        boolean isNum = StringUtils.isNumeric(num);
        if (isNum) {
            return tableName.substring(0, idx);
        }
        return tableName;
    }
}
