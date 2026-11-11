package com.zjjxy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/say")
    public String say() {
        // 返回的是页面文件名，配合视图解析器跳转到 say.jsp 页面
        return "say";
    }
}
