package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
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
        private int RunningCount;
        @JSONField(name = "ConcurrentMax")
        private int ConcurrentMax;
        @JSONField(name = "RequestCount")
        private int RequestCount;
        @JSONField(name = "RequestTimeMillis")
        private int RequestTimeMillis;
        @JSONField(name = "ErrorCount")
        private int ErrorCount;
        @JSONField(name = "LastAccessTime")
        private String LastAccessTime;
        @JSONField(name = "JdbcCommitCount")
        private int JdbcCommitCount;
        @JSONField(name = "JdbcRollbackCount")
        private int JdbcRollbackCount;
        @JSONField(name = "JdbcExecuteCount")
        private int JdbcExecuteCount;
        @JSONField(name = "JdbcExecuteErrorCount")
        private int JdbcExecuteErrorCount;
        @JSONField(name = "JdbcExecutePeak")
        private int JdbcExecutePeak;
        @JSONField(name = "JdbcExecuteTimeMillis")
        private int JdbcExecuteTimeMillis;
        @JSONField(name = "JdbcFetchRowCount")
        private int JdbcFetchRowCount;
        @JSONField(name = "JdbcFetchRowPeak")
        private int JdbcFetchRowPeak;
        @JSONField(name = "JdbcUpdateCount")
        private int JdbcUpdateCount;
        @JSONField(name = "JdbcUpdatePeak")
        private int JdbcUpdatePeak;
        @JSONField(name = "JdbcPoolConnectionOpenCount")
        private int JdbcPoolConnectionOpenCount;
        @JSONField(name = "JdbcPoolConnectionCloseCount")
        private int JdbcPoolConnectionCloseCount;
        @JSONField(name = "JdbcResultSetOpenCount")
        private int JdbcResultSetOpenCount;
        @JSONField(name = "JdbcResultSetCloseCount")
        private int JdbcResultSetCloseCount;
        @JSONField(name = "RequestTimeMillisMax")
        private int RequestTimeMillisMax;
        @JSONField(name = "RequestTimeMillisMaxOccurTime")
        private String RequestTimeMillisMaxOccurTime;
        @JSONField(name = "Histogram")
        private List<Integer> Histogram;
        @JSONField(name = "Profiles")
        private List<?> Profiles;
    }
}
