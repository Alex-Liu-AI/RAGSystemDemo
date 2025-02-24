package org.alex.springboot;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.alex.tools.ModelUtil;

import java.util.List;

public class QuestionSerchDemo {

    public static void main(String[] args) {
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
                .build();
        RedisEmbeddingStore embeddingStore = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .dimension(1536)
                .build();
        //预设⼏个指示，⽣成向量
        TextSegment textSegment1 = TextSegment.textSegment("客服电话是400-8558558");
        TextSegment textSegment2 = TextSegment.textSegment("客服⼯作时间是周⼀到周五");
        TextSegment textSegment3 = TextSegment.textSegment("客服投诉电话是400-8668668");
        Response<Embedding> embed1 = embeddingModel.embed(textSegment1);
        Response<Embedding> embed2 = embeddingModel.embed(textSegment2);
        Response<Embedding> embed3 = embeddingModel.embed(textSegment3);
        // 存储向量
        embeddingStore.add(embed1.content(), textSegment1);
        embeddingStore.add(embed2.content(), textSegment2);
        embeddingStore.add(embed3.content(), textSegment3);
        // 预设⼀个问题，⽣成向量
        Response<Embedding> embed = embeddingModel.embed("客服电话多少");
        // 查询
        List<EmbeddingMatch<TextSegment>> result =
                embeddingStore.findRelevant(embed.content(), 5);
        for (EmbeddingMatch<TextSegment> embeddingMatch : result) {
            System.out.println(embeddingMatch.embedded().text() + ",分数为：" +
                    embeddingMatch.score());
        }

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore) //向量存储模型
                .embeddingModel(embeddingModel) //向量模型
                .maxResults(5) // 最相似的5个结果
                .minScore(0.8) // 只找相似度在0.8以上的内容
                .build();

        String question = "在线⽀付取消订单后钱怎么返还？"; //⽤户的问题
        Query query = new Query(question);
        List<Content> contentList = contentRetriever.retrieve(query);
        for (Content content : contentList) {
            System.out.println(content);
        }
    }
}
