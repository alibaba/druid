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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.util.MySqlUtils;

import java.util.List;

public class MySqlHintStatement extends MySqlStatementImpl {

    private List<SQLCommentHint> hints;

    private List<SQLStatement> hintStatements;

    public MySqlHintStatement() {

    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
        }
        visitor.endVisit(this);
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public int getHintVersion() {
        if (hints.size() != 1) {
            return -1;
        }

        SQLCommentHint hint = hints.get(0);

        String text = hint.getText();
        if (text.length() < 7) {
            return -1;
        }

        char c0 = text.charAt(0);
        char c1 = text.charAt(1);
        char c2 = text.charAt(2);
        char c3 = text.charAt(3);
        char c4 = text.charAt(4);
        char c5 = text.charAt(5);
        char c6 = text.charAt(6);

        if (c0 != '!') {
            return -1;
        }

        if (c1 >= '0' && c1 <= '9'
                && c2 >= '0' && c2 <= '9'
                && c3 >= '0' && c3 <= '9'
                && c4 >= '0' && c4 <= '9'
                && c5 >= '0' && c5 <= '9'
                && c6 == ' ') {
            return    (c1 - '0') * 10000
                    + (c2 - '0') * 1000
                    + (c3 - '0') * 100
                    + (c4 - '0') * 10
                    + (c5 - '0');
        }

        return -1;
    }

    public List<SQLStatement> getHintStatements() {
        if (hintStatements != null) {
            return hintStatements;
        }

        if (hints.size() != 1) {
            return null;
        }

        SQLCommentHint hint = hints.get(0);

        String text = hint.getText();
        if (text.length() < 7) {
            return null;
        }

        char c0 = text.charAt(0);
        char c1 = text.charAt(1);
        char c2 = text.charAt(2);
        char c3 = text.charAt(3);
        char c4 = text.charAt(4);
        char c5 = text.charAt(5);
        char c6 = text.charAt(6);
        if (c0 != '!') {
            return null;
        }

        int start;
        if (c1 == ' ') {
            start = 2;
        } else if (c1 >= '0' && c1 <= '9'
                && c2 >= '0' && c2 <= '9'
                && c3 >= '0' && c3 <= '9'
                && c4 >= '0' && c4 <= '9'
                && c5 >= '0' && c5 <= '9'
                && c6 == ' ') {
            start = 7;
        } else {
            return null;
        }

        String hintSql = text.substring(start);
        hintStatements = SQLUtils.parseStatements(hintSql, dbType);
        return hintStatements;
    }
}
