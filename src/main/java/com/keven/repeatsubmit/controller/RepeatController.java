package com.keven.repeatsubmit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.keven.repeatsubmit.annotation.NoRepeatSubmit;

import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/repeat")
public class RepeatController {

    @RequestMapping("/submit")
    @NoRepeatSubmit(message = "请勿重复提交",interval = 2,timeUnit = TimeUnit.SECONDS)
    public String submit() {
        return "success";
    }
}
