package service

import model.Note
import persistence.Serializer

class NoteService(serializeType: Serializer) {

    private var serializer: Serializer = serializeType

    private var notes = ArrayList<Note>()
    private var lastId = 0
    private fun getId(): Int = lastId++

    fun addNote(note: Note) {
        note.id = getId()
        note.isArchived = false
        notes.add(note)
    }

    fun getNotes(): List<Note> {
        return notes
    }

    fun getActiveNotes(): List<Note>  = notes.filter{ !it.isArchived }

    fun getNotesByCategory(category: String): List<Note> = notes.filter { it.category.equals(category, ignoreCase = true) }

    fun getArchivedNotes(): List<Note> = notes.filter { it.isArchived }

    fun getNotesByPriority(priority: Int): List<Note> = notes.filter { it.priority == priority }

    fun deleteNote(id: Int) : Boolean {
        return notes.removeIf { it.id == id}
    }

    fun updateNote(id: Int, updated: Note) : Boolean {
        val index = notes.indexOfFirst { it.id == id }
        if (index != -1) {
            updated.id = id
            notes[index] = updated
            return true
        }
        return false
    }

    fun findNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }

    fun numberOfNotes() = notes.size

    fun numberOfArchivedNotes() = notes.count { note: Note -> note.isArchived }

    fun numberOfActiveNotes() = notes.count{ note: Note -> !note.isArchived }

    fun numberOfNotesByCategory(category: String) = notes.count { note: Note -> note.category == category }

    fun numberOfNotesByPriority(priority: Int) = notes.count { note: Note -> note.priority == priority }

    fun archiveNote(id: Int): Boolean {
        val note = notes.find { it.id == id && !it.isArchived }

        if (note != null) {
            note.isArchived = true
            return true
        }

        return false
    }


    fun activateNote(id: Int): Boolean {
        for (note in notes) {
            if (note.id == id && note.isArchived) {
                note.isArchived = false
                return true
            }
        }
        return false
    }

    fun load() {
        val array = serializer.read(Array<Note>::class.java)
        notes = array.toCollection(ArrayList())
        lastId = (notes.maxOfOrNull { it.id } ?: -1) + 1
    }

    //@Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }
    fun searchNotesByContent(content: String): List<Note> = notes.filter{ note -> ((note.title.contains(content, ignoreCase = true)
                || (note.body.contains(content, ignoreCase = true))))}



}
