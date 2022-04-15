import objects.*

const val CURRENT_USER_ID = 1
const val SECOND_USER_ID = 2
const val THIRD_USER_ID = 3

fun main() {
    val cs = ChatService

    //messages
    cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = SECOND_USER_ID, "KNOCK-KNOCK, Second!")
    cs.createMessage(authorID = SECOND_USER_ID, addresseeID = CURRENT_USER_ID, "Who's there?")
    cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = SECOND_USER_ID, "It's poor Kotlin here =)")
    //messages
    cs.createMessage(authorID = THIRD_USER_ID, addresseeID = CURRENT_USER_ID, "hello, Current!")
    cs.createMessage(authorID = THIRD_USER_ID, addresseeID = CURRENT_USER_ID, "how are you?")
    cs.createMessage(authorID = CURRENT_USER_ID, addresseeID = THIRD_USER_ID, "hello, Third! I'm fine!")

    println("//print Current chats with last messages")
    cs.printUserChats(CURRENT_USER_ID)
    println()

    println("Current's chats, contains unread message(s) count = " + cs.getUnreadChatsCount(CURRENT_USER_ID))
    println()

    println("//print messages list of Current with Second and, at the same time reading unread Second's messages")
    cs.printChatMessages(CURRENT_USER_ID, SECOND_USER_ID)
    println()

    println("Current's chats, contains unread message(s) count = " + cs.getUnreadChatsCount(CURRENT_USER_ID))
    println()

    println("//print messages list of Current with Second after delete 2-nd and 3-rd message")
    cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 2)
    cs.deleteMessage(CURRENT_USER_ID, SECOND_USER_ID, 3)
    cs.printChatMessages(CURRENT_USER_ID, SECOND_USER_ID)
    println()

    println("//print Current chats after delete chat with 2-nd")
    cs.deleteChat(CURRENT_USER_ID, SECOND_USER_ID)
    cs.printUserChats(CURRENT_USER_ID)
    println()

    println("//print chat of Current with Third and, at the same time reading unread Third's messages")
    cs.printChatMessages(CURRENT_USER_ID, THIRD_USER_ID)
    println()

    println("Current's chats, contains unread message(s) count = " + cs.getUnreadChatsCount(CURRENT_USER_ID))
    println()
}