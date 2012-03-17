package com.alibaba.druid.filter.wall;

import static com.alibaba.druid.filter.wall.spi.WallVisitorUtils.loadResource;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.druid.filter.wall.spi.WallVisitorUtils;

public class WallConfig {

    private boolean             selelctAllow               = true;
    private boolean             selectIntoAllow            = true;
    private boolean             selectWhereAlwayTrueCheck  = true;
    private boolean             selectHavingAlwayTrueCheck = true;

    private boolean             deleteAllow                = true;
    private boolean             deleteWhereAlwayTrueCheck  = true;

    private boolean             updateAllow                = true;
    private boolean             updateWhereAlayTrueCheck   = true;

    private boolean             insertAllow                = true;
    private boolean             mergeAllow                 = true;

    private boolean             shcemaCheck                = true;
    private boolean             tableCheck                 = true;
    private boolean             functionCheck              = true;

    protected final Set<String> permitFunctions            = new HashSet<String>();
    protected final Set<String> permitTables               = new HashSet<String>();
    protected final Set<String> permitSchemas              = new HashSet<String>();
    protected final Set<String> permitNames                = new HashSet<String>();
    protected final Set<String> permitObjects              = new HashSet<String>();

    private String              dir;

    private boolean             inited;

    public WallConfig(){

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

    public void init() {
        loadConfig(dir);
    }

    public void loadConfig(String dir) {
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }

        loadResource(getPermitNames(), dir + "/permit-name.txt");
        loadResource(getPermitSchemas(), dir + "/permit-schema.txt");
        loadResource(getPermitFunctions(), dir + "/permit-function.txt");
        loadResource(getPermitTables(), dir + "/permit-table.txt");
        loadResource(getPermitObjects(), dir + "/permit-object.txt");
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

    public boolean isShcemaCheck() {
        return shcemaCheck;
    }

    public void setShcemaCheck(boolean shcemaCheck) {
        this.shcemaCheck = shcemaCheck;
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

    // ///////////////////

    public Set<String> getPermitFunctions() {
        return permitFunctions;
    }

    public Set<String> getPermitTables() {
        return permitTables;
    }

    public Set<String> getPermitSchemas() {
        return permitSchemas;
    }

    public Set<String> getPermitNames() {
        return permitNames;
    }

    public Set<String> getPermitObjects() {
        return permitObjects;
    }

    public boolean isPermitObjects(String name) {
        name = WallVisitorUtils.form(name);
        return permitObjects.contains(name);
    }

    public boolean isPermitSchema(String name) {
        name = WallVisitorUtils.form(name);
        return this.permitSchemas.contains(name);
    }

    public boolean isPermitFunction(String name) {
        name = WallVisitorUtils.form(name);
        return this.permitFunctions.contains(name);
    }
}
