package org.alex.aiservicedemo;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import org.alex.tools.ModelUtil;

import java.time.LocalDateTime;

public class UserChat {

    public interface Assistant{
        public String chat(@MemoryId Long id, @UserMessage String message);
    }

    @Tool("获取当前⽇期")
    public static String dateUtil(){
        return LocalDateTime.now().toString();
    }

    public static void main(String[] args) throws NoSuchMethodException {

        ChatLanguageModel model = ModelUtil.getModel();
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        ToolSpecification toolSpecification =
                ToolSpecifications.toolSpecificationFrom(UserChat.class.getMethod("dateUtil"));
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .tools(toolSpecification)
                .build();
        //与⽤户1的交互
        System.out.println(">>>"+assistant.chat(1L, "你好，我是Alex"));
        System.out.println(">>>"+assistant.chat(1L, "我的名字是什么"));
        //与⽤户2的交互
        System.out.println(">>>"+assistant.chat(2L, "你好，我是⽼王"));
        System.out.println(">>>"+assistant.chat(2L, "我的名字是什么"));
    }
}
