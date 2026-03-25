package com.alibaba.druid.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fix MapComparator sorting failures due to integer overflow in Long/Date comparisons.
 * <p>
 * The previous implementation used {@code (int)(longA - longB)} which overflows for large
 * values, violating the Comparator contract and causing incorrect sort order.
 * This manifests as the SQL monitoring page sort not working (descending has no effect).
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6624">Issue #6624</a>
 */
public class MapComparatorOverflowTest {
    @Test
    public void test_long_sort_ascending_large_values() {
        List<Map<String, Object>> list = new ArrayList<>();
        long[] values = {Long.MAX_VALUE, 1L, Long.MAX_VALUE / 2, 100L, Long.MAX_VALUE - 1};
        for (long v : values) {
            Map<String, Object> m = new HashMap<>();
            m.put("k", v);
            list.add(m);
        }

        Collections.sort(list, new MapComparator<>("k", false));

        long prev = Long.MIN_VALUE;
        for (Map<String, Object> m : list) {
            long val = (Long) m.get("k");
            assertTrue(val >= prev, "ASC sort failed: " + val + " < " + prev);
            prev = val;
        }
    }

    @Test
    public void test_long_sort_descending_large_values() {
        List<Map<String, Object>> list = new ArrayList<>();
        long[] values = {Long.MAX_VALUE, 1L, Long.MAX_VALUE / 2, 100L, Long.MAX_VALUE - 1};
        for (long v : values) {
            Map<String, Object> m = new HashMap<>();
            m.put("k", v);
            list.add(m);
        }

        Collections.sort(list, new MapComparator<>("k", true));

        long prev = Long.MAX_VALUE;
        for (Map<String, Object> m : list) {
            long val = (Long) m.get("k");
            assertTrue(val <= prev, "DESC sort failed: " + val + " > " + prev);
            prev = val;
        }
    }

    @Test
    public void test_long_sort_desc_differs_from_asc() {
        // Reproduce the exact issue #6624: desc sort should produce different order than asc
        List<Map<String, Object>> data = new ArrayList<>();
        for (long v : new long[]{500L, 100L, 300L, 200L, 400L}) {
            Map<String, Object> m = new HashMap<>();
            m.put("MaxTimespan", v);
            data.add(m);
        }

        List<Map<String, Object>> ascList = new ArrayList<>(data);
        Collections.sort(ascList, new MapComparator<>("MaxTimespan", false));

        List<Map<String, Object>> descList = new ArrayList<>(data);
        Collections.sort(descList, new MapComparator<>("MaxTimespan", true));

        // ASC first should be smallest
        assertEquals(100L, ascList.get(0).get("MaxTimespan"));
        // DESC first should be largest
        assertEquals(500L, descList.get(0).get("MaxTimespan"));

        // ASC and DESC should be reverse of each other
        for (int i = 0; i < data.size(); i++) {
            assertEquals(ascList.get(i).get("MaxTimespan"),
                    descList.get(data.size() - 1 - i).get("MaxTimespan"));
        }
    }

    @Test
    public void test_long_overflow_values() {
        // Values that previously caused int overflow: (int)(3000000000 - 1) = -1294967297
        MapComparator<String, Object> asc = new MapComparator<>("k", false);

        Map<String, Object> big = new HashMap<>();
        big.put("k", 3000000000L);
        Map<String, Object> small = new HashMap<>();
        small.put("k", 1L);

        assertTrue(asc.compare(big, small) > 0, "3B should be > 1");
        assertTrue(asc.compare(small, big) < 0, "1 should be < 3B");
    }

    @Test
    public void test_date_sort_large_time_difference() {
        MapComparator<String, Date> asc = new MapComparator<>("k", false);
        MapComparator<String, Date> desc = new MapComparator<>("k", true);

        Map<String, Date> recent = new HashMap<>();
        recent.put("k", new Date(System.currentTimeMillis()));
        Map<String, Date> old = new HashMap<>();
        old.put("k", new Date(0L)); // epoch

        assertTrue(asc.compare(recent, old) > 0, "ASC: recent should be > epoch");
        assertTrue(desc.compare(recent, old) < 0, "DESC: recent should be < epoch");
    }

    @Test
    public void test_number_small_difference_precision() {
        // Previously: (int)(100.5 - 100.0) = (int)0.5 = 0 (treated as equal)
        MapComparator<String, Object> asc = new MapComparator<>("k", false);

        Map<String, Object> a = new HashMap<>();
        a.put("k", 100.5);
        Map<String, Object> b = new HashMap<>();
        b.put("k", 100.0);

        assertTrue(asc.compare(a, b) > 0, "100.5 should be > 100.0");
    }

    @Test
    public void test_random_long_sort_consistency() {
        // Ensure sort doesn't throw IllegalArgumentException from TimSort
        // due to Comparator contract violation (transitivity)
        Random random = new Random(42);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("k", random.nextLong() & Long.MAX_VALUE);
            list.add(m);
        }

        assertDoesNotThrow(() ->
                Collections.sort(list, new MapComparator<>("k", true)));

        // Verify the result is actually sorted descending
        long prev = Long.MAX_VALUE;
        for (Map<String, Object> m : list) {
            long val = (Long) m.get("k");
            assertTrue(val <= prev, "Random DESC sort failed at " + val);
            prev = val;
        }
    }
}
