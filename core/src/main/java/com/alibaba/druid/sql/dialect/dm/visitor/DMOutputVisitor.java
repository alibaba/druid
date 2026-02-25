/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.dm.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DMAlterTableOption;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;

/**
 * 达梦数据库输出访问器
 * 继承 Oracle 输出访问器，达梦兼容 Oracle 语法
 */
public class DMOutputVisitor extends OracleOutputVisitor implements DMASTVisitor {
    public DMOutputVisitor(StringBuilder appender) {
        super(appender);
        this.dbType = DbType.dm;
    }

    public DMOutputVisitor(StringBuilder appender, boolean printPostSemi) {
        super(appender, printPostSemi);
        this.dbType = DbType.dm;
    }

    @Override
    public boolean visit(DMAlterTableOption x) {
        switch (x.getOptionType()) {
            case PARALLEL:
                print0(ucase ? "PARALLEL" : "parallel");
                if (x.getValue() != null) {
                    print(' ');
                    x.getValue().accept(this);
                }
                break;
            case NOPARALLEL:
                print0(ucase ? "NOPARALLEL" : "noparallel");
                break;
            case READ_ONLY:
                print0(ucase ? "READ ONLY" : "read only");
                break;
            case READ_WRITE:
                print0(ucase ? "READ WRITE" : "read write");
                break;
            case AUTO_INCREMENT:
                print0(ucase ? "AUTO_INCREMENT = " : "auto_increment = ");
                if (x.getValue() != null) {
                    x.getValue().accept(this);
                }
                break;
            case ENABLE_ALL_TRIGGERS:
                print0(ucase ? "ENABLE ALL TRIGGERS" : "enable all triggers");
                break;
            case DISABLE_ALL_TRIGGERS:
                print0(ucase ? "DISABLE ALL TRIGGERS" : "disable all triggers");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        print0(ucase ? "DROP PRIMARY KEY" : "drop primary key");
        if (Boolean.TRUE.equals(x.getAttribute("dm.cascade"))) {
            print0(ucase ? " CASCADE" : " cascade");
        } else if (Boolean.TRUE.equals(x.getAttribute("dm.restrict"))) {
            print0(ucase ? " RESTRICT" : " restrict");
        }
        return false;
    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        print0(ucase ? "TRUNCATE PARTITION " : "truncate partition ");
        x.getName().accept(this);
        appendStorageOption(x);
        return false;
    }

    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        if (Boolean.TRUE.equals(x.getAttribute("dm.subpartition"))) {
            print0(ucase ? "TRUNCATE SUBPARTITION " : "truncate subpartition ");
            printAndAccept(x.getPartitions(), ", ");
            appendStorageOption(x);
            return false;
        }
        return super.visit(x);
    }

    private void appendStorageOption(SQLObject x) {
        Object storageOption = x.getAttribute("dm.storage");
        if ("DROP".equals(storageOption)) {
            print0(ucase ? " DROP STORAGE" : " drop storage");
        } else if ("REUSE".equals(storageOption)) {
            print0(ucase ? " REUSE STORAGE" : " reuse storage");
        }
    }
}
