package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLDialect;
import org.junit.Test;

import static org.junit.Assert.*;

public class SQLDialectTest {
    @Test
    public void odps() {
        DbType dbType = DbType.odps;
        SQLDialect dialect = SQLDialect.of(dbType);
        assertEquals(dbType, dialect.getDbType());
        assertEquals('`', dialect.getQuoteChar());

        assertFalse(dialect.isKeyword(""));
        assertTrue(dialect.isKeyword("AND"));

        assertTrue(dialect.isAliasKeyword("ALTER"));
        assertFalse(dialect.isAliasKeyword("AND"));
    }

    @Test
    public void hive() {
        DbType dbType = DbType.hive;
        SQLDialect dialect = SQLDialect.of(dbType);
        assertEquals(dbType, dialect.getDbType());

        assertTrue(dialect.isBuiltInDataType("TIMESTAMP"));
    }

    @Test
    public void mysql() {
        DbType dbType = DbType.mysql;
        SQLDialect dialect = SQLDialect.of(dbType);
        assertEquals(dbType, dialect.getDbType());

        assertFalse(dialect.isKeyword(""));
        assertTrue(dialect.isKeyword("zerofill"));
    }

    @Test
    public void oracle() {
        DbType dbType = DbType.oracle;
        SQLDialect dialect = SQLDialect.of(dbType);
        assertEquals(dbType, dialect.getDbType());

        assertFalse(dialect.isKeyword(""));
        assertTrue(dialect.isKeyword("whenever"));
    }

    @Test
    public void postgresql() {
        DbType dbType = DbType.postgresql;
        SQLDialect dialect = SQLDialect.of(dbType);
        assertEquals(dbType, dialect.getDbType());
        assertEquals('"', dialect.getQuoteChar());

        assertFalse(dialect.isKeyword(""));
        assertTrue(dialect.isKeyword("asymmetric"));
    }
}
