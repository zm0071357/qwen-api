package api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

public class QwenPicMultipleTest {
    private static final String modelName = "qwen-vl-max-latest";
    public static void MultiRoundConversationCall() throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(Arrays.asList(Collections.singletonMap("text", "You are a helpful assistant."))).build();

        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"),
                        Collections.singletonMap("text", "这是什么?"))).build();
        List<MultiModalMessage> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))                .model(modelName)
                .messages(messages)
                .build();

        MultiModalConversationResult result = conv.call(param);
        System.out.println("第一轮输出："+result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));        // add the result to conversation
        messages.add(result.getOutput().getChoices().get(0).getMessage());
        MultiModalMessage msg = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(Collections.singletonMap("text", "做一首诗描述这个场景"))).build();
        messages.add(msg);
        param.setMessages((List)messages);
        result = conv.call(param);
        System.out.println("第二轮输出："+result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));    }

    public static void main(String[] args) {
        try {
            MultiRoundConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}
