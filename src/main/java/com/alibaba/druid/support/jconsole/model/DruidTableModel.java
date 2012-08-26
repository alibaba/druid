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
package com.alibaba.druid.support.jconsole.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DruidTableModel implements TableModel {
	private ArrayList<LinkedHashMap<String,Object>> list;	
	private ArrayList<String> showKeys;

	public DruidTableModel(ArrayList<LinkedHashMap<String, Object>> list) {
		super();
		this.list = list;
		showKeys = null;
	}	

	public DruidTableModel(ArrayList<LinkedHashMap<String, Object>> list,
			ArrayList<String> showKeys) {
		super();
		this.list = list;
		this.showKeys = showKeys;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		int colCount = 0;
		if (showKeys != null) {
			colCount = showKeys.size();
		} else if (list != null) {
			int listLen = list.size();
			if (listLen > 0) {
				colCount = list.get(0).size();
			}
		}
		return colCount;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (showKeys != null && showKeys.size() > 0) {
			return showKeys.get(columnIndex);
		}
		if (list != null && list.size() > 0) {
			LinkedHashMap<String,Object> firstElement = list.get(0);
			Object[] keys = firstElement.keySet().toArray();
			return keys[columnIndex].toString();
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (list != null && rowIndex < list.size()) {
			LinkedHashMap<String,Object> dataNow = list.get(rowIndex);
			if (showKeys != null) {
				int titleLen = showKeys.size();
				if (titleLen > 0 && columnIndex < titleLen) {
					return dataNow.get(showKeys.get(columnIndex));
				}
			} else {
				Object[] values = dataNow.values().toArray();
				return values[columnIndex];
			}
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
	}

}
