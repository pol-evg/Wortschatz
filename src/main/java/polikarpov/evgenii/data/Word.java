package polikarpov.evgenii.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Word implements Comparable<Word> {

    @Override
    public int compareTo(Word o) {
        return this.value.compareToIgnoreCase(o.value);
    }

    public enum MFN {

        UNDEFINED(""),
        MASCULINE("m"),
        FEMININE("f"),
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
                case "f" -> FEMININE;
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

    @JsonProperty("frm")
    private String forms;

    @JsonProperty("trn")
    private String translation;

    @JsonProperty("xmp")
    private String example;

    public static Word ofConsoleInput(String mfn, String value, String forms, String translation, String example) {
        if (value == null || value.isBlank() || translation == null || translation.isBlank()) {
            throw new RuntimeException("Value and translation can not be blank");
        }
        return new Word(MFN.forValue(mfn), value, forms, translation, example);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (mfn) {
            case FEMININE -> stringBuilder.append("die ");
            case MASCULINE -> stringBuilder.append("der ");
            case NEUTRAL -> stringBuilder.append("das ");
        }
        stringBuilder.append(value);
        if (StringUtils.isNotBlank(forms)) {
            stringBuilder.append(" (");
            stringBuilder.append(forms);
            stringBuilder.append(')');
        }
        stringBuilder.append(": ");
        stringBuilder.append(translation);
        if (example != null && !example.isBlank()) {
            stringBuilder.append(" [");
            stringBuilder.append(example);
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    public String toMarkdown() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('|');
        switch (mfn) {
            case FEMININE -> stringBuilder.append("die ");
            case MASCULINE -> stringBuilder.append("der ");
            case NEUTRAL -> stringBuilder.append("das ");
        }
        stringBuilder.append(value);
        if (StringUtils.isNotBlank(forms)) {
            stringBuilder.append(" (");
            stringBuilder.append(forms);
            stringBuilder.append(')');
        }
        stringBuilder.append('|');
        stringBuilder.append(translation);
        stringBuilder.append('|');
        if (example != null && !example.isBlank()) {
            stringBuilder.append(example);
        } else {
            stringBuilder.append(' ');
        }
        stringBuilder.append('|');
        return stringBuilder.toString();
    }
}
