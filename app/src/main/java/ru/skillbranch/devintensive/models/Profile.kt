package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils

data class Profile(
//    val nickname: String,

    val firstName: String,
    val lastName: String,
    val about: String,
    val repository: String,
    val rating: Int = 0,
    val respect: Int = 0
) {
    private val nickName: String
        get() {
            return when {
                lastName.isBlank() -> Utils.transliteration(firstName)
                firstName.isBlank() -> Utils.transliteration(lastName)
                else -> Utils.transliteration("$firstName $lastName")
            }
        }
    val rank: String = "Junior Android Developer"

    fun toMap(): Map<String, Any> = mapOf(
        "nickName" to nickName,
        "rank" to rank,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository,
        "rating" to rating,
        "respect" to respect
    )
}