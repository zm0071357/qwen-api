package com.qwen.api.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.qwen.api.service.QwenPicService;
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

    @Resource
    private QwenPicService qwenPicService;

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

    @PostMapping("/call_with_stream")
    public String callWithStream(@RequestParam String question) throws NoApiKeyException, InputRequiredException{
        return qwenService.callWithStream(question);
    }

    @PostMapping("/call_with_stream/multiple")
    public String callWithStreamAndMultiple(@RequestParam String question) throws NoApiKeyException, InputRequiredException{
        return qwenService.callWithStreamAndMultiple(question);
    }

    @PostMapping("/call_with_pic")
    public String callWithPic(@RequestParam String pic,
                              @RequestParam(defaultValue = "这是什么", required = false) String question) throws NoApiKeyException, InputRequiredException, UploadFileException {
        return qwenPicService.callWithPic(pic, question);
    }

}
