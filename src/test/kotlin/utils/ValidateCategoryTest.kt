package utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ValidateCategoryTest {

    @Test
    fun `Categories Return Full Categories Set` () {
        assertEquals(categories, setOf("Home", "Work", "College", "Social"))
    }

    @Test
    fun `isValidCategory true when category exists`() {
        assertTrue(isValidCategory("Home"))
        assertTrue(isValidCategory("work"))
        assertTrue(isValidCategory("COLLEGE"))
        assertTrue(isValidCategory("soCiAl"))
    }

    @Test
    fun `isValidCategory false when category does not exist`() {
        assertFalse(isValidCategory("invalid"))
    }
}