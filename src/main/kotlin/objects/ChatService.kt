package objects

import exceptions.*

//Я посчитал логичным хранить сообщения в чате. Не уверен, что это оптимальный подход, но вот моя реализация
//В связи с этой логикой и тем фактом, что для разделения пользователей нам так или иначе необходимо
// передавать параметром userID, поиск чата везде осуществляется по userID и ID собеседника(otherID)

object ChatService {
    private val chats = mutableListOf<Chat>()

    //подсчет с фильтром по сообщениям, в которых пользователь выступал адресатом
    fun getUnreadChatsCount(userID: Int): Int {
        return getChatList(userID)
            .count { it.hasUnreadMessages(userID) }
    }

    fun getChatList(userID: Int): List<Chat> {
        return chats
            .filter { it.talkers.contains(userID) && !it.isDeleted }
    }

    //отмечаются прочитанными сообщения, в которых пользователь выступал адресатом
    fun getMessagesList(
        userID: Int,
        otherID: Int,
        startMessageID: Int = 1,
        count: Int = 10
    ): List<Message> {
        val messages = chatOrException(userID, otherID).messages
        val startIndex = messages
            .indexOfFirst { it.id == startMessageID && !it.isDeleted }
        return messages.asSequence()
            .takeIf { startIndex >= 0 }
            .let {
                it
                    ?: throw MessageNotFoundException(
                        "Message #$startMessageID in chat of users #$userID and #$otherID not found or been deleted!"
                    )
            }
            .drop(startIndex)
            .take(count)
            .apply {
                this
                    .filter { it.authorID != userID }
                    .forEach { it.beenRead = true }
            }
            .toList()
    }

    //если чат уже создавался и был удалён, восстанавливаем его (но не удалённые сообщения)
    fun createMessage(authorID: Int, addresseeID: Int, text: String): Message {
        return chats.firstOrNull { it.talkers.containsAll(listOf(authorID, addresseeID)) }
            ?.apply { isDeleted = false }
            .let { it ?: createChat(authorID, addresseeID) }
            .apply { messages += Message(id = messages.size + 1, authorID = authorID, text = text) }
            .lastMessage()!!
    }

    //В связи с привязкой сообщений к чатам, необходимо сначала "выбрать" нужный чат путём указания ID собеседника
//Для удаления чата здесь не используем deleteChat(), поскольку нет необходимости удалять
//  уже удалённые сообщения (удаление всех сообщений мы подтверждаем в параметре if())
    fun deleteMessage(userID: Int, otherID: Int, messageID: Int): Boolean {
        val chat = chatOrException(userID, otherID)
        val message = chat.messages.firstOrNull { it.id == messageID && !it.isDeleted }
            ?: throw MessageNotFoundException(
                "Message #$messageID in chat of users #$userID and #$otherID not found or been deleted!"
            )
        message.isDeleted = true
        if (!chat.messages.any { !it.isDeleted }) chat.isDeleted = true
        return true
    }

    fun deleteChat(userID: Int, otherID: Int): Boolean {
        val chat = chatOrException(userID, otherID)
        chat.isDeleted = true
        chat.messages.forEach { it.isDeleted = true }
        return true
    }

    fun clear(code: String) {
        if (code == "bonsai!") chats.clear()
    }

//private functions

    private fun createChat(userID: Int, otherID: Int): Chat {
        val newChat = Chat(listOf(userID, otherID).sorted())
        chats += newChat
        return newChat
    }

    private fun chatOrException(userID: Int, otherID: Int): Chat {
        return chats.firstOrNull {
            it.talkers.containsAll(listOf(userID, otherID))
                    && !it.isDeleted
        } ?: throw ChatNotFoundException("Chat of users #$userID and #$otherID not found!")
    }

//output functions

    val printUserChats = { userID: Int ->
        getChatList(userID).forEach { println(it) }
    }

    fun printChatMessages(userID: Int, addresseeID: Int) {
        val messageList = getMessagesList(userID, addresseeID)
        val mark = { message: Message ->
            when (message.authorID) {
                userID -> "outgoing"
                else -> "INCOMING"
            }
        }
        println(chatOrException(userID, addresseeID))
        messageList.forEach { println("${mark(it)} $it") }
    }
}