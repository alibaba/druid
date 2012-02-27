package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.fastjson.JSON;

public class TestTransform extends OracleTest {

    private String          jdbcUrl;
    private String          user;
    private String          password;
    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.18:1521:emdb";
        user = "wardon";
        password = "wardon";

        dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    public void test_transform() throws Exception {
        String sql = "SELECT SNAP_DATE, DBNAME, SQL_ID, PIECE, SQL_TEXT" + //
                     "      , COMMAND_TYPE, LAST_SNAP_DATE, DB_PK, SQL_PARSE_RESULT " + //
                     "  FROM db_day_sql_fulltext " + //
                     "  ORDER BY sql_id, piece";
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        Record r = null;
        
        while (rs.next()) {
            r = new Record();
            
            Date d1 = rs.getDate(1);
            String s2 = rs.getString(2);
            String sqlId = rs.getString(3);
            
            r.setSnapshotDate(d1);
            r.setDbName(s2);
            r.setSqlId(sqlId);
            r.setPiece(rs.getInt(4));
            r.setSqlText(rs.getString(5));

            r.setCommandType(rs.getInt(6));
            r.setLastSnapshotDate(rs.getDate(7));
            r.setDbPk(rs.getLong(8));
            
            System.out.println(r.getSqlText());
            schemaStat(r);
        }
        rs.close();
        stmt.close();
        
        conn.close();
    }
    
    public void schemaStat(Record r) {
        String sql = r.getSqlText();
        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);

        Assert.assertEquals(1, statementList.size());
        System.out.println(output(statementList));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);
        
        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        
        System.out.println();
        System.out.println();
        System.out.println();
    }
    
    public void clearResult() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        
        stmt.execute("DELETE FROM db_day_sql_fulltext");
        stmt.close();
        conn.close();
    }

    public void f_test_migrate() throws Exception {
        clearResult();
        
        String sql = "SELECT SNAP_DATE, DBNAME, SQL_ID, PIECE, SQL_TEXT" + //
                     "      , COMMAND_TYPE, LAST_SNAP_DATE, DB_PK, SQL_PARSE_RESULT " + //
                     "  FROM db_day_sqltext " + //
                     "  WHERE db_pk = 40 and snap_date = trunc(sysdate) " + //
                     "  ORDER BY sql_id, piece";
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        Record r = null;

        String lastSqlId = null;
        while (rs.next()) {
            Date d1 = rs.getDate(1);
            String s2 = rs.getString(2);
            String sqlId = rs.getString(3);

            if (lastSqlId == null || !lastSqlId.equals(sqlId)) {
                if (r != null) {
                    System.out.println(r.getSqlText());
                    System.out.println();

                    insert(r);
                }

                r = new Record();
                r.setSnapshotDate(d1);
                r.setDbName(s2);
                r.setSqlId(sqlId);
                r.setPiece(rs.getInt(4));
                r.setSqlText(rs.getString(5));

                r.setCommandType(rs.getInt(6));
                r.setLastSnapshotDate(rs.getDate(7));
                r.setDbPk(rs.getLong(8));
            } else {
                r.setSqlText(r.getSqlText() + rs.getString(5));
            }
            lastSqlId = sqlId;
        }
        rs.close();
        stmt.close();

        conn.close();
    }

    public void insert(Record r) throws Exception {
        String sql = "INSERT INTO db_day_sql_fulltext " + //
                     "(SNAP_DATE, DBNAME, SQL_ID, PIECE, SQL_TEXT" + //
                     ", COMMAND_TYPE, LAST_SNAP_DATE, DB_PK, SQL_PARSE_RESULT)" + //
                     " VALUES (?, ?, ?, ?, ?,   ?, ?, ?, ?)";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setDate(1, r.getSnapshotDate());
        stmt.setString(2, r.getDbName());
        stmt.setString(3, r.getSqlId());
        stmt.setInt(4, r.getPiece());
        stmt.setString(5, r.getSqlText());

        stmt.setInt(6, r.getCommandType());
        stmt.setDate(7, r.getLastSnapshotDate());
        stmt.setLong(8, r.getDbPk());
        stmt.setString(9, r.getSqlText());
        
        stmt.execute();

        stmt.close();

        conn.close();
    }

    public static class Record {

        private Date    snapshotDate;
        private String  dbName;
        private String  sqlId;
        private String  sqlText;
        private Integer piece;
        private Integer commandType;
        private Date    lastSnapshotDate;
        private Long    dbPk;

        public Date getSnapshotDate() {
            return snapshotDate;
        }

        public void setSnapshotDate(Date snapshotDate) {
            this.snapshotDate = snapshotDate;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getSqlId() {
            return sqlId;
        }

        public void setSqlId(String sqlId) {
            this.sqlId = sqlId;
        }

        public String getSqlText() {
            return sqlText;
        }

        public void setSqlText(String sqlText) {
            this.sqlText = sqlText;
        }

        public Integer getPiece() {
            return piece;
        }

        public void setPiece(Integer piece) {
            this.piece = piece;
        }

        public Integer getCommandType() {
            return commandType;
        }

        public void setCommandType(Integer commandType) {
            this.commandType = commandType;
        }

        public Date getLastSnapshotDate() {
            return lastSnapshotDate;
        }

        public void setLastSnapshotDate(Date lastSnapshotDate) {
            this.lastSnapshotDate = lastSnapshotDate;
        }

        public Long getDbPk() {
            return dbPk;
        }

        public void setDbPk(Long dbPk) {
            this.dbPk = dbPk;
        }

        public String toString() {
            return JSON.toJSONString(this);
        }
    }
}
