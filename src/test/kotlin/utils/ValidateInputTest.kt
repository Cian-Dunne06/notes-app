package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidateInputTest {
    @Test
    fun validRangeTest() {
        assertTrue(validRange(5, 1, 10))
        assertFalse(validRange(0, 1, 10))
        assertFalse(validRange(11, 1, 10))
    }
}
