package org.alex.tools;

import com.google.common.collect.Lists;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;

public class ToolsDemo {

    @Tool("获取当前日期")
    public static String DateUtil(){
         return LocalDate.now().toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ChatLanguageModel model = ModelUtil.getModel();

        ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(ToolsDemo.class.getMethod("DateUtil",null));
        UserMessage userMessage = UserMessage.from("今天是几月几号？");
        Response<AiMessage> response = model.generate(Collections.singletonList(userMessage), toolSpecification);
        AiMessage aiMessage = response.content();
        System.out.println(aiMessage);

        if(aiMessage.hasToolExecutionRequests()){
            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()){
                String methodName = toolSpecification.name();
                Method method = ToolsDemo.class.getMethod(methodName, null);
                String result = (String)method.invoke(null);
                System.out.println(result);

                ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest.id(), toolExecutionRequest.name(), result);
                AiMessage aiMessage1  = model.generate(Lists.newArrayList(userMessage, aiMessage, toolExecutionResultMessage)).content();
                System.out.println(aiMessage1.text());
            }
        }
    }
}
