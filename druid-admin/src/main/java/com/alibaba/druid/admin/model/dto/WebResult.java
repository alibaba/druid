package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linchtech
 * @date 2020-09-17 18:27
 **/
@NoArgsConstructor
@Data
public class WebResult {
    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private List<ContentBean> Content;

    @NoArgsConstructor
    @Data
    public static class ContentBean {
        @JSONField(name = "URI")
        private String URI;
        @JSONField(name = "RunningCount")
        private long RunningCount;
        @JSONField(name = "ConcurrentMax")
        private long ConcurrentMax;
        @JSONField(name = "RequestCount")
        private long RequestCount;
        @JSONField(name = "RequestTimeMillis")
        private long RequestTimeMillis;
        @JSONField(name = "ErrorCount")
        private long ErrorCount;
        @JSONField(name = "LastAccessTime")
        private String LastAccessTime;
        @JSONField(name = "JdbcCommitCount")
        private long JdbcCommitCount;
        @JSONField(name = "JdbcRollbackCount")
        private long JdbcRollbackCount;
        @JSONField(name = "JdbcExecuteCount")
        private long JdbcExecuteCount;
        @JSONField(name = "JdbcExecuteErrorCount")
        private long JdbcExecuteErrorCount;
        @JSONField(name = "JdbcExecutePeak")
        private long JdbcExecutePeak;
        @JSONField(name = "JdbcExecuteTimeMillis")
        private long JdbcExecuteTimeMillis;
        @JSONField(name = "JdbcFetchRowCount")
        private long JdbcFetchRowCount;
        @JSONField(name = "JdbcFetchRowPeak")
        private long JdbcFetchRowPeak;
        @JSONField(name = "JdbcUpdateCount")
        private long JdbcUpdateCount;
        @JSONField(name = "JdbcUpdatePeak")
        private long JdbcUpdatePeak;
        @JSONField(name = "JdbcPoolConnectionOpenCount")
        private long JdbcPoolConnectionOpenCount;
        @JSONField(name = "JdbcPoolConnectionCloseCount")
        private long JdbcPoolConnectionCloseCount;
        @JSONField(name = "JdbcResultSetOpenCount")
        private long JdbcResultSetOpenCount;
        @JSONField(name = "JdbcResultSetCloseCount")
        private long JdbcResultSetCloseCount;
        @JSONField(name = "RequestTimeMillisMax")
        private long RequestTimeMillisMax;
        @JSONField(name = "RequestTimeMillisMaxOccurTime")
        private String RequestTimeMillisMaxOccurTime;
        @JSONField(name = "Histogram")
        private List<Integer> Histogram;
        @JSONField(name = "Profiles")
        private List<?> Profiles;
    }
}
