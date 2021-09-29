package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linchtech
 * @date 2020-09-16 16:18
 **/
@NoArgsConstructor
@Data
public class SqlDetailResult {

    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private ContentBean Content;

    @NoArgsConstructor
    @Data
    public static class ContentBean {
        @JSONField(name = "ExecuteAndResultSetHoldTime")
        private long ExecuteAndResultSetHoldTime;
        @JSONField(name = "LastErrorMessage")
        private Object LastErrorMessage;
        @JSONField(name = "InputStreamOpenCount")
        private long InputStreamOpenCount;
        @JSONField(name = "BatchSizeTotal")
        private long BatchSizeTotal;
        @JSONField(name = "FetchRowCountMax")
        private long FetchRowCountMax;
        @JSONField(name = "ErrorCount")
        private long ErrorCount;
        @JSONField(name = "BatchSizeMax")
        private long BatchSizeMax;
        @JSONField(name = "URL")
        private Object URL;
        @JSONField(name = "Name")
        private Object Name;
        @JSONField(name = "LastErrorTime")
        private Object LastErrorTime;
        @JSONField(name = "ReaderOpenCount")
        private long ReaderOpenCount;
        @JSONField(name = "parsedRelationships")
        private String parsedRelationships;
        @JSONField(name = "EffectedRowCountMax")
        private long EffectedRowCountMax;
        @JSONField(name = "LastErrorClass")
        private Object LastErrorClass;
        @JSONField(name = "InTransactionCount")
        private long InTransactionCount;
        @JSONField(name = "LastErrorStackTrace")
        private Object LastErrorStackTrace;
        @JSONField(name = "ResultSetHoldTime")
        private long ResultSetHoldTime;
        @JSONField(name = "TotalTime")
        private long TotalTime;
        @JSONField(name = "ID")
        private long ID;
        @JSONField(name = "ConcurrentMax")
        private long ConcurrentMax;
        @JSONField(name = "RunningCount")
        private long RunningCount;
        @JSONField(name = "FetchRowCount")
        private long FetchRowCount;
        @JSONField(name = "parsedFields")
        private String parsedFields;
        @JSONField(name = "MaxTimespanOccurTime")
        private String MaxTimespanOccurTime;
        @JSONField(name = "LastSlowParameters")
        private Object LastSlowParameters;
        @JSONField(name = "ReadBytesLength")
        private long ReadBytesLength;
        @JSONField(name = "formattedSql")
        private String formattedSql;
        @JSONField(name = "DbType")
        private String DbType;
        @JSONField(name = "DataSource")
        private Object DataSource;
        @JSONField(name = "SQL")
        private String SQL;
        @JSONField(name = "HASH")
        private long HASH;
        @JSONField(name = "LastError")
        private Object LastError;
        @JSONField(name = "MaxTimespan")
        private long MaxTimespan;
        @JSONField(name = "parsedTable")
        private String parsedTable;
        @JSONField(name = "parsedOrderbycolumns")
        private String parsedOrderbycolumns;
        @JSONField(name = "BlobOpenCount")
        private long BlobOpenCount;
        @JSONField(name = "ExecuteCount")
        private long ExecuteCount;
        @JSONField(name = "EffectedRowCount")
        private long EffectedRowCount;
        @JSONField(name = "ReadStringLength")
        private long ReadStringLength;
        @JSONField(name = "File")
        private Object File;
        @JSONField(name = "ClobOpenCount")
        private long ClobOpenCount;
        @JSONField(name = "LastTime")
        private String LastTime;
        @JSONField(name = "parsedConditions")
        private String parsedConditions;
        @JSONField(name = "EffectedRowCountHistogram")
        private List<Integer> EffectedRowCountHistogram;
        @JSONField(name = "Histogram")
        private List<Integer> Histogram;
        @JSONField(name = "ExecuteAndResultHoldTimeHistogram")
        private List<Integer> ExecuteAndResultHoldTimeHistogram;
        @JSONField(name = "FetchRowCountHistogram")
        private List<Integer> FetchRowCountHistogram;
    }
}
