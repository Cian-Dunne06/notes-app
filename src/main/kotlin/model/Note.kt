package model

data class Note(
    var id: Int = 0,
    var title: String = "",
    var body: String = "",
    var priority: Int = 0,
    var category: String = "",
    var isArchived: Boolean = false
)

{
    override fun toString(): String = """
        |($id) $title
        |Priority: $priority/5 | Category: $category | Archived: ${if (isArchived) " Yes " else " No "}
        |Body: $body
    """.trimMargin()
}
