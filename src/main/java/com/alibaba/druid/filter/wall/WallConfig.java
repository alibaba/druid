package com.alibaba.druid.filter.wall;

public class WallConfig {

    private boolean selelctAllow               = true;
    private boolean selectWhereAlwayTrueCheck  = true;
    private boolean selectHavingAlwayTrueCheck = true;

    private boolean deleteAllow                = true;
    private boolean deleteWhereAlwayTrueCheck  = true;

    private boolean updateAllow                = true;
    private boolean updateWhereAlayTrueCheck   = true;

    private boolean insertAllow                = true;
    private boolean mergeAllow                 = true;

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

}
