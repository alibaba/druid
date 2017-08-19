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
package com.alibaba.druid.util;

public interface JdbcConstants {

    public static final String JTDS              = "jtds";

    public static final String MOCK              = "mock";

    public static final String HSQL              = "hsql";

    public static final String DB2               = "db2";

    public static final String DB2_DRIVER        = "COM.ibm.db2.jdbc.app.DB2Driver";

    public static final String POSTGRESQL        = "postgresql";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    public static final String SYBASE            = "sybase";

    public static final String SQL_SERVER        = "sqlserver";
    public static final String SQL_SERVER_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    public static final String SQL_SERVER_DRIVER_SQLJDBC4 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String SQL_SERVER_DRIVER_JTDS = "net.sourceforge.jtds.jdbc.Driver";

    public static final String ORACLE            = "oracle";
    public static final String ORACLE_DRIVER     = "oracle.jdbc.OracleDriver";
    public static final String ORACLE_DRIVER2    = "oracle.jdbc.driver.OracleDriver";

    public static final String ALI_ORACLE        = "AliOracle";
    public static final String ALI_ORACLE_DRIVER = "com.alibaba.jdbc.AlibabaDriver";

    public static final String MYSQL             = "mysql";
    public static final String MYSQL_DRIVER      = "com.mysql.jdbc.Driver";
    public static final String MYSQL_DRIVER_6    = "com.mysql.cj.jdbc.Driver";
    public static final String MYSQL_DRIVER_REPLICATE = "com.mysql.jdbc.";

    public static final String MARIADB           = "mariadb";
    public static final String MARIADB_DRIVER    = "org.mariadb.jdbc.Driver";

    public static final String DERBY             = "derby";

    public static final String HBASE             = "hbase";

    public static final String HIVE              = "hive";
    public static final String HIVE_DRIVER       = "org.apache.hive.jdbc.HiveDriver";

    public static final String H2                = "h2";
    public static final String H2_DRIVER         = "org.h2.Driver";

    public static final String DM                = "dm";
    public static final String DM_DRIVER         = "dm.jdbc.driver.DmDriver";
    
    public static final String KINGBASE          = "kingbase";
    public static final String KINGBASE_DRIVER   = "com.kingbase.Driver";

    public static final String OCEANBASE         = "oceanbase";
    public static final String OCEANBASE_DRIVER  = "com.mysql.jdbc.Driver";

    public static final String INFORMIX          = "informix";
    
    /**
     * 阿里云odps
     */
    public static final String ODPS              = "odps";
    public static final String ODPS_DRIVER       = "com.aliyun.odps.jdbc.OdpsDriver";
    
    public static final String TERADATA          = "teradata";
    public static final String TERADATA_DRIVER   = "com.teradata.jdbc.TeraDriver";

    /**
     * Log4JDBC
     */
    public static final String LOG4JDBC          = "log4jdbc";
    public static final String LOG4JDBC_DRIVER   = "net.sf.log4jdbc.DriverSpy";

    public static final String PHOENIX           = "phoenix";
    public static final String PHOENIX_DRIVER    = "org.apache.phoenix.jdbc.PhoenixDriver";

    public static final String ENTERPRISEDB        = "edb";
    public static final String ENTERPRISEDB_DRIVER = "com.edb.Driver";

    public static final String KYLIN               = "kylin";
    public static final String KYLIN_DRIVER        = "org.apache.kylin.jdbc.Driver";


    public static final String SQLITE              = "sqlite";
    public static final String SQLITE_DRIVER       = "org.sqlite.JDBC";

    public static final String ALIYUN_ADS          = "aliyun_ads";
    public static final String ALIYUN_DRDS         = "aliyun_drds";

    public static final String PRESTO              = "presto";
}
