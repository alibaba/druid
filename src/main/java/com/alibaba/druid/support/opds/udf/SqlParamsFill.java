package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.support.json.JSONUtils;
import com.aliyun.odps.udf.UDF;

import java.util.List;

public class SqlParamsFill extends UDF {
    public String evaluate(String sql, String params) {
        return evaluate(sql, params, null, false);
    }

    public String evaluate(String sql, String params, String dbTypeName) {
        return evaluate(sql, params, dbTypeName, false);
    }

    public String evaluate(String sql, String params, String dbTypeName, boolean throwError) {
        try {
            DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);
            List<Object> inputParams = (List<Object>) JSONUtils.parse(params);
            return ParameterizedOutputVisitorUtils.restore(sql, dbType, inputParams);
        } catch (ParserException ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            return null;
        }
    }
}
