package com.alibaba.druid.stat.spring;

import com.alibaba.druid.support.spring.stat.annotation.Stat;

public interface UserService {

	@Stat
    public void save();
}
