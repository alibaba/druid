package com.alibaba.druid.filter.mysql8datetime;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;

import java.sql.ResultSetMetaData;
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
     * @param chain  chain the FilterChain object that represents the filter chain
     * @param result the ResultSetProxy object that represents the result set
     * @param columnIndex the index of the column to retrieve
     * @return an Object holding the column value, or {@code null} if the value is SQL NULL
     * @throws SQLException if a database access error occurs or the columnIndex is invalid
     * @see java.sql.ResultSet#getObject(int)
     */
    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return getObjectReplaceLocalDateTime(super.resultSet_getObject(chain, result, columnIndex));
    }

    /**
     * 针对mysql jdbc 8.0.23及以上版本，通过该方法控制将对象类型转换成原来的类型
     *
     * @param chain   chain the FilterChain object that represents the filter chain
     * @param result  the ResultSetProxy object that represents the result set
     * @param columnLabel the label of the column to retrieve
     * @return an Object holding the column value, or {@code null} if the value is SQL NULL
     * @throws SQLException if a database access error occurs or the columnLabel is invalid
     * @see java.sql.ResultSet#getObject(String)
     */
    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return getObjectReplaceLocalDateTime(super.resultSet_getObject(chain, result, columnLabel));
    }

    /**
     * Replaces a LocalDateTime object with its equivalent Timestamp object.
     * If the input object is not an instance of LocalDateTime, it is returned as is.
     * This method is specifically designed to handle cases where upgrading to MySQL JDBC 8.0.23 or above
     * requires converting LocalDateTime objects back to the older compatible type.
     *
     * @param obj the object to be checked and possibly replaced
     * @return the replaced object if it is a LocalDateTime, or the original object otherwise
     */
    public static Object getObjectReplaceLocalDateTime(Object obj) {
        if (!(obj instanceof LocalDateTime)) {
            return obj;
        }
        // 针对升级到了mysql jdbc 8.0.23以上的情况，转换回老的兼容类型
        return Timestamp.valueOf((LocalDateTime) obj);
    }

    /**
     * Retrieves the metadata for the result set, including information about the columns and their properties.
     * This method wraps the original result set metadata with a custom implementation that handles MySQL 8.0.23 or above
     * compatibility for LocalDateTime objects.
     *
     * @param chain the FilterChain object that represents the filter chain
     * @param resultSet the ResultSetProxy object that represents the result set
     * @return a ResultSetMetaData object containing the metadata for the result set
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ResultSetMetaData resultSet_getMetaData(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return new MySQL8DateTimeResultSetMetaData(chain.resultSet_getMetaData(resultSet));
    }
}
