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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlCreateEventStatement extends MySqlStatementImpl implements SQLAlterStatement {
    private SQLName            definer;
    private SQLName            name;

    private boolean            ifNotExists;

    private MySqlEventSchedule schedule;
    private boolean            onCompletionPreserve;
    private SQLName            renameTo;
    private Boolean            enable;
    private boolean            disableOnSlave;
    private SQLExpr            comment;
    private SQLStatement       eventBody;

    public MySqlCreateEventStatement() {
        setDbType(DbType.mysql);
    }

    public SQLName getDefiner() {
        return definer;
    }

    public void setDefiner(SQLName definer) {
        if (definer != null) {
            definer.setParent(this);
        }
        this.definer = definer;
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

    public MySqlEventSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(MySqlEventSchedule schedule) {
        if (schedule != null) {
            schedule.setParent(this);
        }
        this.schedule = schedule;
    }

    public boolean isOnCompletionPreserve() {
        return onCompletionPreserve;
    }

    public void setOnCompletionPreserve(boolean onCompletionPreserve) {
        this.onCompletionPreserve = onCompletionPreserve;
    }

    public SQLName getRenameTo() {
        return renameTo;
    }

    public void setRenameTo(SQLName renameTo) {
        if (renameTo != null) {
            renameTo.setParent(this);
        }
        this.renameTo = renameTo;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isDisableOnSlave() {
        return disableOnSlave;
    }

    public void setDisableOnSlave(boolean disableOnSlave) {
        this.disableOnSlave = disableOnSlave;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public SQLStatement getEventBody() {
        return eventBody;
    }

    public void setEventBody(SQLStatement eventBody) {
        if (eventBody != null) {
            eventBody.setParent(this);
        }
        this.eventBody = eventBody;
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, definer);
            acceptChild(visitor, name);
            acceptChild(visitor, schedule);
            acceptChild(visitor, renameTo);
            acceptChild(visitor, comment);
            acceptChild(visitor, eventBody);
        }
        visitor.endVisit(this);
    }

}
