/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.wall;

import com.alibaba.druid.wall.spi.WallVisitorUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.alibaba.druid.wall.spi.WallVisitorUtils.loadResource;

public class WallConfig implements WallConfigMBean {

    private boolean             noneBaseStatementAllow      = false;

    private boolean             callAllow                   = true;
    private boolean             selelctAllow                = true;
    private boolean             selectIntoAllow             = true;
    private boolean             selectIntoOutfileAllow      = false;
    private boolean             selectWhereAlwayTrueCheck   = true;
    private boolean             selectHavingAlwayTrueCheck  = true;
    private boolean             selectUnionCheck            = true;
    private boolean             selectMinusCheck            = true;
    private boolean             selectExceptCheck           = true;
    private boolean             selectIntersectCheck        = true;
    private boolean             createTableAllow            = true;
    private boolean             dropTableAllow              = true;
    private boolean             alterTableAllow             = true;
    private boolean             renameTableAllow            = true;
    private boolean             hintAllow                   = true;
    private boolean             lockTableAllow              = true;
    private boolean             startTransactionAllow       = true;

    private boolean             conditionAndAlwayTrueAllow  = false;
    private boolean             conditionAndAlwayFalseAllow = false;
    private boolean             conditionDoubleConstAllow   = false;
    private boolean             conditionLikeTrueAllow      = true;

    private boolean             selectAllColumnAllow        = true;

    private boolean             deleteAllow                 = true;
    private boolean             deleteWhereAlwayTrueCheck   = true;
    private boolean             deleteWhereNoneCheck        = false;

    private boolean             updateAllow                 = true;
    private boolean             updateWhereAlayTrueCheck    = true;
    private boolean             updateWhereNoneCheck        = false;

    private boolean             insertAllow                 = true;
    private boolean             mergeAllow                  = true;
    private boolean             minusAllow                  = true;
    private boolean             intersectAllow              = true;
    private boolean             replaceAllow                = true;
    private boolean             setAllow                    = true;
    private boolean             commitAllow                 = true;
    private boolean             rollbackAllow               = true;
    private boolean             useAllow                    = true;

    private boolean             multiStatementAllow         = false;

    private boolean             truncateAllow               = true;

    private boolean             commentAllow                = false;
    private boolean             strictSyntaxCheck           = true;
    private boolean             constArithmeticAllow        = true;
    private boolean             limitZeroAllow              = false;

    private boolean             describeAllow               = true;
    private boolean             showAllow                   = true;

    private boolean             schemaCheck                 = true;
    private boolean             tableCheck                  = true;
    private boolean             functionCheck               = true;
    private boolean             objectCheck                 = true;
    private boolean             variantCheck                = true;

    private boolean             mustParameterized           = false;

    private boolean             doPrivilegedAllow           = false;

    protected final Set<String> denyFunctions               = new ConcurrentSkipListSet<String>();
    protected final Set<String> denyTables                  = new ConcurrentSkipListSet<String>();
    protected final Set<String> denySchemas                 = new ConcurrentSkipListSet<String>();
    protected final Set<String> denyVariants                = new ConcurrentSkipListSet<String>();
    protected final Set<String> denyObjects                 = new ConcurrentSkipListSet<String>();

    protected final Set<String> permitFunctions             = new ConcurrentSkipListSet<String>();
    protected final Set<String> permitTables                = new ConcurrentSkipListSet<String>();
    protected final Set<String> permitSchemas               = new ConcurrentSkipListSet<String>();
    protected final Set<String> permitVariants              = new ConcurrentSkipListSet<String>();

    protected final Set<String> readOnlyTables              = new ConcurrentSkipListSet<String>();

    private String              dir;

    private boolean             inited;

    private String              tenantTablePattern;
    private String              tenantColumn;
    private TenantCallBack      tenantCallBack;

    private boolean             wrapAllow                   = true;
    private boolean             metadataAllow               = true;

    private boolean             conditionOpXorAllow         = false;
    private boolean             conditionOpBitwseAllow      = true;

    private boolean             caseConditionConstAllow     = false;
    
    private boolean             completeInsertValuesCheck   = false;
    private int                 insertValuesCheckSize       = 3;

    public WallConfig(){

    }

