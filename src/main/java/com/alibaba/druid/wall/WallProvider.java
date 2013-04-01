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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.LRUCache;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

public abstract class WallProvider {

    private LRUCache<String, WallSqlStat>              whiteList;

    private int                                        MAX_SQL_SIZE      = 2048;                                          // 1k

    private int                                        whiteSqlMaxLength = 1024;                                          // 1k

    private LRUCache<String, WallSqlStat>              blackList;

    private int                                        blackSqlMaxLength = 1024;                                          // 1k

    protected final WallConfig                         config;

    private final ReentrantReadWriteLock               lock              = new ReentrantReadWriteLock();

    private static final ThreadLocal<Boolean>          privileged        = new ThreadLocal<Boolean>();

    private final ConcurrentMap<String, WallDenyStat>  deniedTables      = new ConcurrentHashMap<String, WallDenyStat>();
    private final ConcurrentMap<String, WallDenyStat>  deniedFunctions   = new ConcurrentHashMap<String, WallDenyStat>();
    private final ConcurrentMap<String, WallDenyStat>  deniedSchemas     = new ConcurrentHashMap<String, WallDenyStat>();

    private final ConcurrentMap<String, WallTableStat> tableStats        = new ConcurrentHashMap<String, WallTableStat>();

    public final WallDenyStat                          commentDeniedStat = new WallDenyStat();

    protected String                                   dbType            = null;
    protected AtomicLong                               checkCount        = new AtomicLong();
    protected AtomicLong                               whiteListHitCount = new AtomicLong();
    protected AtomicLong                               blackListHitCount = new AtomicLong();
    protected AtomicLong                               syntaxErrrorCount = new AtomicLong();
    protected AtomicLong                               violationCount    = new AtomicLong();

    public WallProvider(WallConfig config){
        this.config = config;
    }

    public WallProvider(WallConfig config, String dbType){
        this.config = config;
        this.dbType = dbType;
    }

    public void reset() {
        this.clearWhiteList();
        this.deniedTables.clear();
        this.deniedFunctions.clear();
        this.deniedSchemas.clear();
        this.tableStats.clear();
    }

    public ConcurrentMap<String, WallTableStat> getTableStats() {
        return this.tableStats;
    }

    public WallTableStat getTableStat(String tableName) {
        String lowerCaseName = tableName.toLowerCase();

        WallTableStat stat = tableStats.get(lowerCaseName);
        if (stat == null) {
            if (tableStats.size() > 10000) {
                return null;
            }

            tableStats.putIfAbsent(lowerCaseName, new WallTableStat());
            stat = tableStats.get(lowerCaseName);
        }
        return stat;
    }

    public WallConfig getConfig() {
        return config;
    }

