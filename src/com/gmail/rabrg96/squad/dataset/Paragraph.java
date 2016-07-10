package com.gmail.rabrg96.squad.dataset;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.List;
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

    @Override
    public String toString() {
        return "Paragraph{" +
                "context='" + context + '\'' +
                ", qas=" + qas +
                ", contextSentences=" + getContextSentences() +
                '}';
    }
}
