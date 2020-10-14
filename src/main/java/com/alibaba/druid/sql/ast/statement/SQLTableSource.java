/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;

import java.util.List;

public interface SQLTableSource extends SQLObject {

    String getAlias();
    long aliasHashCode64();

    void setAlias(String alias);
    
    List<SQLHint> getHints();

    SQLTableSource clone();

    String computeAlias();
    boolean containsAlias(String alias);

    SQLExpr getFlashback();
    void setFlashback(SQLExpr flashback);

    SQLColumnDefinition findColumn(String columnName);
    SQLColumnDefinition findColumn(long columnNameHash);

    SQLObject resolveColum(long columnNameHash);

    SQLTableSource findTableSourceWithColumn(String columnName);
    SQLTableSource findTableSourceWithColumn(long columnName_hash);
    SQLTableSource findTableSourceWithColumn(SQLName columnName);

    SQLTableSource findTableSourceWithColumn(long columnName_hash, String name, int option);

    SQLTableSource findTableSource(String alias);
    SQLTableSource findTableSource(long alias_hash);
}
