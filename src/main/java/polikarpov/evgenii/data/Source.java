package polikarpov.evgenii.data;

import lombok.Data;

import java.util.List;

@Data
public class Source {

    private String source;

    private boolean preferred;

    private List<Word> words;
}
