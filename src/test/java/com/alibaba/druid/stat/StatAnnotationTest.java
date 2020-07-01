package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.druid.stat.spring.UserService;
import com.alibaba.druid.support.spring.stat.SpringStatManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/com/alibaba/druid/stat/spring-config-stat-annotation.xml" })
public class StatAnnotationTest extends TestCase {

	@Resource
	private UserService userService;
	
	@Test
	public void test_0() throws InterruptedException{
		
		userService.save();
		
		List<Map<String, Object>> result = SpringStatManager.getInstance().getMethodStatData();
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		
		Map<String, Object> statItem = result.get(0);
		
		Assert.assertEquals("com.alibaba.druid.stat.spring.UserService", statItem.get("Class"));
		Assert.assertEquals("save()", statItem.get("Method"));
		Assert.assertEquals(1L, statItem.get("ExecuteCount"));
	}
	
}
