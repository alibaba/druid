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
package com.alibaba.druid.support.jconsole;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.jconsole.model.RowHeaderTable;
/**
 * 请求地址 /sql.json
 * 
 * 返回数据格式：
 * <pre>
 * {
    "ResultCode": 1,
    "Content": [
        {
            "BatchSizeMax": 0,
            "BatchSizeTotal": 0,
            "ConcurrentMax": 1,
            "DbType": "h2",
            "EffectedRowCount": 18,
            "EffectedRowCountHistogram": [
                0,6,0,0,0,0
            ],
            "EffectedRowCountMax": 4,
            "ErrorCount": 0,
            "ExecuteAndResultHoldTimeHistogram": [
                5,0,1,0,0,0,0,0
            ],
            "ExecuteAndResultSetHoldTime": 0,
            "ExecuteCount": 6,
            "FetchRowCount": 0,
            "FetchRowCountHistogram": [
                6,0,0,0,0,0
            ],
            "FetchRowCountMax": 0,
            "Histogram": [
                5,0,1,0,0,0,0,0
            ],
            "ID": 9,
            "InTransactionCount": 6,
            "LastSlowParameters": "[4]",
            "LastTime": "2012-08-22 02:57:56",
            "MaxTimespan": 18,
            "MaxTimespanOccurTime": "2012-08-22 12:38:56",
            "ResultSetHoldTime": 0,
            "RunningCount": 0,
            "SQL": "delete from acct_group_permission where group_id=?",
            "TotalTime": 21
        },
     ]
  }
  </pre>
 * */
public class DruidSQLPanel extends DruidPanel {

    private static final long serialVersionUID = 1L;
    private static final String REQUEST_URL = "/sql.json";
    private static final ArrayList<String> SHOW_LIST = new ArrayList<String>() {

		private static final long serialVersionUID = 1L;

		{
    		add("SQL");
    		add("ExecuteCount");
    		add("TotalTime");
    		add("InTransactionCount");
    		add("ErrorCount");
    		add("EffectedRowCount"); 
    		add("FetchRowCount");
    		add("RunningCount");
    		add("ConcurrentMax");
    		add("Histogram");
    		add("EffectedRowCountHistogram");
    		add("ExecuteAndResultHoldTimeHistogram");
    		add("FetchRowCountHistogram");
    	}
    };  

	public DruidSQLPanel() {
		super();
		this.url = REQUEST_URL;
	}

	@Override
	protected void tableDataProcess(
			ArrayList<LinkedHashMap<String, Object>> data) {
		tableModel = new DruidTableModel(data,SHOW_LIST);
		table.setModel(tableModel);
		
		RowHeaderTable header = new RowHeaderTable(table,20);
		scrollPane.setRowHeaderView(header);
	}   
}
