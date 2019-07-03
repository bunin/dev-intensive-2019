package ru.skillbranch.devintensive.extensions

fun String.truncate(n: Int = 16): String? {
    if (this.trim().length <= n) {
        return this.trim()
    }
    if (n < 4) {
        return this.substring(0, 2)
    }
//    return this.trim().substring(0..kotlin.math.min(this.trim().length - 4, n - 4)).trim() + "..."
    return this.trim().substring(0 until n).trim() + "..."
}

fun String.stripHtml(): String {
    return this
        .replace(Regex("<.*?>"), " ")
        .replace(Regex("&.*?;"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}