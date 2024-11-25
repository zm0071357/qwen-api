package com.qwen.api.service;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public interface QwenService {

    /**
     * 单轮对话
     * @param question 问题
     * @return api回答
     */
    String callWithMessage(String question) throws ApiException, NoApiKeyException, InputRequiredException;

    /**
     * 多轮对话
     * @param question 问题
     * @return api回答
     */
    String callWithMultiple(String question) throws ApiException, NoApiKeyException, InputRequiredException;

    /**
     * 流式输出
     * @param question 问题
     * @return api回答
     */
    String callWithStream(String question) throws NoApiKeyException, InputRequiredException;

    /**
     * 多轮流式输出
     * @param question 问题
     * @return api回答
     */
    String callWithStreamAndMultiple(String question);
}
