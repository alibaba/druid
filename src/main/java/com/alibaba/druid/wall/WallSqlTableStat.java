package com.alibaba.druid.wall;

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
public class WallSqlTableStat {

    private int selectCount;
    private int selectIntoCount;
    private int insertCount;
    private int updateCount;
    private int deleteCount;
    private int truncateCount;
    private int createCount;
    private int alterCount;
    private int dropCount;

    public int getSelectCount() {
        return selectCount;
    }

    public void incrementSelectCount() {
        this.selectCount++;
    }

    public int getSelectIntoCount() {
        return selectIntoCount;
    }

    public void incrementSelectIntoCount() {
        this.selectIntoCount++;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void incrementInsertCount() {
        this.insertCount++;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void incrementUpdateCount() {
        this.updateCount++;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void incrementDeleteCount() {
        this.deleteCount++;
    }

    public int getTruncateCount() {
        return truncateCount;
    }

    public void incrementTruncateCount() {
        this.truncateCount++;
    }

    public int getCreateCount() {
        return createCount;
    }

    public void incrementCreateCount() {
        this.createCount++;
    }

    public int getAlterCount() {
        return alterCount;
    }

    public void incrementAlterCount() {
        this.alterCount++;
    }

    public int getDropCount() {
        return dropCount;
    }

    public void incrementDropCount() {
        this.dropCount++;
    }

}
