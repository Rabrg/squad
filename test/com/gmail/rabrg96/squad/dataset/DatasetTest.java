package com.gmail.rabrg96.squad.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

public class DatasetTest {

    public static void main(final String[] args) {
        try {
            final Gson gson = new GsonBuilder().create();
            final Dataset dataset = gson.fromJson(new FileReader("./res/dev-v1.0.json"), Dataset.class);
            for (final Article article : dataset.getData()) {
                for (final Paragraph paragraph : article.getParagraphs()) {
                    for (final QuestionAnswerService qas : paragraph.getQas()) {
                        qas.getQuestionType();
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
