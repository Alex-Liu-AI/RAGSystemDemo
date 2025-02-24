package org.alex.base;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.alex.tools.ModelUtil;

import java.util.concurrent.TimeUnit;

public class StreamingDemo {
    public static void main(String[] args) {
        //流式Model,显示Token
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
                .build();

        model.generate("你好，你是谁?", new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.println(token);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }
}
