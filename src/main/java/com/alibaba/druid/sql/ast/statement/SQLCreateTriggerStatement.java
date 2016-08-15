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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateTriggerStatement extends SQLStatementImpl {

    private SQLName                  name;

    private boolean                  orReplace     = false;

    private TriggerType              triggerType;
    private final List<TriggerEvent> triggerEvents = new ArrayList<TriggerEvent>();

    private SQLName                  on;

    private boolean                  forEachRow    = false;

    private SQLStatement             body;
    
    public SQLCreateTriggerStatement() {
        
    }
    
    public SQLCreateTriggerStatement(String dbType) {
        super (dbType);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, on);
            acceptChild(visitor, body);
        }
        visitor.endVisit(this);
    }

    public SQLName getOn() {
        return on;
    }

    public void setOn(SQLName on) {
        this.on = on;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLStatement getBody() {
        return body;
    }

    public void setBody(SQLStatement body) {
        if (body != null) {
            body.setParent(this);
        }
        this.body = body;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public List<TriggerEvent> getTriggerEvents() {
        return triggerEvents;
    }

    public boolean isForEachRow() {
        return forEachRow;
    }

    public void setForEachRow(boolean forEachRow) {
        this.forEachRow = forEachRow;
    }

    public static enum TriggerType {
        BEFORE, AFTER, INSTEAD_OF
    }

    public static enum TriggerEvent {
        INSERT, UPDATE, DELETE
    }
}
