package service

import model.NoteItem
import persistence.Serializer
import kotlin.collections.toCollection

class NoteItemService(
    serializeType: Serializer,
) {
    private var serializer: Serializer = serializeType

    private var noteItems = ArrayList<NoteItem>()

    fun addItemToNote(
        noteId: Int,
        itemId: Int,
    ): Boolean {
        if (noteItems.any { it.noteId == noteId && it.itemId == itemId }) {
            return false
        }
        noteItems.add(NoteItem(noteId, itemId))
        return true
    }

    fun getItemIdsForNote(noteId: Int): List<Int> = noteItems.filter { it.noteId == noteId }.map { it.itemId }

    fun load() {
        val array = serializer.read(Array<NoteItem>::class.java)
        noteItems = array.toCollection(java.util.ArrayList())
    }

    fun store() {
        serializer.write(noteItems)
    }
}
