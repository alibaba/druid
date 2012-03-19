package com.alibaba.druid.wall;

import java.util.Set;

public interface WallConfigMBean {

    boolean isInited();

    String getDir();

    void setDir(String dir);

    void init();

    void loadConfig(String dir);

    boolean isNoneBaseStatementAllow();

    void setNoneBaseStatementAllow(boolean noneBaseStatementAllow);

    boolean isTruncateAllow();

    void setTruncateAllow(boolean truncateAllow);

    boolean isSelelctAllow();

    void setSelelctAllow(boolean selelctAllow);

    boolean isSelectIntoAllow();

    void setSelectIntoAllow(boolean selectIntoAllow);

    boolean isSelectIntoOutfileAllow();

    void setSelectIntoOutfileAllow(boolean selectIntoOutfileAllow);

    boolean isSelectUnionCheck();

    void setSelectUnionCheck(boolean selectUnionCheck);

    boolean isSelectWhereAlwayTrueCheck();

    void setSelectWhereAlwayTrueCheck(boolean selectWhereAlwayTrueCheck);

    boolean isSelectHavingAlwayTrueCheck();

    void setSelectHavingAlwayTrueCheck(boolean selectHavingAlwayTrueCheck);

    boolean isDeleteAllow();

    void setDeleteAllow(boolean deleteAllow);

    boolean isDeleteWhereAlwayTrueCheck();

    void setDeleteWhereAlwayTrueCheck(boolean deleteWhereAlwayTrueCheck);

    boolean isUpdateAllow();

    void setUpdateAllow(boolean updateAllow);

    boolean isUpdateWhereAlayTrueCheck();

    void setUpdateWhereAlayTrueCheck(boolean updateWhereAlayTrueCheck);

    boolean isInsertAllow();

    void setInsertAllow(boolean insertAllow);

    boolean isMergeAllow();

    void setMergeAllow(boolean mergeAllow);

    boolean isMultiStatementAllow();

    void setMultiStatementAllow(boolean multiStatementAllow);

    boolean isSchemaCheck();

    void setSchemaCheck(boolean schemaCheck);

    boolean isTableCheck();

    void setTableCheck(boolean tableCheck);

    boolean isFunctionCheck();

    void setFunctionCheck(boolean functionCheck);

    boolean isVariantCheck();

    void setVariantCheck(boolean variantCheck);

    boolean isObjectCheck();

    void setObjectCheck(boolean objectCheck);

    boolean isCommentAllow();

    void setCommentAllow(boolean commentAllow);

    Set<String> getPermitFunctions();

    Set<String> getPermitTables();

    Set<String> getPermitSchemas();

    Set<String> getPermitVariants();

    Set<String> getPermitObjects();

    Set<String> getReadOnlyTables();

    boolean isPermitObjects(String name);

    boolean isPermitSchema(String name);

    boolean isPermitFunction(String name);

}
