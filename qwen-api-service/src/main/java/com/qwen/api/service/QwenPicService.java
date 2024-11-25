package com.qwen.api.service;


import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

public interface QwenPicService {

    /**
     * 图片识别
     * @param pic 图片路径
     * @param question 提问
     * @return api回答
     * @throws NoApiKeyException 没有密匙
     * @throws UploadFileException 文件问题
     */
    String callWithPic(String pic, String question) throws NoApiKeyException, UploadFileException;

    /**
     * 图片识别并进行多轮对话
     * @param pic 图片路径
     * @param question 提问
     * @return api回答
     */
    String callWithPicMultiple(String pic, String question) throws NoApiKeyException, UploadFileException;

    /**
     * 图片识别后流式输出
     * @param pic 图片路径
     * @param question 提问
     * @return api回答
     */
    String callWithPicStream(String pic, String question) throws NoApiKeyException, UploadFileException;

    /**
     * 图片识别后多轮流式输出
     * @param pic 图片路径
     * @param question 提问
     * @return api回答
     */
    String callWithPicMultipleAndStream(String pic, String question);
}
