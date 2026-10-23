package utils

val statusList = setOf("Complete", "Later", "Doing")

fun isValidStatus(statusToCheck: String?): Boolean{
    for (status in statusList) {
        if (status.equals(statusToCheck, ignoreCase = true)) {
            return true
        }
    }
    return false
}