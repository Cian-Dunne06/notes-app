package service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import model.Note
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import persistence.XMLSerializer
import persistence.JSONSerializer
import java.io.File

private lateinit var emptyNoteService : NoteService
private lateinit var populatedNoteService : NoteService
private lateinit var note1 : Note
private lateinit var note2 : Note
private lateinit var note3 : Note
private lateinit var note4 : Note
private lateinit var note5 : Note

class NoteServiceTest {

    @BeforeEach
    fun setUp() {
        note1 = Note(0, "Test1", "Body1", 1, "Work", false)
        note2 = Note(0, "Test2", "Body2", 2, "Home", true)
        note3 = Note(0, "Test3", "Body3", 3, "College", false)
        note4 = Note(0, "Test4", "Body4", 4, "Social", true)

        note5 = Note(0, "Test5", "Body5", 5, "Hobbies", true)

        emptyNoteService = NoteService(XMLSerializer(File("notes.xml")))
        populatedNoteService = NoteService(XMLSerializer(File("empty-notes.xml")))

        populatedNoteService.addNote(note1)
        populatedNoteService.addNote(note2)
        populatedNoteService.addNote(note3)
        populatedNoteService.addNote(note4)
    }

    @AfterEach
    fun tearDown() {
        println("Test Completed")
    }


    @Nested
    inner class AddNote {
        @Test
        fun `add note to an empty service increases size correctly`() {
            assertEquals(0, emptyNoteService.getNotes().size)
            emptyNoteService.addNote(note1)
            assertEquals(1, emptyNoteService.getNotes().size)
        }

        @Test
        fun `add note to a populated service increases size correctly`() {
            assertEquals(4, populatedNoteService.getNotes().size)
            populatedNoteService.addNote(note5)
            assertEquals(5, populatedNoteService.getNotes().size)
        }

        @Test
        fun `add note stores the correct values`() {
            emptyNoteService.addNote(note1)

            val retrievedNote = emptyNoteService.getNotes().first()

            assertEquals(note1.title, retrievedNote.title)
            assertEquals(note1.body, retrievedNote.body)
            assertEquals(note1.priority, retrievedNote.priority)
            assertEquals(note1.category, retrievedNote.category)
        }

        @Test
        fun `add note sets isArchived to false`() {
            emptyNoteService.addNote(note1)
            emptyNoteService.addNote(note2)

            val retrievedNote1 = emptyNoteService.findNoteById(0)
            val retrievedNote2 = emptyNoteService.findNoteById(1)

            assertEquals(false, retrievedNote1?.isArchived)
            assertEquals(false, retrievedNote2?.isArchived)
        }

        @Test
        fun `ensure ids remain unique after multiple inserts`() {
            assertEquals(0, populatedNoteService.getNotes()[0].id)
            assertEquals(1, populatedNoteService.getNotes()[1].id)
            assertEquals(2, populatedNoteService.getNotes()[2].id)
            assertEquals(3, populatedNoteService.getNotes()[3].id)
        }
    }

