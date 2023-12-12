package com.alibaba.druid.sql.issues;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.statement.SQLSelect;

import junit.framework.TestCase;

/**
 * @auther lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5317">...</a>
 */
public class Issue5317 extends TestCase {

    public void test_cloneSQLSelect() {
        SQLSelect select = new SQLSelect();
        SQLCommentHint hint = new SQLCommentHint("a");
        select.getHints().add(hint);
        SQLSelect selectNew = select.clone();
        assertEquals(select.getHints().get(0), selectNew.getHints().get(0));
    }

}
