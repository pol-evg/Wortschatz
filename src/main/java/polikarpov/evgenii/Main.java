package polikarpov.evgenii;

import polikarpov.evgenii.data.Language;
import polikarpov.evgenii.data.Word;

import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static final String MFN = "mfn";
    public static final String WRD = "wrd";
    public static final String TRN = "trn";
    public static final String XMP = "xmp";

    private static final Pattern aPattern =
            Pattern.compile("^a\\s+(?<" + MFN + ">m|f|n|m\\(f\\)|f\\(m\\))?\\s*#\\s*(?<" +
                    WRD + ">[^#]+)\\s*#\\s*(?<" + TRN + ">[^#]+)\\s*#\\s*(?<" + XMP + ">[^#]+)?\\s*$");

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
        System.out.println("Commands available:");
        System.out.println("a mfn? # word # translate # example? : adds new word to the default source");
        System.out.println("r : builds Readme.md");
        System.out.println("q : quits the program");
    }

    static Word parseWordFromInputString(String cmd) {
        Matcher m = aPattern.matcher(cmd);

        if (!m.matches()) {
            throw new RuntimeException("Please fill all parameters of new word");
        }

        return Word.ofConsoleInput(m.group(MFN), m.group(WRD), m.group(TRN), m.group(XMP));
    }
}