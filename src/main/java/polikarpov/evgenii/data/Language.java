package polikarpov.evgenii.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Data
public class Language {

    public static final String WORTSCHATZ_JSON = "Wortschatz.json";
    private String language;

    private Source[] sources;

    public static Language loadDefaultLanguageAndSource() {

        return Optional.of(Language.class)
                .map(c -> c.getResourceAsStream("/" + WORTSCHATZ_JSON))
                .map(is -> {
                    try {
                        return new ObjectMapper().readValue(is, Language.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow();
    }

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

    public void write() {
        try (var executor = Executors.newSingleThreadExecutor()) {
            executor.submit(() -> {
                ObjectMapper objectMapper = new ObjectMapper();
                System.out.println("Saving into " + WORTSCHATZ_JSON);
                findDictionaryFile(Path.of(".").toFile(), file -> {
                    try {
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
                        System.out.println("Saved");
                        executor.shutdownNow();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    objectMapper.writeValue(System.out, this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void findDictionaryFile(File root, Consumer<File> onSuccess) {
        Optional.ofNullable(root)
                .map(File::listFiles)
                .map(Arrays::stream)
                .ifPresent(s -> s.forEach(file -> {
                    findDictionaryFile(file, onSuccess);
                    String fileName = file.getName();
                    if (fileName.equals(WORTSCHATZ_JSON)) {
                        onSuccess.accept(file);
                    }
                }));
    }

    private Source getDefaultSource() {
        return Arrays.stream(getSources())
                .filter(Source::isPreferred)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default source detected"));
    }

}
