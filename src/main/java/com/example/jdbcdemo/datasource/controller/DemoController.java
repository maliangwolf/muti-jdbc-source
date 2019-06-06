package com.example.jdbcdemo.datasource.controller;

import com.example.jdbcdemo.dao.DemoDao;
import com.example.jdbcdemo.datasource.controller.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @Autowired
    private DemoDao mapper;

    @RequestMapping("/demo")
    public String helloworld(){
        Users users=mapper.getUsers(1);
        System.out.println(users);
        return "success";
    }
}
