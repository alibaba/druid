package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import static org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.druid.stat.spring.UserService;
import com.alibaba.druid.support.spring.stat.SpringStatManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/alibaba/druid/stat/spring-config-stat-annotation.xml"})
public class StatAnnotationTest extends TestCase {
    @Autowired
    private UserService userService;

    @Test
    public void test_0() throws InterruptedException {
        userService.save();

        List<Map<String, Object>> result = SpringStatManager.getInstance().getMethodStatData();
        assertNotNull(result);
        assertEquals(1, result.size());

        Map<String, Object> statItem = result.get(0);

        assertEquals("com.alibaba.druid.stat.spring.UserService", statItem.get("Class"));
        assertEquals("save()", statItem.get("Method"));
        assertEquals(1L, statItem.get("ExecuteCount"));
    }

}
