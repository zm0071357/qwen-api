package com.qwen.api.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.qwen.api.service.QwenService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class QwenServiceImpl implements QwenService {
    private static List<Message> messages = new ArrayList<>();

    /**
     * 构建Message消息对象的静态方法
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
     * @param isStream 是否开启流式输出
     * @return QwenParam对象
     */
    public static GenerationParam createGenerationParam(List<Message> messages, boolean isStream) {
        GenerationParam param = GenerationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-plus")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        if (isStream) {
            param.setIncrementalOutput(true);
        }
        return param;
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
        GenerationParam param = createGenerationParam(Arrays.asList(systemMsg, userMsg), false);
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
        try {
            messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
            messages.add(createMessage(Role.USER, question));
            GenerationParam param = createGenerationParam(messages, false);
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

    @Override
    public String callWithStream(String question) throws NoApiKeyException, InputRequiredException {
        log.info("用户提问:{}",question);
        try {
            Generation gen = new Generation();
            Message systemMsg = createMessage(Role.SYSTEM, "You are a helpful assistant.");
            Message userMsg = createMessage(Role.USER, question);
            GenerationParam param = createGenerationParam(Arrays.asList(systemMsg, userMsg), true);
            Flowable<GenerationResult> result = gen.streamCall(param);
            StringBuilder fullContent = new StringBuilder();
            result.blockingForEach(messages -> {
                String content = messages.getOutput().getChoices().get(0).getMessage().getContent();
                fullContent.append(content);
                log.info("流式输出:{}",content);
            });
            log.info("模型返回:{}", fullContent.toString());
            return fullContent.toString();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.info("错误信息:{}",e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String callWithStreamAndMultiple(String question) {
        log.info("用户提问:{}",question);
        messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
        messages.add(createMessage(Role.USER, question));
        GenerationParam param = createGenerationParam(messages, true);
        StringBuilder fullContent = new StringBuilder();
        try {
            Generation gen = new Generation();
            Flowable<GenerationResult> result = gen.streamCall(param);
            result.blockingForEach(messages -> {
                String content = messages.getOutput().getChoices().get(0).getMessage().getContent();
                fullContent.append(content);
                log.info("流式输出:{}", content);
                // 将每次生成的消息加入到消息历史中
                this.messages.add(messages.getOutput().getChoices().get(0).getMessage());
            });
            log.info("模型返回:{}", fullContent.toString());
            return fullContent.toString();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.info("错误信息:{}", e.getMessage());
            return e.getMessage();
        }
    }
}
