package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linchtech
 * @date 2020-09-21 9:26
 **/

@NoArgsConstructor
@Data
public class ConnectionResult {

    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private List<ContentBean> Content;

    @NoArgsConstructor
    @Data
    public static class ContentBean {

        @JSONField(name = "id")
        private long id;
        @JSONField(name = "connectionId")
        private long connectionId;
        @JSONField(name = "useCount")
        private long useCount;
        @JSONField(name = "lastActiveTime")
        private String lastActiveTime;
        @JSONField(name = "connectTime")
        private String connectTime;
        @JSONField(name = "holdability")
        private long holdability;
        @JSONField(name = "transactionIsolation")
        private long transactionIsolation;
        @JSONField(name = "autoCommit")
        private boolean autoCommit;
        @JSONField(name = "readoOnly")
        private boolean readoOnly;
        @JSONField(name = "keepAliveCheckCount")
        private long keepAliveCheckCount;
        @JSONField(name = "pscache")
        private List<?> pscache;
    }
}
