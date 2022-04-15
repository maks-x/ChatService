package objects

data class Chat(
    val talkers: List<Int>
) {
    //возможно id еще пригодится, оставлю его здесь)
    val id = talkers.hashCode()
    val messages: MutableList<Message> = mutableListOf()
    var isDeleted = false
    val lastMessage = {
        messages.lastOrNull { !it.isDeleted }
    }

    override fun toString(): String {
        return "ChatID#$id (talkers=$talkers)\nlastMessage=${lastMessage() ?: "no messages"}"
    }
}
