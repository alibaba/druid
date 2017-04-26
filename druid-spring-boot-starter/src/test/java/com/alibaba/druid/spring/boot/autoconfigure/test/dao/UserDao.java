package com.alibaba.druid.spring.boot.autoconfigure.test.dao;

import com.alibaba.druid.spring.boot.autoconfigure.test.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Long> {}

