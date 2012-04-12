package com.alibaba.druid.filter.stat;

public class MergeStatFilter extends StatFilter {
	public MergeStatFilter() {
		super.setMergeSql(true);
	}
}
