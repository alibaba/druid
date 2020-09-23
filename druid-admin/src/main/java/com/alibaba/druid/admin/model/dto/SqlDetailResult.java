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
        private int ExecuteAndResultSetHoldTime;
        @JSONField(name = "LastErrorMessage")
        private Object LastErrorMessage;
        @JSONField(name = "InputStreamOpenCount")
        private int InputStreamOpenCount;
        @JSONField(name = "BatchSizeTotal")
        private int BatchSizeTotal;
        @JSONField(name = "FetchRowCountMax")
        private int FetchRowCountMax;
        @JSONField(name = "ErrorCount")
        private int ErrorCount;
        @JSONField(name = "BatchSizeMax")
        private int BatchSizeMax;
        @JSONField(name = "URL")
        private Object URL;
        @JSONField(name = "Name")
        private Object Name;
        @JSONField(name = "LastErrorTime")
        private Object LastErrorTime;
        @JSONField(name = "ReaderOpenCount")
        private int ReaderOpenCount;
        @JSONField(name = "parsedRelationships")
        private String parsedRelationships;
        @JSONField(name = "EffectedRowCountMax")
        private int EffectedRowCountMax;
        @JSONField(name = "LastErrorClass")
        private Object LastErrorClass;
        @JSONField(name = "InTransactionCount")
        private int InTransactionCount;
        @JSONField(name = "LastErrorStackTrace")
        private Object LastErrorStackTrace;
        @JSONField(name = "ResultSetHoldTime")
        private int ResultSetHoldTime;
        @JSONField(name = "TotalTime")
        private int TotalTime;
        @JSONField(name = "ID")
        private int ID;
        @JSONField(name = "ConcurrentMax")
        private int ConcurrentMax;
        @JSONField(name = "RunningCount")
        private int RunningCount;
        @JSONField(name = "FetchRowCount")
        private int FetchRowCount;
        @JSONField(name = "parsedFields")
        private String parsedFields;
        @JSONField(name = "MaxTimespanOccurTime")
        private String MaxTimespanOccurTime;
        @JSONField(name = "LastSlowParameters")
        private Object LastSlowParameters;
        @JSONField(name = "ReadBytesLength")
        private int ReadBytesLength;
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
        private int MaxTimespan;
        @JSONField(name = "parsedTable")
        private String parsedTable;
        @JSONField(name = "parsedOrderbycolumns")
        private String parsedOrderbycolumns;
        @JSONField(name = "BlobOpenCount")
        private int BlobOpenCount;
        @JSONField(name = "ExecuteCount")
        private int ExecuteCount;
        @JSONField(name = "EffectedRowCount")
        private int EffectedRowCount;
        @JSONField(name = "ReadStringLength")
        private int ReadStringLength;
        @JSONField(name = "File")
        private Object File;
        @JSONField(name = "ClobOpenCount")
        private int ClobOpenCount;
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
