package objects

import exceptions.*

//Я посчитал логичным хранить сообщения в чате. Не уверен, что это оптимальный подход, но вот моя реализация
//В связи с этой логикой и тем фактом, что для разделения пользователей нам так или иначе необходимо
// передавать параметром userID, поиск чата везде осуществляется по userID и ID собеседника(otherID)

object ChatService {
    private val chats = mutableListOf<Chat>()

    //подсчет с фильтром по сообщениям, в которых пользователь выступал адресатом
    fun getUnreadChatsCount(userID: Int): Int {
        val chatList = getChatList(userID)
        return when (chatList.isEmpty()) {
            false -> chatList.count { chat ->
                chat.messages.filter { message ->
                    message.toID == userID
                }.any { !it.beenRead }
            }
            true -> 0
        }
    }

    fun getChatList(userID: Int): List<Chat> {
        return chats.filter { it.talkers.contains(userID) && !it.isDeleted }
    }

    //отмечаются прочитанными сообщения, в которых пользователь выступал адресатом
    fun getMessagesList(
        userID: Int,
        otherID: Int,
        startMessageID: Int = 1,
        count: Int = 100500
    ): List<Message> {
        val messageList = chatOrException(userID, otherID).messages.filter {
            it.id >= startMessageID && !it.isDeleted
        }
        val result = when (count) {
            100500 -> messageList
            else -> messageList.take(count)
        }
        result.forEach {
            if (it.toID == userID) it.beenRead = true
        }
        return result
    }

    //если чат уже создавался и был удалён, восстанавливаем его (но не удалённые сообщения)
    fun createMessage(authorID: Int, addresseeID: Int, text: String): Message {
        val chat = chats.find { it.talkers.containsAll(listOf(authorID, addresseeID)) }
            ?: createChat(authorID, addresseeID)
        if (chat.isDeleted) chat.restore()
        chat.messages += Message(
            id = chat.messages.size + 1, authorID = authorID, toID = addresseeID, text = text
        )
        return chat.messages.last()
    }

    //В связи с привязкой сообщений к чатам, необходимо сначала "выбрать" нужный чат путём указания ID собеседника
//  Такой подход также позволяет нам легко добавить проверку авторства сообщения с запретом на удаление чужого
//Для удаления чата здесь не используем deleteChat(), поскольку нет необходимости удалять
//  уже удалённые сообщения (удаление всех сообщений мы подтверждаем в параметре if())
    fun deleteMessage(userID: Int, otherID: Int, messageID: Int): Boolean {
        val chat = chatOrException(userID, otherID)
        val message = chat.messages.find { it.id == messageID && !it.isDeleted }
            ?: throw MessageNotFoundException(
                "Message #$messageID in chat of users #$userID and #$otherID not found or been deleted!"
            )
        message.isDeleted = true
        if (!chat.messages.any { !it.isDeleted }) chat.isDeleted = true
        return true
    }

    private fun createChat(userID: Int, otherID: Int): Chat {
        val newChat = Chat(listOf(userID, otherID).sorted())
        chats += newChat
        return newChat
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

    private fun chatOrException(userID: Int, otherID: Int): Chat {
        return chats.find {
            it.talkers.containsAll(listOf(userID, otherID))
                    && !it.isDeleted
        } ?: throw ChatNotFoundException("Chat of users #$userID and #$otherID not found!")
    }

    private fun Chat.restore() {
        this.isDeleted = false
    }

//output functions

    val printUserChats = { userID: Int ->
        getChatList(userID).forEach { println(it) }
    }

    fun printChatMessages(userID: Int, addresseeID: Int, startMessageID: Int = 1, count: Int = 5) {
        val messageList = getMessagesList(userID, addresseeID, startMessageID, count)
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