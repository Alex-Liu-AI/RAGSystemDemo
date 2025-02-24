package org.alex.springboot.controller;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import jakarta.annotation.Resource;
import org.alex.tools.ModelUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OpenAIController {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Resource
    private OpenAiEmbeddingModel embeddingModel;

    @RequestMapping("/openai/hello")
    public String queryOpenAI() {
        return openAiChatModel.generate("你好，你是谁？");
    }

    /**
     * 美团常见客服问题
     * @param message
     * @return
     */
    @RequestMapping("/openai/meituan/{message}")
    public String queryOpenAI(@PathVariable String message) {

//        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
//                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
//                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
//                .build();
        RedisEmbeddingStore embeddingStore = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .dimension(1536)
                .build();
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore) //向量存储模型
                .embeddingModel(embeddingModel) //向量模型
                .maxResults(5) // 最相似的5个结果
                .minScore(0.8) // 只找相似度在0.8以上的内容
                .build();

        String question = message; //⽤户的问题
        Query query = new Query(question);
        List<Content> contentList = contentRetriever.retrieve(query);
        for (Content content : contentList) {
            System.out.println(content);
        }

        ContentInjector contentInjector = new DefaultContentInjector();
        UserMessage promptMessage = contentInjector.inject(contentList,
                UserMessage.from(question));
        for (dev.langchain4j.data.message.Content content : promptMessage.contents()) {
            System.out.println(content);
        }
        Response<AiMessage> generate = openAiChatModel.generate(promptMessage);
        return generate.content().text();
    }
}
