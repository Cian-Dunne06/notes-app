import io.github.oshai.kotlinlogging.KotlinLogging
import service.NoteService
import model.Note
import model.Item
import persistence.JSONSerializer
import service.ItemService
import service.NoteItemService
import utils.categories
import utils.isValidCategory
import utils.isValidStatus
//import persistence.XMLSerializer
import utils.readNextBoolean
import utils.readNextInt
import utils.readNextLine
import utils.statusList
import utils.validRange
import java.io.File

private val logger = KotlinLogging.logger {}

val notesFile = File("notes.json")
//val noteService = NoteService(XMLSerializer(File("notes.xml")))
val noteService = NoteService(JSONSerializer(notesFile))

val itemsFile = File("items.json")
val itemService = ItemService(JSONSerializer(itemsFile))

val noteItemFile = File("itemNotes.json")
val noteItemService = NoteItemService(JSONSerializer(noteItemFile))


fun main() {
    logger.info { "Notes App Started" }
    load()
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
        >|  6) Activate a Note        |
        >|  7) Search Notes by Content|
        >|  8) Add Item to Note       |
        >|  9) View Items for Note    |
        >| 20) Save Notes             |
        >| 21) Load Notes             |
        >  30) Item Menu              |
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
            6 -> activateNote()
            7 -> searchByContent()
            8 -> addItemToNote()
            9 -> viewItemsForNote()
            20 -> save()
            21 -> load()
            30 -> runItemMenu()
            0 -> { save(); println("Exiting App") }
            else -> println("Invalid Option")
        }
    } while (input != 0)
}

fun addNote(){
    val title =  readNextLine("Title: ")

    val body = readNextLine("Body: ")

    var priority: Int
    do {
        priority = readNextInt("Priority (1-5): ")
        if (!validRange(priority, 1, 5)) {
            println("Invalid priority, please try again.")
        }
    } while (!validRange(priority, 1, 5))

    var category: String
    do {
        category = readNextLine("${categories.joinToString(", ")}| Pick a category: ")
        if (!isValidCategory(category)) {
            println("Invalid category, please try again. Valid Categories: ${categories.joinToString(", ")}")
        }
    } while (!isValidCategory(category))

    var status: String
    do {
        status = readNextLine("${statusList.joinToString(", ")}|Pick a status: ")
        if (!isValidStatus(status)) {
            println("Invalid status, please try again. Valid Status: ${statusList.joinToString(", ")}")
        }
    } while (!isValidStatus(status))

    noteService.addNote(
        Note(0, title, body, priority, category, false, status)
    )

    println("Note added")
}

fun listNotes() {
    val listNotes = noteService.getNotes()
    if (listNotes.isEmpty()) {
        println("No notes stored")
    } else {
        listNotes.forEach { println(it) }
       println("There are ${noteService.numberOfNotes()} notes")
    }
}

fun listActiveNotes() {
    val activeNotes = noteService.getActiveNotes()
    if (activeNotes.isEmpty()) {
        println("No active notes")
    } else {
        activeNotes.forEach { println(it) }
        println("There are ${noteService.numberOfActiveNotes()} active notes")
    }
}

fun listArchivedNotes() {
    val archivedNotes = noteService.getArchivedNotes()
    if (archivedNotes.isEmpty()) {
        println("No archived notes")
    } else {
        archivedNotes.forEach { println(it) }
        println("There are ${noteService.numberOfArchivedNotes()} archived notes")
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
        >------------------------------
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

    val category = readNextLine("${categories.joinToString(", ")}| Pick a category: ")

    val isArchived = readNextBoolean("Is archived (y/n): ")

    val status = readNextLine("${statusList.joinToString(", ")}|Pick a status: ")

    val updated = Note(id, title, body, priority, category, isArchived, status)

    if (noteService.updateNote(id, updated)) {
        println("Updated")
    } else {
        println("Not Found")
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

fun activateNote() {
    noteService.getNotes().forEach {if (it.isArchived) println(it)}

    val id = readNextInt("ID to be activated: ")

    if (noteService.activateNote(id)) {
        println("Activated")
    } else {
        println("Not found")
    }
}

fun searchByContent() {
    val content = readNextLine("Enter content to search: ")
    val results = noteService.searchNotesByContent(content)
    if (results.isEmpty()) {
        println("No notes found")
    } else {
        results.forEach { println(it) }
    }
}

//--------------------------ITEMS---------------------------------------------------------------------

fun listItemMenu(): Int {
    print("""
        >------------------------------
        >|         Item Menu          |  
        >------------------------------
        >| ITEM MENU                  |
        >|  1) Add an Item            |
        >|  2) List Items             |
        >|  3) Search Items           |
        >|  4) Item Report            |
        >------------------------------
        >|  0) Back                   |
        >------------------------------
        > """.trimMargin(">"))
    return readNextInt("==>> ")
}

fun runItemMenu() {
    var input : Int
    do {
        input = listItemMenu()
        when (input) {
            1 -> addItem()
            2 -> listItems()
            3 -> searchItemByContent()
            4 -> itemReport()
            0 -> println("Returning to main menu")
            else -> println("Invalid Option")
        }
    } while (input != 0)
}
fun searchItemByContent() {
    val content = readNextLine("Enter content to search: ")
    val results = itemService.searchItemsByContent(content)
    if (results.isEmpty()) {
        println("No items found")
    } else {
        results.forEach { println(it) }
    }
}

fun addItem() {
    val id = readNextInt("ID: ")

    val description = readNextLine("Description: ")

    val complete = readNextBoolean("Complete(True/False): ")

    itemService.addItem(
        Item(id, description, complete)
    )

    println("Item added")
}

fun listItems(){
    val listItems = itemService.getItem()
    if (listItems.isEmpty()) {
        println("No items stored")
    } else {
        listItems.forEach { println(it) }
        println("There are ${itemService.numberOfItems()} items")
    }
}

fun itemReport() {
    print("""
        >------------------------------
        >       Item Report 
        >------------------------------
        >  Total Items: ${itemService.numberOfItems()}            
        >  Complete Items: ${itemService.numberOfCompleteItems()}           
        >  Incomplete Items: ${itemService.numberOfIncompleteItems()}           
        >------------------------------
        > """.trimMargin(">"))
}


fun addItemToNote() {
    listNotes()
    val noteId = readNextInt("Enter Note ID: ")
    if (noteService.findNoteById(noteId) == null) {
        println("Note not found")
        return
    }
    listItems()
    val itemId = readNextInt("Enter Item ID: ")
    if (itemService.findItemById(itemId) == null) {
        println("Item not found")
        return
    }
    if (noteItemService.addItemToNote(noteId, itemId)) println("Item added to note")
    else println("That item is already on this note")
}

fun viewItemsForNote() {
    listNotes()
    val noteId = readNextInt("Enter Note ID: ")
    val note = noteService.findNoteById(noteId)
    if (note == null) {
        println("Note not found")
        return
    }

    val itemIds = noteItemService.getItemIdsForNote(noteId)
    if (itemIds.isEmpty()) {
        println("No items for note")
        return
    }

    println("Items for note: ${note.title}")
    itemIds.forEach { itemId ->
        val item = itemService.findItemById(itemId)
        if (item != null) println(item)
        }
}
//--------------------------------------------------------------------
fun save() {
    try {
        noteService.store()
        itemService.store()
        noteItemService.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        if (notesFile.exists()) noteService.load()
        if (itemsFile.exists()) itemService.load()
        if (noteItemFile.exists()) noteItemService.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

