package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.qwen.api.service.QwenCreatePicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class QwenCreatePicServiceImpl implements QwenCreatePicService {
    @Override
    public String createPic(String prompt) {
        log.info("图片要求:{}",prompt);
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model(ImageSynthesis.Models.WANX_V1)
                .prompt(prompt)
                .style("<auto>")
                .n(1)
                .size("1280*720")   //allowed size ['1024*1024', '720*1280', '1280*720', '768*1152']
                .build();
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            log.info("图像模型处理时间较长，请稍等片刻");
            result = imageSynthesis.call(param);
            log.info("模型返回:{}",result);
            return result.getOutput().getResults().get(0).get("url");
        } catch (ApiException | NoApiKeyException e){
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String createPicWithReference(String pic, String prompt) {
        log.info("图片参考图:{}要求:{}", pic, prompt);

        //图像处理参数
        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put("ref_strength", 0.8);    //相似度
        parameters.put("ref_mode", "refonly");  // 根据参考图像内容生成图像：ref_mode="repaint"，默认为该值。根据参考图像风格生成图像：ref_mode="refonly"。

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .model(ImageSynthesis.Models.WANX_V1)
                        .prompt(prompt)
                        .style("<auto>")
                        .n(1)
                        .size("1280*720")
                        .refImage(pic)
                        .parameters(parameters)
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            log.info("图像模型处理时间较长，请稍等片刻");
            result = imageSynthesis.call(param);
            log.info("模型返回:{}",result);
            return result.getOutput().getResults().get(0).get("url");
        } catch (ApiException | NoApiKeyException e){
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }
    }


}
