/**
 * Project: druid
 * 
 * File Created at 2010-12-2
 * $Id$
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.druid.filter.encoding;

import java.io.UnsupportedEncodingException;

/**
 * 字符编码转换器
 * 
 * @author xianmao.hexm 2007-3-5 09:51:33
 */
public class CharsetConvert {

	private String clientEncoding = null;

	private String serverEncoding = null;

	private boolean enable = false;

	public CharsetConvert(String clientEncoding, String serverEncoding) {
		this.clientEncoding = clientEncoding;
		this.serverEncoding = serverEncoding;
		if (clientEncoding != null && serverEncoding != null && !clientEncoding.equalsIgnoreCase(serverEncoding)) {
			enable = true;
		}
	}

	/**
	 * 字符串编码
	 * 
	 * @param s
	 *            String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public String encode(String s) throws UnsupportedEncodingException {
		if (enable && !isEmpty(s)) {
			s = new String(s.getBytes(clientEncoding), serverEncoding);
		}
		return s;
	}

	/**
	 * 字符串解码
	 * 
	 * @param s
	 *            String
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public String decode(String s) throws UnsupportedEncodingException {
		if (enable && !isEmpty(s)) {
			s = new String(s.getBytes(serverEncoding), clientEncoding);
		}
		return s;
	}

	/**
	 * 判断空字符串
	 * 
	 * @param s
	 *            String
	 * @return boolean
	 */
	public boolean isEmpty(String s) {
		if (s == null || "".equals(s)) {
			return true;
		} else {
			return false;
		}
	}

}
