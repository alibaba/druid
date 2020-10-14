package com.alibaba.druid.sql.parser;

public enum SQLType {
    SELECT,
    UPDATE,
    INSERT,
    DELETE,
    MERGE,

    CREATE,
    ALTER,
    DROP,
    TRUNCATE,

    REPLACE,
    ANALYZE,

    EXPLAIN,

    SHOW,
    DESC,
    SET,
    DUMP_DATA,
    LIST, // for analyticdb
    WHO, // for analyticdb
    GRANT,
    REVOKE,

    COMMIT,
    ROLLBACK,
    USE,
    KILL,
    MSCK,

    ADD_USER,
    REMOVE_USER,

    CREATE_USER,
    DROP_USER,
    ALTER_USER,

    UNKNOWN
}
