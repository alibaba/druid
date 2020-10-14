package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linchtech
 * @date 2020-09-16 18:32
 **/
@Data
@NoArgsConstructor
public class DataSourceResult {

    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private List<ContentBean> Content;

    @NoArgsConstructor
    @Data
    public static class ContentBean {

        private String serviceId;

        @JSONField(name = "Identity")
        private int Identity;
        @JSONField(name = "Name")
        private String Name;
        @JSONField(name = "DbType")
        private String DbType;
        @JSONField(name = "DriverClassName")
        private String DriverClassName;
        @JSONField(name = "URL")
        private String URL;
        @JSONField(name = "UserName")
        private String UserName;
        @JSONField(name = "WaitThreadCount")
        private int WaitThreadCount;
        @JSONField(name = "NotEmptyWaitCount")
        private int NotEmptyWaitCount;
        @JSONField(name = "NotEmptyWaitMillis")
        private int NotEmptyWaitMillis;
        @JSONField(name = "PoolingCount")
        private int PoolingCount;
        @JSONField(name = "PoolingPeak")
        private int PoolingPeak;
        @JSONField(name = "PoolingPeakTime")
        private String PoolingPeakTime;
        @JSONField(name = "ActiveCount")
        private int ActiveCount;
        @JSONField(name = "ActivePeak")
        private int ActivePeak;
        @JSONField(name = "ActivePeakTime")
        private String ActivePeakTime;
        @JSONField(name = "InitialSize")
        private int InitialSize;
        @JSONField(name = "MinIdle")
        private int MinIdle;
        @JSONField(name = "MaxActive")
        private int MaxActive;
        @JSONField(name = "QueryTimeout")
        private int QueryTimeout;
        @JSONField(name = "TransactionQueryTimeout")
        private int TransactionQueryTimeout;
        @JSONField(name = "LoginTimeout")
        private int LoginTimeout;
        @JSONField(name = "ValidConnectionCheckerClassName")
        private String ValidConnectionCheckerClassName;
        @JSONField(name = "ExceptionSorterClassName")
        private String ExceptionSorterClassName;
        @JSONField(name = "TestOnBorrow")
        private boolean TestOnBorrow;
        @JSONField(name = "TestOnReturn")
        private boolean TestOnReturn;
        @JSONField(name = "TestWhileIdle")
        private boolean TestWhileIdle;
        @JSONField(name = "DefaultAutoCommit")
        private boolean DefaultAutoCommit;
        @JSONField(name = "DefaultReadOnly")
        private Object DefaultReadOnly;
        @JSONField(name = "DefaultTransactionIsolation")
        private Object DefaultTransactionIsolation;
        @JSONField(name = "LogicConnectCount")
        private int LogicConnectCount;
        @JSONField(name = "LogicCloseCount")
        private int LogicCloseCount;
        @JSONField(name = "LogicConnectErrorCount")
        private int LogicConnectErrorCount;
        @JSONField(name = "PhysicalConnectCount")
        private int PhysicalConnectCount;
        @JSONField(name = "PhysicalCloseCount")
        private int PhysicalCloseCount;
        @JSONField(name = "PhysicalConnectErrorCount")
        private int PhysicalConnectErrorCount;
        @JSONField(name = "ExecuteCount")
        private int ExecuteCount;
        @JSONField(name = "ExecuteUpdateCount")
        private int ExecuteUpdateCount;
        @JSONField(name = "ExecuteQueryCount")
        private int ExecuteQueryCount;
        @JSONField(name = "ExecuteBatchCount")
        private int ExecuteBatchCount;
        @JSONField(name = "ErrorCount")
        private int ErrorCount;
        @JSONField(name = "CommitCount")
        private int CommitCount;
        @JSONField(name = "RollbackCount")
        private int RollbackCount;
        @JSONField(name = "PSCacheAccessCount")
        private int PSCacheAccessCount;
        @JSONField(name = "PSCacheHitCount")
        private int PSCacheHitCount;
        @JSONField(name = "PSCacheMissCount")
        private int PSCacheMissCount;
        @JSONField(name = "StartTransactionCount")
        private int StartTransactionCount;
        @JSONField(name = "RemoveAbandoned")
        private boolean RemoveAbandoned;
        @JSONField(name = "ClobOpenCount")
        private int ClobOpenCount;
        @JSONField(name = "BlobOpenCount")
        private int BlobOpenCount;
        @JSONField(name = "KeepAliveCheckCount")
        private int KeepAliveCheckCount;
        @JSONField(name = "KeepAlive")
        private boolean KeepAlive;
        @JSONField(name = "FailFast")
        private boolean FailFast;
        @JSONField(name = "MaxWait")
        private int MaxWait;
        @JSONField(name = "MaxWaitThreadCount")
        private int MaxWaitThreadCount;
        @JSONField(name = "PoolPreparedStatements")
        private boolean PoolPreparedStatements;
        @JSONField(name = "MaxPoolPreparedStatementPerConnectionSize")
        private int MaxPoolPreparedStatementPerConnectionSize;
        @JSONField(name = "MinEvictableIdleTimeMillis")
        private int MinEvictableIdleTimeMillis;
        @JSONField(name = "MaxEvictableIdleTimeMillis")
        private int MaxEvictableIdleTimeMillis;
        @JSONField(name = "LogDifferentThread")
        private boolean LogDifferentThread;
        @JSONField(name = "RecycleErrorCount")
        private int RecycleErrorCount;
        @JSONField(name = "PreparedStatementOpenCount")
        private int PreparedStatementOpenCount;
        @JSONField(name = "PreparedStatementClosedCount")
        private int PreparedStatementClosedCount;
        @JSONField(name = "UseUnfairLock")
        private boolean UseUnfairLock;
        @JSONField(name = "InitGlobalVariants")
        private boolean InitGlobalVariants;
        @JSONField(name = "InitVariants")
        private boolean InitVariants;
        @JSONField(name = "FilterClassNames")
        private List<String> FilterClassNames;
        @JSONField(name = "TransactionHistogram")
        private List<Integer> TransactionHistogram;
        @JSONField(name = "ConnectionHoldTimeHistogram")
        private List<Integer> ConnectionHoldTimeHistogram;
    }
}
