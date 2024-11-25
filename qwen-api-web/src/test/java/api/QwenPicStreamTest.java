package api;

import java.util.Arrays;
import java.util.HashMap;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import io.reactivex.Flowable;

public class QwenPicStreamTest {
    public static void streamCall() throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(new HashMap<String, Object>(){{put("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg");}},
                        new HashMap<String, Object>(){{put("text", "这是什么");}})).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-vl-max-latest")
                .message(userMessage)
                .incrementalOutput(true)
                .build();
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(item -> {
            try {
                System.out.println(JsonUtils.toJson(item));
            } catch (Exception e){
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        try {
            streamCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}
