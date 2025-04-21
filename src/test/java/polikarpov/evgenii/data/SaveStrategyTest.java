package polikarpov.evgenii.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SaveStrategyTest {

    @Test
    void testAlphabeticalStrategy() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        List<Word> mockWords = Arrays.asList(
                primitiveWord("cherry"),
                primitiveWord("apple"),
                primitiveWord("banana")
        );
        mockSource.setWords(mockWords);

        Map<String, List<Word>> expected = new HashMap<>();
        expected.put("C", List.of(primitiveWord("cherry")));
        expected.put("A", List.of(primitiveWord("apple")));
        expected.put("B", List.of(primitiveWord("banana")));

        // Test Alphabetical Strategy
        var alphabetical = new SaveStrategy.Alphabetical();
        var result = alphabetical.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expected, result, "Alphabetical strategy should return words in alphabetical order.");
    }

    @Test
    void testLevenshteinStrategyOneGroup() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        List<Word> mockWords = Arrays.asList(
                primitiveWord("butterfly"),
                primitiveWord("cat"),
                primitiveWord("hamster")
        );
        mockSource.setWords(mockWords);

        List<Word> expectedWords = Arrays.asList(
                primitiveWord("butterfly"),
                primitiveWord("hamster"),
                primitiveWord("cat")
        );

        var expected = new HashMap<>();
        expected.put("Group1", expectedWords);

        // Test Levenshtein Strategy
        var levenshtein = new SaveStrategy.Levenshtein();
        var result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expected, result, "Levenshtein strategy should return all words.");
    }

    @Test
    void testLevenshteinStrategyTwoGroups() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        List<Word> mockWords = Arrays.asList(
                primitiveWord("butterfly"),
                primitiveWord("cat"),
                primitiveWord("rabbit"),
                primitiveWord("rat"),
                primitiveWord("dog"),
                primitiveWord("mouse"),
                primitiveWord("hypo"),
                primitiveWord("horse"),
                primitiveWord("bee"),
                primitiveWord("flower"),
                primitiveWord("lion"),
                primitiveWord("this is a very another string to trigger re-grouping")
        );
        mockSource.setWords(mockWords);

        var expected = new HashMap<>();
        expected.put("Group1", Arrays.asList(
                primitiveWord("rabbit"),
                primitiveWord("rat"),
                primitiveWord("cat"),
                primitiveWord("dog"),
                primitiveWord("bee"),
                primitiveWord("mouse"),
                primitiveWord("horse"),
                primitiveWord("hypo"),
                primitiveWord("lion"),
                primitiveWord("flower"),
                primitiveWord("butterfly")
        ));
        expected.put("Group2", List.of(
                primitiveWord("this is a very another string to trigger re-grouping")));

        // Test Levenshtein Strategy
        var levenshtein = new SaveStrategy.Levenshtein();
        var result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expected, result, "Levenshtein strategy should return all words.");
    }

    @Test
    void testLevenshteinStrategyEmptyWords() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        mockSource.setWords(List.of());

        Map<String, List<Word>> expected = new HashMap<>();

        // Test Levenshtein Strategy
        var levenshtein = new SaveStrategy.Levenshtein();
        var result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expected, result, "Levenshtein strategy should return an empty list for no input words.");
    }

    @Test
    void testLevenshteinStrategySingleWord() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        List<Word> mockWords = List.of(primitiveWord("solo"));
        mockSource.setWords(mockWords);

        Map<String, List<Word>> expected = new HashMap<>();
        expected.put("S", List.of(primitiveWord("solo")));

        // Test Levenshtein Strategy
        var levenshtein = new SaveStrategy.Levenshtein();
        var result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expected, result, "Levenshtein strategy should return the single word unchanged.");
    }

    private static Word primitiveWord(String value) {
        return new Word(Word.MFN.UNDEFINED, value, "", "", "");
    }
}