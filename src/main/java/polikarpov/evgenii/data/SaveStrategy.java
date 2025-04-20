package polikarpov.evgenii.data;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class SaveStrategy {

    private final String readmeFile;

    private final Function<Source, List<Word>> wordSupplier;

    protected SaveStrategy(String readmeFile, Function<Source, List<Word>> wordSupplier) {
        this.readmeFile = readmeFile;
        this.wordSupplier = wordSupplier;
    }

    public static class Alphabetical extends SaveStrategy {

        public Alphabetical() {
            super("./sorted-a.md", Alphabetical::map);
        }

        private static List<Word> map(Source source) {
            return source.getWords();
        }
    }

    public static class Levenshtein extends SaveStrategy {

        public Levenshtein() {
            super("./sorted-lvstn.md", Levenshtein::map);
        }

        private static List<Word> map(Source source) {
            List<Word> words = source.getWords();
            if (words.isEmpty() || words.size() == 1) {
                return words;
            }
            Pair<List<Word>, Integer> minDistanceSortedWords = getSortedWords(
                    words.getFirst(), getOthers(words.getFirst(), words));
            for (int i=1; i<words.size(); i++) {
                Word word = words.get(i);
                var sortedWords = getSortedWords(word, getOthers(word, words));
                if (sortedWords.getValue() < minDistanceSortedWords.getValue()) {
                    minDistanceSortedWords = sortedWords;
                }
            }
            return minDistanceSortedWords.getKey();
        }

        private static LinkedList<Word> getOthers(Word startWord, List<Word> words) {
            return words.stream()
                    .filter(w -> w.compareTo(startWord) != 0)
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        private static Pair<List<Word>, Integer> getSortedWords(Word startWord, List<Word> others) {
            if (startWord == null) {
                return null;
            }
            List<Word> sortedWords = new ArrayList<>(others.size() + 1);
            sortedWords.add(startWord);
            Queue<Word> unsortedWords = new LinkedList<>(others);
            AtomicReference<Word> word = new AtomicReference<>(startWord);
            AtomicReference<Integer> sumDistances = new AtomicReference<>(0);
            while (!unsortedWords.isEmpty()) {
                AtomicInteger minDistance = new AtomicInteger(Integer.MAX_VALUE);
                AtomicReference<Word> closestWordRef = new AtomicReference<>(null);
                AtomicReference<Word> finalWord = word;
                unsortedWords.forEach(word1 -> {
                    if (finalWord.get().compareTo(word1) == 0) return;
                    LevenshteinDistance levenshtein = new LevenshteinDistance();
                    int distance = levenshtein.apply(finalWord.get().getValue(), word1.getValue());
                    if (distance < minDistance.get()) {
                        minDistance.set(distance);
                        closestWordRef.set(word1);
                    }
                });
                Optional.of(closestWordRef)
                        .map(AtomicReference::get)
                        .filter(unsortedWords::remove)
                        .ifPresentOrElse(sortedWords::add,
                                () -> {
                                    throw new RuntimeException(
                                            String.format("Couldn't move unsorted word '%s' to the sorted list", closestWordRef.get()));
                                });
                word = closestWordRef;
                sumDistances.getAndUpdate(value -> value + minDistance.get());
            }
            return Pair.of(sortedWords, sumDistances.get());
        }
    }
}
