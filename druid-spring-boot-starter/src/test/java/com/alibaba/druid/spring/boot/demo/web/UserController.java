package com.alibaba.druid.spring.boot.demo.web;


import com.alibaba.druid.spring.boot.demo.model.User;
import com.alibaba.druid.spring.boot.demo.service.UserService;
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