    @Nested
    inner class GetNote {
        @Test
        fun `getNotes returns an empty list when service is empty`() {
            assertTrue(emptyNoteService.getNotes().isEmpty())
        }

        @Test
        fun `getNotes returns all notes when a service is populated`() {
            val notes = populatedNoteService.getNotes()
            assertEquals(4, notes.size)
        }

        @Test
        fun `getActiveNotes returns only non archived notes`(){
            populatedNoteService.archiveNote(note1.id)
            populatedNoteService.archiveNote(note3.id)

            val activeList = populatedNoteService.getActiveNotes()

            assertEquals(2, activeList.size)
            assertTrue(activeList.all {!it.isArchived})
        }

        @Test
        fun `getActiveNotes returns an empty list when no notes are stored` (){
            val activeList = emptyNoteService.getActiveNotes()
            assertTrue(activeList.isEmpty())
        }

        @Test
        fun `getActiveNotes returns empty list when all notes are archived`(){
            populatedNoteService.archiveNote(note1.id)
            populatedNoteService.archiveNote(note2.id)
            populatedNoteService.archiveNote(note3.id)
            populatedNoteService.archiveNote(note4.id)

            val activeList = populatedNoteService.getActiveNotes()

            assertTrue(activeList.isEmpty())
        }

        @Test
        fun `getActiveNotes returns all notes when none are archived` () {
            val activeList = populatedNoteService.getActiveNotes()

            assertEquals(4, activeList.size)
            assertTrue(activeList.none {it.isArchived})
        }

        @Test
        fun `getActiveNotes preserves correct count and content` (){
            populatedNoteService.archiveNote(note2.id)

            val activeList = populatedNoteService.getActiveNotes()

            assertEquals(3, activeList.size)
            assertTrue(activeList.all {!it.isArchived})

            assertTrue(activeList.contains(note1))
            assertTrue(activeList.contains(note3))
            assertTrue(activeList.contains(note4))
        }

        @Test
        fun `getNotesByCategory handles empty list`() {
            val result = emptyNoteService.getNotesByCategory("Work")
            assertTrue(result.isEmpty())
        }

        @Test
        fun `getNotesByCategory returns all matching notes when all match`() {
            emptyNoteService.addNote(Note(0, "A", "Body A", 1, "Work", false))
            emptyNoteService.addNote(Note(0, "B", "Body B", 2, "Work", false))
            emptyNoteService.addNote(Note(0, "C", "Body C", 3, "Work", false))

            assertEquals(3, emptyNoteService.numberOfNotes())
            val result = emptyNoteService.getNotesByCategory("Work")

            assertEquals(3, result.size)
            assertTrue(result.all { it.category == "Work" })
        }

        @Test
        fun `getNotesByCategory returns no notes when the category is not in the list` () {
            val result = populatedNoteService.getNotesByCategory("Invalid Category")
            assertTrue(result.isEmpty())
        }

        @Test
        fun `getNotesByCategory returns matching categories regardless of case` () {
            val result = populatedNoteService.getNotesByCategory("home")
            assertEquals(1, result.size)
            assertTrue(result.all { it.category == "Home" })
        }

        @Test
        fun `getNotesByCategory excludes notes whose categories do not match` () {
            assertEquals(4, populatedNoteService.numberOfNotes())

            val result = populatedNoteService.getNotesByCategory("Work")
            assertEquals(1, result.size)

            assertTrue(result.contains(note1))
            assertFalse(result.contains(note2))
            assertFalse(result.contains(note3))
            assertFalse(result.contains(note4))
        }

        @Test
        fun `getArchivedNotes returns only non active notes`(){
            populatedNoteService.archiveNote(note1.id)
            populatedNoteService.archiveNote(note3.id)

            val archivedList = populatedNoteService.getArchivedNotes()

            assertEquals(2, archivedList.size)
            assertTrue(archivedList.all {it.isArchived})
        }

        @Test
        fun `getArchivedNotes returns an empty list when no notes are stored` (){
            val archivedList = emptyNoteService.getArchivedNotes()
            assertTrue(archivedList.isEmpty())
        }

        @Test
        fun `getArchivedNotes returns empty list when all notes are active`(){
            populatedNoteService.activateNote(note1.id)
            populatedNoteService.activateNote(note2.id)
            populatedNoteService.activateNote(note3.id)
            populatedNoteService.activateNote(note4.id)

            val archivedList = populatedNoteService.getArchivedNotes()

            assertTrue(archivedList.isEmpty())
        }

        @Test
        fun `getArchivedNotes returns all notes when none are active` () {
            populatedNoteService.archiveNote(note1.id)
            populatedNoteService.archiveNote(note2.id)
            populatedNoteService.archiveNote(note3.id)
            populatedNoteService.archiveNote(note4.id)
            val archivedList = populatedNoteService.getArchivedNotes()

            assertEquals(4, archivedList.size)
            assertTrue(archivedList.all {it.isArchived})
        }

        @Test
        fun `getArchivedNotes preserves correct count and content` (){
            populatedNoteService.archiveNote(note1.id)
            populatedNoteService.archiveNote(note2.id)
            populatedNoteService.archiveNote(note3.id)
            populatedNoteService.activateNote(note4.id)


            val archivedList = populatedNoteService.getArchivedNotes()

            assertEquals(3, archivedList.size)
            assertTrue(archivedList.all {it.isArchived})

            assertTrue(archivedList.contains(note1))
            assertTrue(archivedList.contains(note2))
            assertTrue(archivedList.contains(note3))
        }

        @Test
        fun `getNotesByPriority handles empty list`() {
            val result = emptyNoteService.getNotesByPriority(1)
            assertTrue(result.isEmpty())
        }

        @Test
        fun `getNotesByPriority returns all matching notes when all match`() {
            emptyNoteService.addNote(Note(0, "A", "Body A", 1, "Work", false))
            emptyNoteService.addNote(Note(0, "B", "Body B", 1, "Work", false))
            emptyNoteService.addNote(Note(0, "C", "Body C", 1, "Work", false))

            assertEquals(3, emptyNoteService.numberOfNotes())
            val result = emptyNoteService.getNotesByPriority(1)

            assertEquals(3, result.size)
            assertTrue(result.all { it.priority == 1 })
        }

        @Test
        fun `getNotesByPriority returns no notes when the priority is not in the list` () {
            val result = populatedNoteService.getNotesByPriority(6)
            assertTrue(result.isEmpty())
        }

        @Test
        fun `getNotesByPriority excludes notes whose categories do not match` () {
            assertEquals(4, populatedNoteService.numberOfNotes())

            val result = populatedNoteService.getNotesByPriority(1)
            assertEquals(1, result.size)

            assertTrue(result.contains(note1))
            assertFalse(result.contains(note2))
            assertFalse(result.contains(note3))
            assertFalse(result.contains(note4))
        }

    }

