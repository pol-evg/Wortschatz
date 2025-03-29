package polikarpov.evgenii;

import polikarpov.evgenii.data.Language;
import polikarpov.evgenii.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern newWordPattern = Pattern.compile("\"([^\"]*)\"");

    public static void main(String[] args) {

        Language defaultSource = Language.loadDefaultLanguageAndSource();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printCommands();
            String cmd = scanner.nextLine();
            if (Objects.equals(cmd, "q")) return;
            if (cmd == null || cmd.isBlank()) continue;

            if (cmd.equals("p")) {
                defaultSource.printDefaultSource();
                continue;
            }

            if (cmd.equals("w")) {
                defaultSource.write();
                continue;
            }

            if (cmd.startsWith("a ")) {
                try {
                    Word w = parseWordFromInputString(cmd);
                    defaultSource.checkUniqueAndAddToDefaultSource(w);
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

    }

    private static void printCommands() {
        System.out.println("Commands available:");
        System.out.println("a \"mfn\" \"word\" \"translate\" \"example\" : adds new word to the default source");
        System.out.println("p : prints the default source");
        System.out.println("q : quits the program");
        System.out.println("w : writes the default source in JSON format");
    }

    static Word parseWordFromInputString(String cmd) {
        Matcher m = newWordPattern.matcher(cmd);
        List<String> values = new ArrayList<>();
        while (m.find()) {
            values.add(m.group(1));
        }
        if (values.size() != 4) {
            throw new RuntimeException("Please fill all parameters of new word");
        }

        String mfn = values.get(0);
        String value = values.get(1);
        String translation = values.get(2);
        String example = values.get(3);
        return Word.ofConsoleInput(mfn, value, translation, example);
    }
}