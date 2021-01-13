package com.alibaba.druid.admin.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
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
        private int checkCount;
        @JSONField(name = "hardCheckCount")
        private int hardCheckCount;
        @JSONField(name = "violationCount")
        private int violationCount;
        @JSONField(name = "violationEffectRowCount")
        private int violationEffectRowCount;
        @JSONField(name = "blackListHitCount")
        private int blackListHitCount;
        @JSONField(name = "blackListSize")
        private int blackListSize;
        @JSONField(name = "whiteListHitCount")
        private int whiteListHitCount;
        @JSONField(name = "whiteListSize")
        private int whiteListSize;
        @JSONField(name = "syntaxErrorCount")
        private int syntaxErrorCount;
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
            private int selectCount;

            @JSONField(name = "fetchRowCount")
            private int fetchRowCount;

            @JSONField(name = "fetchRowCountHistogram")
            private List<Integer> fetchRowCountHistogram;
        }

        @NoArgsConstructor
        @Data
        public static class FunctionsBean {
            @JSONField(name = "name")
            private String name;
            @JSONField(name = "invokeCount")
            private int invokeCount;
        }

        @NoArgsConstructor
        @Data
        public static class WhiteListBean {
            @JSONField(name = "sql")
            private String sql;
            @JSONField(name = "sample")
            private String sample;
            @JSONField(name = "executeCount")
            private int executeCount;
            @JSONField(name = "fetchRowCount")
            private int fetchRowCount;
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
