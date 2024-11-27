package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.qwen.api.service.QwenCreatePicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QwenCreatePicServiceImpl implements QwenCreatePicService {
    private static String exist_prompt = null;

    private static String exist_negative_prompt = "";

    Pattern pattern = Pattern.compile("不要.*");

    private static ImageSynthesisParam createImageSynthesisParam(String prompt, String pic, String style, boolean isRef) {
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model(ImageSynthesis.Models.WANX_V1)
                .style(style)
                .prompt(prompt)
                .n(1)
                .size("1280*720")   //allowed size ['1024*1024', '720*1280', '1280*720', '768*1152']
                .build();
        // 在原图上进行修改
        if (pic != null && isRef) {
            HashMap<String,Object> parameters = new HashMap<>();
            parameters.put("ref_strength", 0.6);    // 相似度
            parameters.put("ref_mode", "repaint");  // 根据参考图像内容生成图像：ref_mode="repaint" 根据参考图像风格生成图像：ref_mode="refonly"
            param.setRefImage(pic);
            param.setParameters(parameters);
        }
        return param;
    }

    private String getCallResult(ImageSynthesisParam param) {
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
    public String createPic(String prompt, String style) {
        log.info("图片要求:{}",prompt);
        exist_prompt = prompt;
        ImageSynthesisParam param = createImageSynthesisParam(exist_prompt, null, style, false);
        return getCallResult(param);
    }

    @Override
    public String createPicWithReference(String pic, String prompt, String style) {
        if (pattern.matcher(prompt).find()) {
            exist_negative_prompt += prompt;
        } else {
            exist_prompt += prompt;
        }
        log.info("图片参考图:{}", pic);
        log.info("新增修改要求:{}", prompt);
        log.info("正向要求:{}",exist_prompt);
        log.info("反向要求:{}",exist_negative_prompt);
        ImageSynthesisParam param = createImageSynthesisParam(exist_prompt, pic, style, true);
        param.setNegativePrompt(exist_negative_prompt);
        return getCallResult(param);
    }

}
