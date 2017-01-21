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


/**
 * @author Marcel Dullaart
 * @version 1.0
 */
public class IPAddress implements Cloneable {

    /** IP address */
    protected int ipAddress = 0;

    public IPAddress(String ipAddressStr){
        ipAddress = parseIPAddress(ipAddressStr);
    }

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
            int len = ipAddressStr.length();
            // temp value
            int temp = 0;
            // get the 3 first numbers
            int offset = 0;
            for (int i = 0; i < len; i++) {
                char c = ipAddressStr.charAt(i);
                switch (c) {
                case '.':
                    if (temp < 0 || temp > 255) {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }
                    result += temp << offset;
                    temp = 0;
                    offset += 8;
                    break;
                default:
                    // char to num
                    int num = c - '0';
                    if (num > -1 && num < 10) {
                        temp = (temp * 10) + num;
                    } else {
                        throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
                    }
                    break;
                }
            }

            // IP address must contain three dot
            if (offset == 24 && temp > 0 && temp < 256) {
                result += temp << offset;
                ipAddress = result;
            } else {
                throw new IllegalArgumentException("Invalid IP Address [" + ipAddressStr + "]");
            }
        } catch (Exception ex) {
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
