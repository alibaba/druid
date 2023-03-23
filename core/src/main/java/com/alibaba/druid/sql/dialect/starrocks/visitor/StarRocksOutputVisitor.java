package com.alibaba.druid.sql.dialect.starrocks.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

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

    public StarRocksOutputVisitor(Appendable appender) {
        super(appender);
    }

    public StarRocksOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public StarRocksOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    public boolean visit(StarRocksCreateTableStatement x) {
        super.visit((SQLCreateTableStatement) x);

        SQLName model = x.getModelKey();
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
                case "unique":
                    print0(ucase ? "UNIQUE" : "unique");
                    break;
                case "primary":
                    print0(ucase ? "PRIMARY" : "primary");
                    break;
                default:
                    break;
            }
            print(' ');
            print0(ucase ? "KEY" : "key");
            if (x.getModelKeyParameters().size() > 0) {
                for (int i = 0; i < x.getModelKeyParameters().size(); ++i) {
                    if (i != 0) {
                        println(", ");
                    }
                    x.getModelKeyParameters().get(i).accept(this);
                }
            }
        }

        SQLExpr partitionBy = x.getPartitionBy();
        if (partitionBy != null) {
            println();
            print0(ucase ? "PARTITION BY " : "partition by ");
            partitionBy.accept(this);
            println();
            print0("(");
            println();
            if (x.isLessThan()) {
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
                            print0("(");
                            value.accept(this);
                            print0(")");
                        }
                        i++;
                    }
                }
            } else if (x.isFixedRange()) {
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
            } else if (x.isStartEnd()) {
                if (x.getStart() != null) {
                    print0(ucase ? "  START " : "  start ");
                    print0("(");
                    x.getStart().accept(this);
                    print0(")");
                }
                if (x.getEnd() != null) {
                    print0(ucase ? "  END " : "  end ");
                    print0("(");
                    x.getEnd().accept(this);
                    print0(")");
                }
                if (x.getEvery() != null) {
                    print0(ucase ? "  EVERY " : "  every ");
                    print0("(");
                    x.getEvery().accept(this);
                    print0(")");
                }

            }
            println();
            print0(")");
        }
        println();
        if (x.getDistributedBy() != null) {
            print0(ucase ? "DISTRIBUTED BY " : "distributed by ");
            x.getDistributedBy().accept(this);
            print0(ucase ? " BUCKETS " : "buckets ");
            int buckets = x.getBuckets();
            print0(String.valueOf(buckets));
        }

        println();
        int propertiesSize = x.getPropertiesMap().size();
        int lBracketSize = x.getlBracketPropertiesMap().size();
        if (propertiesSize > 0 || lBracketSize > 0) {
            print0(ucase ? "PROPERTIES " : "properties ");
            print0("(");
            if (propertiesSize > 0) {
                Map<SQLCharExpr, SQLCharExpr> propertiesMap = x.getPropertiesMap();
                Set<SQLCharExpr> keySet = propertiesMap.keySet();
                int i = 0;
                for (SQLCharExpr key : keySet) {
                    println();
                    print0("  ");
                    print0(key.getText());
                    print0(" = ");
                    print0(propertiesMap.get(key).getText());
                    if (lBracketSize > 0 || i != keySet.size() - 1) {
                        print0(",");
                    }
                    i++;
                }
            }

            if (lBracketSize > 0) {
                Map<SQLCharExpr, SQLCharExpr> lBracketPropertiesMap = x.getlBracketPropertiesMap();
                Set<SQLCharExpr> keySet = lBracketPropertiesMap.keySet();
                int i = 0;
                for (SQLCharExpr key : keySet) {
                    println();
                    print0("  ");
                    print0("[");
                    print0(key.getText());
                    print0(" = ");
                    print0(lBracketPropertiesMap.get(key).getText());
                    if (i != keySet.size() - 1) {
                        print0(",");
                    }
                    print0("]");
                    i++;
                }
            }
            println();
            print0(")");
        }

        return false;
    }

    public boolean visit(SQLColumnDefinition x) {
        super.visit((SQLColumnDefinition) x);
        if (x.getAggType() != null) {
            print(' ');
            print0(ucase ? x.getAggType().getText().toUpperCase(Locale.ROOT) : x.getAggType().getText().toLowerCase(Locale.ROOT));
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
}
