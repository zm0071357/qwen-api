package com.qwen.api.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.qwen.api.service.QwenCreatePicService;
import com.qwen.api.service.QwenPicService;
import com.qwen.api.service.QwenService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 通义千问测试接口
 */
@RestController
@RequestMapping("/qwen")
public class TestController {

    @Resource
    private QwenService qwenService;

    @Resource
    private QwenPicService qwenPicService;

    @Resource
    private QwenCreatePicService qwenCreatePicService;

    @GetMapping("/test")
    public String hello() {
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

    @PostMapping("/call_with_pic/multiple")
    public String callWithPicMultiple(@RequestParam(required = false) String pic,
                                      @RequestParam(defaultValue = "这是什么", required = false) String question) throws NoApiKeyException, InputRequiredException, UploadFileException {
        return qwenPicService.callWithPicMultiple(pic, question);
    }

    @PostMapping("/call_with_pic/stream")
    public String callWithPicStream(@RequestParam String pic,
                                    @RequestParam(defaultValue = "这是什么", required = false) String question) throws NoApiKeyException, InputRequiredException, UploadFileException {
        return qwenPicService.callWithPicStream(pic, question);
    }

    @PostMapping("/call_with_pic/multiple_stream")
    public String callWithPicMultipleAndStream(@RequestParam(required = false) String pic,
                                               @RequestParam(defaultValue = "这是什么", required = false) String question) throws NoApiKeyException, InputRequiredException, UploadFileException {
        return qwenPicService.callWithPicMultipleAndStream(pic, question);
    }

    @PostMapping("/call_with_pic/extraction")
    public String textExtraction(@RequestParam(required = false) String pic,
                                 @RequestParam(defaultValue = "提取文字") String question) throws NoApiKeyException, UploadFileException {
        return qwenPicService.textExtraction(pic, question);
    }

    @PostMapping("/create_pic")
    public String createPic(@RequestParam String prompt) {
        return qwenCreatePicService.createPic(prompt, "<auto>");
    }

    @PostMapping("/create_pic/with_reference")
    public String createPicWithReference(@RequestParam String pic,
                                         @RequestParam String prompt) throws NoApiKeyException, UploadFileException {
        return qwenCreatePicService.createPicWithReference(pic, prompt, "<auto>");
    }
}
