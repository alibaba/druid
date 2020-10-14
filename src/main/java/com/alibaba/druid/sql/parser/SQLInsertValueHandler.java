package com.alibaba.druid.sql.parser;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface SQLInsertValueHandler {
    Object newRow() throws SQLException;

    void processInteger(Object row, int index, Number value) throws SQLException;

    void processString(Object row, int index, String value) throws SQLException;

    void processDate(Object row, int index, String value) throws SQLException;
    void processDate(Object row, int index, java.util.Date value) throws SQLException;

    void processTimestamp(Object row, int index, String value) throws SQLException;
    void processTimestamp(Object row, int index, java.util.Date value) throws SQLException;

    void processTime(Object row, int index, String value) throws SQLException;

    void processDecimal(Object row, int index, BigDecimal value) throws SQLException;

    void processBoolean(Object row, int index, boolean value) throws SQLException;

    void processNull(Object row, int index) throws SQLException;

    void processFunction(Object row, int index, String funcName, long funcNameHashCode64, Object... values) throws SQLException;

    void processRow(Object row) throws SQLException;

    void processComplete() throws SQLException;
}
