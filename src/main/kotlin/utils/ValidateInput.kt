package utils

fun readValidCategory(prompt: String): String {
    var input = readNextLine(prompt)
    do {
        if (isValidCategory(input)) {
            return input
        } else {
            input = readNextLine("Invalid category: $input. Please enter a valid category: ")
        }
    } while (true)
}

fun readValidPriority(prompt: String): Int {
    var input = readNextInt(prompt)
    do {
        if (validRange(input, 1, 5)) {
            return input
        } else {
            input = readNextInt("Invalid priority: $input. Please enter a valid priority (1-5): ")
        }
    } while (true)
}

fun validRange(
    input: Int,
    i2: Int,
    i3: Int,
): Boolean = input >= i2 && input <= i3
