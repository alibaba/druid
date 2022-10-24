create table nation
(
    N_NATIONKEY INT,
    N_NAME      STRING,
    N_REGIONKEY INT,
    N_COMMENT   STRING,
    primary key (N_NATIONKEY)
);

create table lineitem
(
    L_ORDERKEY      INT,
    L_PARTKEY       INT,
    L_SUPPKEY       INT,
    L_LINENUMBER    INT,
    L_QUANTITY      DOUBLE,
    L_EXTENDEDPRICE DOUBLE,
    L_DISCOUNT      DOUBLE,
    L_TAX           DOUBLE,
    L_RETURNFLAG    STRING,
    L_LINESTATUS    STRING,
    L_SHIPDATE      STRING,
    L_COMMITDATE    STRING,
    L_RECEIPTDATE   STRING,
    L_SHIPINSTRUCT  STRING,
    L_SHIPMODE      STRING,
    L_COMMENT       STRING,
    primary key (L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER)
);

create table orders
(
    O_ORDERKEY      INT,
    O_CUSTKEY       INT,
    O_ORDERSTATUS   STRING,
    O_TOTALPRICE    DOUBLE,
    O_ORDERDATE     STRING,
    O_ORDERPRIORITY STRING,
    O_CLERK         STRING,
    O_SHIPPRIORITY  INT,
    O_COMMENT       STRING,
    primary key (O_ORDERKEY)
);

create table supplier
(
    S_SUPPKEY   INT,
    S_NAME      STRING,
    S_ADDRESS   STRING,
    S_NATIONKEY INT,
    S_PHONE     STRING,
    S_ACCTBAL   DOUBLE,
    S_COMMENT   STRING,
    primary key (S_SUPPKEY)
);

create table partsupp
(
    PS_PARTKEY    INT,
    PS_SUPPKEY    INT,
    PS_AVAILQTY   INT,
    PS_SUPPLYCOST DOUBLE,
    PS_COMMENT    STRING,
    primary key (PS_PARTKEY, PS_SUPPKEY)
);

create table customer
(
    C_CUSTKEY    INT,
    C_NAME       STRING,
    C_ADDRESS    STRING,
    C_NATIONKEY  INT,
    C_PHONE      STRING,
    C_ACCTBAL    DOUBLE,
    C_MKTSEGMENT STRING,
    C_COMMENT    STRING,
    primary key (C_CUSTKEY)
);

create table part
(
    P_PARTKEY     INT,
    P_NAME        STRING,
    P_MFGR        STRING,
    P_BRAND       STRING,
    P_TYPE        STRING,
    P_SIZE        INT,
    P_CONTAINER   STRING,
    P_RETAILPRICE DOUBLE,
    P_COMMENT     STRING,
    primary key (P_PARTKEY)
);

create table region
(
    R_REGIONKEY INT,
    R_NAME      STRING,
    R_COMMENT   STRING,
    primary key (R_REGIONKEY)
);
