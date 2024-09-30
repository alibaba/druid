package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateResourceStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StarRocksOutputVisitor extends SQLASTOutputVisitor implements StarRocksASTVisitor {
    {
        this.dbType = DbType.starrocks;
        this.shardingSupport = true;
        this.quote = '`';
    }

    public StarRocksOutputVisitor(StringBuilder appender) {
        super(appender);
    }

    public StarRocksOutputVisitor(StringBuilder appender, DbType dbType) {
        super(appender, dbType);
    }

    public StarRocksOutputVisitor(StringBuilder appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    protected void printEngine(SQLCreateTableStatement x) {
        if (x instanceof StarRocksCreateTableStatement) {
            SQLExpr engine = ((StarRocksCreateTableStatement) x).getEngine();
            if (engine != null) {
                print0(ucase ? " ENGINE = " : " engine = ");
                engine.accept(this);
            }
        }
    }

    public boolean visit(StarRocksCreateTableStatement x) {
        print0(ucase ? "CREATE " : "create ");
        printCreateTableFeatures(x);
        print0(ucase ? "TABLE " : "table ");
        if (x.isIfNotExists()) {
            print0(ucase ? "IF NOT EXISTS " : "if not exists ");
        }
        printTableSourceExpr(
                x.getTableSource()
                        .getExpr());
        printCreateTableAfterName(x);
        printTableElements(x.getTableElementList());
        printEngine(x);
        printKeyDesc(x);
        printComment(x.getComment());
        printPartitionBy(x);
        printDistributedBy(x);
        printOrderBy(x);
        printTableOptions(x);
        printSelectAs(x, true);
        return false;
    }

    protected void printKeyDesc(StarRocksCreateTableStatement x) {
        SQLName model = x.getAggDuplicate();
        if (model != null) {
            println();
            String modelName = model.getSimpleName().toLowerCase();
            switch (modelName) {
                case "duplicate":
                    print0(ucase ? "DUPLICATE" : "duplicate");
                    break;
                case "aggregate":
                    print0(ucase ? "AGGREGATE" : "aggregate");
                    break;
                default:
                    break;
            }
            print(' ');
            print0(ucase ? "KEY" : "key");
            if (x.getAggDuplicateParameters().size() > 0) {
                for (int i = 0; i < x.getAggDuplicateParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    SQLExpr sqlExpr = x.getAggDuplicateParameters().get(i);
                    if (!sqlExpr.toString().startsWith("(") && !sqlExpr.toString().startsWith("`")) {
                        print0("(");
                        sqlExpr.accept(this);
                        print0(")");
                    } else {
                        sqlExpr.accept(this);
                    }
                }
            }
        } else if (x.isPrimary()) {
            println();
            print0(ucase ? "PRIMARY" : "primary");
            print(' ');
            print0(ucase ? "KEY" : "key");
            if (x.getPrimaryUniqueParameters().size() > 0) {
                for (int i = 0; i < x.getPrimaryUniqueParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    SQLExpr sqlExpr = x.getPrimaryUniqueParameters().get(i);
                    sqlExpr.accept(this);
                }
            }
        } else if (x.isUnique()) {
            println();
            print0(ucase ? "UNIQUE" : "unique");
            print(' ');
            print0(ucase ? "KEY" : "key");
            if (x.getPrimaryUniqueParameters().size() > 0) {
                for (int i = 0; i < x.getPrimaryUniqueParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    SQLExpr sqlExpr = x.getPrimaryUniqueParameters().get(i);
                    sqlExpr.accept(this);
                }
            }
        }
    }

    @Override
    protected void printPartitionBy(SQLCreateTableStatement statement) {
        if (statement instanceof StarRocksCreateTableStatement) {
            StarRocksCreateTableStatement x = (StarRocksCreateTableStatement) statement;
            List<SQLExpr> partitionBy = x.getPartitionBy();
            if (partitionBy != null && partitionBy.size() > 0) {
                println();
                print0(ucase ? "PARTITION BY " : "partition by ");
                x.getPartitionByName().accept(this);

                print0("(");
                for (int i = 0; i < partitionBy.size(); i++) {
                    partitionBy.get(i).accept(this);
                    if (i != partitionBy.size() - 1) {
                        print0(",");
                    }
                }
                print0(") (");
                if (x.isLessThan()) {
                    println();
                    Map<SQLExpr, SQLExpr> lessThanMap = x.getLessThanMap();
                    Set<SQLExpr> keySet = lessThanMap.keySet();
                    int size = keySet.size();
                    if (size > 0) {
                        int i = 0;
                        for (SQLExpr key : keySet) {
                            if (i != 0) {
                                println(", ");
                            }
                            SQLObject value = lessThanMap.get(key);
                            print0(ucase ? "  PARTITION " : "  partition ");
                            key.accept(this);
                            print0(ucase ? " VALUES LESS THAN " : " values less than ");
                            String s = value.toString();
                            if (s.startsWith("MAXVALUE")) {
                                value.accept(this);
                            } else {
                                value.accept(this);
                            }
                            i++;
                        }
                    }
                    println();
                } else if (x.isFixedRange()) {
                    println();
                    Map<SQLExpr, List<SQLExpr>> fixedRangeMap = x.getFixedRangeMap();
                    Set<SQLExpr> keySet = fixedRangeMap.keySet();
                    int size = keySet.size();
                    if (size > 0) {
                        int i = 0;
                        for (SQLExpr key : keySet) {
                            List<SQLExpr> valueList = fixedRangeMap.get(key);
                            int listSize = valueList.size();

                            print0(ucase ? "  PARTITION " : "  partition ");
                            key.accept(this);
                            print0(ucase ? " VALUES " : " values ");
                            print0("[");

                            for (int j = 0; j < listSize; ++j) {
                                SQLExpr sqlExpr = valueList.get(j);
                                String[] split = sqlExpr.toString().split(",");

                                if (split.length <= 1) {
                                    print0("(");
                                    sqlExpr.accept(this);
                                    print0(")");
                                } else {
                                    sqlExpr.accept(this);
                                }

                                if (j != listSize - 1) {
                                    print0(",");
                                }

                            }
                            print0(")");

                            if (i != size - 1) {
                                print0(",");
                                println();
                            }
                            i++;

                        }
                    }
                    println();
                } else if (x.isStartEnd()) {
                    println();
                    if (x.getStart() != null) {
                        print0(ucase ? "  START " : "  start ");
                        x.getStart().accept(this);
                    }
                    if (x.getEnd() != null) {
                        print0(ucase ? "  END " : "  end ");
                        x.getEnd().accept(this);
                    }
                    if (x.getEvery() != null) {
                        print0(ucase ? "  EVERY " : "  every ");
                        x.getEvery().accept(this);
                    }
                    println();
                }
                print0(")");
            }
        }
    }

    protected void printDistributedBy(StarRocksCreateTableStatement x) {
        if (x.getDistributedBy() != null) {
            println();
            print0(ucase ? "DISTRIBUTED BY " : "distributed by ");
            String distributedByType = x.getDistributedBy().toString().toUpperCase();
            boolean isRandom = false;
            switch (distributedByType) {
                case "HASH": {
                    print0(ucase ? "HASH" : "hash");
                    break;
                }
                case "RANDOM": {
                    print0(ucase ? "RANDOM BUCKETS" : "random buckets");
                    isRandom = true;
                    break;
                }
                default: {
                    break;
                }
            }
            if (x.getDistributedByParameters().size() > 0) {
                for (int i = 0; i < x.getDistributedByParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    SQLExpr sqlExpr = x.getDistributedByParameters().get(i);
                    sqlExpr.accept(this);
                }
            }
            if (x.getBuckets() != 0) {
                if (!isRandom) {
                    print0(ucase ? " BUCKETS" : " buckets");
                }
                print0(" ");
                print0(String.valueOf(x.getBuckets()));
            }
        }
    }

    protected void printOrderBy(StarRocksCreateTableStatement x) {
        if (x.getOrderBy() != null && x.getOrderBy().size() > 0) {
            println();
            print0(ucase ? "ORDER BY " : "order by ");
            for (int i = 0; i < x.getOrderBy().size(); ++i) {
                if (i != 0) {
                    println(", ");
                }
                SQLExpr sqlExpr = x.getOrderBy().get(i);
                sqlExpr.accept(this);
            }

        }
    }

    @Override
    protected void printTableOptions(SQLCreateTableStatement statement) {
        if (statement instanceof StarRocksCreateTableStatement) {
            StarRocksCreateTableStatement x = (StarRocksCreateTableStatement) statement;
            int propertiesSize = x.getPropertiesMap().size();
            int lBracketSize = x.getlBracketPropertiesMap().size();
            if (propertiesSize > 0 || lBracketSize > 0) {
                println();
                print0(ucase ? "PROPERTIES " : "properties ");
                print0("(");
                if (propertiesSize > 0) {
                    printProperties(x.getPropertiesMap(), false);
                }
                if (lBracketSize > 0) {
                    print0(",");
                    printProperties(x.getlBracketPropertiesMap(), true);
                }
                println();
                print0(")");
            }

            if (!x.getBrokerPropertiesMap().isEmpty()) {
                println();
                print0(ucase ? "BROKER PROPERTIES " : "broker properties ");
                print0("(");
                printProperties(x.getBrokerPropertiesMap(), false);
                println();
                print0(")");
            }
        }
    }

    protected void printProperties(Map<SQLCharExpr, SQLCharExpr> properties, boolean useBracket) {
        int i = 0;
        Set<SQLCharExpr> keySet = properties.keySet();
        for (SQLCharExpr key : keySet) {
            println();
            print0("  ");
            if (useBracket) {
                print0("[");
            }
            print0(key.getText());
            print0(" = ");
            print0(properties.get(key).getText());
            if (i != keySet.size() - 1) {
                print0(",");
            }
            if (useBracket) {
                print0("]");
            }
            i++;
        }
    }

    protected void print(List<? extends SQLExpr> exprList) {
        int size = exprList.size();
        if (size == 0) {
            return;
        }

        print0(" (");

        this.indentCount++;
        println();
        for (int i = 0; i < size; ++i) {
            SQLExpr element = exprList.get(i);

            if (element instanceof SQLArrayExpr) {
                SQLArrayExpr array = ((SQLArrayExpr) element);
                SQLExpr expr = array.getExpr();

                if (expr instanceof SQLIdentifierExpr
                        && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.ARRAY
                        && printNameQuote
                ) {
                    print0(((SQLIdentifierExpr) expr).getName());
                } else if (expr != null) {
                    expr.accept(this);
                }

                print('[');
                printAndAccept(array.getValues(), ", ");

                if (i != size - 1) {
                    print0(",");
                }

                print(']');
            } else {
                element.accept(this);
            }

            if (i != size - 1 && !(element instanceof SQLArrayExpr)) {
                print(',');
            }

            if (i != size - 1) {
                println();
            }
        }
        this.indentCount--;
        println();
        print(')');
    }

    public boolean visit(SQLColumnDefinition x) {
        super.visit((SQLColumnDefinition) x);
        if (x.getAsExpr() != null) {
            print(' ');
            print0(ucase ? "AS " : "as ");
            x.getAsExpr().accept(this);
        }
        if (x.getBitmap() != null) {
            print(' ');
            print0(ucase ? "USING " : "using ");
            print0(ucase ? x.getBitmap().getText().toUpperCase(Locale.ROOT) : x.getBitmap().getText().toLowerCase(Locale.ROOT));
        }
        if (x.getIndexComment() != null) {
            print(' ');
            print0(ucase ? "COMMENT " : "comment ");
            x.getIndexComment().accept(this);
        }
        return false;
    }

    public boolean visit(StarRocksCreateResourceStatement x) {
        print0(ucase ? "CREATE " : "create ");
        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        print0(ucase ? "RESOURCE " : "resource ");
        x.getName().accept(this);
        println();

        print0(ucase ? "PROPERTIES" : "properties");
        print(x.getProperties());
        return false;
    }
}
