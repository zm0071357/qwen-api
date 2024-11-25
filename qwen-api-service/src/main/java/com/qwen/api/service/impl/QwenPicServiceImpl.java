package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.qwen.api.service.QwenPicService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class QwenPicServiceImpl implements QwenPicService {

    private static List<MultiModalMessage> messages = new ArrayList<>();

    /**
     * 构建MultiModalMessage消息对象的静态方法
     * @param role 角色 USER / SYSTEM
     * @param content 内容
     * @return MultiModalMessage对象
     */
    private static MultiModalMessage createMultiModalMessage(Role role, List<Map<String, Object>> content) {
        return MultiModalMessage.builder().role(role.getValue()).content(content).build();
    }

    /**
     * 构建MultiModalConversationParam对象的静态方法
     * @param messages 消息集合
     * @param isStream 是否开启流式输出
     * @return MultiModalConversationParam对象
     */
    private static MultiModalConversationParam createMultiModalConversationParam(List<MultiModalMessage> messages, boolean isStream) {
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-vl-max-latest")
                .messages(messages).build();
        if (isStream) {
            param.setIncrementalOutput(true);
        }
        return param;
    }

    @Override
    public String callWithPic(String pic, String question) throws NoApiKeyException, UploadFileException {
        log.info("用户图片:{},提问:{}", pic, question);
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage systemMessage = createMultiModalMessage(Role.SYSTEM, Arrays.asList(
                            Collections.singletonMap("text", "You are a helpful assistant.")));
            MultiModalMessage userMessage = createMultiModalMessage(Role.USER, Arrays.asList(
                          Collections.singletonMap("image", pic),
                          Collections.singletonMap("text", question)));
            MultiModalConversationParam param = createMultiModalConversationParam(Arrays.asList(systemMessage, userMessage), false);
            MultiModalConversationResult result = conv.call(param);
            String answer = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
            log.info("模型返回:{}", answer);
            return answer;
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            log.info("错误信息:{}", e.getMessage());
            return e.getMessage();
        }

    }

    @Override
    public String callWithPicMultiple(String pic, String question) throws NoApiKeyException, UploadFileException {
        log.info("用户图片:{},提问:{}", pic, question);
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage systemMessage = createMultiModalMessage(Role.SYSTEM, Arrays.asList(
                        Collections.singletonMap("text", "You are a helpful assistant.")));
        MultiModalMessage userMessage = null;
        if (pic != null) {
            userMessage = createMultiModalMessage(Role.USER, Arrays.asList(
                    Collections.singletonMap("image", pic),
                    Collections.singletonMap("text", question)));
        } else {
            userMessage = createMultiModalMessage(Role.USER, Arrays.asList(
                    Collections.singletonMap("text", question)));
        }
        messages.add(systemMessage);
        messages.add(userMessage);
        MultiModalConversationParam param = createMultiModalConversationParam(messages, false);
        MultiModalConversationResult result = conv.call(param);
        String answer = (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
        log.info("模型返回:{}", answer);
        messages.add(result.getOutput().getChoices().get(0).getMessage());
        return answer;
    }

    @Override
    public String callWithPicStream(String pic, String question) throws NoApiKeyException, UploadFileException {
        log.info("用户图片:{},提问:{}", pic, question);
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage systemMessage = createMultiModalMessage(Role.SYSTEM, Arrays.asList(
                    new HashMap<String, Object>(){{put("text", "You are a helpful assistant.");}}));
            MultiModalMessage userMessage = createMultiModalMessage(Role.USER, Arrays.asList(
                    new HashMap<String, Object>(){{put("image", pic);}},
                    new HashMap<String, Object>(){{put("text", question);}}));
            MultiModalConversationParam param = createMultiModalConversationParam(Arrays.asList(systemMessage, userMessage), true);
            StringBuilder fullContent = new StringBuilder();
            Flowable<MultiModalConversationResult> result = conv.streamCall(param);
            result.blockingForEach(item -> {
                String content = (String) item.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
                fullContent.append(content);
                log.info("流式输出:{}",content);
            });
            log.info("模型返回:{}", fullContent.toString());
            return fullContent.toString();
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String callWithPicMultipleAndStream(String pic, String question) throws NoApiKeyException, UploadFileException {
        log.info("用户图片:{},提问:{}", pic, question);
        try {
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalMessage systemMessage = createMultiModalMessage(Role.SYSTEM, Arrays.asList(
                    new HashMap<String, Object>(){{put("text", "You are a helpful assistant.");}}));
            MultiModalMessage userMessage = null;
            if (pic != null) {
                userMessage = createMultiModalMessage(Role.USER, Arrays.asList(
                        new HashMap<String, Object>(){{put("image", pic);}},
                        new HashMap<String, Object>(){{put("text", question);}}));
            } else {
                userMessage = createMultiModalMessage(Role.USER,
                        Arrays.asList(new HashMap<String, Object>(){{put("text", question);}}));
            }
            messages.add(systemMessage);
            messages.add(userMessage);
            MultiModalConversationParam param = createMultiModalConversationParam(messages, true);
            StringBuilder fullContent = new StringBuilder();
            Flowable<MultiModalConversationResult> result = conv.streamCall(param);
            result.blockingForEach(item -> {
                String content = (String) item.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
                fullContent.append(content);
                log.info("流式输出:{}",content);
                this.messages.add(item.getOutput().getChoices().get(0).getMessage());
            });
            log.info("模型返回:{}", fullContent.toString());
            return fullContent.toString();
        } catch (Exception e) {
            log.info("错误信息:{}", e.getMessage());
            return e.getMessage();
        }

    }

    @Override
    public String textExtraction(String pic, String question) throws NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        Map<String, Object> map = new HashMap<>();
        map.put("image", pic);
        map.put("max_pixels", "1003520");
        map.put("min_pixels", "3136");
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        map,
                        Collections.singletonMap("text", question))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-vl-ocr")
                .message(userMessage)
                .topP(0.01)
                .temperature(0.1f)
                .maxLength(2000)
                .build();
        MultiModalConversationResult result = conv.call(param);
        return (String) result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");
    }


}
