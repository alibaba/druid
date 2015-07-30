package com.alibaba.druid.support.opds.udf;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.aliyun.odps.udf.UDF;

public class ExportColumns extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null);
    }
    
    public String evaluate(String sql, String dbType) {
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);
        
        for (SQLStatement stmt : statementList) {
            stmt.accept(visitor);
        }
        
        StringBuffer buf = new StringBuffer();
        
        for (TableStat.Column column : visitor.getColumns()) {
            if (buf.length() != 0) {
                buf.append(',');
            }
            buf.append(column.toString());
        }
        
        return buf.toString();
    }
}
