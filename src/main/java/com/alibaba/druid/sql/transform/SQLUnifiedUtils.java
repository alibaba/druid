package com.alibaba.druid.sql.transform;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.StringUtils;

import static com.alibaba.druid.sql.parser.SQLParserFeature.EnableSQLBinaryOpExprGroup;

/**
 * @author lijun.cailj 2020/5/8
 */
public class SQLUnifiedUtils {

    public static long unifyHash(String sql, DbType type) {
        String unifySQL = unifySQL(sql, DbType.mysql);
        return FnvHash.fnv1a_64_lower(unifySQL);
    }

    public static String unifySQL(String sql, DbType type) {
        if (StringUtils.isEmpty(sql)) {
            throw new IllegalArgumentException("sql is empty.");
        }

        SQLType sqlType = SQLParserUtils.getSQLType(sql, DbType.mysql);

        String parameterizeSQL = null;
        switch (sqlType) {
            case INSERT:
            case UPDATE:
            case SELECT:
            case DELETE:
                parameterizeSQL = ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql);
                SQLStatement stmt = SQLUtils.parseSingleStatement(parameterizeSQL, DbType.mysql, EnableSQLBinaryOpExprGroup);
                stmt.accept(new SQLUnifiedVisitor());
                return SQLUtils.toMySqlString(stmt);
            default:
                return ParameterizedOutputVisitorUtils.parameterize(sql, DbType.mysql);
        }
    }
}
