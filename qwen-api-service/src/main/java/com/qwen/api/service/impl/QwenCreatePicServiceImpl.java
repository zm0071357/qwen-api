package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.qwen.api.service.QwenCreatePicService;
import com.qwen.api.service.QwenPicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QwenCreatePicServiceImpl implements QwenCreatePicService {

    @Resource
    private QwenPicService qwenPicService;

    Pattern pattern = Pattern.compile("不要.*");

    private static final String Describe_The_Image = "详细描述这张图片，包括主题、氛围、主题、背景、颜色、光线和阴影、线条和形状、视角与构图、风格与艺术、人物形象、情感表达等";

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
        ImageSynthesisParam param = createImageSynthesisParam(prompt, null, style, false);
        return getCallResult(param);
    }

    @Override
    public String createPicWithReference(String pic, String prompt, String style) throws NoApiKeyException, UploadFileException {
        try {
            String negativePrompt = null;
            String basePrompt = qwenPicService.callWithPicMultipleAndStream(pic, Describe_The_Image);
            log.info("图片参考图:{}", pic);
            log.info("由参考图获取的基础提示词:{}", basePrompt);
            if (pattern.matcher(prompt).find()) {
                negativePrompt = prompt;
                log.info("反向提示词:{}",negativePrompt);
            } else {
                basePrompt += prompt;
                log.info("正向提示词:{}",prompt);
            }
            ImageSynthesisParam param = createImageSynthesisParam(basePrompt, pic, style, true);
            param.setNegativePrompt(negativePrompt);
            return getCallResult(param);
        } catch (ApiException | NoApiKeyException e){
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }

    }

}
