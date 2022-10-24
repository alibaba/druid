package com.alibaba.druid.util;

import java.util.HashSet;
import java.util.Set;

public class HiveUtils {
    private static Set<String> builtinDataTypes;

    public static boolean isBuiltinDataType(String dataType) {
        if (dataType == null) {
            return false;
        }

        String table_lower = dataType.toLowerCase();

        Set<String> dataTypes = builtinDataTypes;

        if (dataTypes == null) {
            dataTypes = new HashSet<String>();
            loadDataTypes(dataTypes);
            builtinDataTypes = dataTypes;
        }

        return dataTypes.contains(table_lower);
    }

    public static void loadDataTypes(Set<String> dataTypes) {
        Utils.loadFromFile("META-INF/druid/parser/hive/builtin_datatypes", dataTypes);
    }
}
