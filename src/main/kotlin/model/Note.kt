package model

data class Note(
    var id: Int,
    var title: String,
    var body: String,
    var priority: Int,
    var category: String,
    var isArchived: Boolean
)
{
    override fun toString(): String = """
        |($id) $title
        |Priority: $priority/5 | Category: $category | Archived: ${if (isArchived) " Yes " else " No "}
        |Body: $body
    """.trimMargin()
}
