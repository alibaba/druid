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
package com.alibaba.druid.support.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Jdk14LoggingImpl implements Log {

    private Logger log;

    private int    errorCount;
    private int    warnCount;
    private int    infoCount;

    public Jdk14LoggingImpl(Class<?> clazz){
        log = Logger.getLogger(clazz.toString());
    }

    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }

    public void error(String s, Throwable e) {
        log.log(Level.SEVERE, s, e);
        errorCount++;
    }

    public void error(String s) {
        log.log(Level.SEVERE, s);
        errorCount++;
    }

    public void debug(String s) {
        log.log(Level.FINE, s);
    }

    public void debug(String s, Throwable e) {
        log.log(Level.FINE, s, e);
    }

    public void warn(String s) {
        log.log(Level.WARNING, s);
        warnCount++;
    }

    @Override
    public void warn(String s, Throwable e) {
        log.log(Level.WARNING, s, e);
        warnCount++;
    }

    @Override
    public int getWarnCount() {
        return warnCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public void resetStat() {
        errorCount = 0;
        warnCount = 0;
        infoCount = 0;
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
        infoCount++;
    }

    @Override
    public int getInfoCount() {
        return infoCount;
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }
}
