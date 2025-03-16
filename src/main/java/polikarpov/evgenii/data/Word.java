package polikarpov.evgenii.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word implements Comparable<Word> {

    @Override
    public int compareTo(Word o) {
        return this.value.compareToIgnoreCase(o.value);
    }

    public enum MFN {

        UNDEFINED(""),
        MASCULINE("m"),
        MASCULINE_AND_FEMININE("m(f)"),
        FEMININE("f"),
        FEMININE_AND_MASCULINE("f(m)"),
        NEUTRAL("n")
        ;

        private final String value;

        MFN(String value) {
            this.value = value;
        }

        @JsonCreator
        public static MFN forValue(String value) {
            if (value == null || value.isEmpty()) return UNDEFINED;
            return switch (value) {
                case "m" -> MASCULINE;
                case "m(f)" -> MASCULINE_AND_FEMININE;
                case "f" -> FEMININE;
                case "f(m)" -> FEMININE_AND_MASCULINE;
                case "n" -> NEUTRAL;
                default -> UNDEFINED;
            };
        }

        @JsonValue
        public String toValue() {
            return value;
        }

    }

    private MFN mfn;

    @JsonProperty("wrd")
    private String value;

    @JsonProperty("trn")
    private String translation;

    @JsonProperty("xmp")
    private String example;

    public static Word ofConsoleInput(String mfn, String value, String translation, String example) {
        if (value == null || value.isBlank() || translation == null || translation.isBlank()) {
            throw new RuntimeException("Value and translation can not be blank");
        }
        return new Word(MFN.forValue(mfn), value, translation, example);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (mfn) {
            case FEMININE -> stringBuilder.append("die ");
            case MASCULINE -> stringBuilder.append("der ");
            case FEMININE_AND_MASCULINE -> stringBuilder.append("die(der) ");
            case MASCULINE_AND_FEMININE -> stringBuilder.append("der(die) ");
            case NEUTRAL -> stringBuilder.append("das ");
        }
        stringBuilder.append(value);
        stringBuilder.append(": ");
        stringBuilder.append(translation);
        if (example != null && !example.isBlank()) {
            stringBuilder.append(" [");
            stringBuilder.append(example);
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }
}
