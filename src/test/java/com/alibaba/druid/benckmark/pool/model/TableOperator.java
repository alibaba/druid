package com.alibaba.druid.benckmark.pool.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class TableOperator {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final int COUNT = 2;

    public TableOperator(){

    }

    public void insert() throws Exception {

        StringBuffer ddl = new StringBuffer();
        ddl.append("INSERT INTO t_big (");
        for (int i = 0; i < COUNT; ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append("F" + i);
        }
        ddl.append(") VALUES (");
        for (int i = 0; i < COUNT; ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append("?");
        }
        ddl.append(")");

        Connection conn = dataSource.getConnection();

        // System.out.println(ddl.toString());
        try {
            PreparedStatement stmt = conn.prepareStatement(ddl.toString());

            for (int i = 0; i < COUNT; ++i) {
                stmt.setInt(i + 1, i);
            }

            stmt.execute();
            stmt.close();
        } finally {

            conn.close();
        }
    }

    public void dropTable() throws SQLException {

        Connection conn = dataSource.getConnection();
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DROP TABLE t_big");
            stmt.close();
        } finally {
            conn.close();
        }
    }

    public void createTable() throws SQLException {
        StringBuffer ddl = new StringBuffer();
        ddl.append("CREATE TABLE t_big (FID INT AUTO_INCREMENT PRIMARY KEY ");
        for (int i = 0; i < COUNT; ++i) {
            ddl.append(", ");
            ddl.append("F" + i);
            ddl.append(" BIGINT NULL");
        }
        ddl.append(")");

        Connection conn = dataSource.getConnection();
        try {
            Statement stmt = conn.createStatement();
            stmt.addBatch("DROP TABLE IF EXISTS t_big");
            stmt.addBatch(ddl.toString());
            stmt.executeBatch();
            stmt.close();
        } finally {
            conn.close();
        }

    }
}
