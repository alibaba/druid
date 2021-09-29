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
        private long Identity;
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
        private long WaitThreadCount;
        @JSONField(name = "NotEmptyWaitCount")
        private long NotEmptyWaitCount;
        @JSONField(name = "NotEmptyWaitMillis")
        private long NotEmptyWaitMillis;
        @JSONField(name = "PoolingCount")
        private long PoolingCount;
        @JSONField(name = "PoolingPeak")
        private long PoolingPeak;
        @JSONField(name = "PoolingPeakTime")
        private String PoolingPeakTime;
        @JSONField(name = "ActiveCount")
        private long ActiveCount;
        @JSONField(name = "ActivePeak")
        private long ActivePeak;
        @JSONField(name = "ActivePeakTime")
        private String ActivePeakTime;
        @JSONField(name = "InitialSize")
        private long InitialSize;
        @JSONField(name = "MinIdle")
        private long MinIdle;
        @JSONField(name = "MaxActive")
        private long MaxActive;
        @JSONField(name = "QueryTimeout")
        private long QueryTimeout;
        @JSONField(name = "TransactionQueryTimeout")
        private long TransactionQueryTimeout;
        @JSONField(name = "LoginTimeout")
        private long LoginTimeout;
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
        private long LogicConnectCount;
        @JSONField(name = "LogicCloseCount")
        private long LogicCloseCount;
        @JSONField(name = "LogicConnectErrorCount")
        private long LogicConnectErrorCount;
        @JSONField(name = "PhysicalConnectCount")
        private long PhysicalConnectCount;
        @JSONField(name = "PhysicalCloseCount")
        private long PhysicalCloseCount;
        @JSONField(name = "PhysicalConnectErrorCount")
        private long PhysicalConnectErrorCount;
        @JSONField(name = "ExecuteCount")
        private long ExecuteCount;
        @JSONField(name = "ExecuteUpdateCount")
        private long ExecuteUpdateCount;
        @JSONField(name = "ExecuteQueryCount")
        private long ExecuteQueryCount;
        @JSONField(name = "ExecuteBatchCount")
        private long ExecuteBatchCount;
        @JSONField(name = "ErrorCount")
        private long ErrorCount;
        @JSONField(name = "CommitCount")
        private long CommitCount;
        @JSONField(name = "RollbackCount")
        private long RollbackCount;
        @JSONField(name = "PSCacheAccessCount")
        private long PSCacheAccessCount;
        @JSONField(name = "PSCacheHitCount")
        private long PSCacheHitCount;
        @JSONField(name = "PSCacheMissCount")
        private long PSCacheMissCount;
        @JSONField(name = "StartTransactionCount")
        private long StartTransactionCount;
        @JSONField(name = "RemoveAbandoned")
        private boolean RemoveAbandoned;
        @JSONField(name = "ClobOpenCount")
        private long ClobOpenCount;
        @JSONField(name = "BlobOpenCount")
        private long BlobOpenCount;
        @JSONField(name = "KeepAliveCheckCount")
        private long KeepAliveCheckCount;
        @JSONField(name = "KeepAlive")
        private boolean KeepAlive;
        @JSONField(name = "FailFast")
        private boolean FailFast;
        @JSONField(name = "MaxWait")
        private long MaxWait;
        @JSONField(name = "MaxWaitThreadCount")
        private long MaxWaitThreadCount;
        @JSONField(name = "PoolPreparedStatements")
        private boolean PoolPreparedStatements;
        @JSONField(name = "MaxPoolPreparedStatementPerConnectionSize")
        private long MaxPoolPreparedStatementPerConnectionSize;
        @JSONField(name = "MinEvictableIdleTimeMillis")
        private long MinEvictableIdleTimeMillis;
        @JSONField(name = "MaxEvictableIdleTimeMillis")
        private long MaxEvictableIdleTimeMillis;
        @JSONField(name = "LogDifferentThread")
        private boolean LogDifferentThread;
        @JSONField(name = "RecycleErrorCount")
        private long RecycleErrorCount;
        @JSONField(name = "PreparedStatementOpenCount")
        private long PreparedStatementOpenCount;
        @JSONField(name = "PreparedStatementClosedCount")
        private long PreparedStatementClosedCount;
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
