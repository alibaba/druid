/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.wall.spi;

import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ExportParameterVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;


public class DB2WallProvider extends WallProvider {

    public final static String DEFAULT_CONFIG_DIR = "META-INF/druid/wall/db2";

    public DB2WallProvider(){
        this(new WallConfig(DEFAULT_CONFIG_DIR));
    }

    public DB2WallProvider(WallConfig config){
        super(config, JdbcConstants.DB2);
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new DB2StatementParser(sql, SQLParserFeature.EnableSQLBinaryOpExprGroup);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new DB2WallVisitor(this);
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor() {
        return new DB2ExportParameterVisitor();
    }

}
