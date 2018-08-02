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
package com.alibaba.druid.sql.repository;

import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 03/08/2017.
 */
public interface SchemaResolveVisitor extends SQLASTVisitor {

    boolean isEnabled(Option option);

    public static enum Option {
        ResolveAllColumn,
        ResolveIdentifierAlias
        ;
        private Option() {
            mask = (1 << ordinal());
        }

        public final int mask;

        public static int of(Option... options) {
            if (options == null) {
                return 0;
            }

            int value = 0;

            for (Option option : options) {
                value |= option.mask;
            }

            return value;
        }
    }

    SchemaRepository getRepository();

    Context getContext();
    Context createContext(SQLObject object);
    void popContext();

    static class Context {
        public final Context parent;
        public final SQLObject object;

        private SQLTableSource tableSource;

        private SQLTableSource from;

        private Map<Long, SQLTableSource> tableSourceMap;

        protected Map<Long, SQLDeclareItem> declares;

        public Context(SQLObject object, Context parent) {
            this.object = object;
            this.parent = parent;
        }

        public SQLTableSource getFrom() {
            return from;
        }

        public void setFrom(SQLTableSource from) {
            this.from = from;
        }

        public SQLTableSource getTableSource() {
            return tableSource;
        }

        public void setTableSource(SQLTableSource tableSource) {
            this.tableSource = tableSource;
        }

        public void addTableSource(long alias_hash, SQLTableSource tableSource) {
            tableSourceMap.put(alias_hash, tableSource);
        }

        protected void declare(SQLDeclareItem x) {
            if (declares == null) {
                declares = new HashMap<Long, SQLDeclareItem>();
            }
            declares.put(x.getName().nameHashCode64(), x);
        }

        protected SQLDeclareItem findDeclare(long nameHash) {
            if (declares == null) {
                return null;
            }
            return declares.get(nameHash);
        }
    }
}
