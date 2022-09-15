package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.aliyun.odps.udf.UDF;

public class SqlPattern extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) {
        return evaluate(sql, dbTypeName, false);
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) {
        try {
            DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);
            return ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        } catch (ParserException ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            return null;
        }
    }
}
