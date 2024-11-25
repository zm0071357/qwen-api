package com.qwen.api.service;


import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

public interface QwenPicService {

    /**
     * 单照片识别
     * @param pic 图片路径
     * @param question 提问
     * @return api回答
     * @throws NoApiKeyException 没有密匙
     * @throws UploadFileException 文件问题
     */
    String callWithPic(String pic, String question) throws NoApiKeyException, UploadFileException;

}
