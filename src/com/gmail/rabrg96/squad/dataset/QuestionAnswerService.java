package com.gmail.rabrg96.squad.dataset;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Sentence;

import java.util.Arrays;
import java.util.List;

public final class QuestionAnswerService {

    private List<Answer> answers;
    private String id;
    private String question;

    private String questionType;

    public List<Answer> getAnswers() {
        return answers;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    private static final List<String> QUESTION_TYPES = Arrays.asList("who", "whom", "whose", "what", "which", "when",
            "how", "why", "where", "can", "will", "could", "should", "would", "is", "are", "am", "shall", "may", "did",
            "do", "does");

    public String getQuestionType() {
        if (questionType == null) { // TODO: enum?
            final Sentence sentence = new Sentence(question);

            // Look for question types as first to words and if so use the first
            final List<String> words = sentence.words();
            final String first = words.get(0).toLowerCase();
            final String second = words.get(1).toLowerCase();
            if (QUESTION_TYPES.contains(first) && QUESTION_TYPES.contains(second))
                return (questionType = first);

            // Check if last word is question type
            final String last = Character.isLetterOrDigit(words.get(words.size() - 1).charAt(0))
                    ? words.get(words.size() - 1).toLowerCase() : words.get(words.size() - 2).toLowerCase();
            if (QUESTION_TYPES.contains(last))
                return (questionType = last);

            // If not begin with two question words, use dependencies
            final SemanticGraph graph = sentence.dependencyGraph();
            for (final SemanticGraphEdge edge : graph.edgeListSorted()) {
                final String source = edge.getSource().word().toLowerCase();
                final String target = edge.getTarget().word().toLowerCase();
                final String type = QUESTION_TYPES.contains(target) ? target : source; // TODO: randomly gave target priority over source
                if (QUESTION_TYPES.contains(type)) {
                    questionType = type;

                    // Debug types which are prone to be error
                    switch (type) {
                        case "do":
                        case "is":
                        case "are":
                            System.out.println("Question: " + question);
                            System.out.println("Detected type: " + type);
                            System.out.println("Edge list: " + graph.edgeListSorted());
                            System.out.println("=====");
                            break;
                    }
                    break;
                }
            }
        }
        return questionType;
    }

    @Override
    public String toString() {
        return "QuestionAnswerService{" +
                "answers=" + answers +
                ", id='" + id + '\'' +
                ", question='" + question + '\'' +
                '}';
    }
}
