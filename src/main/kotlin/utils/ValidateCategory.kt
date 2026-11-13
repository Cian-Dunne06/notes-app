package utils

val categories = setOf("Home", "Work", "College", "Social")

fun isValidCategory(categoryToCheck: String?): Boolean {
    for (category in categories) {
        if (category.equals(categoryToCheck, ignoreCase = true)) {
            return true
        }
    }
    return false
}
