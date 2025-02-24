package org.alex.base;

import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.output.Response;

/**
 * 温和模式，规避敏感词
 */
public class ModerationDemo {
    public static void main(String[] args) {
        OpenAiModerationModel model= OpenAiModerationModel.builder().apiKey("demo").build();
        Response<Moderation> response = model.moderate("杀人偿命，欠债还钱");
        System.out.println(response.content());

    }
}
