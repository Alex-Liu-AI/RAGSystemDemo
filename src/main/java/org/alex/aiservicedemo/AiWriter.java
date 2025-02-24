package org.alex.aiservicedemo;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.alex.tools.ModelUtil;

public class AiWriter {
    interface Writer {
        @SystemMessage("你是一个散文作家，根据输入的题目写一篇200字以内的文章")
        String write(String text);
    }

    interface Writer2 {
        @SystemMessage("你是一个散文作家，写一篇题目是{{title}},字数{{count}}字以内的文章")
        String write(@UserMessage String text, @V("title")String tiltle, @V("count")Long count);
    }

    public static void main(String[] args) {
        ChatLanguageModel model = ModelUtil.getModel();
        Writer writer = AiServices.create(Writer.class, model);
        System.out.println(writer.write("最可爱的人"));

        Writer2 writer2 = AiServices.create(Writer2.class, model);
        System.out.println(writer2.write("写一篇散文","最爱的歌",200L));
    }
}
