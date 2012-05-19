package com.alibaba.druid.hbase.exec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;

import com.alibaba.druid.hbase.HBaseConnection;
import com.alibaba.druid.hbase.HBasePreparedStatement;
import com.alibaba.druid.hbase.HBaseResultSet;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class SingleTableQueryExecutePlan extends SingleTableExecutePlan {

    private List<String>  columeNames = new ArrayList<String>();
    private List<SQLExpr> conditions  = new ArrayList<SQLExpr>();

    public List<SQLExpr> getConditions() {
        return conditions;
    }

    public void setConditions(List<SQLExpr> conditions) {
        this.conditions = conditions;
    }

    public SingleTableQueryExecutePlan(){

    }

    public List<String> getColumeNames() {
        return columeNames;
    }

    @Override
    public HBaseResultSet executeQuery(HBasePreparedStatement statement) throws SQLException {
        try {
            HBaseConnection connection = statement.getConnection();
            HTableInterface htable = connection.getHTable(getTableName());
            String dbType = connection.getConnectProperties().getProperty("dbType");
            
            Scan scan = new Scan();

            for (SQLExpr item : conditions) {
                SQLBinaryOpExpr condition = (SQLBinaryOpExpr) item;
                String fieldName = ((SQLIdentifierExpr) condition.getLeft()).getName();
                Object value = SQLEvalVisitorUtils.eval(dbType, condition.getRight(), statement.getParameters());

                byte[] bytes = HBaseUtils.toBytes(value);
                if ("id".equals(fieldName)) {
                    if (condition.getOperator() == SQLBinaryOperator.GreaterThanOrEqual) {
                        scan.setStartRow(bytes);
                    } else if (condition.getOperator() == SQLBinaryOperator.LessThan) {
                        scan.setStopRow(bytes);
                    } else if (condition.getOperator() == SQLBinaryOperator.LessThanOrEqual) {
                        RowFilter filter = new RowFilter(CompareOp.LESS_OR_EQUAL, new BinaryComparator(bytes));
                        setFilter(scan, filter);
                    } else {
                        throw new SQLException("TODO");
                    }
                } else {
                    
                }
            }

            ResultScanner scanner = htable.getScanner(scan);

            return new HBaseResultSet(statement, htable, scanner);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        }
    }
    
    void setFilter(Scan scan, Filter filter) {
        if (scan.getFilter() == null) {
            scan.setFilter(filter);
        } else if (scan.getFilter() instanceof FilterList) {
            FilterList filterList = (FilterList) scan.getFilter();
            filterList.addFilter(filter);
        } else {
            FilterList filterList = new FilterList(scan.getFilter(), filter);
            scan.setFilter(filterList);
        }
    }

}
