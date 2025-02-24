package org.alex.embeddingdemo;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.alex.tools.ModelUtil;
import java.util.List;

public class VectorDemo {
    public static void main(String[] args) {
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
                .build();
        // ⽣成向量
        Response<Embedding> embed = embeddingModel.embed("你好，我是Alex");
        System.out.println(embed.content().toString());
        System.out.println(embed.content().vector().length);

        RedisEmbeddingStore embeddingStore = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .dimension(1536)//维度，需要与计算结果保持⼀致。如果使⽤其他的模型，可能会有不同的结果。
                .build();

        // 存储向量--单独指定数据ID，索引的ID是固定的embedding-index
        embeddingStore.add("vec1",embed.content());

        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(embeddingModel.embed("我叫Alex").content(), 5, 0.8d);
        for (EmbeddingMatch<TextSegment> match : matches) {
            System.out.println(match.embeddingId()+"=="+match.score());
        }
    }
}
