package org.alex.tools;

import dev.langchain4j.agent.tool.*;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.tool.DefaultToolExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToolDemo1 {
    public static void main(String[] args) {
        //构建模型，过程略
        ChatLanguageModel model = ModelUtil.getModel();
        //指定ToolSpecfication
        List<ToolSpecification> toolSpecifications =
                ToolSpecifications.toolSpecificationsFrom(WeatherUtil.class);
        //1、构建对话消息
        List<ChatMessage> chatMessages = new ArrayList<>();
        UserMessage userMessage = UserMessage.from("北京的天⽓怎么样？");
        chatMessages.add(userMessage);
        //2、调⽤⼤模型，⽣成⼯具调⽤请求
        AiMessage aiMessage = model.generate(chatMessages, toolSpecifications).content();
        List<ToolExecutionRequest> toolExecutionRequests =
                aiMessage.toolExecutionRequests();
        toolExecutionRequests.forEach(toolExecutionRequest -> {
            System.out.println("调⽤⼯具⽅法："+toolExecutionRequest.name());
            System.out.println("调⽤参数："+toolExecutionRequest.arguments());
        });
        chatMessages.add(aiMessage);
        //3、把⼯具请求以及⼯具的执⾏结果⼀起加⼊到对话消息列表中。
        WeatherUtil weatherUtil = new WeatherUtil();
        toolExecutionRequests.forEach(toolExecutionRequest -> {
            DefaultToolExecutor toolExecutor = new DefaultToolExecutor(weatherUtil,
                    toolExecutionRequest);
            String result = toolExecutor.execute(toolExecutionRequest,
                    UUID.randomUUID().toString());
            System.out.println("⼯具执⾏结果"+result);
            ToolExecutionResultMessage toolResultMessage =
                    ToolExecutionResultMessage.from(toolExecutionRequest, result);
            chatMessages.add(toolResultMessage);
        });
        //4、调⽤⼤模型，⽣成最终结果
        AiMessage finalResponse = model.generate(chatMessages).content();
        System.out.println("最终结果："+finalResponse.text());
    }
}
class WeatherUtil{
    @Tool("获取某⼀个具体城市的天⽓")
    public String getWeather(@P("指定的城市")String city){
        return "明天 "+city+" 天⽓晴朗";
    }
}
