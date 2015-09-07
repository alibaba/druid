package com.alibaba.druid.support.opds.udf;

import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.aliyun.odps.udf.UDF;

public class ExportTables extends UDF {

    public String evaluate(String sql) {
        return evaluate(sql, null);
    }

    public String evaluate(String sql, String dbType) {
        try {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            StringBuffer buf = new StringBuffer();

            for (Map.Entry<TableStat.Name, TableStat> entry : visitor.getTables().entrySet()) {
                TableStat.Name name = entry.getKey();

                if (buf.length() != 0) {
                    buf.append(',');
                }
                buf.append(name.toString());
            }

            return buf.toString();
        } catch (Throwable ex) {
            System.err.println("error sql : " + sql);
            ex.printStackTrace();
            return null;
        }
    }
}
