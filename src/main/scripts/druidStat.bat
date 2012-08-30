@echo off

rem Copyright 1999-2011 Alibaba Group Holding Ltd.
rem 
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem 
rem      http://www.apache.org/licenses/LICENSE-2.0
rem 
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.


set _RUNJAVA="%JAVA_HOME%\bin\java.exe"
set _TOOLSJAR="%JAVA_HOME%\lib\tools.jar"

%_RUNJAVA% -classpath "./druid-0.2.6.jar;%_TOOLSJAR%" com.alibaba.druid.support.console.DruidStat %*
