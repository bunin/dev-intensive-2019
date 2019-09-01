package ru.skillbranch.devintensive.utils

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue


object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.trim()?.split("\\s+".toRegex())

        var firstName = parts?.getOrNull(0)
        var lastName = parts?.getOrNull(1)
        if (firstName.isNullOrEmpty()) {
            firstName = null
        }
        if (lastName.isNullOrEmpty()) {
            lastName = null
        }

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = "_"): String {
        val from = arrayListOf(
            ' ',
            'а',
            'б',
            'в',
            'г',
            'д',
            'е',
            'ё',
            'ж',
            'з',
            'и',
            'й',
            'к',
            'л',
            'м',
            'н',
            'о',
            'п',
            'р',
            'с',
            'т',
            'у',
            'ф',
            'х',
            'ц',
            'ч',
            'ш',
            'щ',
            'ъ',
            'ы',
            'ь',
            'э',
            'ю',
            'я'
        )
        val to = arrayListOf(
            divider,
            "a",
            "b",
            "v",
            "g",
            "d",
            "e",
            "e",
            "zh",
            "z",
            "i",
            "i",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "r",
            "s",
            "t",
            "u",
            "f",
            "h",
            "c",
            "ch",
            "sh",
            "sh'",
            "",
            "i",
            "",
            "e",
            "yu",
            "ya"
        )
        val builder = StringBuilder()
        for (currentChar in payload.trim()) {
            var found = false
            for (x in from.indices) {
                if (currentChar == from[x]) {
                    found = true
                    builder.append(to[x])
                    break
                }
                if (currentChar.toUpperCase() == from[x].toUpperCase()) {
                    found = true
                    builder.append(to[x].capitalize())
                    break
                }
            }
            if (!found) {
                builder.append(currentChar)
            }
        }
        return builder.toString().replace(Regex("\\s+"), divider)
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var res = ""
        if (!firstName.isNullOrBlank()) {
            res += firstName.trim()[0]
        }
        if (!lastName.isNullOrBlank()) {
            res += lastName.trim()[0]
        }
        return if (res.isBlank()) null else res.toUpperCase()
    }

    fun isValidRepo(value: String): Boolean =
        value.matches(Regex("^(?:https://|https://www\\.|www\\.|^)github\\.com/(?!enterprise|features|topics|collections|trending|events|marketplace|pricing|nonprofit|customer-stories|security|login|join)[^/\\s\\n]+\$"))


    fun isNightMode(ctx: Context): Boolean {
        val currentNightMode =
            ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

    }

    fun getColor(ctx: Context, id: Int): Int {
        val typedValue = TypedValue()
        val a = ctx.obtainStyledAttributes(typedValue.data, intArrayOf(id))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

}