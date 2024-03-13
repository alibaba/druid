package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.aliyun.odps.udf.UDF;

public class SqlTypeUDF extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) {
        return evaluate(sql, dbTypeName, false);
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) {
        if (sql == null || sql.isEmpty()) {
            return null;
        }

        try {
            DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);
            Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
            SQLType sqlType = lexer.scanSQLTypeV2();
            return sqlType != null ? sqlType.name() : "null";
        } catch (ParserException ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            return null;
        }
    }
}
