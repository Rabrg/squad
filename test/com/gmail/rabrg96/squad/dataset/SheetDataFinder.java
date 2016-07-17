package com.gmail.rabrg96.squad.dataset;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;

import java.util.List;

public class SheetDataFinder {

    private static final LexicalizedParser parser = LexicalizedParser.loadModel();
    private static final PennTreebankLanguagePack languagePack = new PennTreebankLanguagePack();
    private static final GrammaticalStructureFactory structureFactory = languagePack.grammaticalStructureFactory();

    public static Data getData(final String text, final String classification) {
        try {
            final Tree parent = parser.parse(text);
            final Tree tree = findWHTree(parent);
            final String whType = tree.value();
            final String whWord = tree.getChild(0).yieldWords().get(0).word();
            String whTargetWord;
            String whTargetType;
            try {
                whTargetWord = combineWords(tree);
                whTargetType = tree.getChild(1).value();
            } catch (final Exception e) {
                whTargetWord = "NONE";
                whTargetType = "NONE";
            }
            final String subject = getSubject(text);
            return new Data(text, classification, whType, whWord, whTargetType, whTargetWord, subject);
        } catch (final Exception e) {
            System.out.println("Failed: " + text);
        }
        return null;
    }

    private static String combineWords(final Tree tree) {
        final StringBuilder combined = new StringBuilder();
        final Tree[] children = tree.children();
        for (int i = 1; i < children.length; i++)
            combined.append(children[i].yieldWords().get(0).word()).append(' ');
        return combined.substring(0, combined.length() - 1);
    }

    private static Tree findWHTree(final Tree parent) {
        for (final Tree child : parent.children())
            return parent.value().startsWith("WH") && !child.value().startsWith("WH") ? parent : findWHTree(child);
        return null;
    }

    private static String getSubject(final String text) {
        final List<TypedDependency> dependencies = structureFactory.newGrammaticalStructure(parser.parse(text))
                .typedDependenciesCCprocessed();

        int predicateIndex = 0;
        for (final TypedDependency dependency : dependencies)
            if (dependency.reln().toString().equals("root"))
                predicateIndex = dependency.dep().index();

        // TODO: optimize this
        String rootSubject = "";
        String subject = "";
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            final TypedDependency dependency = dependencies.get(i);
            if (dependency.reln().toString().contains("subj") && dependency.gov().index() == predicateIndex) {
                rootSubject = dependency.dep().word();
                subject = dependency.dep().word();
            } else if (dependency.reln().toString().contains("compound") && dependency.gov().word().equals(rootSubject)) {
                subject = dependency.dep().word() + " " + subject;
            }
        }
        return subject;
    }

    public static final class Data {

        private final String text;
        private final String classification;
        private final String whType;
        private final String whWord;
        private final String whTargetType;
        private final String whTargetWord;
        private final String subject;

        private Data(final String text, final String classification, final String whType, final String whWord,
                     final String whTargetType, final String whTargetWord, final String subject) {
            this.text = text;
            this.classification = classification;
            this.whType = whType;
            this.whWord = whWord;
            this.whTargetType = whTargetType;
            this.whTargetWord = whTargetWord;
            this.subject = subject;
        }

        @Override
        public String toString() {
            return text + '\t' + classification + '\t' + whType + '\t' + whWord + '\t' + whTargetType + '\t'
                    + whTargetWord + '\t' + subject + '\n';
        }
    }
}
