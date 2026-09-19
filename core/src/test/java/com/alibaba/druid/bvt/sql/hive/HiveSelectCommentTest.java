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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class HiveSelectCommentTest {
    @Test
    public void leadingCommentsBelongToTheirSelectItems() {
        String sql = "SELECT\n    -- first column\n    a,\n"
                + "    -- second column\n    b\nFROM demo_table";
        SQLSelectStatement statement = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.hive);
        SQLSelectQueryBlock query = statement.getSelect().getQueryBlock();

        assertEquals(Collections.singletonList("-- first column"),
                query.getSelectList().get(0).getBeforeCommentsDirect());
        assertEquals(Collections.singletonList("-- second column"),
                query.getSelectList().get(1).getBeforeCommentsDirect());
        assertFalse(query.getSelectList().get(0).hasAfterComment());
        assertFalse(query.getSelectList().get(1).hasAfterComment());

        assertLeadingCommentsRoundTrip(sql, new SQLUtils.FormatOption(true, true));
        assertLeadingCommentsRoundTrip(sql, new SQLUtils.FormatOption(true, false));
    }

    private void assertLeadingCommentsRoundTrip(String sql, SQLUtils.FormatOption option) {
        String formatted = SQLUtils.format(sql, DbType.hive, option);
        assertEquals(1, formatted.split("-- first column", -1).length - 1);
        assertEquals(1, formatted.split("-- second column", -1).length - 1);
        SQLSelectStatement reparsed = (SQLSelectStatement) SQLUtils.parseSingleStatement(formatted, DbType.hive);
        assertEquals(2, reparsed.getSelect().getQueryBlock().getSelectList().size());
        assertEquals(Collections.singletonList("-- second column"),
                reparsed.getSelect().getQueryBlock().getSelectList().get(1).getBeforeCommentsDirect());
        assertEquals(formatted, SQLUtils.format(formatted, DbType.hive, option));
    }

    @Test
    public void leadingBlockCommentsBelongToTheirSelectItems() {
        String sql = "SELECT /* first column */ a, /* second column */ b FROM demo_table";
        SQLSelectStatement statement = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.hive);
        SQLSelectQueryBlock query = statement.getSelect().getQueryBlock();

        assertEquals(Collections.singletonList("/* first column */"),
                query.getSelectList().get(0).getBeforeCommentsDirect());
        assertEquals(Collections.singletonList("/* second column */"),
                query.getSelectList().get(1).getBeforeCommentsDirect());
        String formatted = SQLUtils.format(sql, DbType.hive);
        assertEquals(formatted, SQLUtils.format(formatted, DbType.hive));
    }

    @Test
    public void trailingLineCommentStaysWithPreviousSelectItem() {
        String sql = "SELECT a, -- first column\n    b FROM demo_table";
        SQLSelectStatement statement = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, DbType.hive);
        SQLSelectQueryBlock query = statement.getSelect().getQueryBlock();

        assertEquals(Collections.singletonList("-- first column"),
                query.getSelectList().get(0).getAfterCommentsDirect());
        assertFalse(query.getSelectList().get(1).hasBeforeComment());
        String formatted = SQLUtils.format(sql, DbType.hive);
        assertEquals(formatted, SQLUtils.format(formatted, DbType.hive));
    }
}
