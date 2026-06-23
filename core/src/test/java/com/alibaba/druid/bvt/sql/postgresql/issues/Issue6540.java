package com.alibaba.druid.bvt.sql.postgresql.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PostgreSQL CREATE TABLE with VARCHAR2/DOUBLE column types (Oracle-compatibility).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6540">Issue #6540</a>
 */
public class Issue6540 {
    @Test
    public void test_create_table_with_varchar2_and_double() {
        String sql = "create table STUDY_FINISH_C313F1B171F14BBEAFCC761BE74DA60B.t_els_course_study_record_0  "
                + "( user_id VARCHAR2(256),course_id VARCHAR2(256),course_period DOUBLE,get_score DOUBLE,"
                + "should_get_score DOUBLE,exam_count int,pass_exam_score VARCHAR2(128),last_exam_score VARCHAR2(128))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmtList.size());
    }

    @Test
    public void test_double_precision_still_parsed() {
        String sql = "create table t (a DOUBLE PRECISION, b INT)";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.postgresql);
        assertEquals(1, stmtList.size());
        assertEquals(
                "CREATE TABLE t (\n"
                        + "\ta DOUBLE PRECISION,\n"
                        + "\tb INT\n"
                        + ")",
                SQLUtils.toPGString(stmtList.get(0)));
    }
}
