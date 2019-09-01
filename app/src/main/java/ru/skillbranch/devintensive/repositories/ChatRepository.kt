package ru.skillbranch.devintensive.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.extensions.format
import ru.skillbranch.devintensive.models.data.Chat
import java.util.*

object ChatRepository {
    const val ARCHIVE_ITEM_ID = "archive"

    class ArchiveData(
        val archivedCount: Int,
        val archivedUnreadCount: Int,
        val archivedLastDate: Date?,
        val archivedLastAuthor: String? = null,
        val archivedLastMessage: String? = null
    )

    private val chats = CacheManager.loadChats()
    private var archivedCount: Int = 0
    private var archivedUnreadCount: Int = 0
    private var archivedLastDate: Date? = null
    private var archivedLastAuthor: String? = null
    private var archivedLastMessage: String? = null

    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats
    }

    fun update(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        val i = chats.value!!.indexOfFirst { it.id == chat.id }
        if (i == -1) {
            return
        }
        copy[i] = chat
        chats.value = copy
        updateArchivedData()
    }

    fun find(chatId: String): Chat? {
        val i = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(i)
    }

    private fun updateArchivedData() {
        archivedCount = 0
        archivedUnreadCount = 0
        archivedLastMessage = null
        archivedLastDate = null
        archivedLastAuthor = null
        chats.value!!.forEach { chat ->
            if (!chat.isArchived) {
                return@forEach
            }
            archivedCount++
            chat.messages.forEach { message ->
                if (!message.isReaded) archivedUnreadCount++
                if (archivedLastDate != null && !message.date.after(archivedLastDate)) {
                    return@forEach
                }
                archivedLastDate = message.date
                archivedLastAuthor = message.from.firstName
                archivedLastMessage = message.getShortMessage()
            }
        }
        Log.d(
            "M_ChatRepository: ",
            "$archivedCount $archivedUnreadCount $archivedLastMessage ${archivedLastDate?.format()} $archivedLastAuthor"
        )
    }

    fun getArchiveData(): ArchiveData {
        updateArchivedData()
        return ArchiveData(
            archivedCount,
            archivedUnreadCount,
            archivedLastDate,
            archivedLastAuthor,
            archivedLastMessage
        )
    }
}