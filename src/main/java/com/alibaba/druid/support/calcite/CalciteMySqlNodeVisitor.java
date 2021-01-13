package com.alibaba.druid.support.calcite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.util.FnvHash;
import org.apache.calcite.avatica.util.TimeUnit;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.fun.*;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.parser.SqlParserUtil;
import org.apache.calcite.util.DateString;
import org.apache.calcite.util.TimeString;
import org.apache.calcite.util.TimestampString;

import java.util.*;

public class CalciteMySqlNodeVisitor extends MySqlASTVisitorAdapter {
    static Map<Long, SqlOperator> operators = new HashMap<Long, SqlOperator>();

    static {

        List<SqlOperator> list = SqlStdOperatorTable.instance().getOperatorList();
        for (SqlOperator op : list) {
            long h = FnvHash.hashCode64(op.getName());
            if (h == FnvHash.Constants.TRIM) {
                continue;
            }
            operators.put(h, op);
        }
        operators.put(FnvHash.Constants.CEILING, SqlStdOperatorTable.CEIL);
    }

    static SqlOperator func(long hash) {
        return operators.get(hash);
    }

    private SqlNode sqlNode;

    public SqlNode getSqlNode() {
        return sqlNode;
    }


    public boolean visit(SQLInsertStatement x) {
        SqlNodeList keywords = new SqlNodeList(new ArrayList<SqlNode>(), SqlParserPos.ZERO);

        SQLExprTableSource tableSource = (SQLExprTableSource) x.getTableSource();
        SqlNode targetTable = convertToSqlNode(tableSource.getExpr());

        SqlNode source;

        SQLSelect query = x.getQuery();
        if (query != null) {
            query.accept(this);
            source = sqlNode;
        } else {
            List<SQLInsertStatement.ValuesClause> valuesList = x.getValuesList();

            SqlNode[] rows = new SqlNode[valuesList.size()];
            for (int j = 0; j < valuesList.size(); j++) {

                List<SQLExpr> values = valuesList.get(j).getValues();

                SqlNode[] valueNodes = new SqlNode[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    SqlNode valueNode = convertToSqlNode(values.get(i));
                    valueNodes[i] = valueNode;
                }
                SqlBasicCall row = new SqlBasicCall(SqlStdOperatorTable.ROW, valueNodes, SqlParserPos.ZERO);
                rows[j] = row;
            }
            source = new SqlBasicCall(SqlStdOperatorTable.VALUES, rows, SqlParserPos.ZERO);
        }

        SqlNodeList columnList = x.getColumns().size() > 0
                ? convertToSqlNodeList(x.getColumns())
                : null;

        this.sqlNode = new SqlInsert(SqlParserPos.ZERO, keywords, targetTable, source, columnList);
        return false;
    }

