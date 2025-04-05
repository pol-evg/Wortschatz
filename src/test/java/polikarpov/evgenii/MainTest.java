package polikarpov.evgenii;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    public static Stream<Arguments> parseWordFromInputStringData() {
        return Stream.of(
                Arguments.of("""
                        a # # #""", false),
                Arguments.of("""
                        a #abc #  #""", false),
                Arguments.of("""
                        a #abc # def #\s""", true),
                Arguments.of("""
                        a f # abc # def #""", true),
                Arguments.of("""
                        a m # abc #def # fhgfj""", true),
                Arguments.of("""
                        a #abc#def#dgf""", true)
        );
    }

    @ParameterizedTest
    @MethodSource("parseWordFromInputStringData")
    void parseWordFromInputStringTest(String cmd, boolean successfullyParsed) {

        if (successfullyParsed) {
            assertDoesNotThrow(() -> Main.parseWordFromInputString(cmd));
        }
        else {
            assertThrows(RuntimeException.class, () -> Main.parseWordFromInputString(cmd));
        }
    }
}