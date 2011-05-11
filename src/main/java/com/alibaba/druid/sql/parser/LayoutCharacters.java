/**
 * Project: fastjson
 * 
 * File Created at 2010-12-2
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
package com.alibaba.druid.sql.parser;

/**
 * @author shaojin.wensj
 */
public interface LayoutCharacters {

    /**
     * Tabulator column increment.
     */
    final static int TabInc = 8;

    /**
     * Tabulator character.
     */
    final static byte TAB = 0x8;

    /**
     * Line feed character.
     */
    final static byte LF = 0xA;

    /**
     * Form feed character.
     */
    final static byte FF = 0xC;

    /**
     * Carriage return character.
     */
    final static byte CR = 0xD;

    /**
     * QS_TODO 为什么不是0x0？<br/>
     * End of input character. Used as a sentinel to denote the character one
     * beyond the last defined character in a source file.
     */
    final static byte EOI = 0x1A;
}
