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
package com.alibaba.druid.sql.parser;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public interface LayoutCharacters {

    /**
     * Tabulator column increment.
     */
    final static int  TabInc = 8;

    /**
     * Tabulator character.
     */
    final static byte TAB    = 0x8;

    /**
     * Line feed character.
     */
    final static byte LF     = 0xA;

    /**
     * Form feed character.
     */
    final static byte FF     = 0xC;

    /**
     * Carriage return character.
     */
    final static byte CR     = 0xD;

    /**
     * End of input character. Used as a sentinel to denote the character one beyond the last defined character in a
     * source file.
     */
    final static byte EOI    = 0x1A;
}
