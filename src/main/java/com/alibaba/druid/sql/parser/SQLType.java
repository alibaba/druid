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
    CREATE_TABLE,
    CREATE_TABLE_AS_SELECT,
    CREATE_VIEW,
    CREATE_FUNCTION,
    CREATE_ROLE,
    DROP_USER,
    DROP_TABLE,
    DROP_VIEW,
    DROP_FUNCTION,
    DROP_RESOURCE,
    ALTER_USER,
    ALTER_TABLE,
    READ,

    UNKNOWN
}
