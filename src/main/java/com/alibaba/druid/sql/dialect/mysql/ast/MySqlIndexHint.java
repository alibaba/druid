package com.alibaba.druid.sql.dialect.mysql.ast;

public interface MySqlIndexHint extends MySqlHint {
    public static enum Option {
        JOIN("JOIN"),
        ORDER_BY("ORDER BY"),
        GROUP_BY("GROUP BY")
        ;
        
        public final String name;
        
        Option(String name) {
            this.name = name;
        }
    }
}
