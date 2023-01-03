package jjun.server.jwttutorial.controller;

import jjun.server.jwttutorial.config.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public BaseResponse<String> hello() {
        return new BaseResponse<>("hello");
    }

}
