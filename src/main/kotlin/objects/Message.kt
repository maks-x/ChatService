package objects

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//private val dtf = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm")

data class Message(
    val id: Int,
    val authorID: Int,
    val toID: Int,
    val text: String
) {
    var isDeleted = false
    //private val dateTime = LocalDateTime.now().format(dtf)
    var beenRead: Boolean = false

    override fun toString(): String {
        return "[$id] Message(authorID:$authorID, toID:$toID, text:$text, beenRead:$beenRead"
    }
}