package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.support.json.JSONUtils;
import com.aliyun.odps.udf.UDF;

import java.util.ArrayList;
import java.util.List;

public class SqlParams extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) {
        return evaluate(sql, dbTypeName, false);
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) {
        try {
            DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);
            List<Object> outParameters = new ArrayList<Object>();
            ParameterizedOutputVisitorUtils.parameterize(sql, dbType, outParameters);
            return JSONUtils.toJSONString(outParameters);
        } catch (ParserException ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            return null;
        }
    }
}
