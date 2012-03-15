package com.alibaba.druid.filter.wall;

import java.util.LinkedHashMap;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public abstract class WallProvider {

    // Dummy value to associate with an Object in the backing Map
    private static final Object           PRESENT           = new Object();

    private LinkedHashMap<String, Object> whiteList;

    private int                           whileListMaxSize  = 1024;

    private int                           whiteSqlMaxLength = 1024;        // 1k

    public synchronized void addWhiteSql(String sql) {
        if (whiteList == null) {
            whiteList = new LinkedHashMap<String, Object>(whileListMaxSize, 0.75f, true);
        }

        whiteList.put(sql, PRESENT);
    }

    public synchronized boolean whiteContains(String sql) {
        if (whiteList == null) {
            return false;
        }
        
        return whiteList.get(sql) != null;
    }

    public abstract SQLStatementParser createParser(String sql);

    public abstract WallVisitor createWallVisitor();

    public boolean check(String sql, boolean throwException) throws WallRuntimeException {
        // first step, check whiteList
        boolean isWhite = whiteContains(sql);
        if (isWhite) {
            return true;
        }

        SQLStatementParser parser = createParser(sql);
        parser.getLexer().setAllowComment(false); // permit comment

        List<SQLStatement> statementList = parser.parseStatementList();
        if (statementList.size() > 1) {
            if (throwException) {
                throw new WallRuntimeException("multi-statement : " + sql);
            }

            return false;
        }

        SQLStatement stmt = statementList.get(0);

        WallVisitor visitor = createWallVisitor();

        stmt.accept(visitor);

        if (visitor.getViolations().size() > 0) {
            if (throwException) {
                throw new WallRuntimeException();
            } else {
                return false;
            }
        }

        if (sql.length() < whiteSqlMaxLength) {
            this.addWhiteSql(sql);
        }

        return true;
    }
}
