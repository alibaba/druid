package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author linchtech
 * @date 2020-09-17 18:18
 **/
@Data
@NoArgsConstructor
public class WallResult {
    @JSONField(name = "ResultCode")
    private int ResultCode;
    @JSONField(name = "Content")
    private ContentBean Content = new ContentBean();

    @NoArgsConstructor
    @Data
    public static class ContentBean {
        @JSONField(name = "checkCount")
        private long checkCount;
        @JSONField(name = "hardCheckCount")
        private long hardCheckCount;
        @JSONField(name = "violationCount")
        private long violationCount;
        @JSONField(name = "violationEffectRowCount")
        private long violationEffectRowCount;
        @JSONField(name = "blackListHitCount")
        private long blackListHitCount;
        @JSONField(name = "blackListSize")
        private long blackListSize;
        @JSONField(name = "whiteListHitCount")
        private long whiteListHitCount;
        @JSONField(name = "whiteListSize")
        private long whiteListSize;
        @JSONField(name = "syntaxErrorCount")
        private long syntaxErrorCount;
        @JSONField(name = "tables")
        private List<TablesBean> tables = new ArrayList<>();
        @JSONField(name = "functions")
        private List<FunctionsBean> functions = new ArrayList<>();
        @JSONField(name = "blackList")
        private List<Object> blackList = new ArrayList<>();
        @JSONField(name = "whiteList")
        private List<WhiteListBean> whiteList = new ArrayList<>();

        @NoArgsConstructor
        @Data
        public static class TablesBean {
            @JSONField(name = "name")
            private String name;

            @JSONField(name = "selectCount")
            private long selectCount;

            @JSONField(name = "fetchRowCount")
            private long fetchRowCount;

            @JSONField(name = "fetchRowCountHistogram")
            private List<Integer> fetchRowCountHistogram;
        }

        @NoArgsConstructor
        @Data
        public static class FunctionsBean {
            @JSONField(name = "name")
            private String name;
            @JSONField(name = "invokeCount")
            private long invokeCount;
        }

        @NoArgsConstructor
        @Data
        public static class WhiteListBean {
            @JSONField(name = "sql")
            private String sql;
            @JSONField(name = "sample")
            private String sample;
            @JSONField(name = "executeCount")
            private long executeCount;
            @JSONField(name = "fetchRowCount")
            private long fetchRowCount;
        }
    }

    /**
     * 累加结果
     *
     * @param wallResult 需要累加的对象
     * @param sumResult  累加后的对象
     */
    public void sum(WallResult wallResult, WallResult sumResult) {
        sumResult.getContent().setCheckCount(sumResult.getContent().getCheckCount() + wallResult.getContent().getCheckCount());
        sumResult.getContent().setHardCheckCount(sumResult.getContent().getHardCheckCount() + wallResult.getContent().getHardCheckCount());
        sumResult.getContent().setViolationCount(sumResult.getContent().getViolationCount() + wallResult.getContent().getViolationCount());
        sumResult.getContent().setViolationEffectRowCount(sumResult.getContent().getViolationEffectRowCount() + wallResult.getContent().getViolationEffectRowCount());
        sumResult.getContent().setBlackListHitCount(sumResult.getContent().getBlackListHitCount() + wallResult.getContent().getBlackListHitCount());
        sumResult.getContent().setBlackListSize(sumResult.getContent().getBlackListSize() + wallResult.getContent().getBlackListSize());
        sumResult.getContent().setWhiteListHitCount(sumResult.getContent().getWhiteListHitCount() + wallResult.getContent().getWhiteListHitCount());
        sumResult.getContent().setWhiteListSize(sumResult.getContent().getWhiteListSize() + wallResult.getContent().getWhiteListSize());
        sumResult.getContent().setSyntaxErrorCount(sumResult.getContent().getSyntaxErrorCount() + wallResult.getContent().getSyntaxErrorCount());


        sumResult.getContent().getTables().addAll(wallResult.getContent().getTables() == null ? Collections.emptyList() : wallResult.getContent().getTables());
        sumResult.getContent().getFunctions().addAll(wallResult.getContent().getFunctions() == null ? Collections.emptyList() :
                wallResult.getContent().getFunctions());
        sumResult.getContent().getBlackList().addAll(wallResult.getContent().getBlackList() == null ? Collections.emptyList() :
                wallResult.getContent().getBlackList());
        sumResult.getContent().getWhiteList().addAll(wallResult.getContent().getWhiteList() == null ? Collections.emptyList() : wallResult.getContent().getWhiteList());
    }
}