    @Nested
    inner class DeleteNote {

        @Test
        fun `deleting an existing note returns true` () {
            assertTrue(populatedNoteService.deleteNote(1))
        }

        @Test
        fun `deleting a non-existing note returns false` () {
            assertFalse(populatedNoteService.deleteNote(6))
        }

        @Test
        fun `deleting from an empty service returns false` () {
            assertFalse(emptyNoteService.deleteNote(2))
        }

        @Test
        fun `deleting an existing note reduces the number of notes by one` () {
            val originalSize = populatedNoteService.getNotes().size
            populatedNoteService.deleteNote(1)
            assertEquals(originalSize-1, populatedNoteService.getNotes().size)
        }

        @Test
        fun `deleting an existing note removes the correct note` () {
            populatedNoteService.deleteNote(1)
            assertFalse(populatedNoteService.getNotes().any { it.id == 1})
        }

        @Test
        fun `deleting an existing note does not remove an other notes` () {
            populatedNoteService.deleteNote(3)
            assertTrue(populatedNoteService.getNotes().any { it.id == 0 })
            assertTrue(populatedNoteService.getNotes().any { it.id == 1 })
            assertTrue(populatedNoteService.getNotes().any { it.id == 2 })
        }
    }

    @Nested
    inner class UpdateNote {

        @Test
        fun `updating an existing note returns true` () {
            val updated = Note(1, "Updated Title", "Updated Body", 5, "Work", true)
            assertTrue(populatedNoteService.updateNote(1, updated))
        }

        @Test
        fun `updating a non-existing note returns false` () {
            val updated = Note(100, "Updated Title", "Updated Body", 5, "Social", true)
            assertFalse(populatedNoteService.updateNote(100, updated))
        }

        @Test
        fun `updating from an empty service returns false` () {
            val updated = Note(1, "Updated Title", "Updated Body", 5, "Work", true)
            assertFalse(emptyNoteService.updateNote(1, updated))
        }

        @Test
        fun `updating an existing note changes its stored values` () {
            val updated = Note(1, "Updated Title", "Updated Body", 5, "Social", true)

            populatedNoteService.updateNote(1, updated)

            val retrieved = populatedNoteService.findNoteById(1)
            assertEquals("Updated Title", retrieved?.title)
            assertEquals("Updated Body", retrieved?.body)
            assertEquals(5, retrieved?.priority)
            assertEquals("Social", retrieved?.category)
            assertTrue(retrieved?.isArchived == true)
        }

        @Test
        fun `updating a note does not change the number of notes` () {
            val originalSize = populatedNoteService.getNotes().size

            val updated = Note(1, "Updated Title", "Updated Body", 5, "Social", true)
            populatedNoteService.updateNote(1, updated)

            assertEquals(originalSize, populatedNoteService.getNotes().size)
        }

        @Test
        fun `updating an existing note preserves its id` () {
            val updated = Note(33, "Updated Title", "Updated Body", 5, "Home", false)

            populatedNoteService.updateNote(1, updated)

            assertEquals(1, populatedNoteService.getNotes()[1].id)
        }
    }

