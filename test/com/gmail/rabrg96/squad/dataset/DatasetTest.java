package com.gmail.rabrg96.squad.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

public class DatasetTest {

    public static void main(final String[] args) {
        try {
            final Gson gson = new GsonBuilder().create();
            final Dataset dataset = gson.fromJson(new FileReader("./res/dev-v1.0.json"), Dataset.class);

            final long start = System.currentTimeMillis();
            int articles = 0, paragraphs = 0, questions = 0;
            for (final Article article : dataset.getData()) {
                articles++;
                for (final Paragraph paragraph : article.getParagraphs()) {
                    paragraphs++;
                    for (final QuestionAnswerService qas : paragraph.getQas()) {
                        questions++;
                        try {
                            if (qas.isMultiplePossibleType()) {
                                System.out.println(qas.getQuestionType() + ": " + qas.getQuestion());
                            }
                        } catch (final IllegalStateException e) {
//                            System.out.println("Failed to detect type: " + qas.getQuestion());
                        }
                    }
                }
            }
            final long elapsed = (System.currentTimeMillis() - start);
            System.out.println("Parsed " + questions + " questions in " + elapsed + "ms (" + (((double) elapsed) / questions) + "ms/q)");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
