package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView(): UserView {
    val nickName = Utils.transliteration("$firstName $lastName")
    val initials = Utils.toInitials(firstName, lastName)
    val status =
        if (lastVisit == null) "Hasn't been online" else if (isOnline) "Online" else "Last seen ${lastVisit?.humanizeDiff()}"


    return UserView(
        id,
        fullName = "$firstName $lastName",
        nickName = nickName,
        initials = initials,
        avatar = avatar,
        status = status
    )
}