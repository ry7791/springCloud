package com.example.msadiscoveryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/accounts")
public class AccountsController {
    @Autowired
    Environment env;

    @GetMapping("/status/check")
    public String status() {
        return String.format("Users-WS] Working on port %s, with token = %s",
                env.getProperty("local.server.port"),
                env.getProperty("token.secret"));
    }

}
