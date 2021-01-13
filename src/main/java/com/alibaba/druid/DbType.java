package com.alibaba.druid;

import com.alibaba.druid.util.FnvHash;

public enum DbType {
    other           (1 << 0),
    jtds            (1 << 1),
    hsql            (1 << 2),
    db2             (1 << 3),
    postgresql      (1 << 4),

    sqlserver       (1 << 5),
    oracle          (1 << 6),
    mysql           (1 << 7),
    mariadb         (1 << 8),
    derby           (1 << 9),

    hive            (1 << 10),
    h2              (1 << 11),
    dm              (1 << 12), // dm.jdbc.driver.DmDriver
    kingbase        (1 << 13),
    gbase           (1 << 14),

    oceanbase       (1 << 15),
    informix        (1 << 16),
    odps            (1 << 17),
    teradata        (1 << 18),
    phoenix         (1 << 19),

    edb             (1 << 20),
    kylin           (1 << 21), // org.apache.kylin.jdbc.Driver
    sqlite          (1 << 22),
    ads             (1 << 23),
    presto          (1 << 24),

    elastic_search  (1 << 25), // com.alibaba.xdriver.elastic.jdbc.ElasticDriver
    hbase           (1 << 26),
    drds            (1 << 27),

    clickhouse      (1 << 28),
    blink           (1 << 29),
    antspark(1 << 30),
    oceanbase_oracle       (1 << 31),
    polardb       (1 << 32),

    ali_oracle          (1 << 33),
    mock          (1 << 34),
    sybase          (1 << 35),
    highgo          (1 << 36),


    ingres          (0),
    cloudscape          (0),
    timesten          (0),
    as400          (0),
    sapdb          (0),
    kdb          (0),
    log4jdbc          (0),
    xugu          (0),
    firebirdsql          (0),
    JSQLConnect          (0),
    JTurbo          (0),
    interbase          (0),
    pointbase          (0),
    edbc          (0),
    mimer          (0),

    ;

    public final long mask;
    public final long hashCode64;

    private DbType(long mask) {
        this.mask = mask;
        this.hashCode64 = FnvHash.hashCode64(name());
    }

    public static long of(DbType... types) {
        long value = 0;

        for (DbType type : types) {
            value |= type.mask;
        }

        return value;
    }

    public static DbType of(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        if ("aliyun_ads".equalsIgnoreCase(name)) {
            return ads;
        }

        try {
            return valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }

    public final boolean equals(String other) {
        return this == of(other);
    }
}
