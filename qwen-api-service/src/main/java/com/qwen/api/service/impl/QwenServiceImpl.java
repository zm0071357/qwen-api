package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.qwen.api.service.QwenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class QwenServiceImpl implements QwenService {
    List<Message> messages = new ArrayList<>();

    /**
     * 构建消息对象的静态方法
     * @param role 角色 USER / SYSTEM
     * @param content 内容 "You are a helpful assistant." / question
     * @return Message对象
     */
    private static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    /**
     * 构建QwenParam对象的静态方法
     * @param messages 消息集合
     * @return QwenParam对象
     */
    public static GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-plus")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
    }

    /**
     * 构建GenerationResult对象的静态方法
     * @param param QwenParam对象
     * @return GenerationResult对象
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    @Override
    public String callWithMessage(String question) throws ApiException, NoApiKeyException, InputRequiredException {
        log.info("用户提问:{}",question);
        Generation gen = new Generation();

        Message systemMsg = createMessage(Role.SYSTEM, "You are a helpful assistant.");
        Message userMsg = createMessage(Role.USER, question);
//        Message systemMsg = Message.builder()
//                .role(Role.SYSTEM.getValue())
//                .content("You are a helpful assistant.")
//                .build();
//        Message userMsg = Message.builder()
//                .role(Role.USER.getValue())
//                .content(question)
//                .build();
//        GenerationParam param = GenerationParam.builder()
//                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
//                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
//                .model("qwen-plus")
//                .messages(Arrays.asList(systemMsg, userMsg))
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .build();
        GenerationParam param = createGenerationParam(Arrays.asList(systemMsg, userMsg));
        try {
            GenerationResult result = callGenerationWithMessages(param);
            String answer = result.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("模型返回:{}",answer);
            return answer;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String callWithMultiple(String question) throws ApiException, NoApiKeyException, InputRequiredException {
        log.info("用户提问:{}",question);
        messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
        try {
            messages.add(createMessage(Role.USER, question));
            GenerationParam param = createGenerationParam(messages);
            GenerationResult result = callGenerationWithMessages(param);
            String answer = result.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("模型返回:{}",answer);
            messages.add(result.getOutput().getChoices().get(0).getMessage());
            return answer;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }
    }
}
