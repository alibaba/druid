/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.wall;

import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.LRUCache;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

public abstract class WallProvider {

    // Dummy value to associate with an Object in the backing Map
    private static final Object               PRESENT           = new Object();

    private LinkedHashMap<String, Object>     whiteList;

    private int                               whileListMaxSize  = 1024;

    private int                               whiteSqlMaxLength = 1024;                        // 1k

    protected final WallConfig                config;

    private final ReentrantReadWriteLock      lock              = new ReentrantReadWriteLock();

    private static final ThreadLocal<Boolean> privileged        = new ThreadLocal<Boolean>();

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
                whiteList = new LRUCache<String, Object>(whileListMaxSize);
            }

            whiteList.put(sql, PRESENT);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<String> getWhiteList() {
        Set<String> hashSet = new HashSet<String>();
        lock.readLock().lock();
        try {
            if (whiteList != null) {
                hashSet.addAll(whiteList.keySet());
            }
        } finally {
            lock.readLock().unlock();
        }

        return hashSet;
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

    public abstract ExportParameterVisitor createExportParameterVisitor();

    public boolean checkValid(String sql) {
        return check(sql).getViolations().isEmpty();
    }

    public WallCheckResult check(String sql) {
        WallCheckResult result = new WallCheckResult();

        if (config.isAllowDoPrivileged() && privileged.get() == Boolean.TRUE) {
            return result;
        }

        // first step, check whiteList
        boolean isWhite = whiteContains(sql);
        if (isWhite) {
            return result;
        }

        SQLStatementParser parser = createParser(sql);

        if (!config.isCommentAllow()) {
            parser.getLexer().setAllowComment(false); // permit comment
        }

        try {
            parser.parseStatementList(result.getStatementList());
        } catch (Exception e) {
            result.getViolations().add(new SyntaxErrorViolation(e, sql));
            return result;
        }

        if (parser.getLexer().token() != Token.EOF) {
            result.getViolations().add(new IllegalSQLObjectViolation(sql));
            return result;
        }

        if (result.getStatementList().size() == 0) {
            return result;
        }

        if (result.getStatementList().size() > 1 && !config.isMultiStatementAllow()) {
            result.getViolations().add(new IllegalSQLObjectViolation(sql));
            return result;
        }

        SQLStatement stmt = result.getStatementList().get(0);

        WallVisitor visitor = createWallVisitor();

        try {
            stmt.accept(visitor);
        } catch (ParserException e) {
            result.getViolations().add(new IllegalSQLObjectViolation(sql));
            return result;
        }

        if (visitor.getViolations().size() == 0) {
            if (sql.length() < whiteSqlMaxLength) {
                this.addWhiteSql(sql);
            }
        }

        result.getViolations().addAll(visitor.getViolations());
        return result;
    }
    
    public static boolean ispPivileged() {
        Boolean value = privileged.get();
        if (value == null) {
            return false;
        }
        
        return value.booleanValue();
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        privileged.set(Boolean.TRUE);
        try {
            return action.run();
        } finally {
            privileged.set(null);
        }
    }

    private static final ThreadLocal<Object> tenantValueLocal = new ThreadLocal<Object>();

    public static void setTenantValue(Object value) {
        tenantValueLocal.set(value);
    }

    public static Object getTenantValue() {
        return tenantValueLocal.get();
    }
}
