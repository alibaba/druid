/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularType;

/**
 * This abstract class provides the implementation of the CompositeData interface. A CompositeData object will be lazily
 * created only when the CompositeData interface is used. Classes that extends this abstract class will implement the
 * getCompositeData() method. The object returned by the getCompositeData() is an instance of CompositeData such that
 * the instance serializes itself as the type CompositeDataSupport.
 */
public abstract class LazyCompositeData implements CompositeData, Serializable {

    private CompositeData compositeData;

    // Implementation of the CompositeData interface
    public boolean containsKey(String key) {
        return compositeData().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return compositeData().containsValue(value);
    }

    public boolean equals(Object obj) {
        return compositeData().equals(obj);
    }

    public Object get(String key) {
        return compositeData().get(key);
    }

    public Object[] getAll(String[] keys) {
        return compositeData().getAll(keys);
    }

    public CompositeType getCompositeType() {
        return compositeData().getCompositeType();
    }

    public int hashCode() {
        return compositeData().hashCode();
    }

    public String toString() {
        /** FIXME: What should this be?? */
        return compositeData().toString();
    }

    public Collection<?> values() {
        return compositeData().values();
    }

    /*
     * Lazy creation of a CompositeData object only when the CompositeData interface is used.
     */
    private synchronized CompositeData compositeData() {
        if (compositeData != null) return compositeData;
        compositeData = getCompositeData();
        return compositeData;
    }

    /**
     * Designate to a CompositeData object when writing to an output stream during serialization so that the receiver
     * only requires JMX 1.2 classes but not any implementation specific class.
     */
    protected Object writeReplace() throws java.io.ObjectStreamException {
        return compositeData();
    }

    /**
     * Returns the CompositeData representing this object. The returned CompositeData object must be an instance of
     * javax.management.openmbean.CompositeDataSupport class so that no implementation specific class is required for
     * unmarshalling besides JMX 1.2 classes.
     */
    protected abstract CompositeData getCompositeData();

    // Helper methods
    static String getString(CompositeData cd, String itemName) {
        if (cd == null) throw new IllegalArgumentException("Null CompositeData");

        return (String) cd.get(itemName);
    }

    static boolean getBoolean(CompositeData cd, String itemName) {
        if (cd == null) throw new IllegalArgumentException("Null CompositeData");

        return ((Boolean) cd.get(itemName)).booleanValue();
    }

    static long getLong(CompositeData cd, String itemName) {
        if (cd == null) throw new IllegalArgumentException("Null CompositeData");

        return ((Number) cd.get(itemName)).longValue();
    }

    static int getInt(CompositeData cd, String itemName) {
        if (cd == null) throw new IllegalArgumentException("Null CompositeData");

        return ((Number) cd.get(itemName)).intValue();
    }

    /**
     * Compares two CompositeTypes and returns true if all items in type1 exist in type2 and their item types are the
     * same.
     */
    protected static boolean isTypeMatched(CompositeType type1, CompositeType type2) {
        if (type1 == type2) return true;

        // We can't use CompositeType.isValue() since it returns false
        // if the type name doesn't match.
        Set<?> allItems = type1.keySet();

        // Check all items in the type1 exist in type2
        if (!type2.keySet().containsAll(allItems)) return false;

        for (Iterator<?> iter = allItems.iterator(); iter.hasNext();) {
            String item = (String) iter.next();
            OpenType<?> ot1 = type1.getType(item);
            OpenType<?> ot2 = type2.getType(item);
            if (ot1 instanceof CompositeType) {
                if (!(ot2 instanceof CompositeType)) return false;
                if (!isTypeMatched((CompositeType) ot1, (CompositeType) ot2)) return false;
            } else if (ot1 instanceof TabularType) {
                if (!(ot2 instanceof TabularType)) return false;
                if (!isTypeMatched((TabularType) ot1, (TabularType) ot2)) return false;
            } else if (!ot1.equals(ot2)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isTypeMatched(TabularType type1, TabularType type2) {
        if (type1 == type2) return true;

        List<String> list1 = type1.getIndexNames();
        List<String> list2 = type2.getIndexNames();

        // check if the list of index names are the same
        if (!list1.equals(list2)) return false;

        return isTypeMatched(type1.getRowType(), type2.getRowType());
    }
}
