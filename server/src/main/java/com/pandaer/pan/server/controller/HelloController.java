package com.pandaer.pan.server.controller;


import com.pandaer.pan.core.response.Resp;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Api("测试接口 ")
@RestController
@Validated
public class HelloController {

    @GetMapping("/hello")
    public Resp<String> hello(@NotEmpty(message = "name不能为空") String name) {
        return Resp.successAndData("Hello, MPan! haha");
    }
}
