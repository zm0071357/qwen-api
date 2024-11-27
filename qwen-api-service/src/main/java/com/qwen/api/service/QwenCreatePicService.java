package com.qwen.api.service;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

public interface QwenCreatePicService {
    /**
     * 文本生成图像
     * @param prompt 文本
     * @param style 风格
     * @return 生成图像路径
     */
    String createPic(String prompt, String style);

    /**
     * 基于参考图加文本生成图像
     * @param pic 参考图路径
     * @param prompt 文本
     * @param style 风格
     * @return 生成图像路径
     */
    String createPicWithReference(String pic, String prompt, String style) throws NoApiKeyException, UploadFileException;
}
