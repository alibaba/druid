package com.alibaba.druid.util;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class SortUtils {

    public static void sortMapList(final String orderByKey, List<Map<String, Object>> targetList, final boolean isDesc) {
        if (orderByKey == null || orderByKey.length() == 0) return;

        Collections.sort(targetList, new Comparator<Map<String, Object>>() {

            private int compare(Number o1, Number o2) {
                return (int) (o1.doubleValue() - o2.doubleValue());
            }

            private int compare(String o1, String o2) {
                return Collator.getInstance().compare(o1, o2);
            }

            private int compare(Date o1, Date o2) {
                return (int) (o1.getTime() - o2.getTime());
            }

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int result = compare_0(o1, o2);

                if (isDesc) result = -result;

                return result;
            }

            public int compare_0(Map<String, Object> o1, Map<String, Object> o2) {
                Object v1 = o1.get(orderByKey);
                Object v2 = o2.get(orderByKey);

                if (v1 == null && v2 == null) return 0;
                if (v1 == null) return -1;
                if (v2 == null) return 1;

                if (v1 instanceof Number) return compare((Number) v1, (Number) v2);

                if (v1 instanceof String) return compare((String) v1, (String) v2);

                if (v1 instanceof Date) return compare((Date) v1, (Date) v2);

                return 0;
            }
        });
    }
}
