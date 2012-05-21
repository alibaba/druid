package com.alibaba.druid.hdriver.impl.execute;

import java.io.IOException;
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
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import com.alibaba.druid.hdriver.impl.HBaseConnectionImpl;
import com.alibaba.druid.hdriver.impl.HPreparedStatementImpl;
import com.alibaba.druid.hdriver.impl.HResultSetMetaDataImpl;
import com.alibaba.druid.hdriver.impl.HScannerResultSetImpl;
import com.alibaba.druid.hdriver.impl.mapping.HMapping;
import com.alibaba.druid.hdriver.impl.mapping.HMappingDefaultImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class SingleTableQueryExecutePlan extends SingleTableExecutePlan {

    private List<String>           columeNames = new ArrayList<String>();
    private List<SQLExpr>          conditions  = new ArrayList<SQLExpr>();

    private HResultSetMetaDataImpl resultMetaData;

    private String                 dbType      = "hbase";

    private Scan                   scan;
    private HPreparedStatementImpl statement;

    public SingleTableQueryExecutePlan(){

    }

    public List<SQLExpr> getConditions() {
        return conditions;
    }

    public void setConditions(List<SQLExpr> conditions) {
        this.conditions = conditions;
    }

    public List<String> getColumeNames() {
        return columeNames;
    }

    public HResultSetMetaDataImpl getResultMetaData() {
        return resultMetaData;
    }

    public void setResultMetaData(HResultSetMetaDataImpl resultMetaData) {
        this.resultMetaData = resultMetaData;
    }

    @Override
    public HScannerResultSetImpl executeQuery(HPreparedStatementImpl statement) throws SQLException {
        try {
            HMapping mapping = this.getMapping();
            if (mapping == null) {
                mapping = new HMappingDefaultImpl();
            }

            HBaseConnectionImpl connection = statement.getConnection();

            scan = new Scan();
            this.statement = statement;

            Filter filter = null;
            for (SQLExpr item : conditions) {
                SQLBinaryOpExpr condition = (SQLBinaryOpExpr) item;
                filter = setFilter(condition, filter, true);
            }
            if (filter != null) {
                scan.setFilter(filter);
            }

            HTableInterface htable = connection.getHTable(getTableName());
            ResultScanner scanner = htable.getScanner(scan);

            HScannerResultSetImpl resultSet = new HScannerResultSetImpl(statement, htable, scanner);
            resultSet.setMetaData(resultMetaData);

            return resultSet;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("executeQuery error", e);
        } finally {
            scan = null;
            this.statement = null;
        }
    }

    private Filter setFilter(SQLBinaryOpExpr condition, Filter filter, boolean and) throws IOException, SQLException {
        HMapping mapping = this.getMapping();

        if (condition.getOperator() == SQLBinaryOperator.BooleanAnd) {
            filter = setFilter((SQLBinaryOpExpr) condition.getLeft(), filter, true);
            filter = setFilter((SQLBinaryOpExpr) condition.getRight(), filter, true);
            return filter;
        } else if (condition.getOperator() == SQLBinaryOperator.BooleanOr) {
            filter = setFilter((SQLBinaryOpExpr) condition.getLeft(), filter, false);
            filter = setFilter((SQLBinaryOpExpr) condition.getRight(), filter, false);
            return filter;
        }
        
        String fieldName = ((SQLIdentifierExpr) condition.getLeft()).getName();
        Object value = SQLEvalVisitorUtils.eval(dbType, condition.getRight(), statement.getParameters());

        byte[] bytes = mapping.toBytes(fieldName, value);
        if (mapping.isRow(fieldName)) {
            if (filter == null && condition.getOperator() == SQLBinaryOperator.GreaterThanOrEqual) {
                scan.setStartRow(bytes);
                return null;
            } else if (filter == null && condition.getOperator() == SQLBinaryOperator.LessThan) {
                scan.setStopRow(bytes);
                return null;
            } else {
                CompareOp compareOp = toCompareOp(condition.getOperator());
                RowFilter rowFilter = new RowFilter(compareOp, new BinaryComparator(bytes));

                return setFilter(filter, rowFilter, and);
            }
        } else {
            byte[] qualifier = mapping.getQualifier(fieldName);
            byte[] family = mapping.getFamily(fieldName);

            CompareOp compareOp = toCompareOp(condition.getOperator());

            SingleColumnValueFilter columnFilter = new SingleColumnValueFilter(family, qualifier, compareOp, bytes);
            return setFilter(filter, columnFilter, and);
        }

    }

    CompareOp toCompareOp(SQLBinaryOperator operator) {
        switch (operator) {
            case Equality:
                return CompareOp.EQUAL;
            case NotEqual:
                return CompareOp.NOT_EQUAL;
            case GreaterThan:
                return CompareOp.GREATER;
            case GreaterThanOrEqual:
                return CompareOp.GREATER_OR_EQUAL;
            case LessThan:
                return CompareOp.LESS;
            case LessThanOrEqual:
                return CompareOp.LESS_OR_EQUAL;
            default:
                throw new UnsupportedOperationException("TODO");
        }
    }

    Filter setFilter(Filter parentFilter, Filter filter, boolean and) {
        if (parentFilter == null) {
            return filter;
        } else if (parentFilter instanceof FilterList) {
            FilterList filterList = (FilterList) parentFilter;
            if (and) {
                if (filterList.getOperator() == FilterList.Operator.MUST_PASS_ALL) {
                    filterList.addFilter(filter);
                    return filterList;
                } else {
                    return new FilterList(FilterList.Operator.MUST_PASS_ALL, parentFilter, filter);
                }
            } else {
                if (filterList.getOperator() == FilterList.Operator.MUST_PASS_ONE) {
                    filterList.addFilter(filter);
                    return filterList;
                } else {
                    return new FilterList(FilterList.Operator.MUST_PASS_ONE, parentFilter, filter);
                }
            }
        } else {
            List<Filter> filters = new ArrayList<Filter>();
            filters.add(parentFilter);
            filters.add(filter);
            
            FilterList.Operator filterOp = and ? FilterList.Operator.MUST_PASS_ALL : FilterList.Operator.MUST_PASS_ONE;
            return new FilterList(filterOp, filters);
        }
    }

}
