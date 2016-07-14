package com.gmail.rabrg96.squad.dataset;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class QAQCSheet {

    // TODO: algorithm to automatically weight statistics
    public static void main(final String[] args) throws Exception {
        final Map<String, List<String>> dataset = loadDataset();
        final List<String> statistics = Files.readAllLines(Paths.get("./res/statistics-qaqc.tsv"));

        final double min = -1, max = 1, accur = 0.1;
        final long start = System.currentTimeMillis();
        double bestKeyWeight= 0, bestSetWeight = 0, bestAccuracy = 0;
        for (double keyWeight = min; keyWeight < max; keyWeight += accur) {
            for (double setWeight = min; setWeight < max; setWeight += accur) {
                int correct = 0, total = 0;
                for (final Map.Entry<String, List<String>> entry : dataset.entrySet()) {
                    for (final String sentence : entry.getValue()) {
                        final Map<String, Double> probability = getProbabilityStatistics(statistics, sentence, keyWeight, setWeight);
                        if (!probability.isEmpty() && probability.entrySet().iterator().next().getKey().equals(entry.getKey())) {
                            correct++;
                        } else {
                            System.out.println(sentence);
                        }
                        total++;
                    }
                }
                final double accuracy = ((double) correct) / total;
                if (accuracy > bestAccuracy) {
                    bestKeyWeight = keyWeight;
                    bestSetWeight = setWeight;
                    bestAccuracy = accuracy;
                }
            }
        }
        System.out.println("best accuracy: " + bestAccuracy + " with key weight: " + bestKeyWeight + " and set weight: " + bestSetWeight + " detected in: " + (System.currentTimeMillis() - start) + "ms" + " with min weight: " + min + " and max weight: " + max + " with accur of " + accur);
    }

    // TODO: flatten input usage into <parent.child> key map
    private static Map<String, List<String>> loadDataset() throws IOException {
        final Map<String, List<String>> parentMap = new HashMap<>();
        final List<String> lines = Files.readAllLines(Paths.get("./res/train_1000.label.txt"), Charset.forName("Cp1252"));
        for (final String line : lines) {
            final String[] classesSplit = line.substring(0, line.indexOf(' ')).split(":");
            final String parentClass = classesSplit[0];
            final String childClass = classesSplit[1];
            final String sentence = line.substring(line.indexOf(' ') + 1);

            final List<String> sentences = parentMap.getOrDefault(childClass, new ArrayList<>());
            sentences.add(sentence);

            parentMap.putIfAbsent(parentClass + "." + childClass, sentences);
        }
        return parentMap;
    }

    private static Map<String, Integer> countSentences(final Map<String, Map<String, List<String>>> parentMap) {
        final Map<String, Integer> sentenceCount = new HashMap<>();
        for (final Map.Entry<String, Map<String, List<String>>> parentEntry : parentMap.entrySet()) {
            for (final Map.Entry<String, List<String>> childEntry : parentEntry.getValue().entrySet()) {
                sentenceCount.put(parentEntry.getKey() + "." + childEntry.getKey(), childEntry.getValue().size());
            }
        }
        return sentenceCount;
    }

    private static void inputStatistics(final Map<String, Map<String, List<String>>> parentMap,
                                        final Map<String, Integer> sentenceCount)  throws IOException {
        int counter = 0;
        final Map<String, Integer> result = new HashMap<>();
        final List<String> previous = new ArrayList<>();
        try (final Scanner scanner = new Scanner(System.in);
             final FileWriter writer = new FileWriter("./res/statistics-qaqc.tsv", true)) {
            String line;
            while ((line = scanner.nextLine()) != null) {
                if (previous.contains(line))
                    continue;
                previous.add(line);
                final String mode = line.substring(0, line.indexOf(':'));
                final String term =  line.substring(line.indexOf(':') + 1);
                for (final Map.Entry<String, Map<String, List<String>>> parentEntry : parentMap.entrySet()) {
                    for (final Map.Entry<String, List<String>> childEntry : parentEntry.getValue().entrySet()) {
                        final String key = parentEntry.getKey() + "." + childEntry.getKey();
                        for (String sentence : childEntry.getValue()) {
                            sentence = sentence.toLowerCase();
                            if ("start".equals(mode) && sentence.startsWith(term)
                                    || "end".equals(mode) && sentence.endsWith(term)
                                    || "contain".equals(mode) && sentence.contains(term)) {
                                result.put(key, result.getOrDefault(key, 0) + 1);
                                counter++;
                            }
                        }
                    }
                }
                for (final Map.Entry<String, Integer> entry : result.entrySet()) {
                    writer.write(entry.getKey() + "\t"
                            + truncate(entry.getValue() / (double) sentenceCount.get(entry.getKey())) + "\t"
                            + truncate(entry.getValue() / (double) counter) + "\t" + mode + "\t" + term + "\n");
                }
                writer.flush();
                counter = 0;
                result.clear();
            }
        }
    }

    private static double truncate(final double i) {
        return Math.floor(i * 1000) / 1000;
    }

    private static Map<String, Double> getProbabilityStatistics(final List<String> statistics, String sentence, final double keyWeight, final double setWeight) {
        sentence = sentence.toLowerCase();

        final Map<String, Double> probability = new HashMap<>();
        for (final String statistic : statistics) {
            final String[] split = statistic.split("\t");
            final String key = split[0];
            final double keyProbability = Double.parseDouble(split[1]);
            final double setProbability = Double.parseDouble(split[2]);
            final String mode = split[3];
            final String term = split[4];
            if ("start".equals(mode) && sentence.startsWith(term)
                    || "end".equals(mode) && sentence.endsWith(term)
                    || "contain".equals(mode) && sentence.contains(term)) {
                probability.put(key, probability.getOrDefault(key, 0D) + (keyProbability * keyWeight) + (setProbability * setWeight));
            }
        }
        return sortByValue(probability);
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue()) * -1;
            }
        });

        final Map<K, V> result = new LinkedHashMap<K, V>();
        for (final Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
