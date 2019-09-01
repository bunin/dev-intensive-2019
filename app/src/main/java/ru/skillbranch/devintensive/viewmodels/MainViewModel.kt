package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats
            .filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()
        val filterF = {
            val queryStr = query.value!!
            val chats = handleArchiveRow(chats.value!!)
            result.value = if (queryStr.isEmpty()) chats
            else chats.filter { it.title.contains(queryStr, true) }
//            result.value = handleArchiveRow(result.value!!)
        }
        result.addSource(chats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }
        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    private fun handleArchiveRow(chats: List<ChatItem>): List<ChatItem> {
        val archiveData = chatRepository.getArchiveData()
        if (archiveData.archivedCount < 1) return chats
        val f = listOf(
            ChatItem(
                ChatRepository.ARCHIVE_ITEM_ID,
                null,
                "",
                "Архив чатов",
                archiveData.archivedLastMessage,
                archiveData.archivedUnreadCount,
                archiveData.archivedLastDate?.shortFormat(),
                false,
                ChatType.ARCHIVE,
                archiveData.archivedLastAuthor
            )
        )
        return f + chats
    }

}