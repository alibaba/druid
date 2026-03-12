package com.alibaba.druid.test.wall;

import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

public class ResourceTest {
    private String[] items;

    @BeforeEach
    protected void setUp() throws Exception {
        File file = new File("D:\\scan_error.txt");
        FileInputStream is = new FileInputStream(file);
        String all = Utils.read(is);
        is.close();

        items = all.split("\\|\\r\\n\\|");
    }

    @Test
    public void test_xx() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        for (int i = 0; i < items.length; ++i) {
            String sql = items[i];

            WallCheckResult result = provider.check(sql);
            if (result.getViolations().size() > 0) {
                Violation violation = result.getViolations().get(0);
                System.err.println("error (" + i + ") : " + violation.getMessage());
                System.out.println(sql);
                System.out.println();
//                break;
            }
        }

        System.out.println("violaionCount : " + provider.getViolationCount());
    }
}
