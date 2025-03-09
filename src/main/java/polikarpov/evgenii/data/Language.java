package polikarpov.evgenii.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.Arrays;

@Data
public class Language {

    private String language;

    private boolean preferred;

    private Source[] sources;

    public void checkUniqueAndAddToDefaultSource(Word wordToAdd) {
        for (Source source : sources) {
            for (Word word: source.getWords()) {
                if (word.compareTo(wordToAdd) == 0) {
                    throw new RuntimeException(String.format("%s already exists in source '%s'", wordToAdd.getValue(), source.getSource()));
                }
            }
        }

        Arrays.stream(sources)
                .filter(Source::isPreferred)
                .findFirst()
                .map(Source::getWords)
                .ifPresent(words -> {
                    words.add(wordToAdd);
                    words.sort(Word::compareTo);
                });

        printStatistics();
    }

    public void printStatistics() {
        Source defaultSource = getDefaultSource();

        System.out.printf("Default %s source '%s' contains %d words%n",
                getLanguage(), defaultSource.getSource(), defaultSource.getWords().size());
    }

    public void printDefaultSource() {
        Source defaultSource = getDefaultSource();

        for (Word word: defaultSource.getWords()) {
            System.out.println(word);
        }
    }

    public void writeDefaultSource() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(System.out, this);
    }

    private Source getDefaultSource() {
        return Arrays.stream(getSources())
                .filter(Source::isPreferred)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default source detected"));
    }

}
