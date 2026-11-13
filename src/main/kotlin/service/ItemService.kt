package service

import model.Item
import persistence.Serializer

class ItemService(
    serializeType: Serializer,
) {
    private var serializer: Serializer = serializeType

    private var items = ArrayList<Item>()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun getItem(): List<Item> = items

    fun findItemById(id: Int): Item? = items.find { it.id == id }

    fun searchItemsByContent(content: String): List<Item> = items.filter { item -> (item.description.contains(content, ignoreCase = true)) }

    fun numberOfItems() = items.size

    fun numberOfCompleteItems() = items.filter { it.isCompleted }.size

    fun numberOfIncompleteItems() = items.filter { !it.isCompleted }.size

    fun load() {
        val array = serializer.read(Array<Item>::class.java)
        items = array.toCollection(ArrayList())
    }

    fun store() {
        serializer.write(items)
    }
}
