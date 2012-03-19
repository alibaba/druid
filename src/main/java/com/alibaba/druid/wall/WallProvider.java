package com.alibaba.druid.wall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public abstract class WallProvider {

    // Dummy value to associate with an Object in the backing Map
    private static final Object           PRESENT           = new Object();

    private LinkedHashMap<String, Object> whiteList;

    private int                           whileListMaxSize  = 1024;

    private int                           whiteSqlMaxLength = 1024;                        // 1k

    protected final WallConfig            config;

    private final ReentrantReadWriteLock  lock              = new ReentrantReadWriteLock();

    public WallProvider(WallConfig config){
        this.config = config;
    }

    public WallConfig getConfig() {
        return config;
    }

    public void addWhiteSql(String sql) {
        lock.writeLock().lock();
        try {
            if (whiteList == null) {
                whiteList = new LinkedHashMap<String, Object>(whileListMaxSize, 0.75f, true);
            }

            whiteList.put(sql, PRESENT);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void clearCache() {
        lock.writeLock().lock();
        try {
            if (whiteList != null) {
                whiteList = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean whiteContains(String sql) {
        lock.readLock().lock();
        try {
            if (whiteList == null) {
                return false;
            }

            return whiteList.get(sql) != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public abstract SQLStatementParser createParser(String sql);

    public abstract WallVisitor createWallVisitor();

    public boolean checkValid(String sql) {
        return check(sql).size() == 0;
    }

    public List<Violation> check(String sql) {
        // first step, check whiteList
        boolean isWhite = whiteContains(sql);
        if (isWhite) {
            return Collections.emptyList();
        }

        SQLStatementParser parser = createParser(sql);

        if (!config.isCommentAllow()) {
            parser.getLexer().setAllowComment(false); // permit comment
        }

        List<SQLStatement> statementList = new ArrayList<SQLStatement>();

        try {
            parser.parseStatementList(statementList);
        } catch (ParserException e) {
            return Collections.<Violation> singletonList(new IllegalSQLObjectViolation(sql));
        }

        if (parser.getLexer().token() != Token.EOF) {
            return Collections.<Violation> singletonList(new IllegalSQLObjectViolation(sql));
        }

        if (statementList.size() > 1) {
            return Collections.<Violation> singletonList(new IllegalSQLObjectViolation(sql));
        }

        SQLStatement stmt = statementList.get(0);

        WallVisitor visitor = createWallVisitor();

        try {
            stmt.accept(visitor);
        } catch (ParserException e) {
            return Collections.<Violation> singletonList(new IllegalSQLObjectViolation(sql));
        }

        if (visitor.getViolations().size() == 0) {
            if (sql.length() < whiteSqlMaxLength) {
                this.addWhiteSql(sql);
            }
        }

        return visitor.getViolations();
    }

}
