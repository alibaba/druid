package com.alibaba.druid.bvt.sql.repository;

import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.StatementConfiguration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by wenshao on 03/08/2017.
 */
public class HSQLlJoinResolveTest extends TestCase {


    public static final String DDL1 = "CREATE TABLE t_user(uid INT NOT NULL,gid INT NULL ,uname VARCHAR(20) NULL  ,PRIMARY KEY(uid))";
    public static final String DDL2 = "CREATE TABLE t_group(id INT NULL ,name VARCHAR(20) NULL  )";
    public static final String DDL3 = "CREATE TABLE TEST_TABLE\n" +
            "(COL1 INTEGER NOT NULL,\n" +
            "COL2 CHAR(25),\n" +
            "COL3 VARCHAR(25) IDENTITY,\n" +
            "COL4 DECIMAL(10,2) NOT NULL,\n" +
            "COL5 DATE,\n" +
            "PRIMARY KEY (COL1)) ";

    public void test_for_issue() throws Exception {

        {
            SchemaRepository repository = new SchemaRepository(JdbcConstants.HSQL);
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
            {
                Statement stmt = c.createStatement();
                int success=stmt.executeUpdate(DDL1);
                DbUtils.closeQuietly(stmt);
                //assertEquals(1,success);
            }
            {
                Statement stmt = c.createStatement();
                stmt.executeUpdate(DDL2);
                DbUtils.closeQuietly(stmt);
            }
            QueryRunner qr = new QueryRunner();
            qr.update(c,"insert into t_group values(10,'test')");
            DatabaseMetaData metadata = c.getMetaData();
            repository.registerSchemaObject(metadata, null, "%", "%", new String[] { "TABLE", "VIEW" });
            compare(repository);
            DbUtils.closeQuietly(c);
        }
        {
            SchemaRepository repository = new SchemaRepository(JdbcConstants.HSQL);
            repository.console(DDL1);
            repository.console(DDL2);
            compare(repository);
        }
    }

    private void compare(SchemaRepository repository) {
        assertEquals("SELECT a.uid, a.gid, a.uname\n" +
                        "FROM t_user a\n" +
                        "\tINNER JOIN t_group b\n" +
                        "WHERE a.uid = b.id"
                , repository.resolve("select a.* from t_user a inner join t_group b where a.uid = b.id"));
    }


}