    @Nested
    inner class countNotes {

        @Test
        fun `numberOfNotes returns correct value for populated list` () {
            assertEquals(4, populatedNoteService.numberOfNotes())
        }

        @Test
        fun `numberOfNotes returns zero for empty service`() {
            assertEquals(0, emptyNoteService.numberOfNotes())
        }

        @Test
        fun `numberOfArchivedNotes returns correct count after archiving notes`() {
            assertTrue(populatedNoteService.archiveNote(2))
            assertTrue(populatedNoteService.archiveNote(3))
            assertEquals(2, populatedNoteService.numberOfArchivedNotes())
        }

        @Test
        fun `numberOfArchivedNotes returns zero when no notes are archived`() {
            emptyNoteService.addNote(note1)
            emptyNoteService.addNote(note2)
            emptyNoteService.addNote(note3)

            assertEquals(0, emptyNoteService.numberOfArchivedNotes())
        }

        @Test
        fun `numberOfActiveNotes returns correct count when all notes are still active`() {
            assertEquals(4, populatedNoteService.numberOfActiveNotes())
        }

        @Test
        fun `numberOfActiveNotes returns zero when all notes are archived`() {
            emptyNoteService.addNote(note1)
            emptyNoteService.addNote(note2)
            emptyNoteService.addNote(note3)

            emptyNoteService.archiveNote(note1.id)
            emptyNoteService.archiveNote(note2.id)
            emptyNoteService.archiveNote(note3.id)

            assertEquals(0, emptyNoteService.numberOfActiveNotes())
        }

        @Test
        fun `numberOfNotesByCategory returns correct count for Work`() {
            assertEquals(1, populatedNoteService.numberOfNotesByCategory("Work"))
        }

        @Test
        fun `numberOfNotesByCategory returns zero when category does not exist`() {
            assertEquals(0, populatedNoteService.numberOfNotesByCategory("Travel"))
        }

        @Test
        fun `numberOfNotesByPriority returns correct count`() {
            assertEquals(1, populatedNoteService.numberOfNotesByPriority(2))
        }

        @Test
        fun `numberOfNotesByPriority returns zero when priority does not exist`() {
            assertEquals(0, populatedNoteService.numberOfNotesByPriority(99))
        }

    }

    @Nested
    inner class PersistenceTests {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            // Saving an empty notes.XML file.
            val storingNotes = NoteService(XMLSerializer(File("notes.xml")))
            storingNotes.store()

            //Loading the empty notes.xml file into a new object
            val loadedNotes = NoteService(XMLSerializer(File("notes.xml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving notes to XML and then loading them doesn't loose data`() {
            // Storing 3 notes to the notes.XML file.
            val storingNotes = NoteService(XMLSerializer(File("notes.xml")))
            storingNotes.addNote(note1)
            storingNotes.addNote(note2)
            storingNotes.addNote(note3)
            storingNotes.store()

            //Loading notes.xml into a different collection
            val loadedNotes = NoteService(XMLSerializer(File("notes.xml")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNoteById(0), loadedNotes.findNoteById(0))
            assertEquals(storingNotes.findNoteById(1), loadedNotes.findNoteById(1))
            assertEquals(storingNotes.findNoteById(2), loadedNotes.findNoteById(2))
        }

        @Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
            // Saving an empty notes.JSON file.
            val storingNotes = NoteService(JSONSerializer(File("notes.json")))
            storingNotes.store()

            //Loading the empty notes.json file into a new object
            val loadedNotes = NoteService(JSONSerializer(File("notes.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the XML loaded notes (loadedNotes)
            assertEquals(0, storingNotes.numberOfNotes())
            assertEquals(0, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
        }

        @Test
        fun `saving notes to JSON and then loading them doesn't loose data`() {
            // Storing 3 notes to the notes.json file.
            val storingNotes = NoteService(JSONSerializer(File("notes.json")))
            storingNotes.addNote(note1)
            storingNotes.addNote(note2)
            storingNotes.addNote(note3)
            storingNotes.store()

            //Loading notes.json into a different collection
            val loadedNotes = NoteService(JSONSerializer(File("notes.json")))
            loadedNotes.load()

            //Comparing the source of the notes (storingNotes) with the JSON loaded notes (loadedNotes)
            assertEquals(3, storingNotes.numberOfNotes())
            assertEquals(3, loadedNotes.numberOfNotes())
            assertEquals(storingNotes.numberOfNotes(), loadedNotes.numberOfNotes())
            assertEquals(storingNotes.findNoteById(0), loadedNotes.findNoteById(0))
            assertEquals(storingNotes.findNoteById(1), loadedNotes.findNoteById(1))
            assertEquals(storingNotes.findNoteById(2), loadedNotes.findNoteById(2))
        }
    }

}