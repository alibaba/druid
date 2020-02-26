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
package com.alibaba.druid.util;

public interface JdbcConstants {

    String JTDS                       = "jtds";

    String MOCK                       = "mock";

    String HSQL                       = "hsql";

    String DB2                        = "db2";

    String DB2_DRIVER                 = "com.ibm.db2.jcc.DB2Driver";

    String POSTGRESQL                 = "postgresql";
    String POSTGRESQL_DRIVER          = "org.postgresql.Driver";

    String SYBASE                     = "sybase";

    String SQL_SERVER                 = "sqlserver";
    String SQL_SERVER_DRIVER          = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    String SQL_SERVER_DRIVER_SQLJDBC4 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String SQL_SERVER_DRIVER_JTDS     = "net.sourceforge.jtds.jdbc.Driver";

    String ORACLE                     = "oracle";
    String ORACLE_DRIVER              = "oracle.jdbc.OracleDriver";
    String ORACLE_DRIVER2             = "oracle.jdbc.driver.OracleDriver";

    String ALI_ORACLE                 = "AliOracle";
    String ALI_ORACLE_DRIVER          = "com.alibaba.jdbc.AlibabaDriver";

    String MYSQL                      = "mysql";
    String MYSQL_DRIVER               = "com.mysql.jdbc.Driver";
    String MYSQL_DRIVER_6             = "com.mysql.cj.jdbc.Driver";
    String MYSQL_DRIVER_REPLICATE     = "com.mysql.jdbc.";

    String MARIADB                    = "mariadb";
    String MARIADB_DRIVER             = "org.mariadb.jdbc.Driver";

    String DERBY                      = "derby";

    String HBASE                      = "hbase";

    String HIVE                       = "hive";
    String HIVE_DRIVER                = "org.apache.hive.jdbc.HiveDriver";

    String H2                         = "h2";
    String H2_DRIVER                  = "org.h2.Driver";

    String DM                         = "dm";
    String DM_DRIVER                  = "dm.jdbc.driver.DmDriver";

    String KINGBASE                   = "kingbase";
    String KINGBASE_DRIVER            = "com.kingbase.Driver";

    String GBASE                      = "gbase";
    String GBASE_DRIVER               = "com.gbase.jdbc.Driver";

    String XUGU                       = "xugu";
    String XUGU_DRIVER                = "com.xugu.cloudjdbc.Driver";

    String OCEANBASE                  = "oceanbase";
    String OCEANBASE_DRIVER           = "com.mysql.jdbc.Driver";
    String INFORMIX                   = "informix";
    
    /**
     * 阿里云odps
     */
    String ODPS                       = "odps";
    String ODPS_DRIVER                = "com.aliyun.odps.jdbc.OdpsDriver";

    String TERADATA                   = "teradata";
    String TERADATA_DRIVER            = "com.teradata.jdbc.TeraDriver";

    /**
     * Log4JDBC
     */
    String LOG4JDBC                   = "log4jdbc";
    String LOG4JDBC_DRIVER            = "net.sf.log4jdbc.DriverSpy";

    String PHOENIX                    = "phoenix";
    String PHOENIX_DRIVER             = "org.apache.phoenix.jdbc.PhoenixDriver";
    String ENTERPRISEDB               = "edb";
    String ENTERPRISEDB_DRIVER        = "com.edb.Driver";

    String KYLIN                      = "kylin";
    String KYLIN_DRIVER               = "org.apache.kylin.jdbc.Driver";


    String SQLITE                     = "sqlite";
    String SQLITE_DRIVER              = "org.sqlite.JDBC";

    String ALIYUN_ADS                 = "aliyun_ads";
    String ALIYUN_DRDS                = "aliyun_drds";

    String PRESTO                     = "presto";
    String PRESTO_DRIVER              = "com.facebook.presto.jdbc.PrestoDriver";

    String ELASTIC_SEARCH             = "elastic_search";
    
    String ELASTIC_SEARCH_DRIVER      = "com.alibaba.xdriver.elastic.jdbc.ElasticDriver";

    String CLICKHOUSE                 = "clickhouse";
    String CLICKHOUSE_DRIVER          = "ru.yandex.clickhouse.ClickHouseDriver";

    /**
     * Aliyun PolarDB
     */
    String POLARDB                    = "polardb";
    String POLARDB_DRIVER             = "com.aliyun.polardb.Driver";
}
