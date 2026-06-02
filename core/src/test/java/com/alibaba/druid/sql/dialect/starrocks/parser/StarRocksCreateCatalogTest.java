package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateCatalogStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StarRocksCreateCatalogTest {
    @Test
    public void testCreateExternalCatalog() {
        String sql = "CREATE EXTERNAL CATALOG hive_catalog\n"
                + "PROPERTIES (\n"
                + "\t\"type\" = \"hive\",\n"
                + "\t\"hive.metastore.uris\" = \"thrift://host:9083\"\n"
                + ")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateCatalogStatement.class, stmt);

        StarRocksCreateCatalogStatement catalogStmt = (StarRocksCreateCatalogStatement) stmt;
        assertTrue(catalogStmt.isExternal());
        assertFalse(catalogStmt.isIfNotExists());
        assertEquals("hive_catalog", catalogStmt.getName().getSimpleName());
        assertNull(catalogStmt.getComment());
        assertEquals(2, catalogStmt.getProperties().size());

        assertEquals(sql, stmt.toString());
    }

    @Test
    public void testCreateCatalogIfNotExistsWithComment() {
        String sql = "CREATE CATALOG IF NOT EXISTS iceberg_cat\n"
                + "COMMENT \"Iceberg lakehouse\"\n"
                + "PROPERTIES (\n"
                + "\t\"type\" = \"iceberg\",\n"
                + "\t\"iceberg.catalog.type\" = \"rest\",\n"
                + "\t\"iceberg.catalog.uri\" = \"http://rest:8181\"\n"
                + ")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateCatalogStatement.class, stmt);

        StarRocksCreateCatalogStatement catalogStmt = (StarRocksCreateCatalogStatement) stmt;
        assertFalse(catalogStmt.isExternal());
        assertTrue(catalogStmt.isIfNotExists());
        assertEquals("iceberg_cat", catalogStmt.getName().getSimpleName());
        assertNotNull(catalogStmt.getComment());
        assertEquals(3, catalogStmt.getProperties().size());

        assertEquals(sql, stmt.toString());
    }

    @Test
    public void testCreateExternalCatalogJdbc() {
        String sql = "CREATE EXTERNAL CATALOG jdbc_cat\n"
                + "PROPERTIES (\n"
                + "\t\"type\" = \"jdbc\",\n"
                + "\t\"jdbc_url\" = \"jdbc:mysql://host:3306/db\",\n"
                + "\t\"jdbc_user\" = \"root\",\n"
                + "\t\"jdbc_password\" = \"pass\",\n"
                + "\t\"driver_class\" = \"com.mysql.cj.jdbc.Driver\"\n"
                + ")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateCatalogStatement.class, stmt);

        StarRocksCreateCatalogStatement catalogStmt = (StarRocksCreateCatalogStatement) stmt;
        assertTrue(catalogStmt.isExternal());
        assertFalse(catalogStmt.isIfNotExists());
        assertEquals("jdbc_cat", catalogStmt.getName().getSimpleName());
        assertNull(catalogStmt.getComment());
        assertEquals(5, catalogStmt.getProperties().size());

        assertEquals(sql, stmt.toString());
    }

    @Test
    public void testCreateExternalCatalogIfNotExists() {
        String sql = "CREATE EXTERNAL CATALOG IF NOT EXISTS my_catalog\n"
                + "COMMENT \"test catalog\"\n"
                + "PROPERTIES (\n"
                + "\t\"type\" = \"hive\",\n"
                + "\t\"hive.metastore.uris\" = \"thrift://localhost:9083\"\n"
                + ")";
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.starrocks);
        assertInstanceOf(StarRocksCreateCatalogStatement.class, stmt);

        StarRocksCreateCatalogStatement catalogStmt = (StarRocksCreateCatalogStatement) stmt;
        assertTrue(catalogStmt.isExternal());
        assertTrue(catalogStmt.isIfNotExists());
        assertEquals("my_catalog", catalogStmt.getName().getSimpleName());
        assertNotNull(catalogStmt.getComment());
        assertEquals(2, catalogStmt.getProperties().size());

        assertEquals(sql, stmt.toString());
    }
}
