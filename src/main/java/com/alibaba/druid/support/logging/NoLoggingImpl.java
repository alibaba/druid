/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.logging;

public class NoLoggingImpl implements Log {

    private int    infoCount;
    private int    errorCount;
    private int    warnCount;
    private int    debugCount;
    private String loggerName;
    
    private boolean debugEnable = false;
    private boolean infoEnable = true;
    private boolean warnEnable = true;
    private boolean errorEnable = true;

    public NoLoggingImpl(String loggerName){
        this.loggerName = loggerName;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public boolean isDebugEnabled() {
        return debugEnable;
    }

    public void error(String s, Throwable e) {
        if (!errorEnable) {
            return;
        }
        
        error(s);

        if (e != null) {
            e.printStackTrace();
        }
    }

    public void error(String s) {
        errorCount++;
        if (s != null) {
            System.err.println(loggerName + " : " + s);
        }
    }

    public void debug(String s) {
        debugCount++;
    }

    public void debug(String s, Throwable e) {
        debugCount++;
    }

    public void warn(String s) {
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        warnCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
        debugCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return infoEnable;
    }

    @Override
    public void info(String s) {
        infoCount++;
    }

    @Override
    public boolean isWarnEnabled() {
        return warnEnable;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public boolean isErrorEnabled() {
        return errorEnable;
    }
    
    public void setErrorEnabled(boolean value) {
        this.errorEnable = value;
    }
}
