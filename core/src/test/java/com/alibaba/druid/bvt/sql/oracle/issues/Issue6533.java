package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle {@code AT LOCAL}（{@link OracleDatetimeExpr}）在小写渲染（ucase=false）下的输出测试。
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6533">issue #6533</a>
 */
public class Issue6533 {
    private static OracleDatetimeExpr atLocal() {
        // 直接构造 AT LOCAL 节点，聚焦输出 visitor 的渲染行为
        SQLExpr base = new SQLIdentifierExpr("d");
        return new OracleDatetimeExpr(base, new SQLIdentifierExpr("LOCAL"));
    }

    @Test
    public void atLocal_lowercaseRender() {
        StringBuilder sb = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(sb);
        visitor.config(VisitorFeature.OutputUCase, false);
        atLocal().accept(visitor);

        String out = sb.toString();
        assertTrue(out.contains(" at local"), () -> "小写渲染应输出 ' at local'，实际: " + out);
        assertFalse(out.contains("alter session set"), () -> "小写渲染被错误串污染: " + out);
    }

    @Test
    public void atLocal_uppercaseRender() {
        StringBuilder sb = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(sb);
        visitor.config(VisitorFeature.OutputUCase, true);
        atLocal().accept(visitor);

        String out = sb.toString();
        assertTrue(out.contains(" AT LOCAL"), () -> "大写渲染应输出 ' AT LOCAL'，实际: " + out);
    }
}
