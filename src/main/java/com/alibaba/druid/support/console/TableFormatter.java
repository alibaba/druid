package com.alibaba.druid.support.console;

import java.util.ArrayList;
import java.util.List;

public class TableFormatter {

	public static String format(List<String[]> rows) {
		String[] titlerow  = rows.get(0);
		int[] maxLens = new int[titlerow.length];
		for (int i=0; i<rows.size(); i++) {
			String[] row = rows.get(i);
			for (int j=0; j<row.length; j++) {
				int len = displayLen(row[j]);
				if ( len > maxLens[j]) {
					maxLens[j] = len;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(makeSplitLine(maxLens));
		for (int i=0; i<rows.size(); i++) {
			String[] row = rows.get(i);
			sb.append("|");
			for (int j=0; j<row.length; j++) {
				sb.append( padStr( row[j], maxLens[j]));
				sb.append(" |");
			}
			sb.append("\n");
			//title row
			if (i==0) {
				sb.append(makeSplitLine(maxLens));
			}
		}

		sb.append(makeSplitLine(maxLens));
		return sb.toString();
	}

	public static String makeSplitLine(int[] maxLens) {
		StringBuilder sb = new StringBuilder("+");
		for (int len : maxLens) {
			for (int i=0; i<len; i++) {
				sb.append("-");
			}
			sb.append("-+");
		}
		sb.append("\n");
		return sb.toString();
	}

	public static int displayLen(String value) {
		return value.length();
	}

	public static String padStr(String old, int length) {
		if (old == null ) return "";
		int vlen = displayLen(old);
		if (vlen > length) return old;
		StringBuffer sb = new StringBuffer(old);
		for (int i=0; i< length -vlen ; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		List<String[]> v = new ArrayList<String[]>();
		v.add(new String[]{"name","age","addr"});
		v.add(new String[]{"shrek", "200", "hangzhou zhejiang "});
		v.add(new String[]{"what as", "11", "asdfa"});
		String formattedStr = format(v);
		System.out.println(formattedStr);
	}

}
