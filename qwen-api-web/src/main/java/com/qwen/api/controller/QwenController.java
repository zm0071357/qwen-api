package com.qwen.api.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.qwen.api.service.QwenService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 通义千问接口
 */
@RestController
@RequestMapping("/qwen")
public class QwenController {

    @Resource
    private QwenService qwenService;

    @GetMapping("/test")
    public String test() {
        return "hello";
    }

    @PostMapping("/call_with_message")
    public String callWithMessage(@RequestParam String question) throws NoApiKeyException, InputRequiredException {
        return qwenService.callWithMessage(question);
    }

    @PostMapping("/call_with_multiple")
    public String callWithMultiple(@RequestParam String question) throws NoApiKeyException, InputRequiredException {
        return qwenService.callWithMultiple(question);
    }

}