    public boolean visit(MySqlInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    private boolean visit(List<SQLInsertStatement.ValuesClause> valuesList) {
        boolean isBatch = false;
        List<SQLInsertStatement.ValuesClause> newValuesList = convertToSingleValuesIfNeed(valuesList);
        if (newValuesList.size() < valuesList.size()) {
            isBatch = true;
            valuesList = newValuesList;
        }

        SqlNode[] rows = new SqlNode[valuesList.size()];
        for (int j = 0; j < valuesList.size(); j++) {

            List<SQLExpr> values = valuesList.get(j).getValues();

            SqlNode[] valueNodes = new SqlNode[values.size()];
            for (int i = 0; i < values.size(); i++) {
                SqlNode valueNode = convertToSqlNode(values.get(i));
                valueNodes[i] = valueNode;
            }
            SqlBasicCall row = new SqlBasicCall(SqlStdOperatorTable.ROW, valueNodes, SqlParserPos.ZERO);
            rows[j] = row;
        }

        this.sqlNode = new SqlBasicCall(SqlStdOperatorTable.VALUES, rows, SqlParserPos.ZERO);

        return isBatch;
    }

    public boolean visit(MySqlUpdateStatement x) {

        if(x.getTableSource().getClass() != SQLExprTableSource.class) {
            throw new UnsupportedOperationException("Support single table only for SqlUpdate statement of calcite.");
        }
        SQLExprTableSource tableSource = (SQLExprTableSource) x.getTableSource();
        SqlNode targetTable = convertToSqlNode(tableSource.getExpr());

        List<SqlNode> columns = new ArrayList<SqlNode>();
        List<SqlNode> values = new ArrayList<SqlNode>();

        for (SQLUpdateSetItem item : x.getItems()) {
            columns.add(convertToSqlNode(item.getColumn()));
            values.add(convertToSqlNode(item.getValue()));
        }
        SqlNodeList targetColumnList = new SqlNodeList(columns, SqlParserPos.ZERO);
        SqlNodeList sourceExpressList = new SqlNodeList(values, SqlParserPos.ZERO);

        SqlNode condition = convertToSqlNode(x.getWhere());


        SqlIdentifier alias = null;
        if(x.getTableSource().getAlias() != null) {
            alias = new SqlIdentifier(tableSource.getAlias(), SqlParserPos.ZERO);
        }

        sqlNode = new SqlUpdate(SqlParserPos.ZERO, targetTable, targetColumnList, sourceExpressList, condition, null, alias);

        return false;
    }

    public boolean visit(MySqlDeleteStatement x) {

        SQLExprTableSource tableSource = (SQLExprTableSource) x.getTableSource();
        SqlNode targetTable = convertToSqlNode(tableSource.getExpr());

        SqlNode condition = convertToSqlNode(x.getWhere());


        SqlIdentifier alias = null;
        if(x.getTableSource().getAlias() != null) {
            alias = new SqlIdentifier(tableSource.getAlias(), SqlParserPos.ZERO);
        }

        sqlNode = new SqlDelete(SqlParserPos.ZERO, targetTable, condition, null, alias);

        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {

        SqlNode[] nodes;
        if (x.getRelations().size() > 2) {
            nodes = new SqlNode[x.getRelations().size()];
            for (int i = 0; i < x.getRelations().size(); i++) {
                nodes[i] = convertToSqlNode(x.getRelations().get(i));
            }
        } else {
            SqlNode left = convertToSqlNode(x.getLeft());
            SqlNode right = convertToSqlNode(x.getRight());

            nodes = new SqlNode[] {left, right};
        }

        //order by
        SqlNodeList orderBySqlNode = null;
        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            orderBySqlNode = convertOrderby(orderBy);
        }

        //limit
        SqlNode offset = null;
        SqlNode fetch = null;
        SQLLimit limit = x.getLimit();
        if (limit != null) {
            offset = convertToSqlNode(limit.getOffset());
            fetch = convertToSqlNode(limit.getRowCount());
        }

        SQLUnionOperator operator = x.getOperator();

        SqlNode union = null;
        switch (operator) {
            case UNION_ALL:
                union = new SqlBasicCall(SqlStdOperatorTable.UNION_ALL,
                        nodes,
                        SqlParserPos.ZERO);
                break;
            case UNION:
            case DISTINCT:
                union = new SqlBasicCall(SqlStdOperatorTable.UNION,
                        nodes,
                        SqlParserPos.ZERO);
                break;
            case INTERSECT:
                union = new SqlBasicCall(SqlStdOperatorTable.INTERSECT,
                        nodes,
                        SqlParserPos.ZERO);
                break;
            case EXCEPT:
                union = new SqlBasicCall(SqlStdOperatorTable.EXCEPT,
                        nodes,
                        SqlParserPos.ZERO);
                break;
            default:
                throw new FastsqlException("unsupported join type: " + operator);
        }

        if (null == orderBy && null == offset && null == fetch) {
            sqlNode = union;
        } else {
            if (orderBySqlNode == null) {
                orderBySqlNode = SqlNodeList.EMPTY;
            }
            sqlNode = new SqlOrderBy(SqlParserPos.ZERO, union, orderBySqlNode, offset, fetch);
        }

        return false;

    }

    public boolean visit(MySqlSelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    public boolean visit(SQLSelectQueryBlock x) {
        SqlNodeList keywordList = null;
        List<SqlNode> keywordNodes = new ArrayList<SqlNode>(5);
        int option = x.getDistionOption();
        if (option != 0) {
            if (option == SQLSetQuantifier.DISTINCT
                    || option == SQLSetQuantifier.DISTINCTROW) {
                keywordNodes.add(SqlSelectKeyword.DISTINCT.symbol(SqlParserPos.ZERO));
            } else if (option == SQLSetQuantifier.ALL) {
                keywordNodes.add(SqlSelectKeyword.ALL.symbol(SqlParserPos.ZERO));
            }

            keywordList = new SqlNodeList(keywordNodes, SqlParserPos.ZERO);
        }

        // select list
        List<SqlNode> columnNodes = new ArrayList<SqlNode>(x.getSelectList().size());
        for (SQLSelectItem selectItem : x.getSelectList()) {
            SqlNode column = convertToSqlNode(selectItem);
            columnNodes.add(column);
        }

        //select item
        SqlNodeList selectList = new SqlNodeList(columnNodes, SqlParserPos.ZERO);

        //from
        SqlNode from = null;

        SQLTableSource tableSource = x.getFrom();
        if (tableSource != null) {
            from = convertToSqlNode(tableSource);
        }

        //where
        SqlNode where = convertToSqlNode(x.getWhere());

        //order by
        SqlNodeList orderBySqlNode = null;
        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            orderBySqlNode = convertOrderby(orderBy);
        }

        //group by
        SqlNodeList groupBySqlNode = null;
        SqlNode having = null;
        SQLSelectGroupByClause groupBys = x.getGroupBy();

        if (groupBys != null) {
            if (groupBys.getHaving() != null) {
                having = convertToSqlNode(groupBys.getHaving());
            }

            if (groupBys.getItems().size() > 0) {
                List<SqlNode> groupByNodes = new ArrayList<SqlNode>(groupBys.getItems().size());

                for (SQLExpr groupBy : groupBys.getItems()) {
                    SqlNode groupByNode = convertToSqlNode(groupBy);
                    groupByNodes.add(groupByNode);
                }
                groupBySqlNode = new SqlNodeList(groupByNodes, SqlParserPos.ZERO);
            }

            SqlInternalOperator op = null;
            if (groupBys.isWithRollUp()) {
                op = SqlStdOperatorTable.ROLLUP;
            } else if (groupBys.isWithCube()) {
                op = SqlStdOperatorTable.CUBE;
            }

            if (op != null) {
                List<SqlNode> rollupNodes = new ArrayList<SqlNode>(1);

                boolean isRow = false;
                for (SqlNode node : groupBySqlNode.getList()) {
                    if (node instanceof SqlBasicCall && ((SqlBasicCall) node).getOperator() == SqlStdOperatorTable.ROW) {
                        isRow = true;
                        break;
                    }
                }

                if (isRow) {
                    rollupNodes.add(op.createCall(SqlParserPos.ZERO, groupBySqlNode.toArray()));
                    groupBySqlNode = new SqlNodeList(rollupNodes, SqlParserPos.ZERO);
                } else {
                    rollupNodes.add(op.createCall(SqlParserPos.ZERO, groupBySqlNode));
                    groupBySqlNode = new SqlNodeList(rollupNodes, SqlParserPos.ZERO);
                }

            }
        }

        //limit
        SqlNode offset = null;
        SqlNode fetch = null;
        SQLLimit limit = x.getLimit();
        if (limit != null) {
            offset = convertToSqlNode(limit.getOffset());
            fetch = convertToSqlNode(limit.getRowCount());
        }

        //hints
        SqlNodeList hints = convertHints(x.getHints());

        if (orderBy != null && x.getParent() instanceof SQLUnionQuery) {
            this.sqlNode = new com.alibaba.druid.support.calcite.TDDLSqlSelect(SqlParserPos.ZERO
                    , keywordList
                    , selectList
                    , from
                    , where
                    , groupBySqlNode
                    , having
                    , null
                    , null
                    , offset
                    , fetch
                    , hints
                    , null
            );
            sqlNode = new SqlOrderBy(SqlParserPos.ZERO
                    , sqlNode
                    , orderBySqlNode
                    , null
                    , fetch
            );
        } else {
            if (orderBySqlNode == null) {
                orderBySqlNode = SqlNodeList.EMPTY;
            }
            if (hints == null || SqlNodeList.isEmptyList(hints)) {
                this.sqlNode = new SqlSelect(SqlParserPos.ZERO
                        , keywordList
                        , selectList
                        , from
                        , where
                        , groupBySqlNode
                        , having
                        , null
                        , SqlNodeList.EMPTY
                        , null
                        , null
                );

                if ((!SqlNodeList.isEmptyList(orderBySqlNode))
                        || offset != null
                        || fetch != null
                ) {
                    sqlNode = new SqlOrderBy(SqlParserPos.ZERO
                            , sqlNode
                            , orderBySqlNode
                            , offset
                            , fetch);
                }
            } else {
                this.sqlNode = new com.alibaba.druid.support.calcite.TDDLSqlSelect(SqlParserPos.ZERO
                        , keywordList
                        , selectList
                        , from
                        , where
                        , groupBySqlNode
                        , having
                        , null
                        , orderBySqlNode
                        , offset
                        , fetch
                        , hints
                        , null
                );
            }
        }


        return false;
    }

    public boolean visit(SQLTableSource x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLJoinTableSource.class) {
            visit((SQLJoinTableSource) x);
        } else  if (clazz == SQLExprTableSource.class) {
            visit((SQLExprTableSource) x);
        } else  if (clazz == SQLSubqueryTableSource.class) {
            visit((SQLSubqueryTableSource) x);
        } else {
            x.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        SqlIdentifier table;
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLIdentifierExpr) {
            table = buildIdentifier((SQLIdentifierExpr) expr);
        } else if (expr instanceof SQLPropertyExpr) {
            table = buildIdentifier((SQLPropertyExpr) expr);
        } else {
            throw new FastsqlException("not support : " + expr);
        }

        if (x.getAlias() != null) {
            SqlIdentifier alias = new SqlIdentifier(x.computeAlias(), SqlParserPos.ZERO);
            SqlBasicCall as = new SqlBasicCall(SqlStdOperatorTable.AS, new SqlNode[] { table, alias },
                                               SqlParserPos.ZERO);
            sqlNode = as;
        } else {
            sqlNode = table;
        }

        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        SQLJoinTableSource.JoinType joinType = x.getJoinType();

        SqlNode left = convertToSqlNode(x.getLeft());
        SqlNode right = convertToSqlNode(x.getRight());
        SqlNode condition = convertToSqlNode(x.getCondition());

        SqlLiteral conditionType = condition == null
                ? JoinConditionType.NONE.symbol(SqlParserPos.ZERO)
                : JoinConditionType.ON.symbol(SqlParserPos.ZERO);

        if (condition == null && !x.getUsing().isEmpty()) {
            List<SQLExpr> using = x.getUsing();
            conditionType = JoinConditionType.USING.symbol(SqlParserPos.ZERO);
            condition = convertToSqlNodeList(x.getUsing());
        }

        switch (joinType) {
            case COMMA:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO, left,
                                           SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
                                           JoinType.COMMA.symbol(SqlParserPos.ZERO), right,
                                           JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
                                           null);
                break;
            case JOIN:
            case INNER_JOIN:
                if (condition == null) {
                    this.sqlNode = new SqlJoin(SqlParserPos.ZERO
                                                , left
                                                , SqlLiteral.createBoolean(false, SqlParserPos.ZERO)
                                                , JoinType.COMMA.symbol(SqlParserPos.ZERO)
                                                , right
                                                , conditionType
                                                , null);
                } else {
                    this.sqlNode = new SqlJoin(SqlParserPos.ZERO, left
                                                , SqlLiteral.createBoolean(false, SqlParserPos.ZERO)
                                                , JoinType.INNER.symbol(SqlParserPos.ZERO), right
                                                , conditionType
                                                , condition);
                }
                break;
            case LEFT_OUTER_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO,
                                           left,
                                           SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
                                           JoinType.LEFT.symbol(SqlParserPos.ZERO),
                                           right,
                                           conditionType,
                                           condition);
                break;
            case RIGHT_OUTER_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO,
                                           left,
                                           SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
                                           JoinType.RIGHT.symbol(SqlParserPos.ZERO),
                                           right,
                                           conditionType,
                                           condition);
                break;
            case NATURAL_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO,
                                           left,
                                           SqlLiteral.createBoolean(true, SqlParserPos.ZERO),
                                           JoinType.COMMA.symbol(SqlParserPos.ZERO),
                                           right,
                                           JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
                                           null);
                break;
            case CROSS_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO,
                        left,
                        SqlLiteral.createBoolean(false, SqlParserPos.ZERO),
                        JoinType.CROSS.symbol(SqlParserPos.ZERO),
                        right,
                        JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
                        null);
                break;
            case NATURAL_CROSS_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO,
                        left,
                        SqlLiteral.createBoolean(true, SqlParserPos.ZERO),
                        JoinType.CROSS.symbol(SqlParserPos.ZERO),
                        right,
                        JoinConditionType.NONE.symbol(SqlParserPos.ZERO),
                        null);
                break;
            case FULL_OUTER_JOIN:
                this.sqlNode = new SqlJoin(SqlParserPos.ZERO
                        , left
                        , SqlLiteral.createBoolean(false, SqlParserPos.ZERO)
                        , JoinType.FULL.symbol(SqlParserPos.ZERO)
                        , right
                        , condition == null
                            ? JoinConditionType.NONE.symbol(SqlParserPos.ZERO)
                            : conditionType
                            , condition);
                break;
            default:
                throw new UnsupportedOperationException("unsupported : " + joinType);
        }

        return false;
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        sqlNode = convertToSqlNode(x.getSelect());

        final String alias = x.getAlias();
        if (alias != null) {
            SqlIdentifier aliasIdentifier = new SqlIdentifier(alias, SqlParserPos.ZERO);

            List<SQLName> columns = x.getColumns();

            SqlNode[] operands;
            if (columns.size() == 0) {
                operands = new SqlNode[] { sqlNode, aliasIdentifier };
            } else {
                operands = new SqlNode[columns.size() + 2];
                operands[0] = sqlNode;
                operands[1] = aliasIdentifier;
                for (int i = 0; i < columns.size(); i++) {
                    SQLName column = columns.get(i);
                    operands[i + 2] = new SqlIdentifier(
                            SQLUtils.normalize(column.getSimpleName()), SqlParserPos.ZERO);
                }
            }
            sqlNode = new SqlBasicCall(SqlStdOperatorTable.AS, operands, SqlParserPos.ZERO);
        }

        return false;
    }

    public boolean visit(SQLUnionQueryTableSource x) {
        x.getUnion().accept(this);

        final String alias = x.getAlias();
        if (alias != null) {
            SqlIdentifier aliasIdentifier = new SqlIdentifier(alias, SqlParserPos.ZERO);
            sqlNode = new SqlBasicCall(SqlStdOperatorTable.AS,
                    new SqlNode[] { sqlNode, aliasIdentifier },
                    SqlParserPos.ZERO);
        }

        return false;
    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        SqlNode left = convertToSqlNode(x.getExpr());
        SqlBinaryOperator subOperator = SqlStdOperatorTable.IN;
        if (x.isNot()) {
            subOperator = SqlStdOperatorTable.NOT_IN;
        }
        SqlNode right = convertToSqlNode(x.subQuery);

        sqlNode = new SqlBasicCall(subOperator, new SqlNode[] { left, right }, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLCastExpr x) {
        SqlLiteral functionQualifier = null;

        SqlNode sqlNode = convertToSqlNode(x.getExpr());

        SQLDataType dataType = x.getDataType();
        String typeName = dataType.getName().toUpperCase();
        if (dataType.nameHashCode64() == FnvHash.Constants.INT) {
            typeName = "INTEGER";
        } else if (dataType.nameHashCode64() == FnvHash.Constants.NUMERIC) {
            typeName = "DECIMAL";
        }

        SqlIdentifier dataTypeNode = (SqlIdentifier)convertToSqlNode(
                new SQLIdentifierExpr(typeName));

        int scale = -1;
        int precision = -1;

        List<SQLExpr> arguments = dataType.getArguments();
        if (arguments != null && !arguments.isEmpty()) {
            scale = ((SQLNumericLiteralExpr)arguments.get(0)).getNumber().intValue();
            if (arguments.size() > 1) {
                precision = ((SQLNumericLiteralExpr) arguments.get(1)).getNumber().intValue();
            }
        }


        SqlDataTypeSpec sqlDataTypeSpec
                = new SqlDataTypeSpec(dataTypeNode, scale, precision, null, null, SqlParserPos.ZERO);

        SqlOperator sqlOperator = new SqlCastFunction();

        this.sqlNode = new com.alibaba.druid.support.calcite.CalciteSqlBasicCall(sqlOperator, new SqlNode[]{ sqlNode, sqlDataTypeSpec}, SqlParserPos.ZERO,
                                               false, functionQualifier);
        return false;
    }

    public boolean visit(SQLCaseExpr x) {// CASE WHEN
        SQLExpr valueExpr = x.getValueExpr();
        SqlNode nodeValue = null;
        SqlNodeList nodeWhen = new SqlNodeList(SqlParserPos.ZERO);
        SqlNodeList nodeThen = new SqlNodeList(SqlParserPos.ZERO);
        if (valueExpr != null) {
            nodeValue = convertToSqlNode(valueExpr);
        }

        List items = x.getItems();
        int elExpr = 0;

        for (int size = items.size(); elExpr < size; ++elExpr) {
            this.visit((SQLCaseExpr.Item) items.get(elExpr));
            if (this.sqlNode != null && this.sqlNode instanceof SqlNodeList) {
                SqlNodeList nodeListTemp = (SqlNodeList) this.sqlNode;
                nodeWhen.add(nodeListTemp.get(0));
                nodeThen.add(nodeListTemp.get(1));
            }
        }
        SQLExpr elseExpr = x.getElseExpr();
        SqlNode nodeElse = convertToSqlNode(elseExpr);
        SqlNodeList sqlNodeList = new SqlNodeList(SqlParserPos.ZERO);
        sqlNodeList.add(nodeValue);
        sqlNodeList.add(nodeWhen);
        sqlNodeList.add(nodeThen);
        sqlNodeList.add(nodeElse);
        sqlNode = SqlCase.createSwitched(SqlParserPos.ZERO, nodeValue, nodeWhen, nodeThen, nodeElse);
        return false;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        SQLExpr conditionExpr = x.getConditionExpr();
        SqlNode sqlNode1 = convertToSqlNode(conditionExpr);
        SQLExpr valueExpr = x.getValueExpr();
        SqlNode sqlNode2 = convertToSqlNode(valueExpr);
        SqlNodeList sqlNodeList = new SqlNodeList(SqlParserPos.ZERO);
        sqlNodeList.add(sqlNode1);
        sqlNodeList.add(sqlNode2);
        sqlNode = sqlNodeList;
        return false;
    }

    public boolean visit(SQLListExpr x) {
        List<SQLExpr> items = x.getItems();
        List<SqlNode> objects = new ArrayList<SqlNode>();
        for (int i = 0; i < items.size(); i++) {
            SQLExpr sqlExpr = items.get(i);
            SqlNode sqlNode = convertToSqlNode(sqlExpr);
            objects.add(sqlNode);
        }
        sqlNode = SqlStdOperatorTable.ROW.createCall(SqlParserPos.ZERO, objects);
        return false;
    }

    @Override
    public boolean visit(SQLSelect x) {
        SQLWithSubqueryClause with = x.getWithSubQuery();
        if (with != null) {
            SqlNodeList withList = new SqlNodeList(SqlParserPos.ZERO);
            final List<SQLWithSubqueryClause.Entry> entries = with.getEntries();
            for (SQLWithSubqueryClause.Entry entry : entries) {
                visit(entry);
                withList.add(sqlNode);
            }
            SqlNode query = convertToSqlNode(x.getQuery());

            if (query instanceof SqlOrderBy) {
                SqlOrderBy orderBy = (SqlOrderBy) query;

                SqlWith w = new SqlWith(SqlParserPos.ZERO, withList, orderBy.query);
                sqlNode = new SqlOrderBy(SqlParserPos.ZERO
                        , w
                        , orderBy.orderList
                        , orderBy.offset
                        , orderBy.fetch
                );
            } else {
                sqlNode = new SqlWith(SqlParserPos.ZERO, withList, query);
            }

            if(query instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) query;
                SqlNode fetch = select.getFetch();
                SqlNodeList orderList = select.getOrderList();

                if (fetch != null
                        || (orderList != null && orderList.size() > 0)) {
                    SqlNodeList orderByList = null;
                    if (orderList != null) {
                        orderByList = new SqlNodeList(orderList.getList(), SqlParserPos.ZERO);
                        orderList.getList().clear();
                    } else {
                        orderByList = SqlNodeList.EMPTY;
                    }

                    sqlNode = new SqlOrderBy(SqlParserPos.ZERO
                            , sqlNode
                            , orderByList
                            , null
                            , fetch
                    );

                    if (fetch != null) {
                        select.setFetch(null);
                    }
                }
            }

        } else {
            sqlNode = convertToSqlNode(x.getQuery());
        }

        return false;
    }

    public boolean visit(SQLWithSubqueryClause.Entry x) {
        SqlNodeList columnList = null;
        final List<SQLName> columns = x.getColumns();
        if (columns.size() > 0) {
            columnList = new SqlNodeList(SqlParserPos.ZERO);
            for (SQLName column : columns) {
                columnList.add(new SqlIdentifier(column.getSimpleName(), SqlParserPos.ZERO));
            }
        }
        SqlNode query = convertToSqlNode(x.getSubQuery());
        SqlIdentifier name = new SqlIdentifier(x.getAlias(), SqlParserPos.ZERO);
        sqlNode = new SqlWithItem(SqlParserPos.ZERO, name, columnList, query);
        return false;
    }

    @Override
    public boolean visit(SQLSelectStatement x) {

        SqlNode sqlNode = convertToSqlNode(x.getSelect());

        if(sqlNode instanceof com.alibaba.druid.support.calcite.TDDLSqlSelect) {
            com.alibaba.druid.support.calcite.TDDLSqlSelect select = (com.alibaba.druid.support.calcite.TDDLSqlSelect) sqlNode;

            SqlNodeList headHints = convertHints(x.getHeadHintsDirect());
            select.setHeadHints(headHints);
            this.sqlNode = select;
        } else {
            this.sqlNode = sqlNode;
        }

        return false;
    }

    protected void visit(SQLSelectQuery x) {
        Class<?> clazz = x.getClass();
        if (clazz == MySqlSelectQueryBlock.class) {
            visit((MySqlSelectQueryBlock) x);
        } else if (clazz == SQLUnionQuery.class) {
            visit((SQLUnionQuery) x);
        } else {
            x.accept(this);
        }
    }

    public boolean visit(SQLAllExpr x) {
        sqlNode = convertToSqlNode(x.getSubQuery());
        return false;
    }

    public boolean visit(SQLAnyExpr x) {
        sqlNode = convertToSqlNode(x.getSubQuery());
        return false;
    }

    private boolean isSqlAllExpr(SQLExpr x) {
        return x.getClass() == SQLAllExpr.class;
    }

    private boolean isAnyOrSomeExpr(SQLExpr x) {
        return x.getClass() == SQLAnyExpr.class || x.getClass() == SQLSomeExpr.class;
    }

    public boolean visit(SQLSelectItem x) {
        SQLExpr expr = x.getExpr();

        if (expr instanceof SQLIdentifierExpr) {
            visit((SQLIdentifierExpr) expr);
        } else if (expr instanceof SQLPropertyExpr) {
            visit((SQLPropertyExpr) expr);
        } else if (expr instanceof SQLAggregateExpr) {
            visit((SQLAggregateExpr) expr);
        } else {
            expr.accept(this);
        } // select a + (select count(1) from b) as mm from c;
        // select a + (select COUNT(1) from b) as 'a + (select count(1) as
        // 'count(1)' from b)' from c;
        String alias = x.getAlias();
        if (alias != null && alias.length() > 0) {
            String alias2 = x.getAlias2();
            sqlNode = new SqlBasicCall(SqlStdOperatorTable.AS,
                    new SqlNode[] { sqlNode, new SqlIdentifier(SQLUtils.normalize(alias2, DbType.mysql), SqlParserPos.ZERO) },
                    SqlParserPos.ZERO);
        }

        return false;
    }

    @Override public boolean visit(SQLIdentifierExpr x) {
        if (x.getName().equalsIgnoreCase("unknown")) {
            sqlNode = SqlLiteral.createUnknown(SqlParserPos.ZERO);
            return false;
        }
        sqlNode = buildIdentifier(x);
        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        sqlNode = buildIdentifier(x);
        return false;
    }

    SqlIdentifier buildIdentifier(SQLIdentifierExpr x) {
        return new SqlIdentifier(SQLUtils.normalize(x.getName()), SqlParserPos.ZERO);
    }

    SqlIdentifier buildIdentifier(SQLPropertyExpr x) {
        String name = SQLUtils.normalize(x.getName());
        if ("*".equals(name)) {
            name = "";
        }

        SQLExpr owner = x.getOwner();

        List<String> names;
        if (owner instanceof SQLIdentifierExpr) {
            names = Arrays.asList(((SQLIdentifierExpr) owner).normalizedName(), name);
        } else if (owner instanceof SQLPropertyExpr) {
            names = new ArrayList<String>();
            buildIdentifier((SQLPropertyExpr) owner, names);
            names.add(name);
        } else {
            throw new FastsqlException("not support : " + owner);
        }

        return new SqlIdentifier(names, SqlParserPos.ZERO);
    }

    void buildIdentifier(SQLPropertyExpr x, List<String> names) {
        String name = SQLUtils.normalize(x.getName());

        SQLExpr owner = x.getOwner();
        if (owner instanceof SQLIdentifierExpr) {
            names.add(((SQLIdentifierExpr) owner).normalizedName());
        } else if (owner instanceof SQLPropertyExpr) {
            buildIdentifier((SQLPropertyExpr) owner, names);
        } else {
            throw new FastsqlException("not support : " + owner);
        }

        names.add(name);
    }

    public boolean visit(SQLBinaryOpExprGroup x) {
        SqlOperator operator = null;
        switch (x.getOperator()) {
            case BooleanAnd:
                operator = SqlStdOperatorTable.AND;
                break;
            case BooleanOr:
                operator = SqlStdOperatorTable.OR;
                break;
            default:
                break;
        }

        final List<SQLExpr> items = x.getItems();
        SqlNode group = null;
        for (int i = 0; i < items.size(); i++) {
            SQLExpr item = items.get(i);
            final SqlNode calciteNode = convertToSqlNode(item);
            if (group == null) {
                group = calciteNode;
            } else {
                group = new SqlBasicCall(operator, new SqlNode[] {group, calciteNode}, SqlParserPos.ZERO);;
            }
        }
        this.sqlNode = group;
        return false;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        SqlOperator operator = null;

        SqlQuantifyOperator someOrAllOperator = null;

        SqlNode left = convertToSqlNode(x.getLeft());

        SQLExpr rightExpr = x.getRight();
        SqlNode right = convertToSqlNode(rightExpr);

        switch (x.getOperator()) {
            case Equality:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_EQ;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_EQ;
                } else {
                    operator = SqlStdOperatorTable.EQUALS;
                }
                break;
            case GreaterThan:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_GT;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_GT;
                } else {
                    operator = SqlStdOperatorTable.GREATER_THAN;
                }
                break;
            case GreaterThanOrEqual:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_GE;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_GE;
                } else {
                    operator = SqlStdOperatorTable.GREATER_THAN_OR_EQUAL;
                }
                break;
            case LessThan:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_LT;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_LT;
                } else {
                    operator = SqlStdOperatorTable.LESS_THAN;
                }
                break;
            case LessThanOrEqual:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_LE;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_LE;
                } else {
                    operator = SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
                }
                break;
            case NotEqual:
            case LessThanOrGreater:
                if (isSqlAllExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.ALL_NE;
                } else if (isAnyOrSomeExpr(rightExpr)) {
                    someOrAllOperator = SqlStdOperatorTable.SOME_NE;
                } else {
                    operator = SqlStdOperatorTable.NOT_EQUALS;
                }
                break;
            case Add:
                operator = SqlStdOperatorTable.PLUS;
                break;
            case Subtract:
                operator = SqlStdOperatorTable.MINUS;
                break;
            case Multiply:
                operator = SqlStdOperatorTable.MULTIPLY;
                break;
            case Divide:
                operator = SqlStdOperatorTable.DIVIDE;
                break;
            case Modulus:
                operator = SqlStdOperatorTable.MOD;
                break;
            case Like:
                operator = SqlStdOperatorTable.LIKE;
                break;
            case NotLike:
                operator = SqlStdOperatorTable.NOT_LIKE;
                break;
            case BooleanAnd:
                operator = SqlStdOperatorTable.AND;
                break;
            case BooleanOr:
                operator = SqlStdOperatorTable.OR;
                break;
            case Concat:
                operator = SqlStdOperatorTable.CONCAT;
                break;
            case Is: {
                if (rightExpr instanceof SQLNullExpr) {
                    operator = SqlStdOperatorTable.IS_NULL;
                } else if (rightExpr instanceof SQLIdentifierExpr) {
                    long hashCode64 = ((SQLIdentifierExpr) rightExpr).nameHashCode64();
                    if (hashCode64 == FnvHash.Constants.JSON
                            || hashCode64 == JSON_VALUE) {
                        operator = SqlStdOperatorTable.IS_JSON_VALUE;
                    } else if (hashCode64 == JSON_OBJECT) {
                        operator = SqlStdOperatorTable.IS_JSON_OBJECT;
                    } else if (hashCode64 == JSON_ARRAY) {
                        operator = SqlStdOperatorTable.IS_JSON_ARRAY;
                    } else if (hashCode64 == JSON_SCALAR) {
                        operator = SqlStdOperatorTable.IS_JSON_SCALAR;
                    } else if (hashCode64 == FnvHash.Constants.UNKNOWN) {
                        operator = SqlStdOperatorTable.IS_UNKNOWN;
                    }
                } else if (rightExpr instanceof SQLBooleanExpr) {
                    if (((SQLBooleanExpr) rightExpr).getValue()) {
                        operator = SqlStdOperatorTable.IS_TRUE;
                    } else {
                        operator = SqlStdOperatorTable.IS_FALSE;
                    }
                }
            }
                break;
            case IsNot:
                if(rightExpr instanceof SQLNullExpr) {
                    operator = SqlStdOperatorTable.IS_NOT_NULL;
                } else if (rightExpr instanceof SQLIdentifierExpr) {
                    long hashCode64 = ((SQLIdentifierExpr) rightExpr).nameHashCode64();
                    if (hashCode64 == FnvHash.Constants.JSON
                            || hashCode64 == JSON_VALUE) {
                        operator = SqlStdOperatorTable.IS_NOT_JSON_VALUE;
                    } else if (hashCode64 == JSON_OBJECT) {
                        operator = SqlStdOperatorTable.IS_NOT_JSON_OBJECT;
                    } else if (hashCode64 == JSON_ARRAY) {
                        operator = SqlStdOperatorTable.IS_NOT_JSON_ARRAY;
                    } else if (hashCode64 == JSON_SCALAR) {
                        operator = SqlStdOperatorTable.IS_NOT_JSON_SCALAR;
                    } else if (hashCode64 == FnvHash.Constants.UNKNOWN) {
                        operator = SqlStdOperatorTable.IS_NOT_UNKNOWN;
                    }
                } else if(rightExpr instanceof SQLBooleanExpr){
                    if(((SQLBooleanExpr) rightExpr).getValue()){
                        operator = SqlStdOperatorTable.IS_NOT_TRUE;
                    } else {
                        operator = SqlStdOperatorTable.IS_NOT_FALSE;
                    }
                }
                break;
            case Escape: {
                SqlBasicCall like = (SqlBasicCall) left;
                sqlNode = new SqlBasicCall(like.getOperator(), new SqlNode[] { like.operands[0], like.operands[1], right },
                        SqlParserPos.ZERO);
                return false;
            }
            default:
                throw new FastsqlException("not support " + x.getOperator());

        }


        if (someOrAllOperator != null) {
            this.sqlNode = new SqlBasicCall(someOrAllOperator, new SqlNode[] { left, right },
                    SqlParserPos.ZERO);
        } else {
            if(operator == SqlStdOperatorTable.IS_NULL
                    || operator == SqlStdOperatorTable.IS_NOT_NULL
                    || operator == SqlStdOperatorTable.IS_TRUE
                    || operator == SqlStdOperatorTable.IS_NOT_TRUE) {
                this.sqlNode = new SqlBasicCall(operator,
                        new SqlNode[]{left},
                        SqlParserPos.ZERO);
            } else {
                this.sqlNode = new SqlBasicCall(operator,
                        new SqlNode[]{left,right},
                        SqlParserPos.ZERO);
            }
        }
        return false;
    }

    public boolean visit(SQLBetweenExpr x) {
        SQLExpr testExpr = x.getTestExpr();
        SqlOperator sqlOperator = SqlStdOperatorTable.BETWEEN;
        if (x.isNot()) {
            sqlOperator = SqlStdOperatorTable.NOT_BETWEEN;
        }
        SqlNode sqlNode = convertToSqlNode(testExpr);
        SqlNode sqlNodeBegin = convertToSqlNode(x.getBeginExpr());
        SqlNode sqlNodeEnd = convertToSqlNode(x.getEndExpr());
        ArrayList<SqlNode> sqlNodes = new ArrayList<SqlNode>(3);
        sqlNodes.add(sqlNode);
        sqlNodes.add(sqlNodeBegin);
        sqlNodes.add(sqlNodeEnd);
        this.sqlNode = new SqlBasicCall(sqlOperator, SqlParserUtil.toNodeArray(sqlNodes), SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLExistsExpr x) {
        SqlOperator sqlOperator = SqlStdOperatorTable.EXISTS;
        SqlNode sqlNode = sqlOperator.createCall(SqlParserPos.ZERO,
                convertToSqlNode(x.getSubQuery()));
        if(x.isNot()){
            sqlNode = SqlStdOperatorTable.NOT.createCall(SqlParserPos.ZERO,sqlNode);
        }
        this.sqlNode = sqlNode;
        return false;
    }

    public boolean visit(SQLAllColumnExpr x) {
        sqlNode = new SqlIdentifier(Arrays.asList(""), SqlParserPos.ZERO);

        return false;
    }

    public boolean visit(SQLCharExpr x) {
        String text = x.getText();
        text = text.replaceAll("\\\\", "\\\\\\\\");
        sqlNode = SqlLiteral.createCharString(text, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLNCharExpr x) {
        String text = x.getText();
        text = text.replaceAll("\\\\", "\\\\\\\\");
        sqlNode = SqlLiteral.createCharString(text, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLNullExpr x) {
        sqlNode = SqlLiteral.createNull(SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLIntegerExpr x) {
        sqlNode = SqlLiteral.createExactNumeric(x.getNumber().toString(), SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLBooleanExpr x) {
        sqlNode = SqlLiteral.createBoolean(x.getBooleanValue(), SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLNumberExpr x) {
        String str = x.toString();
        if (str.indexOf('E') > 0 || str.indexOf('e') > 0) {
            sqlNode = SqlLiteral.createApproxNumeric(str, SqlParserPos.ZERO);
        } else {
            sqlNode = SqlLiteral.createExactNumeric(str, SqlParserPos.ZERO);
        }
        return false;
    }

    public boolean visit(SQLTimestampExpr x) {
        String literal = x.getLiteral();
        int precision = 0;
        if (literal.endsWith("00")) {
            char c3 = literal.charAt(literal.length() - 3);
            if (c3 >= '0' && c3 <= '9') {
                literal = literal.substring(0, literal.length() - 2);
                precision = 3;
            }
        }
        TimestampString ts = new TimestampString(literal);
        sqlNode = SqlLiteral.createTimestamp(ts, precision, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLDateExpr x) {
        String literal = x.getLiteral();
        DateString ds = new DateString(literal);
        sqlNode = SqlLiteral.createDate(ds, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLTimeExpr x) {
        String literal = ((SQLCharExpr) x.getLiteral()).getText();
        TimeString ds = new TimeString(literal);
        sqlNode = SqlLiteral.createTime(ds, 0, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLCurrentTimeExpr x) {
        sqlNode = new SqlIdentifier(x.getType().name, SqlParserPos.ZERO);
        return false;
    }

    public boolean visit(SQLAggregateExpr x) {
        SqlOperator functionOperator;

        String methodName = x.getMethodName();


        long hashCode64 = x.methodNameHashCode64();

        functionOperator = func(hashCode64);

        if (functionOperator == null) {
            functionOperator = new SqlUnresolvedFunction(new SqlIdentifier(methodName, SqlParserPos.ZERO),
                    null,
                    null,
                    null,
                    null,
                    SqlFunctionCategory.USER_DEFINED_FUNCTION);
        }

        SqlLiteral functionQualifier = null;

        if (x.getOption() == SQLAggregateOption.DISTINCT) {
            functionQualifier = SqlSelectKeyword.DISTINCT.symbol(SqlParserPos.ZERO);
        }
        List<SQLExpr> arguments = x.getArguments();
        List<SqlNode> argNodes = new ArrayList<SqlNode>(arguments.size());
        for (int i = 0, size = arguments.size(); i < size; ++i) {
            argNodes.add(convertToSqlNode(arguments.get(i)));
        }
        this.sqlNode = functionOperator.createCall(functionQualifier,
                SqlParserPos.ZERO,
                SqlParserUtil.toNodeArray(argNodes)
                );

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            SqlNodeList orderByItems = convertOrderby(orderBy);

            this.sqlNode = SqlStdOperatorTable.WITHIN_GROUP
                    .createCall(SqlParserPos.ZERO, this.sqlNode, orderByItems);
        }

        SQLOver over = x.getOver();
        if (over != null) {
            SqlNode aggNode = this.sqlNode;
            SQLOver.WindowingBound windowingBetweenBeginBound = over.getWindowingBetweenBeginBound();
            SQLOver.WindowingBound windowingBetweenEndBound = over.getWindowingBetweenEndBound();

            boolean isRow = over.getWindowingType() != SQLOver.WindowingType.RANGE;
            SqlNode lowerBound;
            if (over.getWindowingBetweenBegin() != null) {
                over.getWindowingBetweenBegin().accept(this);
                lowerBound = SqlWindow.createPreceding(sqlNode, SqlParserPos.ZERO);
            } else {
                lowerBound = createSymbol(windowingBetweenBeginBound);
            }
            SqlNode upperBound = createSymbol(windowingBetweenEndBound);

            SqlWindow window = new SqlWindow(SqlParserPos.ZERO
                    , null
                    , null
                    , convertToSqlNodeList(over.getPartitionBy())
                    , convertOrderby(over.getOrderBy())
                    , SqlLiteral.createBoolean(isRow, SqlParserPos.ZERO)
                    , lowerBound
                    , upperBound
                    , null
            );
            sqlNode = SqlStdOperatorTable.OVER.createCall(
                    SqlParserPos.ZERO,
                    aggNode,
                    window);
        }


        SQLExpr filter = x.getFilter();
        if (filter != null) {
            SqlNode aggNode = this.sqlNode;

            filter.accept(this);
            sqlNode = SqlStdOperatorTable.FILTER.createCall(
                    SqlParserPos.ZERO,
                    aggNode,
                    sqlNode);
        }


        return false;
    }

    protected static SqlNode createSymbol(SQLOver.WindowingBound bound) {
        if (bound == null) {
            return null;
        }

        switch (bound) {
            case CURRENT_ROW:
                return SqlWindow.createCurrentRow(SqlParserPos.ZERO);
            case UNBOUNDED_FOLLOWING:
                return SqlWindow.createUnboundedFollowing(SqlParserPos.ZERO);
            case UNBOUNDED_PRECEDING:
                return SqlWindow.createUnboundedPreceding(SqlParserPos.ZERO);
            default:
                return null;
        }
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        List<SQLExpr> arguments = x.getArguments();
        List<SqlNode> argNodes = new ArrayList<SqlNode>(arguments.size());

        long nameHashCode64 = x.methodNameHashCode64();

        SqlOperator functionOperator = func(nameHashCode64);


        String methodName = x.getMethodName();

        if (functionOperator == null) {
            if (nameHashCode64 == FnvHash.Constants.TRIM) {
                functionOperator = SqlStdOperatorTable.TRIM;
                if (arguments.size() == 1) {
                    SqlNode sqlNode = convertToSqlNode(arguments.get(0));

                    this.sqlNode = new com.alibaba.druid.support.calcite.CalciteSqlBasicCall(functionOperator,
                            new SqlNode[]{
                                    SqlLiteral.createSymbol(SqlTrimFunction.Flag.BOTH, SqlParserPos.ZERO)
                                    , SqlCharStringLiteral.createCharString(" ", SqlParserPos.ZERO)
                                    , sqlNode
                            },
                            SqlParserPos.ZERO,
                            false,
                            null);
                    return false;
                }

            } else {
                functionOperator = new SqlUnresolvedFunction(
                        new SqlIdentifier(methodName, SqlParserPos.ZERO),
                        null,
                        null,
                        null,
                        null,
                        SqlFunctionCategory.USER_DEFINED_FUNCTION);
            }
        }

        SqlLiteral functionQualifier = null;


        for (SQLExpr exp : arguments) {
            argNodes.add(convertToSqlNode(exp));
        }

        if ((nameHashCode64 == FnvHash.Constants.TIMESTAMPDIFF || nameHashCode64 == FnvHash.Constants.TIMESTAMPADD)
                && argNodes.size() > 0
                && argNodes.get(0) instanceof SqlIdentifier
        ) {
            SqlIdentifier arg0 = (SqlIdentifier) argNodes.get(0);
            TimeUnit timeUnit = TimeUnit.valueOf(arg0.toString().toUpperCase());
            argNodes.set(0
                    , SqlLiteral.createSymbol(timeUnit, SqlParserPos.ZERO)
            );
        }

        this.sqlNode = new com.alibaba.druid.support.calcite.CalciteSqlBasicCall(functionOperator,
                                               SqlParserUtil.toNodeArray(argNodes),
                                               SqlParserPos.ZERO,
                                               false,
                                               functionQualifier);
        return false;
    }

    public boolean visit(SQLInListExpr x) {
        SqlNodeList sqlNodes = convertToSqlNodeList(x.getTargetList());
        SqlOperator sqlOperator = x.isNot() ? SqlStdOperatorTable.NOT_IN : SqlStdOperatorTable.IN;
        sqlNode = new SqlBasicCall(sqlOperator, new SqlNode[] { convertToSqlNode(x.getExpr()), sqlNodes },
                                   SqlParserPos.ZERO);

        return false;
    }

    public boolean visit(SQLVariantRefExpr x){
        if ("?".equals(x.getName())) {
            this.sqlNode = new SqlDynamicParam(x.getIndex(),
                    SqlParserPos.ZERO);
            return false;
        } else {
            System.out.println("end");
        }
        return false;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        SQLUnaryOperator operator = x.getOperator();
        switch (operator) {
            case NOT:
                this.sqlNode = SqlStdOperatorTable.NOT.createCall(SqlParserPos.ZERO,
                                                                  convertToSqlNode(x.getExpr()));
                break;
            case Negative:
                this.sqlNode = SqlStdOperatorTable.UNARY_MINUS.createCall(SqlParserPos.ZERO,
                                                                          convertToSqlNode(x.getExpr()));
                break;
            case Not:
            case Compl:
            case BINARY:
            default:
                super.visit(x);
        }
        return false;
    }

    protected SqlNodeList convertToSqlNodeList(SQLExpr expr) {
        if (expr instanceof SQLListExpr) {
            return convertToSqlNodeList(((SQLListExpr) expr).getItems());
        } else {
            List<SqlNode> nodes = new ArrayList<SqlNode>(1);
            return new SqlNodeList(nodes, SqlParserPos.ZERO);
        }
    }

    protected SqlNodeList convertToSqlNodeList(List<? extends SQLExpr> exprList) {
        final int size = exprList.size();

        List<SqlNode> nodes = new ArrayList<SqlNode>(size);
        for (int i = 0; i < size; ++i) {
            SQLExpr expr = exprList.get(i);
            SqlNode node;
            if (expr instanceof SQLListExpr) {
                node = convertToSqlNodeList(((SQLListExpr) expr).getItems());
            } else {
                node = convertToSqlNode(expr);
            }
            nodes.add(node);
        }

        return new SqlNodeList(nodes, SqlParserPos.ZERO);
    }


    protected SqlNode convertToSqlNode(SQLObject ast) {
        if (ast == null) {
            return null;
        }
        CalciteMySqlNodeVisitor visitor = new CalciteMySqlNodeVisitor();
        ast.accept(visitor);
        return visitor.getSqlNode();
    }

    private SqlNodeList convertOrderby(SQLOrderBy orderBy) {
        if (orderBy == null) {
            return new SqlNodeList(new ArrayList(), SqlParserPos.ZERO);
        }

        List<SQLSelectOrderByItem> items = orderBy.getItems();
        List<SqlNode> orderByNodes = new ArrayList<SqlNode>(items.size());

        for (SQLSelectOrderByItem item : items) {
            SqlNode node = convertToSqlNode(item.getExpr());
            if (item.getType() == SQLOrderingSpecification.DESC) {
                node = new SqlBasicCall(SqlStdOperatorTable.DESC, new SqlNode[] { node }, SqlParserPos.ZERO);
            }
            SQLSelectOrderByItem.NullsOrderType nullsOrderType = item.getNullsOrderType();
            if (nullsOrderType != null) {
                switch (nullsOrderType) {
                    case NullsFirst:
                        node = new SqlBasicCall(SqlStdOperatorTable.NULLS_FIRST, new SqlNode[] { node }, SqlParserPos.ZERO);
                        break;
                    case NullsLast:
                        node = new SqlBasicCall(SqlStdOperatorTable.NULLS_LAST, new SqlNode[] { node }, SqlParserPos.ZERO);
                        break;
                    default:
                        break;
                }
            }
            orderByNodes.add(node);
        }

        return new SqlNodeList(orderByNodes, SqlParserPos.ZERO);
    }

    private SqlNodeList convertHints(List<SQLCommentHint> hints) {
        if (hints == null) {
            return null;
        }

        List<SqlNode> nodes = new ArrayList<SqlNode>(hints.size());

        for (SQLCommentHint hint : hints) {
            if (hint instanceof TDDLHint) {
                nodes.add(convertTDDLHint((TDDLHint) hint));
            }
        }

        return new SqlNodeList(nodes, SqlParserPos.ZERO);

    }

    private SqlNodeList convertTDDLHint(TDDLHint hint) {

        List<TDDLHint.Function> functions = hint.getFunctions();
        List<SqlNode> funNodes = new ArrayList<SqlNode>(functions.size());

        for (TDDLHint.Function function : functions) {
            String functionName = function.getName();

            List<TDDLHint.Argument> arguments = function.getArguments();

            SqlNode[] argNodes = new SqlNode[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                TDDLHint.Argument argument = arguments.get(i);
                SqlNode argName = convertToSqlNode(argument.getName());
                SqlNode argValue = convertToSqlNode(argument.getValue());

                List<SqlNode> arg = new ArrayList<SqlNode>();
                if(argName != null) {
                    arg.add(argName);
                }
                if(argValue != null) {
                    arg.add(argValue);
                }

                SqlNode argNode = null;
                if (arg.size() == 2) {
                    argNode = SqlStdOperatorTable.EQUALS.createCall(SqlParserPos.ZERO, arg);
                } else if (arg.size() == 1) {
                    argNode = argName;
                }

                argNodes[i] = argNode;
            }

            SqlNode funNode = new SqlBasicCall(
                    new SqlUnresolvedFunction(new SqlIdentifier(functionName, SqlParserPos.ZERO), null, null,
                                              null, null, SqlFunctionCategory.USER_DEFINED_FUNCTION), argNodes,
                    SqlParserPos.ZERO);

            funNodes.add(funNode);
        }

        return new SqlNodeList(funNodes, SqlParserPos.ZERO);
    }

    /**
     * If there are multiple VALUES, and all values in VALUES CLAUSE are literal,
     * convert the value clauses to a single value clause.
     * @param valuesClauseList
     * @return
     */
    public static List<SQLInsertStatement.ValuesClause> convertToSingleValuesIfNeed(List<SQLInsertStatement.ValuesClause> valuesClauseList) {
        if (valuesClauseList.size() <= 1) {
            return valuesClauseList;
        }

        // If they are all literals
        for (SQLInsertStatement.ValuesClause clause : valuesClauseList) {
            for (SQLExpr expr : clause.getValues()) {
                if (expr instanceof SQLVariantRefExpr) {
                    if (((SQLVariantRefExpr) expr).getName().equals("?")) {
                        continue;
                    }
                }
                return valuesClauseList;
            }
        }

        // Return only the first values clause.
        return Arrays.asList(valuesClauseList.get(0));
    }

    public boolean visit(SQLIntervalExpr x) {
        TimeUnit timeUnits[] = getTimeUnit(x.getUnit());
        List<SqlNode> convertedArgs = new ArrayList<SqlNode>(2);
        SqlIntervalQualifier unitNode = new SqlIntervalQualifier(timeUnits[0], timeUnits[1], SqlParserPos.ZERO);
        SqlLiteral valueNode = (SqlLiteral) convertToSqlNode(x.getValue());
        sqlNode = SqlIntervalLiteral.createInterval(1, valueNode.toValue(), unitNode, SqlParserPos.ZERO);
        return false;
    }

    public static TimeUnit[] getTimeUnit(SQLIntervalUnit unit) {
        TimeUnit[] timeUnits = new TimeUnit[2];
        switch (unit) {
            // case MICROSECOND:
            // timeUnits[0] = TimeUnit.MICROSECOND;
            // timeUnits[1] = TimeUnit.MICROSECOND;
            // break;
            case SECOND:
                timeUnits[0] = TimeUnit.SECOND;
                timeUnits[1] = TimeUnit.SECOND;
                break;
            case MINUTE:
                timeUnits[0] = TimeUnit.MINUTE;
                timeUnits[1] = TimeUnit.MINUTE;
                break;
            case HOUR:
                timeUnits[0] = TimeUnit.HOUR;
                timeUnits[1] = TimeUnit.HOUR;
                break;
            case DAY:
                timeUnits[0] = TimeUnit.DAY;
                timeUnits[1] = TimeUnit.DAY;
                break;
            case WEEK:
                timeUnits[0] = TimeUnit.WEEK;
                timeUnits[1] = TimeUnit.WEEK;
                break;
            case MONTH:
                timeUnits[0] = TimeUnit.MONTH;
                timeUnits[1] = TimeUnit.MONTH;
                break;
            case QUARTER:
                timeUnits[0] = TimeUnit.QUARTER;
                timeUnits[1] = TimeUnit.QUARTER;
                break;
            case YEAR:
                timeUnits[0] = TimeUnit.YEAR;
                timeUnits[1] = TimeUnit.YEAR;
                break;
            // case MINUTE_MICROSECOND:
            // timeUnits[0] = TimeUnit.MINUTE;
            // timeUnits[1] = TimeUnit.MICROSECOND;
            // break;
            case MINUTE_SECOND:
                timeUnits[0] = TimeUnit.MINUTE;
                timeUnits[1] = TimeUnit.SECOND;
                break;
            // case HOUR_MICROSECOND:
            // timeUnits[0] = TimeUnit.HOUR;
            // timeUnits[1] = TimeUnit.MICROSECOND;
            // break;
            case HOUR_SECOND:
                timeUnits[0] = TimeUnit.HOUR;
                timeUnits[1] = TimeUnit.SECOND;
                break;
            case HOUR_MINUTE:
                timeUnits[0] = TimeUnit.HOUR;
                timeUnits[1] = TimeUnit.MINUTE;
                break;
            // case DAY_MICROSECOND:
            // timeUnits[0] = TimeUnit.DAY;
            // timeUnits[1] = TimeUnit.MICROSECOND;
            // break;
            case DAY_SECOND:
                timeUnits[0] = TimeUnit.DAY;
                timeUnits[1] = TimeUnit.SECOND;
                break;
            case DAY_MINUTE:
                timeUnits[0] = TimeUnit.DAY;
                timeUnits[1] = TimeUnit.MINUTE;
                break;
            case DAY_HOUR:
                timeUnits[0] = TimeUnit.DAY;
                timeUnits[1] = TimeUnit.HOUR;
                break;
            case YEAR_MONTH:
                timeUnits[0] = TimeUnit.YEAR;
                timeUnits[1] = TimeUnit.MONTH;
                break;
            default:
                throw new ParserException("Unsupported time unit");
        }
        return timeUnits;
    }

    public boolean visit(SQLNotExpr x) {
        SQLExpr expr = x.getExpr();
        if (expr instanceof SQLIdentifierExpr) {
            long hashCode64 = ((SQLIdentifierExpr) expr).nameHashCode64();
            if (hashCode64 == FnvHash.Constants.UNKNOWN) {
                sqlNode = SqlStdOperatorTable.NOT.createCall(SqlParserPos.ZERO, SqlLiteral.createUnknown(SqlParserPos.ZERO));
                return false;
            }
        }
        expr.accept(this);
        sqlNode = SqlStdOperatorTable.NOT.createCall(SqlParserPos.ZERO, sqlNode);
        return false;
    }

    @Override
    public boolean visit(SQLExtractExpr x) {
        x.getValue().accept(this);
        TimeUnit timeUnits[] = getTimeUnit(x.getUnit());

        sqlNode = SqlStdOperatorTable.EXTRACT
                .createCall(SqlParserPos.ZERO
                        , new SqlIntervalQualifier(timeUnits[0], timeUnits[1], SqlParserPos.ZERO)
                        , sqlNode);
        return false;
    }

    @Override
    public boolean visit(SQLGroupingSetExpr x) {
        sqlNode = SqlStdOperatorTable.GROUPING_SETS.createCall(SqlParserPos.ZERO
                , convertToSqlNodeList(x.getParameters())
        );
        return false;
    }

    @Override
    public boolean visit(SQLValuesQuery x) {
        List<SqlNode> valuesNodes = new ArrayList<SqlNode>();
        for (SQLExpr value : x.getValues()) {
            valuesNodes.add(
                    SqlStdOperatorTable.ROW.createCall(SqlParserPos.ZERO, convertToSqlNodeList(value)));
        }

        sqlNode = SqlStdOperatorTable.VALUES.createCall(SqlParserPos.ZERO, valuesNodes);
        return false;
    }


    @Override
    public boolean visit(SQLUnnestTableSource x) {
        sqlNode = SqlStdOperatorTable.UNNEST
                .createCall(SqlParserPos.ZERO
                        , convertToSqlNodeList(x.getItems()));

        String alias = x.getAlias();
        if (alias != null) {
            sqlNode = new SqlBasicCall(SqlStdOperatorTable.AS
                        , new SqlNode[] { sqlNode, new SqlIdentifier(alias, SqlParserPos.ZERO) }
                        , SqlParserPos.ZERO
            );
        }
        return false;
    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        sqlNode = SqlStdOperatorTable.DEFAULT.createCall(SqlParserPos.ZERO);
        return false;
    }

    @Override
    public boolean visit(MySqlExplainStatement x) {
        x.getStatement().accept(this);
        SqlNode explicandum = this.sqlNode;
        sqlNode = new SqlExplain(SqlParserPos.ZERO
                , explicandum
                , SqlLiteral.createSymbol(SqlExplainLevel.EXPPLAN_ATTRIBUTES, SqlParserPos.ZERO)
                , SqlLiteral.createSymbol(SqlExplain.Depth.PHYSICAL, SqlParserPos.ZERO)
                , SqlLiteral.createSymbol(SqlExplainFormat.TEXT, SqlParserPos.ZERO)
                , 0
        );
        return false;
    }

    static long JSON_VALUE = FnvHash.fnv1a_64_lower("JSON VALUE");
    static long JSON_OBJECT = FnvHash.fnv1a_64_lower("JSON OBJECT");
    static long JSON_ARRAY = FnvHash.fnv1a_64_lower("JSON ARRAY");
    static long JSON_SCALAR = FnvHash.fnv1a_64_lower("JSON SCALAR");
}
