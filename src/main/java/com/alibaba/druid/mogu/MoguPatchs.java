package com.alibaba.druid.mogu;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.DruidPooledStatement;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.support.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;


/**
 * @author by jiuru on 15/10/29.
 */
public class MoguPatchs {

    private static final Logger logger = LoggerFactory.getLogger(MoguPatchs.class);

    public static final String SLOW_SQL_MILLIS = "mogu.ds.slowSqlMillis";

    public static final String MAX_RESULT = "mogu.ds.maxResult";
    /**
     * 如果返回的结果集超过这个值，则打印日志
     */
    public static int maxResult = 200;

    public static void log(String msg, Object... objects) {
        logger.warn(msg, objects);
    }

    public static void logBigResultIfPossible(int fetchRowCount, DruidPooledStatement stmt) throws SQLException {
        if (fetchRowCount >= maxResult) {
            if (stmt instanceof DruidPooledPreparedStatement) {
                DruidPooledPreparedStatement dps = (DruidPooledPreparedStatement) stmt;
                DruidPooledPreparedStatement.PreparedStatementKey preparedStatementKey = dps.getKey();
                StatementProxy statementProxy = dps.unwrap(StatementProxy.class);
                String params = buildParameters(statementProxy);
                log("big result sql:{} {} params:{}", fetchRowCount, dps.getSql(), params);
            }
        }
    }

    public static String buildParameters(StatementProxy statement) {
        JSONWriter out = new JSONWriter();

        out.writeArrayStart();
        for (int i = 0, parametersSize = statement.getParametersSize(); i < parametersSize; ++i) {
            JdbcParameter parameter = statement.getParameter(i);
            if (i != 0) {
                out.writeComma();
            }
            if (parameter == null) {
                continue;
            }

            Object value = parameter.getValue();
            if (value == null) {
                out.writeNull();
            } else if (value instanceof String) {
                String text = (String) value;
                if (text.length() > 100) {
                    out.writeString(text.substring(0, 97) + "...");
                } else {
                    out.writeString(text);
                }
            } else if (value instanceof Number) {
                out.writeObject(value);
            } else if (value instanceof java.util.Date) {
                out.writeObject(value);
            } else if (value instanceof Boolean) {
                out.writeObject(value);
            } else if (value instanceof InputStream) {
                out.writeString("<InputStream>");
            } else if (value instanceof NClob) {
                out.writeString("<NClob>");
            } else if (value instanceof Clob) {
                out.writeString("<Clob>");
            } else if (value instanceof Blob) {
                out.writeString("<Blob>");
            } else {
                out.writeString('<' + value.getClass().getName() + '>');
            }
        }
        out.writeArrayEnd();

        return out.toString();
    }
}
