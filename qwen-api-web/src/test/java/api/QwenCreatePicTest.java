package api;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;

/**
 * style
 * <auto>：默认值，由模型决定输出的图像风格。
 * <photography>：摄影。
 * <portrait>：人像写真。
 * <3d cartoon>：3D卡通。
 * <anime>：动画。
 * <oil painting>：油画。
 * <watercolor>：水彩。
 * <sketch>：素描。
 * <chinese painting>：中国画。
 * <flat illustration>：扁平插画。
 *
 * 正向提示词 prompt
 * 反向提示词 negative_prompt
 *
 *
 */

public class QwenCreatePicTest {
    public void syncCall() {
        String prompt = "近景镜头，18岁长发少女穿JK站在长满樱花树的街道下";
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .model(ImageSynthesis.Models.WANX_V1)
                        .prompt(prompt)
                        .style("<anime>")
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("图像模型处理时间较长，请稍等片刻");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(result.getOutput().getResults().get(0).get("url"));
    }

    public static void main(String[] args){
        QwenCreatePicTest main = new QwenCreatePicTest();
        main.syncCall();
    }
}
