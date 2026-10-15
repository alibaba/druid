package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest5 {
    private DruidDataSource dataSource;

    private final AtomicBoolean validate = new AtomicBoolean(true);

    @BeforeEach
    protected void setUp() throws Exception {
        validate.set(true);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

        dataSource.setValidConnectionChecker(new ValidConnectionCheckerAdapter() {
            @Override
            public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) {
                return validate.get();
            }
        });
    }

    public void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testValidate() throws Exception {
        validate.set(false);

        Exception error = null;
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            error = e;
        }
        assertNotNull(error);

        validate.set(true);

        Connection conn = dataSource.getConnection();
        conn.close();
    }

    @Test
    public void testValidate_1() throws Exception {
        validate.set(false);

        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        assertNotNull(error);

        validate.set(true);

        Connection conn = dataSource.getConnection();
        conn.close();
    }

    @Test
    public void testValidate_3() throws Exception {
        validate.set(false);

        Exception error = null;
        try {
            dataSource.init();
        } catch (SQLException e) {
            error = e;
        }
        assertNotNull(error);

        validate.set(true);

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
