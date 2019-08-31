package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

/**
 * Created by Makweb on 24.06.2019.
 */
abstract class BaseMessage(
    val id: String,
    val from: User,
    val chat: Chat,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = false
) {
    abstract fun formatMessage(): String

    fun getAuthorName(): String = from.firstName ?: "??"

    abstract fun getShortMessage(): String

    companion object AbstractFactory {
        var lastId = -1

        fun makeMessage(
            from: User,
            chat: Chat,
            date: Date = Date(),
            type: String = "text",
            payload: Any?
        ): BaseMessage {
            lastId++
            return when (type) {
                "image" -> ImageMessage(
                    id = "$lastId",
                    from = from,
                    chat = chat,
                    date = date,
                    image = payload as String
                )
                else -> TextMessage(
                    id = "$lastId",
                    from = from,
                    chat = chat,
                    date = date,
                    text = payload as String
                )
            }
        }
    }
}