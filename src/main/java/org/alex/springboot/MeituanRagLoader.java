package org.alex.springboot;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.alex.tools.ModelUtil;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MeituanRagLoader {
    public static void main(String[] args) throws URISyntaxException {

        /**
         * 读取本地知识库文件
         */
        Path documentPath =
                Paths.get(MeituanRagLoader.class.getClassLoader().getResource("meituan-question.txt").toURI());
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(documentPath,
                documentParser);

        DocumentSplitter splitter = new MyDocumentSplitter();
        List<TextSegment> segments = splitter.split(document);

        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
                .build();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        RedisEmbeddingStore embeddingStore = RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6379)
                .dimension(1536)//维度，需要与计算结果保持⼀致。如果使⽤其他的模型，可能会有不同的结果。
                .build();

        embeddingStore.addAll(embeddings, segments);
    }
}
