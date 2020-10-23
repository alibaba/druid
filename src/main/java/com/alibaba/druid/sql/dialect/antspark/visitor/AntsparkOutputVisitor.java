/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alibaba.druid.sql.dialect.antspark.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.antspark.ast.AntsparkCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author peiheng.qph
 * @version $Id: AntsparkOutputVisitor.java, v 0.1 2018年09月17日 10:40 peiheng.qph Exp $
 */
public class AntsparkOutputVisitor extends HiveOutputVisitor implements AntsparkVisitor {

    public AntsparkOutputVisitor(Appendable appender, DbType dbType) {
        super(appender, dbType);
    }

    public AntsparkOutputVisitor(Appendable appender) {
        super(appender);
    }

    //add using statment
    @Override
    public boolean visit(AntsparkCreateTableStatement x) {
        print0(ucase ? "CREATE " : "create ");

        if (x.isExternal()) {
            print0(ucase ? "EXTERNAL " : "external ");
        }

        if (x.isIfNotExists()) {
            print0(ucase ? "TABLE IF NOT EXISTS " : "table if not exists ");
        } else {
            print0(ucase ? "TABLE " : "table ");
        }

        x.getName().accept(this);

        if (x.getLike() != null) {
            print0(ucase ? " LIKE " : " like ");
            x.getLike().accept(this);
        }

        final List<SQLTableElement> tableElementList = x.getTableElementList();
        int size = tableElementList.size();
        if (size > 0) {
            print0(" (");

            if (this.isPrettyFormat() && x.hasBodyBeforeComment()) {
                print(' ');
                printlnComment(x.getBodyBeforeCommentsDirect());
            }

            this.indentCount++;
            println();
            for (int i = 0; i < size; ++i) {
                SQLTableElement element = tableElementList.get(i);
                element.accept(this);

                if (i != size - 1) {
                    print(',');
                }
                if (this.isPrettyFormat() && element.hasAfterComment()) {
                    print(' ');
                    printlnComment(element.getAfterCommentsDirect());
                }

                if (i != size - 1) {
                    println();
                }
            }
            this.indentCount--;
            println();
            print(')');
        }
        if(x.getDatasource()!=null){
            println();
            print0(ucase?"USING ":"using ");
            print0(x.getDatasource().toString());
        }
        if (x.getComment() != null) {
            println();
            print0(ucase ? "COMMENT " : "comment ");
            x.getComment().accept(this);
        }


        int partitionSize = x.getPartitionColumns().size();
        if (partitionSize > 0) {
            println();
            print0(ucase ? "PARTITIONED BY (" : "partitioned by (");
            this.indentCount++;
            println();
            for (int i = 0; i < partitionSize; ++i) {
                SQLColumnDefinition column = x.getPartitionColumns().get(i);
                column.accept(this);

                if (i != partitionSize - 1) {
                    print(',');
                }
                if (this.isPrettyFormat() && column.hasAfterComment()) {
                    print(' ');
                    printlnComment(column.getAfterCommentsDirect());
                }

                if (i != partitionSize - 1) {
                    println();
                }
            }
            this.indentCount--;
            println();
            print(')');
        }

        List<SQLSelectOrderByItem> clusteredBy = x.getClusteredBy();
        if (clusteredBy.size() > 0) {
            println();
            print0(ucase ? "CLUSTERED BY (" : "clustered by (");
            printAndAccept(clusteredBy, ",");
            print(')');
        }

        List<SQLSelectOrderByItem> sortedBy = x.getSortedBy();
        if (sortedBy.size() > 0) {
            println();
            print0(ucase ? "SORTED BY (" : "sorted by (");
            printAndAccept(sortedBy, ", ");
            print(')');
        }

        int buckets = x.getBuckets();
        if (buckets > 0) {
            println();
            print0(ucase ? "INTO " : "into ");
            print(buckets);
            print0(ucase ? " BUCKETS" : " buckets");
        }

        SQLExpr storedAs = x.getStoredAs();
        if (storedAs != null) {
            println();
            print0(ucase ? "STORED AS " : "stored as ");
            storedAs.accept(this);
        }

        SQLSelect select = x.getSelect();
        if (select != null) {
            println();
            print0(ucase ? "AS" : "as");
            println();
            select.accept(this);
        }

        Map<String, SQLObject> serdeProperties = x.getSerdeProperties();
        if (serdeProperties.size() > 0) {
            println();
            print0(ucase ? "TBLPROPERTIES (" : "tblproperties (");
            String seperator="";
            for(Entry<String,SQLObject> entry:serdeProperties.entrySet()){
                print0("'"+entry.getKey()+"'='");
                entry.getValue().accept(this);
                print0("'"+seperator);
                seperator=",";
            }
            print(')');
        }

        SQLExpr location = x.getLocation();
        if (location != null) {
            println();
            print0(ucase ? "LOCATION " : "location ");
            location.accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(AntsparkCreateTableStatement x) {

    }
}