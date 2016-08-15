package com.alibaba.druid.wall;

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
public class WallSqlTableStat {

    private int    selectCount;
    private int    selectIntoCount;
    private int    insertCount;
    private int    updateCount;
    private int    deleteCount;
    private int    truncateCount;
    private int    createCount;
    private int    alterCount;
    private int    dropCount;
    private int    replaceCount;
    private int    showCount;

    private String sample;

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public int getReplaceCount() {
        return replaceCount;
    }

    public int incrementReplaceCount() {
        return replaceCount++;
    }

    public void addReplaceCount(int value) {
        this.replaceCount += value;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void incrementSelectCount() {
        this.selectCount++;
    }

    public void addSelectCount(int value) {
        this.selectCount += value;
    }

    public int getSelectIntoCount() {
        return selectIntoCount;
    }

    public void incrementSelectIntoCount() {
        this.selectIntoCount++;
    }

    public void addSelectIntoCount(int value) {
        this.selectIntoCount += value;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void incrementInsertCount() {
        this.insertCount++;
    }

    public void addInsertCount(int value) {
        this.insertCount += value;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void incrementUpdateCount() {
        this.updateCount++;
    }

    public void addUpdateCount(int value) {
        this.deleteCount += value;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void incrementDeleteCount() {
        this.deleteCount++;
    }

    public void addDeleteCount(int value) {
        this.deleteCount += value;
    }

    public int getTruncateCount() {
        return truncateCount;
    }

    public void incrementTruncateCount() {
        this.truncateCount++;
    }

    public void addTruncateCount(int value) {
        this.truncateCount += value;
    }

    public int getCreateCount() {
        return createCount;
    }

    public void incrementCreateCount() {
        this.createCount++;
    }

    public void addCreateCount(int value) {
        this.createCount += value;
    }

    public int getAlterCount() {
        return alterCount;
    }

    public void incrementAlterCount() {
        this.alterCount++;
    }

    public void addAlterCount(int value) {
        this.alterCount += value;
    }

    public int getDropCount() {
        return dropCount;
    }

    public void incrementDropCount() {
        this.dropCount++;
    }

    public void addDropCount(int value) {
        this.dropCount += value;
    }

    public int getShowCount() {
        return showCount;
    }

    public void incrementShowCount() {
        this.showCount++;
    }
}