    public boolean isCaseConditionConstAllow() {
        return caseConditionConstAllow;
    }

    public void setCaseConditionConstAllow(boolean caseConditionConstAllow) {
        this.caseConditionConstAllow = caseConditionConstAllow;
    }

    public boolean isConditionDoubleConstAllow() {
        return conditionDoubleConstAllow;
    }

    public void setConditionDoubleConstAllow(boolean conditionDoubleConstAllow) {
        this.conditionDoubleConstAllow = conditionDoubleConstAllow;
    }

    public boolean isConditionLikeTrueAllow() {
        return conditionLikeTrueAllow;
    }

    public void setConditionLikeTrueAllow(boolean conditionLikeTrueAllow) {
        this.conditionLikeTrueAllow = conditionLikeTrueAllow;
    }

    public boolean isLimitZeroAllow() {
        return limitZeroAllow;
    }

    public void setLimitZeroAllow(boolean limitZero) {
        this.limitZeroAllow = limitZero;
    }

    public boolean isUseAllow() {
        return useAllow;
    }

    public void setUseAllow(boolean useAllow) {
        this.useAllow = useAllow;
    }

    public boolean isCommitAllow() {
        return commitAllow;
    }

    public void setCommitAllow(boolean commitAllow) {
        this.commitAllow = commitAllow;
    }

    public boolean isRollbackAllow() {
        return rollbackAllow;
    }

    public void setRollbackAllow(boolean rollbackAllow) {
        this.rollbackAllow = rollbackAllow;
    }

    public boolean isIntersectAllow() {
        return intersectAllow;
    }

    public void setIntersectAllow(boolean intersectAllow) {
        this.intersectAllow = intersectAllow;
    }

    public boolean isMinusAllow() {
        return minusAllow;
    }

    public void setMinusAllow(boolean minusAllow) {
        this.minusAllow = minusAllow;
    }

    public boolean isConditionOpXorAllow() {
        return conditionOpXorAllow;
    }

    public void setConditionOpXorAllow(boolean conditionOpXorAllow) {
        this.conditionOpXorAllow = conditionOpXorAllow;
    }

    public String getTenantTablePattern() {
        return tenantTablePattern;
    }

    public void setTenantTablePattern(String tenantTablePattern) {
        this.tenantTablePattern = tenantTablePattern;
    }

    public String getTenantColumn() {
        return tenantColumn;
    }

    public void setTenantColumn(String tenantColumn) {
        this.tenantColumn = tenantColumn;
    }

    public TenantCallBack getTenantCallBack() {
        return tenantCallBack;
    }

    public void setTenantCallBack(TenantCallBack tenantCallBack) {
        this.tenantCallBack = tenantCallBack;
    }

    public boolean isMetadataAllow() {
        return metadataAllow;
    }

    public void setMetadataAllow(boolean metadataAllow) {
        this.metadataAllow = metadataAllow;
    }

    public boolean isWrapAllow() {
        return wrapAllow;
    }

    public void setWrapAllow(boolean wrapAllow) {
        this.wrapAllow = wrapAllow;
    }

    public boolean isDoPrivilegedAllow() {
        return doPrivilegedAllow;
    }

    public void setDoPrivilegedAllow(boolean doPrivilegedAllow) {
        this.doPrivilegedAllow = doPrivilegedAllow;
    }

    public boolean isSelectAllColumnAllow() {
        return selectAllColumnAllow;
    }

    public void setSelectAllColumnAllow(boolean selectAllColumnAllow) {
        this.selectAllColumnAllow = selectAllColumnAllow;
    }

    public boolean isInited() {
        return inited;
    }

    public WallConfig(String dir){
        this.dir = dir;
        this.init();
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public final void init() {
        loadConfig(dir);
    }

    public void loadConfig(String dir) {
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }

        loadResource(this.denyVariants, dir + "/deny-variant.txt");
        loadResource(this.denySchemas, dir + "/deny-schema.txt");
        loadResource(this.denyFunctions, dir + "/deny-function.txt");
        loadResource(this.denyTables, dir + "/deny-table.txt");
        loadResource(this.denyObjects, dir + "/deny-object.txt");
        loadResource(this.readOnlyTables, dir + "/readonly-table.txt");

        loadResource(this.permitFunctions, dir + "/permit-function.txt");
        loadResource(this.permitTables, dir + "/permit-table.txt");
        loadResource(this.permitSchemas, dir + "/permit-schema.txt");
        loadResource(this.permitVariants, dir + "/permit-variant.txt");
    }

