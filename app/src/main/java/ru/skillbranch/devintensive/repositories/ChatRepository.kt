package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat

object ChatRepository {
    private val chats = CacheManager.loadChats()

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
    }

    fun find(chatId: String): Chat? {
        val i = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(i)
    }
}