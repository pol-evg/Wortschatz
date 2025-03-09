package polikarpov.evgenii.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class Loader {

    public static Language loadDefaultLanguageAndSource() {

        Language[] languages = Optional.of(Loader.class)
                .map(c -> c.getResourceAsStream("/Wortschatz.json"))
                .map(is -> {
                    try {
                        return new ObjectMapper().readValue(is, Language[].class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow();

        Language defaultLanguage = Arrays.stream(languages)
                .filter(Language::isPreferred)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No default language detected"));
        defaultLanguage.printStatistics();

        return defaultLanguage;
    }
}
