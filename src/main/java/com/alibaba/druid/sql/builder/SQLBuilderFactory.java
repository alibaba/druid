package com.alibaba.druid.sql.builder;

import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import com.alibaba.druid.sql.builder.impl.dialect.MySqlSelectBuilderImpl;
import com.alibaba.druid.sql.builder.impl.dialect.PGSelectBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;


public class SQLBuilderFactory {
    public static SQLSelectBuilder createSelectSQLBuilder(String dbType) {
        if (JdbcConstants.MYSQL.equals(dbType)) {
            return new MySqlSelectBuilderImpl();    
        }
        
        if (JdbcConstants.POSTGRESQL.equals(dbType)) {
            return new PGSelectBuilderImpl();    
        }
        
        if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            return new PGSelectBuilderImpl();    
        }
        
        if (JdbcConstants.ORACLE.equals(dbType)) {
            return new PGSelectBuilderImpl();    
        }
        
        return new SQLSelectBuilderImpl(dbType);
    }
    
    public static SQLDeleteBuilder createDeleteBuilder(String dbType) {
        return new SQLDeleteBuilderImpl(dbType);
    }
    
    public static SQLUpdateBuilder createUpdateBuilder(String dbType) {
        return new SQLUpdateBuilderImpl(dbType);
    }
}
