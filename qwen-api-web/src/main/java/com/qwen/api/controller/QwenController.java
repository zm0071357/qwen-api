package com.qwen.api.controller;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.qwen.api.service.QwenCreatePicService;
import com.qwen.api.service.QwenPicService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * 通义千问接口
 */
@RestController
@RequestMapping("/qwen/api")
public class QwenController {
    @Resource
    private QwenPicService qwenPicService;
    @Resource
    private QwenCreatePicService qwenCreatePicService;

    @PostMapping("/talk")
    public String talk(@RequestParam(name = "pic", required = false) String pic,
                       @RequestParam(name = "question") String question) throws NoApiKeyException, UploadFileException {
        Pattern pattern = Pattern.compile("提取.*文字");
        if (pattern.matcher(question).find() && pic != null) {
            return qwenPicService.textExtraction(pic, question);
        }
        return qwenPicService.callWithPicMultipleAndStream(pic, question);
    }

    @PostMapping("/pic")
    public String createPic(@RequestParam(name = "pic", required = false) String pic,
                            @RequestParam(name = "prompt") String prompt,
                            @RequestParam(name = "style", required = false, defaultValue = "<auto>") String style) throws NoApiKeyException, UploadFileException {
        // 生成新图
        if (pic == null) {
            return qwenCreatePicService.createPic(prompt, style);
        }
        // 修改原图
        return qwenCreatePicService.createPicWithReference(pic, prompt, style);
    }
}
