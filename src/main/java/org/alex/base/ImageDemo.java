package org.alex.base;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import org.alex.tools.ModelUtil;

/**
 * 图片模型
 */
public class ImageDemo {
    public static void main(String[] args) {
        OpenAiImageModel model = OpenAiImageModel.builder()
                .apiKey(ModelUtil.MODEL_KEY_OPENAI)
                .baseUrl(ModelUtil.MODEL_URL_OPENAI)
                .build();

        Response<Image> response = model.generate("生成一张复古风的汽车");
        System.out.println(response.content().url());
    }
}
