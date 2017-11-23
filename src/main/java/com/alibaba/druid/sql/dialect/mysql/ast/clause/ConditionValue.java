/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.clause;


/**
 * 
 * @author zhujun [455910092@qq.com]
 * 2016-04-16
 */
public class ConditionValue {
	// type for condition   SQLSTATE | SELF | SYSTEM | mysql_error_code
	private ConditionType type;
	
	//value for condition  condition_name | sqlstate | SQLWARNING | NOT FOUND | SQLEXCEPTION | mysql_error_code
	private String value;
	public ConditionType getType() {
		return type;
	}
	public void setType(ConditionType type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public enum ConditionType{
		SQLSTATE,
		SELF,
		SYSTEM,
		MYSQL_ERROR_CODE
	}
	
}
