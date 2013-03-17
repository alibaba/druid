/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import static com.alibaba.druid.wall.spi.WallVisitorUtils.loadResource;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.druid.wall.spi.WallVisitorUtils;

public class WallConfig implements WallConfigMBean {

    private boolean             noneBaseStatementAllow     = false;

    private boolean             callAllow                  = true;
    private boolean             selelctAllow               = true;
    private boolean             selectIntoAllow            = true;
    private boolean             selectIntoOutfileAllow     = false;
    private boolean             selectWhereAlwayTrueCheck  = true;
    private boolean             selectHavingAlwayTrueCheck = true;
    private boolean             selectUnionCheck           = true;

    private boolean             selectAllColumnAllow       = true;

    private boolean             deleteAllow                = true;
    private boolean             deleteWhereAlwayTrueCheck  = true;

    private boolean             updateAllow                = true;
    private boolean             updateWhereAlayTrueCheck   = true;

    private boolean             insertAllow                = true;
    private boolean             mergeAllow                 = true;

    private boolean             multiStatementAllow        = false;

    private boolean             truncateAllow              = false;

    private boolean             commentAllow               = false;

    private boolean             describeAllow              = false;

    private boolean             schemaCheck                = true;
    private boolean             tableCheck                 = true;
    private boolean             functionCheck              = true;
    private boolean             objectCheck                = true;
    private boolean             variantCheck               = true;

    private boolean             mustParameterized          = false;

    private boolean             doPrivilegedAllow          = false;

    protected final Set<String> denyFunctions            = new HashSet<String>();
    protected final Set<String> denyTables               = new HashSet<String>();
    protected final Set<String> denySchemas              = new HashSet<String>();
    protected final Set<String> denyVariants             = new HashSet<String>();
    protected final Set<String> denyObjects              = new HashSet<String>();

    protected final Set<String> readOnlyTables             = new HashSet<String>();

    private String              dir;

    private boolean             inited;

    private String              tenantTablePattern;
    private String              tenantColumn;

    private boolean             wrapAllow                  = true;
    private boolean             metadataAllow              = true;

    public WallConfig(){

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

    public boolean isTruncateAllow() {
        return truncateAllow;
    }

    public void setTruncateAllow(boolean truncateAllow) {
        this.truncateAllow = truncateAllow;
    }

    public boolean isSelelctAllow() {
        return selelctAllow;
    }

    public void setSelelctAllow(boolean selelctAllow) {
        this.selelctAllow = selelctAllow;
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

    public boolean isSelectUnionCheck() {
        return selectUnionCheck;
    }

    public void setSelectUnionCheck(boolean selectUnionCheck) {
        this.selectUnionCheck = selectUnionCheck;
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

    public boolean isDeleteAllow() {
        return deleteAllow;
    }

    public void setDeleteAllow(boolean deleteAllow) {
        this.deleteAllow = deleteAllow;
    }

    public boolean isDeleteWhereAlwayTrueCheck() {
        return deleteWhereAlwayTrueCheck;
    }

    public void setDeleteWhereAlwayTrueCheck(boolean deleteWhereAlwayTrueCheck) {
        this.deleteWhereAlwayTrueCheck = deleteWhereAlwayTrueCheck;
    }

    public boolean isUpdateAllow() {
        return updateAllow;
    }

    public void setUpdateAllow(boolean updateAllow) {
        this.updateAllow = updateAllow;
    }

    public boolean isUpdateWhereAlayTrueCheck() {
        return updateWhereAlayTrueCheck;
    }

    public void setUpdateWhereAlayTrueCheck(boolean updateWhereAlayTrueCheck) {
        this.updateWhereAlayTrueCheck = updateWhereAlayTrueCheck;
    }

    public boolean isInsertAllow() {
        return insertAllow;
    }

    public void setInsertAllow(boolean insertAllow) {
        this.insertAllow = insertAllow;
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

}
