package com.alibaba.druid.sql.dialect.mysql.ast.expr;

public enum MySqlIntervalUnit {
    YEAR,
    YEAR_MONTH,

    QUARTER,

    MONTH,
    WEEK,
    DAY,
    DAY_HOUR,
    DAY_MINUTE,
    DAY_SECOND,
    DAY_MICROSECOND,

    HOUR,
    HOUR_MINUTE,
    HOUR_SECOND,
    HOUR_MICROSECOND,

    MINUTE,
    MINUTE_SECOND,
    MINUTE_MICROSECOND,

    SECOND,
    SECOND_MICROSECOND,

    MICROSECOND
}
