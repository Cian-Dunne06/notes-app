import io.github.oshai.kotlinlogging.KotlinLogging
import service.NoteService
import model.Note
import persistence.JSONSerializer
import persistence.XMLSerializer
import utils.readNextBoolean
import utils.readNextInt
import utils.readNextLine
import java.io.File

private val logger = KotlinLogging.logger {}

//val noteService = NoteService(XMLSerializer(File("notes.xml")))
val noteService = NoteService(JSONSerializer(File("notes.json")))

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
        >|  2) List Notes             |
        >|  3) Update a Note          |
        >|  4) Delete a Note          |
        >|  5) Archive a Note         |
        >| 20) Save Notes             |
        >| 21) Load Notes             |
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
            2 -> runListMenu()
            3 -> updateNote()
            4 -> deleteNote()
            5 -> archiveNote()
            20 -> save()
            21 -> load()
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

fun listNotes() {
    val listNotes = noteService.getNotes()
    if (listNotes.isEmpty()) {
        println("No notes stored")
    } else {
        listNotes.forEach { println(it) }
        noteService.numberOfNotes()
    }
}

fun listActiveNotes() {
    val activeNotes = noteService.getActiveNotes()
    if (activeNotes.isEmpty()) {
        println("No active notes")
    } else {
        activeNotes.forEach { println(it) }
        println("There are ${activeNotes.size} active notes")
        noteService.numberOfActiveNotes()
    }
}

fun listArchivedNotes() {
    val archivedNotes = noteService.getArchivedNotes()
    if (archivedNotes.isEmpty()) {
        println("No archived notes")
    } else {
        archivedNotes.forEach { println(it) }
        noteService.numberOfArchivedNotes()
    }
}

fun listNotesByCategory() {
    val category = readNextLine("Enter category: ")
    val notesByCategory = noteService.getNotesByCategory(category)

    if (notesByCategory.isEmpty()) {
        println("No notes found in category")
    } else {
        notesByCategory.forEach { println(it) }
    }
}

fun listNotesByPriority() {
    val priority = readNextInt("Priority: ")
    val notesByPriority = noteService.getNotesByPriority(priority)
    if (notesByPriority.isEmpty()) {
        println("No notes with priority $priority")
    } else {
        notesByPriority.forEach { println(it) }
    }
}

fun listNoteMenu (): Int {
    print("""
        >------------------------------
        >|      List Notes            |  
        >------------------------------
        >|  LIST NOTE MENU            |
        >|  1) List All Notes         |
        >|  2) List Archived Notes    |
        >|  3) List Active Notes      |
        >|  4) List Notes by Category |
        >|  5) List Notes by Priority |
        >>-----------------------------
        >|  0) Go Back                |
        >------------------------------
    """.trimMargin(">"))
    return readNextInt("==>> ")
}

fun runListMenu() {
    var input: Int
    do {
        input = listNoteMenu()
        when (input) {
            1 -> listNotes()
            2 -> listArchivedNotes()
            3 -> listActiveNotes()
            4 -> listNotesByCategory()
            5 -> listNotesByPriority()
            0 -> println("Returning to Main Menu...") // Option 0 breaks the loop and returns naturally
            else -> println("Invalid Option")
        }
    } while (input != 0)
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

fun save() {
    try {
        noteService.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteService.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun archiveNote() {
    noteService.getNotes().forEach {if (!it.isArchived) println(it)}

    val id = readNextInt("ID to be archived: ")

    if (noteService.archiveNote(id)) {
        println("Archived")
    } else {
        println("Not found")
    }
}