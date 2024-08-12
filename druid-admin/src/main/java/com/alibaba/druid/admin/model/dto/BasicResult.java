package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BasicResult {

    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private List<ContentBean> Content;

    @NoArgsConstructor
    @Data
    public static class ContentBean {
        private String serviceId;
        private String name;

        @JSONField(name = "Version")
        private String Version;
        @JSONField(name = "Drivers")
        private List<String> Drivers;
        @JSONField(name = "ResetEnable")
        private boolean ResetEnable;
        @JSONField(name = "ResetCount")
        private int ResetCount;
        @JSONField(name = "JavaVMName")
        private String JavaVMName;
        @JSONField(name = "JavaVersion")
        private String JavaVersion;
        @JSONField(name = "JavaClassPath")
        private String JavaClassPath;
        @JSONField(name = "StartTime")
        private String StartTime;
    }

}
