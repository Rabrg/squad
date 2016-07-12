package com.gmail.rabrg96.squad.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;

import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DatasetTest {

    public static void main(final String[] args) {
        try {
            final Gson gson = new GsonBuilder().create();
            final Dataset dataset = gson.fromJson(new FileReader("./res/dev-v1.0.json"), Dataset.class);

            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


            for (final Paragraph paragraph : dataset.getData().get(0).getParagraphs()) {
                String text = paragraph.getContext();
                Annotation document = new Annotation(text);
                pipeline.annotate(document);

                Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
                System.out.println("Graph: " + graph.toString());
                for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
                    CorefChain chain = entry.getValue();
                    CorefChain.CorefMention repMention = chain.getRepresentativeMention();
                    System.out.println("Chain: " + chain.toString());
                    System.out.println("Rep: " + repMention.toString());
                }
//                for (final String text : paragraph.getContextSentences()) {
//                    final Sentence sentence = new Sentence(text);
//                    final List<String> words = sentence.words();
//                    final List<String> nerTags = sentence.nerTags(); // TODO: verify 7 tag
//                    System.out.print("CONTEXT SENTENCE: ");
//                    for (int i = 0; i < words.size(); i++) {
//                        System.out.print(words.get(i) + " ");
//                        if (!"O".equals(nerTags.get(i))) {
//                            System.out.print("(" + nerTags.get(i) + ") ");
//                        }
//                    }
//                    System.out.println();
//                }

//                for (final QuestionAnswerService qas : paragraph.getQas()) {
//                    if ("who".equals(qas.getQuestionType())) {
//                        System.out.println("QUESTION: " + qas.getQuestion());
//                        System.out.println("ANSWER: \"" + qas.getAnswers().get(0).getText() + "\"");
//                        int i = 0;
//                        for (final String context : paragraph.getRelevancyOrederedContextSentences(qas).keySet()) {
//                            if (context.contains(qas.getAnswers().get(0).getText())) {
//                                System.out.println("Detected in sentence: " + i);
//                                break;
//                            }
//                            i++;
//                        }
//                        System.out.println();
//                    }
//                }
            }
//            final long start = System.currentTimeMillis();
//            int articles = 0, paragraphs = 0, questions = 0;
//            for (final Article article : dataset.getData()) {
//                articles++;
//                for (final Paragraph paragraph : article.getParagraphs()) {
//                    paragraphs++;
//                    for (final String sentenceText : paragraph.getContextSentences()) {
//                        System.out.println(new Sentence(sentenceText).nerTags() + ": " + sentenceText);
//                    }
//                    for (final QuestionAnswerService qas : paragraph.getQas()) {
//                        questions++;
//                        try {
//                            if ("who".equals(qas.getQuestionType())) {
//                                System.out.println(qas.getAnswers() + ": " + qas.getQuestion());
//                            }
//                        } catch (final IllegalStateException e) {
////                            System.out.println("Failed to detect type: " + qas.getQuestion());
//                        }
//                    }
//                    break; // one paragraph
//                }
//            }
//            final long elapsed = (System.currentTimeMillis() - start);
//            System.out.println("Parsed " + questions + " questions in " + elapsed + "ms (" + (((double) elapsed) / questions) + "ms/q)");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
