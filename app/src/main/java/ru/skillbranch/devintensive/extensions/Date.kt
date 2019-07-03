package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time

    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val isInPast = this.time < date.time
    val diff = abs(this.time - date.time)
    val diffSeconds = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
    val diffMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
    val diffHours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
    val diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    if (abs(diffSeconds) <= 1)
        return "только что"
    if (diffSeconds <= 45) {
        return wrap("несколько секунд", isInPast)
    }
//    if (diffSeconds <= 75) {
//        return minutesDiff(diffMinutes, isInPast)
//    }
    if (diffMinutes <= 45)
        return minutesDiff(diffMinutes, isInPast)
    if (diffHours <= 22)
        return wrap(plural(diffHours.toInt(), Triple("час", "часа", "часов")), isInPast)
    if (diffDays <= 360)
        return wrap(plural(diffDays.toInt(), Triple("день", "дня", "дней")), isInPast)
    return if (isInPast) "более года назад" else "более чем через год"
}

private fun minutesDiff(n: Long, past: Boolean): String {
    return wrap(plural(n.toInt(), Triple("минуту", "минуты", "минут")), past)
}

private fun wrap(s: String, past: Boolean): String = if (past) "$s назад" else "через $s"

fun plural(n: Int): Int {
    return (if (n % 10 == 1 && n % 100 != 11) 0 else if (n % 10 in 2..4 && (n % 100 < 10 || n % 100 >= 20)) 1 else 2)
}

fun plural(n: Int, s: Triple<String, String, String>, skipOne: Boolean = true): String {
    val i = plural(n)
    if (n == 1 && skipOne) {
        return s.first
    }
    return when (i) {
        0 -> "$n ${s.first}"
        1 -> "$n ${s.second}"
        else -> "$n ${s.third}"
    }
}

enum class TimeUnits {
    SECOND {
        override fun plural(value: Int): String = plural(value, Triple("секунду", "секунды", "секунд"), false)
    },
    MINUTE {
        override fun plural(value: Int): String = plural(value, Triple("минуту", "минуты", "минут"), false)
    },
    HOUR {
        override fun plural(value: Int): String = plural(value, Triple("час", "часа", "часов"), false)
    },
    DAY {
        override fun plural(value: Int): String = plural(value, Triple("день", "дня", "дней"), false)
    };

    abstract fun plural(value: Int): String
}