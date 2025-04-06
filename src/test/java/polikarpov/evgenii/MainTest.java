package polikarpov.evgenii;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import polikarpov.evgenii.data.Word;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static polikarpov.evgenii.data.Word.MFN.*;

class MainTest {

    public static Stream<Arguments> parseWordFromInputStringData() {
        return Stream.of(
                Arguments.of("""
                        a # # #""", null),
                Arguments.of("""
                        a #abc #  #""", null),
                Arguments.of("""
                        a #abc # # def #\s""", new Word(UNDEFINED, "abc", "", "def", "")),
                Arguments.of("""
                        a f # abc # bca # def #""", new Word(FEMININE, "abc", "bca", "def", "")),
                Arguments.of("""
                        a m # abc # bca #def # fhgfj""", new Word(MASCULINE, "abc", "bca", "def", "fhgfj")),
                Arguments.of("""
                        a #abc##def#dgf""", new Word(UNDEFINED, "abc", "", "def", "dgf"))
        );
    }

    @ParameterizedTest
    @MethodSource("parseWordFromInputStringData")
    void parseWordFromInputStringTest(String cmd, Word expected) {

        if (expected != null) {
            assertEquals(expected, Main.parseWordFromInputString(cmd));
        }
        else {
            assertThrows(RuntimeException.class, () -> Main.parseWordFromInputString(cmd));
        }
    }
}