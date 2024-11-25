package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.qwen.api.service.QwenPicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
@Slf4j
public class QwenPicServiceImpl implements QwenPicService {

    @Override
    public String callWithPic(String pic, String question) throws NoApiKeyException, UploadFileException {
        log.info("用户图片:{},提问:{}", pic, question);
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("image", pic),
                            Collections.singletonMap("text", question))).build();
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .model("qwen-vl-max-latest")
                    .message(userMessage)
                    .build();
            MultiModalConversationResult result = conv.call(param);
            String answer = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
            log.info("模型返回:{}", answer);
            return answer;
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            log.info("错误信息:{}", e.getMessage());
            return e.getMessage();
        }

    }
}