    public WallSqlStat addWhiteSql(String sql, Map<String, WallSqlTableStat> tableStats) {
        lock.writeLock().lock();
        try {
            if (whiteList == null) {
                whiteList = new LRUCache<String, WallSqlStat>(whiteSqlMaxLength);
            }

            WallSqlStat wallStat = whiteList.get(sql);
            if (wallStat == null) {
                wallStat = new WallSqlStat(tableStats);
                whiteList.put(sql, wallStat);
            }

            return wallStat;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public WallSqlStat addBlackSql(String sql, Map<String, WallSqlTableStat> tableStats, List<Violation> violations) {
        lock.writeLock().lock();
        try {
            if (blackList == null) {
                blackList = new LRUCache<String, WallSqlStat>(blackSqlMaxLength);
            }

            WallSqlStat wallStat = blackList.get(sql);
            if (wallStat == null) {
                wallStat = new WallSqlStat(tableStats, violations);
                blackList.put(sql, wallStat);
            }

            return wallStat;
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

        return Collections.<String> unmodifiableSet(hashSet);
    }

    public Set<String> getBlackList() {
        Set<String> hashSet = new HashSet<String>();
        lock.readLock().lock();
        try {
            if (blackList != null) {
                hashSet.addAll(blackList.keySet());
            }
        } finally {
            lock.readLock().unlock();
        }

        return Collections.<String> unmodifiableSet(hashSet);
    }

    public void clearCache() {
        clearWhiteList();
    }

    public void clearWhiteList() {
        lock.writeLock().lock();
        try {
            if (whiteList != null) {
                whiteList = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public WallSqlStat getWhiteSql(String sql) {
        lock.readLock().lock();
        try {
            if (whiteList == null) {
                return null;
            }

            return whiteList.get(sql);
        } finally {
            lock.readLock().unlock();
        }
    }

    public WallSqlStat getBlackSql(String sql) {
        lock.readLock().lock();
        try {
            if (blackList == null) {
                return null;
            }

            return blackList.get(sql);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean whiteContains(String sql) {
        return getWhiteSql(sql) != null;
    }

    public abstract SQLStatementParser createParser(String sql);

    public abstract WallVisitor createWallVisitor();

    public abstract ExportParameterVisitor createExportParameterVisitor();

    public boolean checkValid(String sql) {
        WallContext originalContext = WallContext.current();

        try {
            WallContext.create(dbType);
            WallCheckResult result = checkInternal(sql);
            return result.getViolations().isEmpty();
        } finally {

            if (originalContext == null) {
                WallContext.clearContext();
            }
        }
    }

    public void incrementCommentDeniedCount() {
        this.commentDeniedStat.incrementAndGetDenyCount();
    }

    public boolean checkDenyFunction(String functionName) {
        if (functionName == null) {
            return true;
        }

        functionName = functionName.toLowerCase();
        if (getConfig().getDenyFunctions().contains(functionName)) {
            WallDenyStat denyStat = this.deniedFunctions.get(functionName);
            if (denyStat == null) {
                this.deniedFunctions.putIfAbsent(functionName, new WallDenyStat());
                denyStat = this.deniedFunctions.get(functionName);
            }
            denyStat.incrementAndGetDenyCount();
            return false;
        }

        return true;
    }

    public boolean checkDenySchema(String schemaName) {
        if (schemaName == null) {
            return true;
        }

        schemaName = schemaName.toLowerCase();
        if (getConfig().getDenySchemas().contains(schemaName)) {
            WallDenyStat denyStat = this.deniedSchemas.get(schemaName);
            if (denyStat == null) {
                this.deniedSchemas.putIfAbsent(schemaName, new WallDenyStat());
                denyStat = this.deniedSchemas.get(schemaName);
            }
            denyStat.incrementAndGetDenyCount();
            return false;
        }

        return true;
    }

    public boolean checkDenyTable(String tableName) {
        if (tableName == null) {
            return true;
        }

        tableName = WallVisitorUtils.form(tableName);
        if (getConfig().getDenyTables().contains(tableName)) {
            WallDenyStat denyStat = this.deniedTables.get(tableName);
            if (denyStat == null) {
                this.deniedTables.putIfAbsent(tableName, new WallDenyStat());
                denyStat = this.deniedTables.get(tableName);
            }
            denyStat.incrementAndGetDenyCount();
            return false;
        }

        return true;
    }

    public boolean checkReadOnlyTable(String tableName) {
        if (tableName == null) {
            return true;
        }

        tableName = WallVisitorUtils.form(tableName);
        if (getConfig().isReadOnly(tableName)) {
            WallDenyStat denyStat = this.deniedTables.get(tableName);
            if (denyStat == null) {
                this.deniedTables.putIfAbsent(tableName, new WallDenyStat());
                denyStat = this.deniedTables.get(tableName);
            }
            denyStat.incrementAndGetDenyCount();
            return false;
        }

        return true;
    }

    public WallDenyStat getDenniedTableStat(String tableName) {
        if (tableName == null) {
            return null;
        }

        String formName = WallVisitorUtils.form(tableName);
        return deniedTables.get(formName);
    }

    public WallDenyStat getDenniedSchemaStat(String schemaName) {
        if (schemaName == null) {
            return null;
        }

        String formName = WallVisitorUtils.form(schemaName);
        return deniedSchemas.get(formName);
    }

    public WallDenyStat getCommentDenyStat() {
        return this.commentDeniedStat;
    }

    public WallCheckResult check(String sql) {
        WallContext originalContext = WallContext.current();

        try {
            WallContext.create(dbType);
            return checkInternal(sql);
        } finally {
            if (originalContext == null) {
                WallContext.clearContext();
            }
        }
    }

    public WallCheckResult checkInternal(String sql) {
        checkCount.incrementAndGet();

        WallContext context = WallContext.current();

        if (context != null && !this.dbType.equals(context.getDbType())) {
            WallContext.clearContext();
        }

        WallContext.createIfNotExists(this.dbType);
        WallCheckResult result = new WallCheckResult();

        if (config.isDoPrivilegedAllow() && ispPivileged()) {
            return result;
        }

        // first step, check whiteList
        {
            WallSqlStat sqlStat = getWhiteSql(sql);
            if (sqlStat != null) {
                whiteListHitCount.incrementAndGet();
                sqlStat.incrementAndGetExecuteCount();

                if (sqlStat.getTableStats() != null) {
                    for (Map.Entry<String, WallSqlTableStat> entry : sqlStat.getTableStats().entrySet()) {
                        String tableName = entry.getKey();
                        WallSqlTableStat sqlTableStat = entry.getValue();
                        WallTableStat tableStat = getTableStat(tableName);
                        if (tableStat != null) {
                            tableStat.addSqlTableStat(sqlTableStat);
                        }
                    }
                }
                return result;
            }
        }
        // check black list
        {
            WallSqlStat sqlStat = getBlackSql(sql);
            if (sqlStat != null) {
                blackListHitCount.incrementAndGet();
                sqlStat.incrementAndGetExecuteCount();

                if (sqlStat.getTableStats() != null) {
                    for (Map.Entry<String, WallSqlTableStat> entry : sqlStat.getTableStats().entrySet()) {
                        String tableName = entry.getKey();
                        WallSqlTableStat sqlTableStat = entry.getValue();
                        WallTableStat tableStat = getTableStat(tableName);
                        if (tableStat != null) {
                            tableStat.addSqlTableStat(sqlTableStat);
                        }
                    }
                }

                List<Violation> blackViolations = sqlStat.getViolations();

                result.getViolations().addAll(blackViolations);
                return result;
            }
        }

        try {
            SQLStatementParser parser = createParser(sql);
            parser.getLexer().setCommentHandler(WallCommentHandler.instance);

            if (!config.isCommentAllow()) {
                parser.getLexer().setAllowComment(false); // deny comment
            }

            parser.parseStatementList(result.getStatementList());

            final Token lastToken = parser.getLexer().token();
            if (lastToken != Token.EOF) {
                result.getViolations().add(new IllegalSQLObjectViolation("not terminal sql, token " + lastToken, sql));
            }
        } catch (NotAllowCommentException e) {
            result.getViolations().add(new SyntaxErrorViolation(e, sql));
            incrementCommentDeniedCount();
        } catch (ParserException e) {
            syntaxErrrorCount.incrementAndGet();
            if (config.isStrictSyntaxCheck()) {
                result.getViolations().add(new SyntaxErrorViolation(e, sql));
            }
        } catch (Exception e) {
            result.getViolations().add(new SyntaxErrorViolation(e, sql));
        }

        if (result.getViolations().size() == 0 && result.getStatementList().size() > 1
            && !config.isMultiStatementAllow()) {
            result.getViolations().add(new IllegalSQLObjectViolation("multi-statement not allow", sql));
        }

        WallVisitor visitor = createWallVisitor();

        if (result.getStatementList().size() > 0) {
            SQLStatement stmt = result.getStatementList().get(0);
            try {
                stmt.accept(visitor);
            } catch (ParserException e) {
                result.getViolations().add(new SyntaxErrorViolation(e, sql));
            }
        }

        if (visitor.getViolations().size() > 0) {
            result.getViolations().addAll(visitor.getViolations());
        }

        if (result.getViolations().size() > 0) {
            violationCount.incrementAndGet();

            if (sql.length() < MAX_SQL_SIZE) {
                addBlackSql(sql, context.getTableStats(), result.getViolations());
            }
        } else {
            if (sql.length() < MAX_SQL_SIZE) {
                addWhiteSql(sql, context.getTableStats());
            }
        }

        if (context != null && context.getTableStats() != null) {
            for (Map.Entry<String, WallSqlTableStat> entry : context.getTableStats().entrySet()) {
                String tableName = entry.getKey();
                WallSqlTableStat sqlTableStat = entry.getValue();
                WallTableStat tableStat = getTableStat(tableName);
                if (tableStat != null) {
                    tableStat.addSqlTableStat(sqlTableStat);
                }
            }
        }

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

    public long getWhiteListHitCount() {
        return whiteListHitCount.get();
    }

    public long getBlackListHitCount() {
        return blackListHitCount.get();
    }

    public long getSyntaxErrorCount() {
        return syntaxErrrorCount.get();
    }

    public long getCheckCount() {
        return checkCount.get();
    }

    public long getViolationCount() {
        return violationCount.get();
    }
    
    public static class WallCommentHandler implements Lexer.CommentHandler {
        public final static WallCommentHandler instance = new WallCommentHandler();

        @Override
        public boolean handle(Token lastToken, String comment) {
            if (lastToken == null) {
                return false;
            }
            
            switch (lastToken) {
                case SELECT:
                case INSERT:
                case DELETE:
                case UPDATE:
                case TRUNCATE:
                case SET:
                case CREATE:
                case ALTER:
                case DROP:
                    return true;
                default:
                    break;
            }
            
            return false;
        }
        
    }
}
