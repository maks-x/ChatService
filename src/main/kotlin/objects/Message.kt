package objects

data class Message(
    val id: Int,
    val authorID: Int,
    val text: String
) {
    var isDeleted = false
    var beenRead: Boolean = false

    override fun toString(): String {
        return "[$id] Message(authorID:$authorID, text:$text, beenRead:$beenRead"
    }
}