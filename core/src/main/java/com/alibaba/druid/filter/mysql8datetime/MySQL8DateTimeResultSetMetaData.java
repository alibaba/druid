package com.alibaba.druid.filter.mysql8datetime;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 针对mysql jdbc 8.0.23及以上版本，通过该方法控制将对象类型转换成原来的类型
 * @author lizongbo
 * @see <a href="https://dev.mysql.com/doc/relnotes/connector-j/8.0/en/news-8-0-24.html">...</a>
 */
public class MySQL8DateTimeResultSetMetaData implements ResultSetMetaData {
    private ResultSetMetaData resultSetMetaData;

    public MySQL8DateTimeResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        super();
        this.resultSetMetaData = resultSetMetaData;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return resultSetMetaData.unwrap(iface);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return resultSetMetaData.getColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return resultSetMetaData.isAutoIncrement(column);
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return resultSetMetaData.isCaseSensitive(column);
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return resultSetMetaData.isSearchable(column);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return resultSetMetaData.isWrapperFor(iface);
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return resultSetMetaData.isCurrency(column);
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return resultSetMetaData.isNullable(column);
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return resultSetMetaData.isSigned(column);
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return resultSetMetaData.getColumnDisplaySize(column);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return resultSetMetaData.getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return resultSetMetaData.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return resultSetMetaData.getSchemaName(column);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return resultSetMetaData.getPrecision(column);
    }

    @Override
    public int getScale(int column) throws SQLException {
        return resultSetMetaData.getScale(column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return resultSetMetaData.getTableName(column);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return resultSetMetaData.getCatalogName(column);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return resultSetMetaData.getColumnType(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return resultSetMetaData.getColumnTypeName(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return resultSetMetaData.isReadOnly(column);
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return resultSetMetaData.isWritable(column);
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return resultSetMetaData.isDefinitelyWritable(column);
    }

    /**
     * 针对8.0.24版本开始，如果把mysql DATETIME映射回Timestamp，就需要把javaClass的类型也改回去
     * 相关类在com.mysql.cj.MysqlType 中
     * 旧版本jdbc为
     *  DATETIME("DATETIME", Types.TIMESTAMP, Timestamp.class, 0, MysqlType.IS_NOT_DECIMAL, 26L, "[(fsp)]"),
     *  8.0.24及以上版本jdbc实现改为
     * DATETIME("DATETIME", Types.TIMESTAMP, LocalDateTime.class, 0, MysqlType.IS_NOT_DECIMAL, 26L, "[(fsp)]"),
     * @param column 列的索引位
     * @return 列名称
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     * @throws SQLException 如果发生数据库访问错误
     */
    @Override
    public String getColumnClassName(int column) throws SQLException {
        String className = resultSetMetaData.getColumnClassName(column);
        if (LocalDateTime.class.getName().equals(className)) {
            return Timestamp.class.getName();
        }
        return className;
    }

}
