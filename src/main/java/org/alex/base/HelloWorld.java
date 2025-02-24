package org.alex.base;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;

public class HelloWorld {
    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String answer = model.generate("你是谁?");
        System.out.println(answer);

        UserMessage userMessage = UserMessage.from("你是谁？");
        Response<AiMessage> response = model.generate(userMessage);
        AiMessage aiMessage = response.content();
        System.out.println(aiMessage.text());

        UserMessage userMessage2 = UserMessage.from("请重复一遍");
        Response<AiMessage> response2 = model.generate(aiMessage,userMessage,userMessage2);
        AiMessage aiMessage2 = response2.content();
        System.out.println(aiMessage2.text());
    }
}
