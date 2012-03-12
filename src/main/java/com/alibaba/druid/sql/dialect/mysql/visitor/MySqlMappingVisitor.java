package com.alibaba.druid.sql.dialect.mysql.visitor;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;

public class MySqlMappingVisitor extends MySqlASTVisitorAdapter {

    private final Map<String, SQLExpr> mapping = new HashMap<String, SQLExpr>();
    
    public void addMapping(String column, String targetExpr) {
        
    }
    
}
