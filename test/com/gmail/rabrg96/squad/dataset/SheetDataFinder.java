package com.gmail.rabrg96.squad.dataset;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SheetDataFinder {

    private static final  LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    public static List<Object> getData(final String text) {
        try {
            final Sentence sentence = new Sentence(text);

            final Tree parent = sentence.parse();
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

            final String subject = getSubject(sentence);

            // subject, object, relation
//            System.out.println("[whType=" + whType + ", whWord=" + whWord + ", whTargetWord=" + whTargetWord + ", whTargetType=" + whTargetType + ", subject=" + subject + "]");

            final List<Object> data = new ArrayList<>();
            data.add(text);
            data.add(whType);
            data.add(whWord);
            data.add(whTargetType);
            data.add(whTargetWord);
            data.add(subject);
            return data;
        } catch (final Exception e) {
            System.out.println("Failed: " + text);
        }
        return new ArrayList<>();
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
            return parent.value().startsWith("WH") && !child.value().startsWith("WH")? parent : findWHTree(child);
        return null;
    }

    // TODO: make this pretty
    private static String getSubject(final Sentence sentence) {

        final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        final List<CoreLabel> words = tokenizerFactory.getTokenizer(new StringReader(sentence.text())).tokenize();
        final Tree parseTree = parser.parseTree(words);

        final PennTreebankLanguagePack languagePack = new PennTreebankLanguagePack();
        final GrammaticalStructureFactory structureFactory = languagePack.grammaticalStructureFactory();
        final GrammaticalStructure structure = structureFactory.newGrammaticalStructure(parseTree);

        final List<TypedDependency> tdl = structure.typedDependenciesCCprocessed();

        int predicateIndex = 0;
        // Find predicate
        for (int indx = 0; indx < tdl.size(); indx++)
            if (tdl.get(indx).reln().toString().equals("root"))
                predicateIndex = tdl.get(indx).dep().index();
        // Find subject
        String subject = "";
        String rootSubject = "";
        for (int indx = tdl.size() - 1; indx >= 0; indx--) {
            final TypedDependency dependency = tdl.get(indx);
            if (dependency.reln().toString().contains("subj") && (tdl.get(indx).gov().index() == predicateIndex)) {
                rootSubject = dependency.dep().word();
                subject = dependency.dep().word();
            }
            if (dependency.reln().toString().contains("compound") && dependency.gov().word().equals(rootSubject)) {
                subject = dependency.dep().word() + " " + subject;
            }
        }
        return subject;
    }
}
