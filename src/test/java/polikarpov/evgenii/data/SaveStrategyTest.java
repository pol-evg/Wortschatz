package polikarpov.evgenii.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        // Test Alphabetical Strategy
        SaveStrategy.Alphabetical alphabetical = new SaveStrategy.Alphabetical();
        List<Word> result = alphabetical.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(mockWords, result, "Alphabetical strategy should return words in alphabetical order.");
    }

    @Test
    void testLevenshteinStrategy() {
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

        // Test Levenshtein Strategy
        SaveStrategy.Levenshtein levenshtein = new SaveStrategy.Levenshtein();
        List<Word> result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(expectedWords, result, "Levenshtein strategy should return all words.");
    }

    @Test
    void testLevenshteinStrategyEmptyWords() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        mockSource.setWords(List.of());

        // Test Levenshtein Strategy
        SaveStrategy.Levenshtein levenshtein = new SaveStrategy.Levenshtein();
        List<Word> result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Levenshtein strategy should return an empty list for no input words.");
    }

    @Test
    void testLevenshteinStrategySingleWord() {
        // Mock Source
        Source mockSource = new Source();

        // Mock implementation for getWords()
        List<Word> mockWords = List.of(primitiveWord("solo"));
        mockSource.setWords(mockWords);

        // Test Levenshtein Strategy
        SaveStrategy.Levenshtein levenshtein = new SaveStrategy.Levenshtein();
        List<Word> result = levenshtein.getWordSupplier().apply(mockSource);

        // Validate the result
        assertNotNull(result);
        assertEquals(mockWords, result, "Levenshtein strategy should return the single word unchanged.");
    }

    private static Word primitiveWord(String value) {
        return new Word(Word.MFN.UNDEFINED, value, "", "", "");
    }
}