    public boolean isNoneBaseStatementAllow() {
        return noneBaseStatementAllow;
    }

    public void setNoneBaseStatementAllow(boolean noneBaseStatementAllow) {
        this.noneBaseStatementAllow = noneBaseStatementAllow;
    }

    /**
     * allow mysql describe statement
     * 
     * @since 0.2.10
     * @return
     */
    public boolean isDescribeAllow() {
        return describeAllow;
    }

    /**
     * set allow mysql describe statement
     * 
     * @since 0.2.10
     * @return
     */
    public void setDescribeAllow(boolean describeAllow) {
        this.describeAllow = describeAllow;
    }

    public boolean isShowAllow() {
        return showAllow;
    }

    public void setShowAllow(boolean showAllow) {
        this.showAllow = showAllow;
    }

    public boolean isTruncateAllow() {
        return truncateAllow;
    }

    public void setTruncateAllow(boolean truncateAllow) {
        this.truncateAllow = truncateAllow;
    }

    public boolean isSelectIntoAllow() {
        return selectIntoAllow;
    }

    public void setSelectIntoAllow(boolean selectIntoAllow) {
        this.selectIntoAllow = selectIntoAllow;
    }

    public boolean isSelectIntoOutfileAllow() {
        return selectIntoOutfileAllow;
    }

    public void setSelectIntoOutfileAllow(boolean selectIntoOutfileAllow) {
        this.selectIntoOutfileAllow = selectIntoOutfileAllow;
    }

    public boolean isCreateTableAllow() {
        return createTableAllow;
    }

    public void setCreateTableAllow(boolean createTableAllow) {
        this.createTableAllow = createTableAllow;
    }

    public boolean isDropTableAllow() {
        return dropTableAllow;
    }

    public void setDropTableAllow(boolean dropTableAllow) {
        this.dropTableAllow = dropTableAllow;
    }

    public boolean isAlterTableAllow() {
        return alterTableAllow;
    }

    public void setAlterTableAllow(boolean alterTableAllow) {
        this.alterTableAllow = alterTableAllow;
    }

    public boolean isRenameTableAllow() {
        return renameTableAllow;
    }

    public void setRenameTableAllow(boolean renameTableAllow) {
        this.renameTableAllow = renameTableAllow;
    }

    public boolean isSelectUnionCheck() {
        return selectUnionCheck;
    }

    public void setSelectUnionCheck(boolean selectUnionCheck) {
        this.selectUnionCheck = selectUnionCheck;
    }

    public boolean isSelectMinusCheck() {
        return selectMinusCheck;
    }

    public void setSelectMinusCheck(boolean selectMinusCheck) {
        this.selectMinusCheck = selectMinusCheck;
    }

    public boolean isSelectExceptCheck() {
        return selectExceptCheck;
    }

    public void setSelectExceptCheck(boolean selectExceptCheck) {
        this.selectExceptCheck = selectExceptCheck;
    }

    public boolean isSelectIntersectCheck() {
        return selectIntersectCheck;
    }

    public void setSelectIntersectCheck(boolean selectIntersectCheck) {
        this.selectIntersectCheck = selectIntersectCheck;
    }

    public boolean isDeleteAllow() {
        return deleteAllow;
    }

    public void setDeleteAllow(boolean deleteAllow) {
        this.deleteAllow = deleteAllow;
    }

    public boolean isDeleteWhereNoneCheck() {
        return deleteWhereNoneCheck;
    }

    public void setDeleteWhereNoneCheck(boolean deleteWhereNoneCheck) {
        this.deleteWhereNoneCheck = deleteWhereNoneCheck;
    }

    public boolean isUpdateAllow() {
        return updateAllow;
    }

    public void setUpdateAllow(boolean updateAllow) {
        this.updateAllow = updateAllow;
    }

    public boolean isUpdateWhereNoneCheck() {
        return updateWhereNoneCheck;
    }

