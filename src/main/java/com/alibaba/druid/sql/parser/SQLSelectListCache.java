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
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLSelectListCache {
    private final static Log                LOG             = LogFactory.getLog(SQLSelectListCache.class);
    private final String                    dbType;
    private final List<Entry>               entries         = new CopyOnWriteArrayList<Entry>();

    public SQLSelectListCache(String dbType) {
        this.dbType = dbType;
    }

    public void add(String select) {
        if (select == null || select.length() == 0) {
            return;
        }

        SQLSelectParser selectParser = SQLParserUtils.createSQLStatementParser(select, dbType)
                                                     .createSQLSelectParser();
        SQLSelectQueryBlock queryBlock = SQLParserUtils.createSelectQueryBlock(dbType);
        selectParser.accept(Token.SELECT);

        selectParser.parseSelectList(queryBlock);

        selectParser.accept(Token.FROM);
        selectParser.accept(Token.EOF);

        String printSql = queryBlock.toString();
        long printSqlHash = FnvHash.fnv1a_64_lower(printSql);
        entries.add(
                new Entry(select.substring(6)
                        , queryBlock
                        , printSql
                        , printSqlHash)
        );

        if (entries.size() > 5) {
            LOG.warn("SelectListCache is too large.");
        }
    }

    public int getSize() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
    }

    public boolean match(Lexer lexer, SQLSelectQueryBlock queryBlock) {
        if (lexer.token != Token.SELECT) {
            return false;
        }

        int pos = lexer.pos;
        String text = lexer.text;

        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            String block = entry.sql;
            if (text.startsWith(block, pos)) {
                //SQLSelectQueryBlock queryBlockCached = queryBlockCache.get(i);
                // queryBlockCached.cloneSelectListTo(queryBlock);
                queryBlock.setCachedSelectList(entry.printSql, entry.printSqlHash);

                int len = pos + block.length();
                lexer.reset(len, text.charAt(len), Token.FROM);
                return true;
            }
        }
        return false;
    }

    private static class Entry {
        public final String              sql;
        public final SQLSelectQueryBlock queryBlock;
        public final String              printSql;
        public final long                printSqlHash;

        public Entry(String sql, SQLSelectQueryBlock queryBlock, String printSql, long printSqlHash) {
            this.sql = sql;
            this.queryBlock = queryBlock;
            this.printSql = printSql;
            this.printSqlHash = printSqlHash;
        }
    }
}
