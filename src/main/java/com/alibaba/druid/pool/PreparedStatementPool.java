/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.OracleUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PreparedStatementPool {

    private final static Log              LOG = LogFactory.getLog(PreparedStatementPool.class);

    private final LRUCache                map;
    private final DruidAbstractDataSource dataSource;

    public PreparedStatementPool(DruidConnectionHolder holder){
        this.dataSource = holder.getDataSource();
        int initCapacity = holder.getDataSource().getMaxPoolPreparedStatementPerConnectionSize();
        if (initCapacity <= 0) {
            initCapacity = 16;
        }
        map = new LRUCache(initCapacity);
    }

    public static enum MethodType {
        M1, M2, M3, M4, M5, M6, Precall_1, Precall_2, Precall_3
    }

    public PreparedStatementHolder get(PreparedStatementKey key) throws SQLException {
        PreparedStatementHolder holder = map.get(key);

        if (holder != null) {
            if (holder.isInUse() && (!dataSource.isSharePreparedStatements())) {
                return null;
            }

            holder.incrementHitCount();
            dataSource.incrementCachedPreparedStatementHitCount();
            if (holder.isEnterOracleImplicitCache()) {
                OracleUtils.exitImplicitCacheToActive(holder.getStatement());
            }
        } else {
            dataSource.incrementCachedPreparedStatementMissCount();
        }

        return holder;
    }

    public void put(PreparedStatementHolder stmtHolder) throws SQLException {
        PreparedStatement stmt = stmtHolder.getStatement();

        if (stmt == null) {
            return;
        }

        if (dataSource.isOracle() && dataSource.isUseOracleImplicitCache()) {
            OracleUtils.enterImplicitCache(stmt);
            stmtHolder.setEnterOracleImplicitCache(true);
        } else {
            stmtHolder.setEnterOracleImplicitCache(false);
        }

        PreparedStatementKey key = stmtHolder.getKey();

        PreparedStatementHolder oldStmtHolder = map.put(key, stmtHolder);

        if (oldStmtHolder == stmtHolder) {
            return;
        }

        if (oldStmtHolder != null) {
            oldStmtHolder.setPooling(false);
            closeRemovedStatement(oldStmtHolder);
        } else {
            if (stmtHolder.getHitCount() == 0) {
                dataSource.incrementCachedPreparedStatementCount();
            }
        }

        stmtHolder.setPooling(true);

        if (LOG.isDebugEnabled()) {
            String message = null;
            if (stmtHolder.getStatement() instanceof PreparedStatementProxy) {
                PreparedStatementProxy stmtProxy = (PreparedStatementProxy) stmtHolder.getStatement();
                if (stmtProxy instanceof CallableStatementProxy) {
                    message = "{conn-" + stmtProxy.getConnectionProxy().getId() + ", cstmt-" + stmtProxy.getId()
                              + "} enter cache";
                } else {
                    message = "{conn-" + stmtProxy.getConnectionProxy().getId() + ", pstmt-" + stmtProxy.getId()
                              + "} enter cache";
                }
            } else {
                message = "stmt enter cache";
            }

            LOG.debug(message);
        }
    }

    public void clear() {
        Iterator<Entry<PreparedStatementKey, PreparedStatementHolder>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<PreparedStatementKey, PreparedStatementHolder> entry = iter.next();

            closeRemovedStatement(entry.getValue());

            iter.remove();
        }
    }

    public void closeRemovedStatement(PreparedStatementHolder holder) {
        if (LOG.isDebugEnabled()) {
            String message = null;
            if (holder.getStatement() instanceof PreparedStatementProxy) {
                PreparedStatementProxy stmtProxy = (PreparedStatementProxy) holder.getStatement();
                if (stmtProxy instanceof CallableStatementProxy) {
                    message = "{conn-" + stmtProxy.getConnectionProxy().getId() + ", cstmt-" + stmtProxy.getId()
                              + "} exit cache";
                } else {
                    message = "{conn-" + stmtProxy.getConnectionProxy().getId() + ", pstmt-" + stmtProxy.getId()
                              + "} exit cache";
                }
            } else {
                message = "stmt exit cache";
            }

            LOG.debug(message);
        }

        holder.setPooling(false);
        if (holder.isInUse()) {
            return;
        }

        if (holder.isEnterOracleImplicitCache()) {
            try {
                OracleUtils.exitImplicitCacheToClose(holder.getStatement());
            } catch (Exception ex) {
                LOG.error("exitImplicitCacheToClose error", ex);
            }
        }
        dataSource.closePreapredStatement(holder);
    }

    public Map<PreparedStatementKey, PreparedStatementHolder> getMap() {
        return map;
    }

    public int size() {
        return this.map.size();
    }

    public class LRUCache extends LinkedHashMap<PreparedStatementKey, PreparedStatementHolder> {

        private static final long serialVersionUID = 1L;

        public LRUCache(int maxSize){
            super(maxSize, 0.75f, true);
        }

        protected boolean removeEldestEntry(Entry<PreparedStatementKey, PreparedStatementHolder> eldest) {
            boolean remove = (size() > dataSource.getMaxPoolPreparedStatementPerConnectionSize());

            if (remove) {
                closeRemovedStatement(eldest.getValue());
            }

            return remove;
        }
    }
}