    public void setUpdateWhereNoneCheck(boolean updateWhereNoneCheck) {
        this.updateWhereNoneCheck = updateWhereNoneCheck;
    }

    public boolean isInsertAllow() {
        return insertAllow;
    }

    public void setInsertAllow(boolean insertAllow) {
        this.insertAllow = insertAllow;
    }

    public boolean isReplaceAllow() {
        return replaceAllow;
    }

    public void setReplaceAllow(boolean replaceAllow) {
        this.replaceAllow = replaceAllow;
    }

    public boolean isSetAllow() {
        return setAllow;
    }

    public void setSetAllow(boolean value) {
        this.setAllow = value;
    }

    public boolean isMergeAllow() {
        return mergeAllow;
    }

    public void setMergeAllow(boolean mergeAllow) {
        this.mergeAllow = mergeAllow;
    }

    public boolean isMultiStatementAllow() {
        return multiStatementAllow;
    }

    public void setMultiStatementAllow(boolean multiStatementAllow) {
        this.multiStatementAllow = multiStatementAllow;
    }

    public boolean isSchemaCheck() {
        return schemaCheck;
    }

    public void setSchemaCheck(boolean schemaCheck) {
        this.schemaCheck = schemaCheck;
    }

    public boolean isTableCheck() {
        return tableCheck;
    }

    public void setTableCheck(boolean tableCheck) {
        this.tableCheck = tableCheck;
    }

    public boolean isFunctionCheck() {
        return functionCheck;
    }

    public void setFunctionCheck(boolean functionCheck) {
        this.functionCheck = functionCheck;
    }

    public boolean isVariantCheck() {
        return variantCheck;
    }

    public void setVariantCheck(boolean variantCheck) {
        this.variantCheck = variantCheck;
    }

    public boolean isObjectCheck() {
        return objectCheck;
    }

    public void setObjectCheck(boolean objectCheck) {
        this.objectCheck = objectCheck;
    }

    // ///////////////////

    public boolean isCommentAllow() {
        return commentAllow;
    }

    public void setCommentAllow(boolean commentAllow) {
        this.commentAllow = commentAllow;
    }

    public boolean isStrictSyntaxCheck() {
        return strictSyntaxCheck;
    }

    public void setStrictSyntaxCheck(boolean strictSyntaxCheck) {
        this.strictSyntaxCheck = strictSyntaxCheck;
    }

    public boolean isConstArithmeticAllow() {
        return constArithmeticAllow;
    }

    public void setConstArithmeticAllow(boolean constArithmeticAllow) {
        this.constArithmeticAllow = constArithmeticAllow;
    }

    public Set<String> getDenyFunctions() {
        return denyFunctions;
    }

    public Set<String> getDenyTables() {
        return denyTables;
    }

    public Set<String> getDenySchemas() {
        return denySchemas;
    }

    public Set<String> getDenyVariants() {
        return denyVariants;
    }

    public Set<String> getDenyObjects() {
        return denyObjects;
    }

    public Set<String> getReadOnlyTables() {
        return readOnlyTables;
    }

    public void addReadOnlyTable(String tableName) {
        this.readOnlyTables.add(tableName);
    }

    public boolean isReadOnly(String tableName) {
        return this.readOnlyTables.contains(tableName);
    }

    public Set<String> getPermitFunctions() {
        return permitFunctions;
    }

    public Set<String> getPermitTables() {
        return permitTables;
    }

    public Set<String> getPermitSchemas() {
        return permitSchemas;
    }

    public Set<String> getPermitVariants() {
        return permitVariants;
    }

    public boolean isMustParameterized() {
        return mustParameterized;
    }

    public void setMustParameterized(boolean mustParameterized) {
        this.mustParameterized = mustParameterized;
    }

    public boolean isDenyObjects(String name) {
        if (!objectCheck) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        return denyObjects.contains(name);
    }

    public boolean isDenySchema(String name) {
        if (!schemaCheck) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        return this.denySchemas.contains(name);
    }

    public boolean isDenyFunction(String name) {
        if (!functionCheck) {
            return false;
        }

        name = WallVisitorUtils.form(name);
        return this.denyFunctions.contains(name);
    }

    public boolean isCallAllow() {
        return callAllow;
    }

