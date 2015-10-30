package com.alibaba.druid.mogu;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

import java.util.Properties;

import static com.alibaba.druid.util.Utils.getInteger;
import static com.alibaba.druid.util.Utils.getLong;

/**
 * @author by jiuru on 15/10/29.
 */
public class MoguStatFilter extends FilterEventAdapter {

    /**
     * 慢sql阈值,默认执行时间超过50ms为慢sql
     */
    private long slowSqlMillis = 50;

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    /**
     * update
     */
    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        internalAfterStatementExecute(statement, false, updateCount);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        internalAfterStatementExecute(statement, true);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        internalAfterStatementExecute(statement, firstResult);
    }

    private void internalBeforeStatementExecute(StatementProxy statement, String sql) {
        if (statement.getLastExecuteStartNano() <= 0) {
            statement.setLastExecuteStartNano();
        }
    }

    private void internalAfterStatementExecute(StatementProxy statement, boolean firstResult, int... updateCountArray) {
        final long nowNano = System.nanoTime();
        final long nanos = nowNano - statement.getLastExecuteStartNano();
        long millis = nanos / (1000 * 1000);
        if (millis >= slowSqlMillis) {
            String slowParameters = MoguPatchs.buildParameters(statement);
            MoguPatchs.log("slow sql:{} millis. {}  params:{}", millis, statement.getLastExecuteSql(), slowParameters);
        }
    }

    public void configFromProperties(Properties properties) {
        Long value = getLong(properties, MoguPatchs.SLOW_SQL_MILLIS);
        if (value != null) {
            this.slowSqlMillis = value;
        }

        //为了尽量少的修改druid源代码，就这样做啦
        Integer maxResult = getInteger(properties, MoguPatchs.MAX_RESULT);
        if (maxResult != null) {
            MoguPatchs.maxResult = maxResult;
        }
    }

}
