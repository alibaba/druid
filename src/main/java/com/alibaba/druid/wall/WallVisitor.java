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
package com.alibaba.druid.wall;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface WallVisitor extends SQLASTVisitor {

    WallConfig getConfig();

    WallProvider getProvider();

    List<Violation> getViolations();

    void addViolation(Violation violation);

    boolean isDenyTable(String name);

    String toSQL(SQLObject obj);

    boolean isSqlModified();

    void setSqlModified(boolean sqlModified);
    
    String getDbType();
    
    boolean isSqlEndOfComment();

    void setSqlEndOfComment(boolean sqlEndOfComment);
}
