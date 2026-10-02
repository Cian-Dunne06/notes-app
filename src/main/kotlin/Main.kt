import service.NoteService
import model.Note

val noteService = NoteService()

fun main() {
    runMenu()
}

fun mainMenu(): Int {
    print("""
        >------------------------------
        >|      Note Keeper App       |  
        >------------------------------
        >| NOTE MENU                  |
        >|  1) Add a Note             |
        >|  2) List all Notes         |
        >|  3) Update a Note          |
        >|  4) Delete a Note          |
        >------------------------------
        >|  0) Exit                   |
        >------------------------------
        > ==>> """.trimMargin(">"))
    return readlnOrNull()?.toIntOrNull() ?: -1
}

fun runMenu() {
    var input : Int
    do {
        input = mainMenu()
        when (input) {
            1 -> addNote()
            2 -> listNotes()
            3 -> println("Update note will be coded here")
            4 -> println("Delete note will be coded here")
            0 -> println("Exiting App")
            else -> println("Invalid Option")
        }
    } while (input != 0)
}

fun addNote(){
    print("Title: ")
    val title = readlnOrNull() ?: ""

    print("Body: ")
    val body = readlnOrNull() ?: ""

    print("Priority (1-5): ")
    val priority = readlnOrNull()?.toIntOrNull() ?: 1

    print("Category: ")
    val category = readlnOrNull() ?: ""

    noteService.addNote(
        Note(0, title, body, priority, category, false)
    )

    println("Note added")
}

fun listNotes(){
    println(noteService.getNotes())
}