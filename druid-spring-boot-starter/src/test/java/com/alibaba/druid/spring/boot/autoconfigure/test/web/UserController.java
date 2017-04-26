package com.alibaba.druid.spring.boot.autoconfigure.test.web;

import com.alibaba.druid.spring.boot.autoconfigure.test.service.UserService;
import com.alibaba.druid.spring.boot.autoconfigure.test.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController  {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/{id}")
    public User list(@PathVariable Long id) {
        return userService.findById(id);
    }
}
