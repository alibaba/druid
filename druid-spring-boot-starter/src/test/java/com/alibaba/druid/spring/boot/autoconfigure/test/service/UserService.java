package com.alibaba.druid.spring.boot.autoconfigure.test.service;

import com.alibaba.druid.spring.boot.autoconfigure.test.model.User;

public interface UserService {
    User findById(Long id);
}
