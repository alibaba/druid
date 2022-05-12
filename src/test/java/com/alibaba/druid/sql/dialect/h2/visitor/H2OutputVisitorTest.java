package com.alibaba.druid.sql.dialect.h2.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.junit.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class H2OutputVisitorTest {

    private final static Log LOG = LogFactory.getLog(H2OutputVisitorTest.class);

    @BeforeClass
    public static void loadDriver() {
        org.h2.Driver.load();
    }

    @AfterClass
    public static void unloadDriver() {
        org.h2.Driver.unload();
    }

    private Connection connection;

    @Before
    public void initConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL");
    }

    @After
    public void closeConnection() throws SQLException {
        connection.close();
    }

    @Test
    public void testConvertCreateIndex() throws SQLException {
        String mysqlSql = "CREATE SCHEMA hinex;CREATE TABLE hinex.employees (jobTitle VARCHAR2(50));CREATE FULLTEXT INDEX hinex.jobTitle USING BTREE ON hinex.employees(jobTitle);";
        convertAndExecute(mysqlSql);
    }

    private void convertAndExecute(String mysqlSql) throws SQLException {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(mysqlSql, DbType.mysql);

        String h2Sql = SQLUtils.toSQLString(sqlStatements, DbType.h2);

        executeUpdate(h2Sql);
    }

    private void executeUpdate(String sql) throws SQLException {
        try (Statement sqlStat = connection.createStatement()) {
            sqlStat.executeUpdate(sql);
        }
    }

    @Test
    public void testScript1() throws Exception {
        loadScript("wordpress.sql");
    }

    private void loadScript(String s) throws Exception {
        long time0 = System.currentTimeMillis();

        LOG.info("Executing script " + s);

        InputStream input = new BufferedInputStream(getClass().getResourceAsStream("/com/alibaba/druid/sql/dialect/h2/visitor/" + s));
        PushbackInputStream pb = new PushbackInputStream( input, 2 );
        byte [] magicbytes = new byte[2];
        pb.read(magicbytes);
        pb.unread(magicbytes);
        ByteBuffer bb = ByteBuffer.wrap(magicbytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        short magic = bb.getShort();
        if( magic == (short) GZIPInputStream.GZIP_MAGIC ) {
            executeScript(new InputStreamReader(new GZIPInputStream(pb)));
        } else {
            executeScript(new InputStreamReader(pb));
        }

        LOG.info("Done in " + (System.currentTimeMillis() - time0) + "ms");
    }

    private void executeScript(Reader reader) throws IOException, SQLException {
        char[] buffer = new char[1024];
        int l = reader.read(buffer);
        StringBuilder scripts = new StringBuilder();
        while (l > 0) {
            scripts.append(buffer, 0, l);
            l = reader.read(buffer);
        }

        convertAndExecute(scripts.toString());
    }

    @Test
    public void testScript2() throws Exception {
        loadScript("drupal.sql.gz");
    }

    @Test
    public void testScript3() throws Exception {
        loadScript("xwiki.sql.gz");
    }

    @Test
    public void testScript4() throws Exception {
        loadScript("xwiki-no-foreign-key-checks.sql.gz");
    }

    @Test
    public void testScript5() throws Exception {
        loadScript("xwiki-sqlyog.sql");
    }

    @Test
    public void testScriptCreateTableWithOnDeleteAction() throws Exception {
        loadScript("create-table-with-constraint-on-delete.sql");
    }

    @Test
    public void testScriptExportTriggerWithDelimiter() throws Exception {
        loadScript("export-trigger-with-delimiter.sql");
    }

    @Test
    public void testScriptCreateTableWithKey() throws Exception {
        loadScript("create-table-with-key.sql");
    }

    @Test
    public void testChampScripts() throws Exception {
        loadScript("champ.sql");
    }

}
