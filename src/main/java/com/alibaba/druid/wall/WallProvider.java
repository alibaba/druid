/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.util.LRUCache;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import com.alibaba.druid.wall.violation.ErrorCode;
import com.alibaba.druid.wall.violation.IllegalSQLObjectViolation;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

public abstract class WallProvider {

    private String                                        name;

    private final Map<String, Object>                     attributes              = new ConcurrentHashMap<String, Object>(
                                                                                                                          1,
                                                                                                                          0.75f,
                                                                                                                          1);

    private boolean                                       whiteListEnable         = true;
    private LRUCache<String, WallSqlStat>                 whiteList;

    private int                                           MAX_SQL_LENGTH          = 8192;                                              // 8k

    private int                                           whiteSqlMaxSize         = 1000;

    private boolean                                       blackListEnable         = true;
    private LRUCache<String, WallSqlStat>                 blackList;
    private LRUCache<String, WallSqlStat>                 blackMergedList;

    private int                                           blackSqlMaxSize         = 200;

    protected final WallConfig                            config;

    private final ReentrantReadWriteLock                  lock                    = new ReentrantReadWriteLock();

    private static final ThreadLocal<Boolean>             privileged              = new ThreadLocal<Boolean>();

    private final ConcurrentMap<String, WallFunctionStat> functionStats           = new ConcurrentHashMap<String, WallFunctionStat>(
                                                                                                                                    16,
                                                                                                                                    0.75f,
                                                                                                                                    1);
    private final ConcurrentMap<String, WallTableStat>    tableStats              = new ConcurrentHashMap<String, WallTableStat>(
                                                                                                                                 16,
                                                                                                                                 0.75f,
                                                                                                                                 1);

    public final WallDenyStat                             commentDeniedStat       = new WallDenyStat();

    protected String                                      dbType                  = null;
    protected final AtomicLong                            checkCount              = new AtomicLong();
    protected final AtomicLong                            hardCheckCount          = new AtomicLong();
    protected final AtomicLong                            whiteListHitCount       = new AtomicLong();
    protected final AtomicLong                            blackListHitCount       = new AtomicLong();
    protected final AtomicLong                            syntaxErrorCount        = new AtomicLong();
    protected final AtomicLong                            violationCount          = new AtomicLong();
    protected final AtomicLong                            violationEffectRowCount = new AtomicLong();

    public WallProvider(WallConfig config){
        this.config = config;
    }

