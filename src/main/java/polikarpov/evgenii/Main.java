package polikarpov.evgenii;

import polikarpov.evgenii.data.Language;
import polikarpov.evgenii.data.Word;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static final String MFN = "mfn";
    public static final String WRD = "wrd";
    public static final String FRM = "frm";
    public static final String TRN = "trn";
    public static final String XMP = "xmp";

    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final Pattern aPattern =
            Pattern.compile("^a\\s+(?<" + MFN + ">[mfn])?\\s*#\\s*(?<" +
                    WRD + ">[^#]+)\\s*#\\s*(?<" + FRM + ">[^#]+)?\\s*#\\s*(?<" +
                    TRN + ">[^#]+)\\s*#\\s*(?<" + XMP + ">[^#]+)?\\s*$");

    public static void main(String[] args) {

        Language language = Language.loadDefaultLanguageAndSource();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printCommands();
            String cmd = scanner.nextLine();
            if (Objects.equals(cmd, "q")) return;
            if (cmd == null || cmd.isBlank()) continue;

            if (cmd.equals("r")) {
                language.write();
                continue;
            }

            if (cmd.startsWith("a ")) {
                try {
                    Word w = parseWordFromInputString(cmd);
                    language.checkUniqueAndAddToDefaultSource(w);
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

    }

    private static void printCommands() {
        System.out.print(ANSI_PURPLE);
        System.out.println("Commands available:");
        System.out.println("a mfn? # word # forms? # translate # example? : adds new word to the default source");
        System.out.println("r : builds Readme.md");
        System.out.println("q : quits the program" + ANSI_RESET);
    }

    static Word parseWordFromInputString(String cmd) {
        Matcher m = aPattern.matcher(cmd);

        if (!m.matches()) {
            throw new RuntimeException("Please fill all parameters of new word");
        }

        return Word.ofConsoleInput(
                m.group(MFN), trim(m.group(WRD)), trim(m.group(FRM)), trim(m.group(TRN)), trim(m.group(XMP)));
    }

    static String trim(String s) {
        return Optional.ofNullable(s)
                .map(String::trim)
                .orElse("");
    }
}