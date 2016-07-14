package com.gmail.rabrg96.squad.dataset;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QAQCSheet {

    public static void main(final String[] args) throws Exception {
        final Map<String, Map<String, List<String>>> parentMap = new HashMap<>();
        final List<String> lines = Files.readAllLines(Paths.get("./res/train_1000.label.txt"), Charset.forName("Cp1252"));
        for (final String line : lines) {
            final String[] classesSplit = line.substring(0, line.indexOf(' ')).split(":");
            final String parentClass = classesSplit[0];
            final String childClass = classesSplit[1];
            final String sentence = line.substring(line.indexOf(' ') + 1);

            final Map<String, List<String>> childMap = parentMap.getOrDefault(parentClass, new HashMap<>());
            final List<String> sentences = childMap.getOrDefault(childClass, new ArrayList<>());
            sentences.add(sentence);

            childMap.putIfAbsent(childClass, sentences);
            parentMap.putIfAbsent(parentClass, childMap);
        }

        for (final Map.Entry<String, Map<String, List<String>>> parentEntry : parentMap.entrySet()) {
            for (final Map.Entry<String, List<String>> childEntry : parentEntry.getValue().entrySet()) {
                System.out.println(parentEntry.getKey() + "." + childEntry.getKey());
                for (final String sentence : childEntry.getValue()) {
                    System.out.println(sentence);
                }
            }
        }
    }
}
