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
package com.alibaba.druid.bvt.sql.mysql;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.sql.SQLUtils;

public class MySqlUpdateStatementLimitTest extends TestCase{
	public void test_limit(){
		String sql = "update t set name = 'x' where id < 100 limit 10";
		String rs = SQLUtils.formatMySql(sql);
		Assert.assertEquals("UPDATE t"
		        + "\nSET name = 'x'"
		        + "\nWHERE id < 100"
		        + "\nLIMIT 10", rs);
	}
}