    public WallProvider(WallConfig config, String dbType){
        this.config = config;
        this.dbType = dbType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void reset() {
        this.checkCount.set(0);
        this.hardCheckCount.set(0);
        this.violationCount.set(0);
        this.whiteListHitCount.set(0);
        this.blackListHitCount.set(0);
        this.clearWhiteList();
        this.clearBlackList();
        this.functionStats.clear();
        this.tableStats.clear();
    }

    public ConcurrentMap<String, WallTableStat> getTableStats() {
        return this.tableStats;
    }

    public ConcurrentMap<String, WallFunctionStat> getFunctionStats() {
        return this.functionStats;
    }

    public WallSqlStat getSqlStat(String sql) {
        WallSqlStat sqlStat = this.getWhiteSql(sql);

        if (sqlStat == null) {
            sqlStat = this.getBlackSql(sql);
        }

        return sqlStat;
    }

    public WallTableStat getTableStat(String tableName) {
        String lowerCaseName = tableName.toLowerCase();
        if (lowerCaseName.startsWith("`") && lowerCaseName.endsWith("`")) {
            lowerCaseName = lowerCaseName.substring(1, lowerCaseName.length() - 1);
        }

        return getTableStatWithLowerName(lowerCaseName);
    }

    public void addUpdateCount(WallSqlStat sqlStat, long updateCount) {
        sqlStat.addUpdateCount(updateCount);

        Map<String, WallSqlTableStat> sqlTableStats = sqlStat.getTableStats();
        if (sqlTableStats == null) {
            return;
        }

        for (Map.Entry<String, WallSqlTableStat> entry : sqlTableStats.entrySet()) {
            String tableName = entry.getKey();
            WallTableStat tableStat = this.getTableStat(tableName);
            if (tableStat == null) {
                continue;
            }

            WallSqlTableStat sqlTableStat = entry.getValue();

            if (sqlTableStat.getDeleteCount() > 0) {
                tableStat.addDeleteDataCount(updateCount);
            } else if (sqlTableStat.getUpdateCount() > 0) {
                tableStat.addUpdateDataCount(updateCount);
            } else if (sqlTableStat.getInsertCount() > 0) {
                tableStat.addInsertDataCount(updateCount);
            }
        }
    }

    public void addFetchRowCount(WallSqlStat sqlStat, long fetchRowCount) {
        sqlStat.addAndFetchRowCount(fetchRowCount);

        Map<String, WallSqlTableStat> sqlTableStats = sqlStat.getTableStats();
        if (sqlTableStats == null) {
            return;
        }

        for (Map.Entry<String, WallSqlTableStat> entry : sqlTableStats.entrySet()) {
            String tableName = entry.getKey();
            WallTableStat tableStat = this.getTableStat(tableName);
            if (tableStat == null) {
                continue;
            }

            WallSqlTableStat sqlTableStat = entry.getValue();

            if (sqlTableStat.getSelectCount() > 0) {
                tableStat.addFetchRowCount(fetchRowCount);
            }
        }
    }

    public WallTableStat getTableStatWithLowerName(String lowerCaseName) {
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

    public WallFunctionStat getFunctionStat(String functionName) {
        String lowerCaseName = functionName.toLowerCase();
        return getFunctionStatWithLowerName(lowerCaseName);
    }

    public WallFunctionStat getFunctionStatWithLowerName(String lowerCaseName) {
        WallFunctionStat stat = functionStats.get(lowerCaseName);
        if (stat == null) {
            if (functionStats.size() > 10000) {
                return null;
            }

            functionStats.putIfAbsent(lowerCaseName, new WallFunctionStat());
            stat = functionStats.get(lowerCaseName);
        }
        return stat;
    }

    public WallConfig getConfig() {
        return config;
    }

    public WallSqlStat addWhiteSql(String sql, Map<String, WallSqlTableStat> tableStats,
                                   Map<String, WallSqlFunctionStat> functionStats, boolean syntaxError) {

        if (!whiteListEnable) {
            WallSqlStat stat = new WallSqlStat(tableStats, functionStats, syntaxError);
            return stat;
        }

        String mergedSql;
        try {
            mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        } catch (Exception ex) {
            WallSqlStat stat = new WallSqlStat(tableStats, functionStats, syntaxError);
            stat.incrementAndGetExecuteCount();
            return stat;
        }

        if (mergedSql != sql) {
            WallSqlStat mergedStat;
            lock.readLock().lock();
            try {
                if (whiteList == null) {
                    whiteList = new LRUCache<String, WallSqlStat>(whiteSqlMaxSize);
                }

                mergedStat = whiteList.get(mergedSql);
            } finally {
                lock.readLock().unlock();
            }

            if (mergedStat == null) {
                WallSqlStat newStat = new WallSqlStat(tableStats, functionStats, syntaxError);
                newStat.setSample(sql);

                lock.writeLock().lock();
                try {
                    mergedStat = whiteList.get(mergedSql);
                    if (mergedStat == null) {
                        whiteList.put(mergedSql, newStat);
                        mergedStat = newStat;
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            }

            mergedStat.incrementAndGetExecuteCount();

            return mergedStat;
        }

        lock.writeLock().lock();
        try {
            if (whiteList == null) {
                whiteList = new LRUCache<String, WallSqlStat>(whiteSqlMaxSize);
            }

            WallSqlStat wallStat = whiteList.get(sql);
            if (wallStat == null) {
                wallStat = new WallSqlStat(tableStats, functionStats, syntaxError);
                whiteList.put(sql, wallStat);
                wallStat.setSample(sql);

                wallStat.incrementAndGetExecuteCount();
            }

            return wallStat;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public WallSqlStat addBlackSql(String sql, Map<String, WallSqlTableStat> tableStats,
                                   Map<String, WallSqlFunctionStat> functionStats, List<Violation> violations,
                                   boolean syntaxError) {
        if (!blackListEnable) {
            return new WallSqlStat(tableStats, functionStats, violations, syntaxError);
        }

        String mergedSql;
        try {
            mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        } catch (Exception ex) {
            // skip
            mergedSql = sql;
        }

        lock.writeLock().lock();
        try {
            if (blackList == null) {
                blackList = new LRUCache<String, WallSqlStat>(blackSqlMaxSize);
            }

            if (blackMergedList == null) {
                blackMergedList = new LRUCache<String, WallSqlStat>(blackSqlMaxSize);
            }

            WallSqlStat wallStat = blackList.get(sql);
            if (wallStat == null) {
                wallStat = blackMergedList.get(mergedSql);
                if (wallStat == null) {
                    wallStat = new WallSqlStat(tableStats, functionStats, violations, syntaxError);
                    blackMergedList.put(mergedSql, wallStat);
                    wallStat.setSample(sql);
                }

                wallStat.incrementAndGetExecuteCount();
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

    public Set<String> getSqlList() {
        Set<String> hashSet = new HashSet<String>();
        lock.readLock().lock();
        try {
            if (whiteList != null) {
                hashSet.addAll(whiteList.keySet());
            }

            if (blackMergedList != null) {
                hashSet.addAll(blackMergedList.keySet());
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
        lock.writeLock().lock();
        try {
            if (whiteList != null) {
                whiteList = null;
            }

            if (blackList != null) {
                blackList = null;
            }
            if (blackMergedList != null) {
                blackMergedList = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
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

    public void clearBlackList() {
        lock.writeLock().lock();
        try {
            if (blackList != null) {
                blackList = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public WallSqlStat getWhiteSql(String sql) {
        WallSqlStat stat = null;
        lock.readLock().lock();
        try {
            if (whiteList == null) {
                return null;
            }
            stat = whiteList.get(sql);
        } finally {
            lock.readLock().unlock();
        }

        if (stat != null) {
            return stat;
        }

        String mergedSql;
        try {
            mergedSql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType, (List<Object>) null);
        } catch (Exception ex) {
            // skip
            return null;
        }

        lock.readLock().lock();
        try {
            stat = whiteList.get(mergedSql);
        } finally {
            lock.readLock().unlock();
        }
        return stat;
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
            return result
                    .getViolations()
                    .isEmpty();
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

        return !getConfig().getDenyFunctions().contains(functionName);

    }

    public boolean checkDenySchema(String schemaName) {
        if (schemaName == null) {
            return true;
        }

        if (!this.config.isSchemaCheck()) {
            return true;
        }

        schemaName = schemaName.toLowerCase();
        return !getConfig().getDenySchemas().contains(schemaName);

    }

    public boolean checkDenyTable(String tableName) {
        if (tableName == null) {
            return true;
        }

        tableName = WallVisitorUtils.form(tableName);
        return !getConfig().getDenyTables().contains(tableName);

    }

    public boolean checkReadOnlyTable(String tableName) {
        if (tableName == null) {
            return true;
        }

        tableName = WallVisitorUtils.form(tableName);
        return !getConfig().isReadOnly(tableName);

    }

    public WallDenyStat getCommentDenyStat() {
        return this.commentDeniedStat;
    }

    public WallCheckResult check(String sql) {
        WallContext originalContext = WallContext.current();

        try {
            WallContext.createIfNotExists(dbType);
            return checkInternal(sql);
        } finally {
            if (originalContext == null) {
                WallContext.clearContext();
            }
        }
    }

    private WallCheckResult checkInternal(String sql) {
        checkCount.incrementAndGet();

        WallContext context = WallContext.current();

        if (config.isDoPrivilegedAllow() && ispPrivileged()) {
            WallCheckResult checkResult = new WallCheckResult();
            checkResult.setSql(sql);
            return checkResult;
        }

        // first step, check whiteList
        boolean mulltiTenant = config.getTenantTablePattern() != null && config.getTenantTablePattern().length() > 0;
        if (!mulltiTenant) {
            WallCheckResult checkResult = checkWhiteAndBlackList(sql);
            if (checkResult != null) {
                checkResult.setSql(sql);
                return checkResult;
            }
        }

        hardCheckCount.incrementAndGet();
        final List<Violation> violations = new ArrayList<Violation>();
        List<SQLStatement> statementList = new ArrayList<SQLStatement>();
        boolean syntaxError = false;
        boolean endOfComment = false;
        try {
            SQLStatementParser parser = createParser(sql);
            parser.getLexer().setCommentHandler(WallCommentHandler.instance);

            if (!config.isCommentAllow()) {
                parser.getLexer().setAllowComment(false); // deny comment
            }
            if (!config.isCompleteInsertValuesCheck()) {
                parser.setParseCompleteValues(false);
                parser.setParseValuesSize(config.getInsertValuesCheckSize());
            }
            
            parser.parseStatementList(statementList);

            final Token lastToken = parser.getLexer().token();
            if (lastToken != Token.EOF && config.isStrictSyntaxCheck()) {
                violations.add(new IllegalSQLObjectViolation(ErrorCode.SYNTAX_ERROR, "not terminal sql, token "
                                                                                     + lastToken, sql));
            }
            endOfComment = parser.getLexer().isEndOfComment();
        } catch (NotAllowCommentException e) {
            violations.add(new IllegalSQLObjectViolation(ErrorCode.COMMENT_STATEMENT_NOT_ALLOW, "comment not allow", sql));
            incrementCommentDeniedCount();
        } catch (ParserException e) {
            syntaxErrorCount.incrementAndGet();
            syntaxError = true;
            if (config.isStrictSyntaxCheck()) {
                violations.add(new SyntaxErrorViolation(e, sql));
            }
        } catch (Exception e) {
            if (config.isStrictSyntaxCheck()) {
                violations.add(new SyntaxErrorViolation(e, sql));
            }
        }

        if (statementList.size() > 1 && !config.isMultiStatementAllow()) {
            violations.add(new IllegalSQLObjectViolation(ErrorCode.MULTI_STATEMENT, "multi-statement not allow", sql));
        }

        WallVisitor visitor = createWallVisitor();
        visitor.setSqlEndOfComment(endOfComment);

        if (statementList.size() > 0) {
            boolean lastIsHint = false;
            for (int i=0; i<statementList.size(); i++) {
                SQLStatement stmt = statementList.get(i);
                if ((i == 0 || lastIsHint) && stmt instanceof MySqlHintStatement) {
                    lastIsHint = true;
                    continue;
                }
                try {
                    stmt.accept(visitor);
                } catch (ParserException e) {
                    violations.add(new SyntaxErrorViolation(e, sql));
                }
            }
        }

        if (visitor.getViolations().size() > 0) {
            violations.addAll(visitor.getViolations());
        }

        Map<String, WallSqlTableStat> tableStat = context.getTableStats();

        boolean updateCheckHandlerEnable = false;
        {
            WallUpdateCheckHandler updateCheckHandler = config.getUpdateCheckHandler();
            if (updateCheckHandler != null) {
                for (SQLStatement stmt : statementList) {
                    if (stmt instanceof SQLUpdateStatement) {
                        SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
                        SQLName table = updateStmt.getTableName();
                        if (table != null) {
                            String tableName = table.getSimpleName();
                            Set<String> updateCheckColumns = config.getUpdateCheckTable(tableName);
                            if (updateCheckColumns != null && updateCheckColumns.size() > 0) {
                                updateCheckHandlerEnable = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        WallSqlStat sqlStat = null;
        if (violations.size() > 0) {
            violationCount.incrementAndGet();

            if ((!updateCheckHandlerEnable) && sql.length() < MAX_SQL_LENGTH) {
                sqlStat = addBlackSql(sql, tableStat, context.getFunctionStats(), violations, syntaxError);
            }
        } else {
            if ((!updateCheckHandlerEnable) && sql.length() < MAX_SQL_LENGTH) {
                sqlStat = addWhiteSql(sql, tableStat, context.getFunctionStats(), syntaxError);
            }
        }
        
        if(sqlStat == null && updateCheckHandlerEnable){
            sqlStat = new WallSqlStat(tableStat, context.getFunctionStats(), violations, syntaxError);
        }

        Map<String, WallSqlTableStat> tableStats = null;
        Map<String, WallSqlFunctionStat> functionStats = null;
        if (context != null) {
            tableStats = context.getTableStats();
            functionStats = context.getFunctionStats();
            recordStats(tableStats, functionStats);
        }

        WallCheckResult result;
        if (sqlStat != null) {
            context.setSqlStat(sqlStat);
            result = new WallCheckResult(sqlStat, statementList);
        } else {
            result = new WallCheckResult(null, violations, tableStats, functionStats, statementList, syntaxError);
        }

        String resultSql;
        if (visitor.isSqlModified()) {
            resultSql = SQLUtils.toSQLString(statementList, dbType);
        } else {
            resultSql = sql;
        }
        result.setSql(resultSql);

        result.setUpdateCheckItems(visitor.getUpdateCheckItems());

        return result;
    }

    private WallCheckResult checkWhiteAndBlackList(String sql) {
        if (config.getUpdateCheckHandler() != null) {
            return null;
        }

        // check black list
        if (blackListEnable) {
            WallSqlStat sqlStat = getBlackSql(sql);
            if (sqlStat != null) {
                blackListHitCount.incrementAndGet();
                violationCount.incrementAndGet();

                if (sqlStat.isSyntaxError()) {
                    syntaxErrorCount.incrementAndGet();
                }

                sqlStat.incrementAndGetExecuteCount();
                recordStats(sqlStat.getTableStats(), sqlStat.getFunctionStats());

                return new WallCheckResult(sqlStat);
            }
        }

        if (whiteListEnable) {
            WallSqlStat sqlStat = getWhiteSql(sql);
            if (sqlStat != null) {
                whiteListHitCount.incrementAndGet();
                sqlStat.incrementAndGetExecuteCount();

                if (sqlStat.isSyntaxError()) {
                    syntaxErrorCount.incrementAndGet();
                }

                recordStats(sqlStat.getTableStats(), sqlStat.getFunctionStats());
                WallContext context = WallContext.current();
                if (context != null) {
                    context.setSqlStat(sqlStat);
                }
                return new WallCheckResult(sqlStat);
            }
        }

        return null;
    }

    void recordStats(Map<String, WallSqlTableStat> tableStats, Map<String, WallSqlFunctionStat> functionStats) {
        if (tableStats != null) {
            for (Map.Entry<String, WallSqlTableStat> entry : tableStats.entrySet()) {
                String tableName = entry.getKey();
                WallSqlTableStat sqlTableStat = entry.getValue();
                WallTableStat tableStat = getTableStat(tableName);
                if (tableStat != null) {
                    tableStat.addSqlTableStat(sqlTableStat);
                }
            }
        }
        if (functionStats != null) {
            for (Map.Entry<String, WallSqlFunctionStat> entry : functionStats.entrySet()) {
                String tableName = entry.getKey();
                WallSqlFunctionStat sqlTableStat = entry.getValue();
                WallFunctionStat functionStat = getFunctionStatWithLowerName(tableName);
                if (functionStat != null) {
                    functionStat.addSqlFunctionStat(sqlTableStat);
                }
            }
        }
    }

    public static boolean ispPrivileged() {
        Boolean value = privileged.get();
        if (value == null) {
            return false;
        }

        return value;
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        final Boolean original = privileged.get();
        privileged.set(Boolean.TRUE);
        try {
            return action.run();
        } finally {
            privileged.set(original);
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
        return syntaxErrorCount.get();
    }

    public long getCheckCount() {
        return checkCount.get();
    }

    public long getViolationCount() {
        return violationCount.get();
    }

    public long getHardCheckCount() {
        return hardCheckCount.get();
    }

    public long getViolationEffectRowCount() {
        return violationEffectRowCount.get();
    }

    public void addViolationEffectRowCount(long rowCount) {
        violationEffectRowCount.addAndGet(rowCount);
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
                case SHOW:
                case REPLACE:
                    return true;
                default:
                    break;
            }

            WallContext context = WallContext.current();
            if (context != null) {
                context.incrementCommentCount();
            }

            return false;
        }
    }

    public WallProviderStatValue getStatValue(boolean reset) {
        WallProviderStatValue statValue = new WallProviderStatValue();

        statValue.setName(name);
        statValue.setCheckCount(get(checkCount, reset));
        statValue.setHardCheckCount(get(hardCheckCount, reset));
        statValue.setViolationCount(get(violationCount, reset));
        statValue.setViolationEffectRowCount(get(violationEffectRowCount, reset));
        statValue.setBlackListHitCount(get(blackListHitCount, reset));
        statValue.setWhiteListHitCount(get(whiteListHitCount, reset));
        statValue.setSyntaxErrorCount(get(syntaxErrorCount, reset));

        for (Map.Entry<String, WallTableStat> entry : this.tableStats.entrySet()) {
            String tableName = entry.getKey();
            WallTableStat tableStat = entry.getValue();

            WallTableStatValue tableStatValue = tableStat.getStatValue(reset);

            if (tableStatValue.getTotalExecuteCount() == 0) {
                continue;
            }

            tableStatValue.setName(tableName);

            statValue.getTables().add(tableStatValue);
        }

        for (Map.Entry<String, WallFunctionStat> entry : this.functionStats.entrySet()) {
            String functionName = entry.getKey();
            WallFunctionStat functionStat = entry.getValue();

            WallFunctionStatValue functionStatValue = functionStat.getStatValue(reset);

            if (functionStatValue.getInvokeCount() == 0) {
                continue;
            }
            functionStatValue.setName(functionName);

            statValue.getFunctions().add(functionStatValue);
        }

        final Lock lock = reset ? this.lock.writeLock() : this.lock.readLock();
        lock.lock();
        try {
            if (this.whiteList != null) {
                for (Map.Entry<String, WallSqlStat> entry : whiteList.entrySet()) {
                    String sql = entry.getKey();
                    WallSqlStat sqlStat = entry.getValue();
                    WallSqlStatValue sqlStatValue = sqlStat.getStatValue(reset);

                    if (sqlStatValue.getExecuteCount() == 0) {
                        continue;
                    }

                    sqlStatValue.setSql(sql);

                    long sqlHash = sqlStat.getSqlHash();
                    if (sqlHash == 0) {
                        sqlHash = Utils.fnv_64(sql);
                        sqlStat.setSqlHash(sqlHash);
                    }
                    sqlStatValue.setSqlHash(sqlHash);

                    statValue.getWhiteList().add(sqlStatValue);
                }
            }

            if (this.blackMergedList != null) {
                for (Map.Entry<String, WallSqlStat> entry : blackMergedList.entrySet()) {
                    String sql = entry.getKey();
                    WallSqlStat sqlStat = entry.getValue();
                    WallSqlStatValue sqlStatValue = sqlStat.getStatValue(reset);

                    if (sqlStatValue.getExecuteCount() == 0) {
                        continue;
                    }

                    sqlStatValue.setSql(sql);
                    statValue.getBlackList().add(sqlStatValue);
                }
            }
        } finally {
            lock.unlock();
        }

        return statValue;
    }

    public Map<String, Object> getStatsMap() {
        return getStatValue(false).toMap();
    }

    public boolean isWhiteListEnable() {
        return whiteListEnable;
    }

    public void setWhiteListEnable(boolean whiteListEnable) {
        this.whiteListEnable = whiteListEnable;
    }

    public boolean isBlackListEnable() {
        return blackListEnable;
    }

    public void setBlackListEnable(boolean blackListEnable) {
        this.blackListEnable = blackListEnable;
    }
}
