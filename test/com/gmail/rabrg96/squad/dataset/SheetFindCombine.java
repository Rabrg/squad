package com.gmail.rabrg96.squad.dataset;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetFindCombine {

    public static void main(final String[] args) throws IOException {
        SheetDataFinder.getData("What team did baseball 's St. Louis Browns become ?");
//        final Map<String, List<String>> dataset = loadDataset();
//        int i = 1;
//        for (final Map.Entry<String, List<String>> entry : dataset.entrySet()) {
//            for (final String sentence : entry.getValue()) {
//                final List<Object> data = SheetDataFinder.getData(sentence);
//                data.add(entry.getKey());
////                SheetsQuickstart.setValue("Sheet1", "A" + i++, String.valueOf((char) ('B' + data.size())), data);
//            }
//        }
    }

    private static Map<String, List<String>> loadDataset() throws IOException {
        final Map<String, List<String>> parentMap = new HashMap<>();
        final List<String> lines = Files.readAllLines(Paths.get("./res/train_1000.label.txt"), Charset.forName("Cp1252"));
        for (final String line : lines) {
            final String[] classesSplit = line.substring(0, line.indexOf(' ')).split(":");
            final String parentClass = classesSplit[0];
            final String childClass = classesSplit[1];
            final String sentence = line.substring(line.indexOf(' ') + 1);

            final List<String> sentences = parentMap.getOrDefault(parentClass + "." + childClass, new ArrayList<>());
            sentences.add(sentence.toLowerCase());
            parentMap.putIfAbsent(parentClass + "." + childClass, sentences);
        }
        return parentMap;
    }
}
