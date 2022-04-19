package objects

import exceptions.ChatNotFoundException
import exceptions.MessageNotFoundException
import org.junit.Assert.*
import org.junit.Test

private const val CURRENT_USER_ID = 1
private const val SECOND_USER_ID = 2
private const val THIRD_USER_ID = 3

class ChatServiceTest {

    private val cs = ChatService


    private fun ChatService.reset() {
        clear("bonsai!")
        //messages
        cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = SECOND_USER_ID, "KNOCK-KNOCK, Second!")
        cs.createMessage(authorID = SECOND_USER_ID, addresseeID = CURRENT_USER_ID, "Who's there?")
        cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = SECOND_USER_ID, "It's poor Kotlin here =)")
        //messages
        cs.createMessage(authorID = THIRD_USER_ID, addresseeID = CURRENT_USER_ID, "hello, Current!")
        cs.createMessage(authorID = THIRD_USER_ID, addresseeID = CURRENT_USER_ID, "how are you?")
        cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = THIRD_USER_ID, "hello, Third! I'm fine!")
    }

    @Test
    fun getUnreadChatsCount() {
        cs.reset()
        assertTrue(
            cs.getUnreadChatsCount(CURRENT_USER_ID) == 2 &&
                    cs.getUnreadChatsCount(SECOND_USER_ID) == 1 &&
                    cs.getUnreadChatsCount(THIRD_USER_ID) == 1
        )
    }

    @Test
    fun getUnreadChatsCount_DeletedChat() {
        cs.reset()
        cs.deleteChat(CURRENT_USER_ID, SECOND_USER_ID)
        assertTrue(cs.getUnreadChatsCount(SECOND_USER_ID) == 0)
    }

    @Test
    fun getUnreadChatsCount_Read() {
        cs.reset()
        cs.getMessagesList(SECOND_USER_ID, CURRENT_USER_ID)
        assertTrue(cs.getUnreadChatsCount(SECOND_USER_ID) == 0)
    }

    @Test
    fun getChatList() {
        cs.reset()
        val chatList = listOf(
            Chat(listOf(CURRENT_USER_ID, SECOND_USER_ID)),
            Chat(listOf(CURRENT_USER_ID, THIRD_USER_ID))
        )
        assertTrue(cs.getChatList(CURRENT_USER_ID) == chatList)
    }

    @Test
    fun getChatListEmpty() {
        cs.reset()
        assertTrue(cs.getChatList(4).isEmpty())
    }

    @Test
    fun getMessagesList() {
        cs.reset()
        val messages = listOf(
            Message(3, CURRENT_USER_ID, "It's poor Kotlin here =)")
        )
        assertTrue(messages == cs.getMessagesList(CURRENT_USER_ID, SECOND_USER_ID, 3, 1))
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessagesListThrowsChatExc() {
        cs.reset()
        cs.getMessagesList(CURRENT_USER_ID,4)
    }

    @Test(expected = MessageNotFoundException::class)
    fun getMessagesListThrowsMessageExc() {
        cs.reset()
        cs.getMessagesList(CURRENT_USER_ID, SECOND_USER_ID, 4)
    }

    @Test(expected = MessageNotFoundException::class)
    fun getMessagesListDeletedMessage() {
        cs.reset()
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 3)
        cs.getMessagesList(CURRENT_USER_ID, SECOND_USER_ID, 3)
    }

    @Test
    fun createMessageDeletedChat() {
        cs.reset()
        cs.deleteChat(CURRENT_USER_ID, SECOND_USER_ID)
        val message = Message(4, CURRENT_USER_ID, "")
        val newMessage = cs.createMessage(CURRENT_USER_ID, SECOND_USER_ID, "")
        val chatMessagesCount = cs.getMessagesList(CURRENT_USER_ID, SECOND_USER_ID, 4).count()

        assertTrue(message == newMessage && chatMessagesCount == 1)
    }

    @Test
    fun deleteMessageTrue() {
        cs.reset()
        assertTrue(cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 3))
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteMessageThrowsMessExc() {
        cs.reset()
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 4)
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteDeletedMessage() {
        cs.reset()
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 1)
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 1)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteLastMessage() {
        cs.reset()
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 1)
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 2)
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 3)
        cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 999)

    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessageThrowsChatExc() {
        cs.reset()
        cs.deleteMessage(CURRENT_USER_ID, 4, 4)
    }

    @Test
    fun deleteChatTrue() {
        cs.reset()
        val message = cs.createMessage(4, 5, "")
        val firstChat = cs.getChatList(4).first()
        cs.deleteChat(5, 4)
        assertTrue(firstChat.isDeleted && message.isDeleted)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChatThrowsEx() {
        cs.reset()
        cs.deleteChat(CURRENT_USER_ID,4)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteDeletedChatThrowsEx() {
        cs.reset()
        cs.deleteChat(CURRENT_USER_ID, SECOND_USER_ID)
        cs.deleteChat(CURRENT_USER_ID, SECOND_USER_ID)
    }
}