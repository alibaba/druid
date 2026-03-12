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
package com.alibaba.druid.wall.spi;

import com.alibaba.druid.DbType;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGExportParameterVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallVisitor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class KingbaseWallProvider extends WallProvider {
    /** 日志. */
    private static final Log LOG =
            LogFactory.getLog(KingbaseWallProvider.class);
    /** 默认配置目录. */
    public static final String DEFAULT_CONFIG_DIR =
            PGWallProvider.DEFAULT_CONFIG_DIR;

    /** 缓存的数据库兼容模式. */
    private volatile DbType compatMode = null;
    /** 兼容模式锁. */
    private final Object compatModeLock = new Object();
    /** 数据源代理. */
    private DataSourceProxy dataSource;

    /**
     * 默认构造函数.
     */
    public KingbaseWallProvider() {
        this(new WallConfig(DEFAULT_CONFIG_DIR));
    }

    /**
     * 构造函数.
     * @param config 配置
     */
    public KingbaseWallProvider(final WallConfig config) {
        super(config, DbType.kingbase);
    }

    /**
     * 构造函数.
     * @param config 配置
     * @param dataSourceProxy 数据源代理
     */
    public KingbaseWallProvider(final WallConfig config,
                                final DataSourceProxy dataSourceProxy) {
        super(config, DbType.kingbase);
        this.dataSource = dataSourceProxy;
    }

    /**
     * 查询 Kingbase 数据库的兼容模式.
     * 支持多种查询方式：
     * - SHOW database_mode
     * - SELECT show_compat_mode()
     * - SELECT current_setting('database_mode')
     * @return 兼容模式对应的 DbType
     */
    private DbType queryCompatibilityMode() {
        if (dataSource == null) {
            LOG.warn("DataSourceProxy is null, cannot query compatibility "
                    + "mode, using PostgreSQL as default");
            return DbType.postgresql;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 获取原始连接（绕过 Filter 链，避免循环）
            conn = dataSource.getRawDriver().connect(
                    dataSource.getRawJdbcUrl(),
                    dataSource.getConnectProperties());
            if (conn == null) {
                LOG.warn("Failed to get connection, using PostgreSQL "
                        + "as default");
                return DbType.postgresql;
            }

            stmt = conn.createStatement();

            // 尝试多种查询方式
            String[] queries = {
                "SHOW database_mode",
                "SELECT show_compat_mode()",
                "SELECT current_setting('database_mode')",
                "SHOW compat_mode"
            };

            for (String query : queries) {
                try {
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String mode = rs.getString(1);
                        if (mode != null) {
                            mode = mode.trim().toLowerCase();
                            LOG.info("Detected Kingbase compatibility mode: "
                                    + mode);

                            // 映射模式到 DbType
                            if (mode.contains("oracle")) {
                                return DbType.oracle;
                            } else if (mode.contains("mysql")) {
                                return DbType.mysql;
                            } else if (mode.contains("mssql")
                                    || mode.contains("sqlserver")) {
                                return DbType.sqlserver;
                            } else if (mode.contains("pg")
                                    || mode.contains("postgresql")) {
                                return DbType.postgresql;
                            }
                        }
                    }
                    if (rs != null) {
                        rs.close();
                        rs = null;
                    }
                } catch (SQLException e) {
                    // 尝试下一种查询方式
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }
                        rs = null;
                    }
                }
            }

            LOG.warn("Could not determine compatibility mode, using "
                    + "PostgreSQL as default");
            return DbType.postgresql;

        } catch (SQLException e) {
            LOG.warn("Failed to query compatibility mode: " + e.getMessage()
                    + ", using PostgreSQL as default", e);
            return DbType.postgresql;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * 获取兼容模式，如果未初始化则查询并缓存.
     * @return 兼容模式对应的 DbType
     */
    private DbType getCompatibilityMode() {
        if (compatMode == null) {
            synchronized (compatModeLock) {
                if (compatMode == null) {
                    compatMode = queryCompatibilityMode();
                }
            }
        }
        return compatMode;
    }

    /**
     * 创建 SQL 解析器.
     * @param sql SQL 语句
     * @return SQL 解析器
     */
    @Override
    public SQLStatementParser createParser(final String sql) {
        DbType mode = getCompatibilityMode();
        SQLParserFeature[] features =
                {SQLParserFeature.EnableSQLBinaryOpExprGroup};

        switch (mode) {
            case oracle:
                return new OracleStatementParser(sql, features);
            case mysql:
                return new MySqlStatementParser(sql, features);
            case sqlserver:
                return new SQLServerStatementParser(sql, features);
            case postgresql:
            default:
                return new PGSQLStatementParser(sql, features);
        }
    }

    /**
     * 创建 Wall Visitor.
     * @return Wall Visitor
     */
    @Override
    public WallVisitor createWallVisitor() {
        DbType mode = getCompatibilityMode();

        switch (mode) {
            case oracle:
                return new OracleWallVisitor(this);
            case mysql:
                return new MySqlWallVisitor(this);
            case sqlserver:
                return new SQLServerWallVisitor(this);
            case postgresql:
            default:
                return new PGWallVisitor(this);
        }
    }

    /**
     * 创建参数导出 Visitor.
     * @return 参数导出 Visitor
     */
    @Override
    public ExportParameterVisitor createExportParameterVisitor() {
        DbType mode = getCompatibilityMode();

        switch (mode) {
            case oracle:
                return new com.alibaba.druid.sql.dialect.oracle.visitor
                        .OracleExportParameterVisitor();
            case mysql:
                return new com.alibaba.druid.sql.dialect.mysql.visitor
                        .MySqlExportParameterVisitor();
            case sqlserver:
                return new com.alibaba.druid.sql.dialect.sqlserver.visitor
                        .MSSQLServerExportParameterVisitor();
            case postgresql:
            default:
                return new PGExportParameterVisitor();
        }
    }
}
