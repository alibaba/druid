/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.http.util;

import java.util.NoSuchElementException;

/**
 * This class represents an IP address represented by an 32 bits integer value. Dotted-decimal notation divides the
 * 32-bit Internet address into four 8-bit (byte) fields and specifies the value of each field independently as a
 * decimal number with the fields separated by dots :<br/>
 * <br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp;10010001 . 00001010 . 00100010 . 00000011<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;145&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;10&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;34
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3<br/>
 * <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;-> 145.10.34.3<br/>
 * </code> <br/>
 * <br/>
 * IP address are classified into three classes :<br/>
 * <br/>
 * class A:<br/>
 * <br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp;bit#&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;1&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;7&nbsp;8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;31<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +--+-------------------+------------------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |0&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +--+-------------------+------------------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <-- network number -->&nbsp;<------- host number --------><br/>
 * </code> <br/>
 * <br/>
 * class B:<br/>
 * <br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp;bit#&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;2&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;15&nbsp;16&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;31<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +--+-------------------------+------------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |10|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +--+-------------------------+------------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <----- network number ----->&nbsp;<---- host number -----><br/>
 * </code> <br/>
 * <br/>
 * class C:<br/>
 * <br/>
 * <code>
 * &nbsp;&nbsp;&nbsp;&nbsp;bit#&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;3&nbsp;
 * &nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;23&nbsp;24
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;31<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +---+-----------------------------+-------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |110|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * +---+-----------------------------+-------------------+<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <------- network  number -------->&nbsp;<-- host number --><br/>
 * </code> <br/>
 * <br/>
 * 
 * @author Marcel Dullaart
 * @version 1.0
 */
public class IPAddress implements Cloneable {

    /** IP address */
    protected int ipAddress = 0;

    // -------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param ip String representation of the IP address. The format of the ip's string representation must follow the
     * decimal-dotted notation xxx.xxx.xxx.xxx.
     * @exception InvalidIPAddressException Throws this exception when the specified string doesn't represent a valid IP
     * address.
     */
    public IPAddress(String ipAddressStr){
        ipAddress = parseIPAddress(ipAddressStr);
    }

    // -------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param ip Binary representation of the IP address.
     */
    public IPAddress(int address){
        ipAddress = address;
    }

    // -------------------------------------------------------------------------
    /**
     * Return the integer representation of the IP address.
     * 
     * @return The IP address.
     */
    public final int getIPAddress() {
        return ipAddress;
    }

    // -------------------------------------------------------------------------
    /**
     * Return the string representation of the IP Address following the common decimal-dotted notation xxx.xxx.xxx.xxx.
     * 
     * @return Return the string representation of the IP address.
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        int temp;

        temp = ipAddress & 0x000000FF;
        result.append(temp);
        result.append(".");

        temp = (ipAddress >> 8) & 0x000000FF;
        result.append(temp);
        result.append(".");

        temp = (ipAddress >> 16) & 0x000000FF;
        result.append(temp);
        result.append(".");

        temp = (ipAddress >> 24) & 0x000000FF;
        result.append(temp);

        return result.toString();
    }

    // -------------------------------------------------------------------------
    /**
     * Check if the IP address is belongs to a Class A IP address.
     * 
     * @return Return <code>true</code> if the encapsulated IP address belongs to a class A IP address, otherwise
     * returne <code>false</code>.
     */
    public final boolean isClassA() {
        return (ipAddress & 0x00000001) == 0;
    }

    // -------------------------------------------------------------------------
    /**
     * Check if the IP address is belongs to a Class B IP address.
     * 
     * @return Return <code>true</code> if the encapsulated IP address belongs to a class B IP address, otherwise
     * returne <code>false</code>.
     */
    public final boolean isClassB() {
        return (ipAddress & 0x00000003) == 1;
    }

    // -------------------------------------------------------------------------
    /**
     * Check if the IP address is belongs to a Class C IP address.
     * 
     * @return Return <code>true</code> if the encapsulated IP address belongs to a class C IP address, otherwise
     * returne <code>false</code>.
     */
    public final boolean isClassC() {
        return (ipAddress & 0x00000007) == 3;
    }

    // -------------------------------------------------------------------------
    /**
     * Convert a decimal-dotted notation representation of an IP address into an 32 bits interger value.
     * 
     * @param ipAddressStr Decimal-dotted notation (xxx.xxx.xxx.xxx) of the IP address.
     * @return Return the 32 bits integer representation of the IP address.
     * @exception InvalidIPAddressException Throws this exception if the specified IP address is not compliant to the
     * decimal-dotted notation xxx.xxx.xxx.xxx.
     */
    final int parseIPAddress(String ipAddressStr) {
        int result = 0;

        if (ipAddressStr == null) {
            throw new IllegalArgumentException();
        }

        try {
            String tmp = ipAddressStr;

            // get the 3 first numbers
            int offset = 0;
            for (int i = 0; i < 3; i++) {

                // get the position of the first dot
                int index = tmp.indexOf('.');

                // if there is not a dot then the ip string representation is
                // not compliant to the decimal-dotted notation.
                if (index != -1) {

                    // get the number before the dot and convert it into
                    // an integer.
                    String numberStr = tmp.substring(0, index);
                    int number = Integer.parseInt(numberStr);
                    if ((number < 0) || (number > 255)) {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }

                    result += number << offset;
                    offset += 8;
                    tmp = tmp.substring(index + 1);
                } else {
                    throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                }
            }

            // the remaining part of the string should be the last number.
            if (tmp.length() > 0) {
                int number = Integer.parseInt(tmp);
                if ((number < 0) || (number > 255)) {
                    throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                }

                result += number << offset;
                ipAddress = result;
            } else {
                throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
            }
        } catch (NoSuchElementException ex) {
            throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]", ex);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]", ex);
        }

        return result;
    }

    public int hashCode() {
        return this.ipAddress;
    }

    public boolean equals(Object another) {
        return another instanceof IPAddress && ipAddress == ((IPAddress) another).ipAddress;
    }
}
