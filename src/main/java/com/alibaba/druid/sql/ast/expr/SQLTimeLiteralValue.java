/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.ast.expr;

public class SQLTimeLiteralValue {
    private int hours;
    private int minutes;
    private int seconds;
    private int secondsFraction;

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSecondsFraction() {
        return secondsFraction;
    }

    public void setSecondsFraction(int secondsFraction) {
        this.secondsFraction = secondsFraction;
    }

    public void output(StringBuffer buf) {
        buf.append(hours);
        buf.append(':');
        buf.append(minutes);
        buf.append(':');
        buf.append(seconds);
        if (secondsFraction > 0) {
            buf.append('.');
            buf.append(secondsFraction);
        }
    }
}
