package com.gmail.rabrg96.squad.dataset;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.*;
import java.util.stream.Collectors;

public final class Paragraph {

    private String context;
    private List<QuestionAnswerService> qas;

    private List<String> contextSentences;

    public String getContext() {
        return context;
    }

    public List<QuestionAnswerService> getQas() {
        return qas;
    }

    public List<String> getContextSentences() {
        if (contextSentences == null) {
            final Document document = new Document(context);
            contextSentences = document.sentences().stream().map(Sentence::text).collect(Collectors.toList());
        }
        return contextSentences;
    }

    // answer is in the first or second sentence over 70% of the time
    // could be improved by ignoring (or weighting) common words
    public Map<String, Integer> getRelevancyOrederedContextSentences(final QuestionAnswerService qas) {
        final Map<String, Integer> relevancyOrdered = new HashMap<>(getContextSentences().size());
        final Sentence questionSentence = new Sentence(qas.getQuestion());
        for (final String context : getContextSentences()) {
            final Sentence contextSentence = new Sentence(context);
            int sentenceCounter = 0;
            for (final String contextWord : contextSentence.lemmas()) {
                for (final String questionWord : questionSentence.lemmas()) {
                    if (contextWord.toLowerCase().equals(questionWord.toLowerCase())) {
                        sentenceCounter++;
                    }
                }
            }
            relevancyOrdered.put(context, sentenceCounter);
        }
        sortByValue(relevancyOrdered);
        return relevancyOrdered;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Paragraph{" +
                "context='" + context + '\'' +
                ", qas=" + qas +
                ", contextSentences=" + getContextSentences() +
                '}';
    }
}
