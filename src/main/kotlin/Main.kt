import io.github.oshai.kotlinlogging.KotlinLogging
import service.NoteService
import model.Note
import utils.readNextBoolean
import utils.readNextInt
import utils.readNextLine

private val logger = KotlinLogging.logger {}

val noteService = NoteService()

fun main() {
    logger.info { "Notes App Started" }
    runMenu()
    logger.info { "Notes App Exiting" }
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
        > """.trimMargin(">"))
    return readNextInt("==>> ")
}

fun runMenu() {
    var input : Int
    do {
        input = mainMenu()
        when (input) {
            1 -> addNote()
            2 -> listNotes()
            3 -> updateNote()
            4 -> deleteNote()
            0 -> println("Exiting App")
            else -> println("Invalid Option")
        }
    } while (input != 0)
}

fun addNote(){
    val title =  readNextLine("Title: ")

    val body = readNextLine("Body: ")

    val priority = readNextInt("Priority (1-5): ")

    val category = readNextLine("Category: ")

    val isArchived = readNextBoolean("Is archived (y/n): ")

    noteService.addNote(
        Note(0, title, body, priority, category, isArchived)
    )

    println("Note added")
}

fun listNotes(){
    noteService.getNotes().forEach {println(it)}
}

fun deleteNote() {
    val id = readNextInt("ID: ")

    if (noteService.deleteNote(id)) {
        println("Deleted")
    } else {
        println("Not found")
    }
}

fun updateNote() {
    val id = readNextInt("ID: ")

    val title = readNextLine("Title: ")

    val body = readNextLine("Body: ")

    val priority = readNextInt("Priority (1-5): ")

    val category = readNextLine("Category: ")

    val isArchived = readNextBoolean("Is archived (y/n): ")

    val updated = Note(id, title, body, priority, category, isArchived)

    if (noteService.updateNote(id, updated)) {
        println("Updated")
    } else {
        println("Not Found")
    }
}