    public void setCallAllow(boolean callAllow) {
        this.callAllow = callAllow;
    }

    public boolean isHintAllow() {
        return hintAllow;
    }

    public void setHintAllow(boolean hintAllow) {
        this.hintAllow = hintAllow;
    }

    public static abstract interface TenantCallBack {

        public static enum StatementType {
            SELECT, UPDATE, INSERT, DELETE
        }

        Object getTenantValue(StatementType statementType, String tableName);

        String getTenantColumn(StatementType statementType, String tableName);

        /**
         * 返回resultset隐藏列名
         * 
         * @param tableName
         * @return
         */
        String getHiddenColumn(String tableName);

        /**
         * resultset返回值中如果包含tenantColumn的回调函数
         * 
         * @param value tenantColumn对应的值
         */
        void filterResultsetTenantColumn(Object value);
    }

    public boolean isSelelctAllow() {
        return selelctAllow;
    }

    public void setSelelctAllow(boolean selelctAllow) {
        this.selelctAllow = selelctAllow;
    }

    public boolean isSelectWhereAlwayTrueCheck() {
        return selectWhereAlwayTrueCheck;
    }

    public void setSelectWhereAlwayTrueCheck(boolean selectWhereAlwayTrueCheck) {
        this.selectWhereAlwayTrueCheck = selectWhereAlwayTrueCheck;
    }

    public boolean isSelectHavingAlwayTrueCheck() {
        return selectHavingAlwayTrueCheck;
    }

    public void setSelectHavingAlwayTrueCheck(boolean selectHavingAlwayTrueCheck) {
        this.selectHavingAlwayTrueCheck = selectHavingAlwayTrueCheck;
    }

    public boolean isConditionAndAlwayTrueAllow() {
        return conditionAndAlwayTrueAllow;
    }

    public void setConditionAndAlwayTrueAllow(boolean conditionAndAlwayTrueAllow) {
        this.conditionAndAlwayTrueAllow = conditionAndAlwayTrueAllow;
    }

    public boolean isConditionAndAlwayFalseAllow() {
        return conditionAndAlwayFalseAllow;
    }

    public void setConditionAndAlwayFalseAllow(boolean conditionAndAlwayFalseAllow) {
        this.conditionAndAlwayFalseAllow = conditionAndAlwayFalseAllow;
    }

    public boolean isDeleteWhereAlwayTrueCheck() {
        return deleteWhereAlwayTrueCheck;
    }

    public void setDeleteWhereAlwayTrueCheck(boolean deleteWhereAlwayTrueCheck) {
        this.deleteWhereAlwayTrueCheck = deleteWhereAlwayTrueCheck;
    }

    public boolean isUpdateWhereAlayTrueCheck() {
        return updateWhereAlayTrueCheck;
    }

    public void setUpdateWhereAlayTrueCheck(boolean updateWhereAlayTrueCheck) {
        this.updateWhereAlayTrueCheck = updateWhereAlayTrueCheck;
    }

    public boolean isConditionOpBitwseAllow() {
        return conditionOpBitwseAllow;
    }

    public void setConditionOpBitwseAllow(boolean conditionOpBitwseAllow) {
        this.conditionOpBitwseAllow = conditionOpBitwseAllow;
    }

    public void setInited(boolean inited) {
        this.inited = inited;
    }

    public boolean isLockTableAllow() {
        return lockTableAllow;
    }

    public void setLockTableAllow(boolean lockTableAllow) {
        this.lockTableAllow = lockTableAllow;
    }

    public boolean isStartTransactionAllow() {
        return startTransactionAllow;
    }

    public void setStartTransactionAllow(boolean startTransactionAllow) {
        this.startTransactionAllow = startTransactionAllow;
    }

    public boolean isCompleteInsertValuesCheck() {
        return completeInsertValuesCheck;
    }

    public void setCompleteInsertValuesCheck(boolean completeInsertValuesCheck) {
        this.completeInsertValuesCheck = completeInsertValuesCheck;
    }

    public int getInsertValuesCheckSize() {
        return insertValuesCheckSize;
    }

    public void setInsertValuesCheckSize(int insertValuesCheckSize) {
        this.insertValuesCheckSize = insertValuesCheckSize;
    }

}
