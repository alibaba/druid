package com.alibaba.druid.filter.mysql8datetime;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 针对mysql jdbc 8.0.23及以上版本，如果调用方没有使用orm框架，而是直接调用ResultSet的getObject方法，则针对DATETIME类型的字段，得到的对象从TimeStamp类型变成了LocalDateTime类型，导致调用方出现类型转换异常
 * 通过Filter控制将对象类型转换成原来的类型
 *
 * @author lizongbo
 * @see <a href="https://dev.mysql.com/doc/relnotes/connector-j/8.0/en/news-8-0-23.html">MySQL 8.0.23 更新说明</a>
 */
public class MySQL8DateTimeSqlTypeFilter extends FilterAdapter {
    /**
     * 针对mysql jdbc 8.0.23及以上版本，通过该方法控制将对象类型转换成原来的类型
     *
     * @param chain
     * @param result
     * @param columnIndex
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(int)
     */
    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return getObjectReplaceLocalDateTime(super.resultSet_getObject(chain, result, columnIndex));
    }

    /**
     * 针对mysql jdbc 8.0.23及以上版本，通过该方法控制将对象类型转换成原来的类型
     *
     * @param chain
     * @param result
     * @param columnLabel
     * @return
     * @throws SQLException
     * @see java.sql.ResultSet#getObject(String)
     */
    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return getObjectReplaceLocalDateTime(super.resultSet_getObject(chain, result, columnLabel));
    }

    /**
     * 针对mysql jdbc 8.0.23及以上版本，通过该方法控制将对象类型转换成原来的类型
     *
     * @param obj
     * @return
     */
    public static Object getObjectReplaceLocalDateTime(Object obj) {
        if (!(obj instanceof LocalDateTime)) {
            return obj;
        }
        // 针对升级到了mysql jdbc 8.0.23以上的情况，转换回老的兼容类型
        return Timestamp.valueOf((LocalDateTime) obj);
    }
}
