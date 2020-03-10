package info.karlovskiy.yaus.util;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62Test {

    @DisplayName("Positive encode <-> decode cycle")
    @ParameterizedTest
    @CsvSource({
            "0,0",
            "1,1",
            "9,9",
            "10,a",
            "35,z",
            "36,A",
            "61,Z",
            "364,5S",
            Long.MAX_VALUE + ",aZl8N0y58M7"
    })
    void encodeDecodePositive(long id, String value) {
        assertEquals(value, Base62.encode(id));
        assertEquals(id, Base62.decode(value));
    }

    @DisplayName("Encode incorrect id")
    @ParameterizedTest
    @ValueSource(longs = {-1L, Long.MIN_VALUE})
    void encodeIncorrectId(long id) {
        val exception = assertThrows(IllegalArgumentException.class, () -> Base62.encode(id));
        assertEquals("Incorrect id for encode", exception.getMessage());
    }

    @DisplayName("Decode incorrect string")
    @ParameterizedTest
    @CsvSource({
            ",Empty value for decode",
            "'',Empty value for decode",
            "-a-,Incorrect value for decode",
    })
    void decodeIncorrectString(String value, String expectedMessage) {
        val exception = assertThrows(IllegalArgumentException.class, () -> Base62.decode(value));
        assertEquals(expectedMessage, exception.getMessage());
    }

}
