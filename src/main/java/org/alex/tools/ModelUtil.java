package org.alex.tools;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ModelUtil {
    //need to buy API_KEY on OpenAI
    public final static String MODEL_KEY_OPENAI = "";
    public final static String MODEL_URL_OPENAI = "";

    public static ChatLanguageModel getModel(){
        return OpenAiChatModel.builder().apiKey("demo").modelName("gpt-4o-mini").build();
    }

}
