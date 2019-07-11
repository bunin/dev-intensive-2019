package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val validateText = question.validate(answer)
        if (validateText != "") {
            return "$validateText\n${question.question}" to status.color
        }
        if (question.answers.contains(answer.toLowerCase())) {
//            status = status.prevStatus()
            question = question.nextQuestion()
            return "Отлично - ты справился\n${question.question}" to status.color
        } else {
            if (status == Status.CRITICAL && status.nextStatus() == Status.CRITICAL) {
                status = Status.NORMAL
                question = Question.NAME
                return "Это неправильный ответ. Давай все по новой\n${question.question}" to status.color
            }
            status = status.nextStatus()
            return "Это неправильный ответ\n${question.question}" to status.color

        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun prevStatus(): Status = when {
            ordinal > 0 -> values()[ordinal - 1]
            else -> values()[0]
        }

        fun nextStatus(): Status = when {
            this.ordinal < values().lastIndex -> values()[this.ordinal + 1]
            else -> CRITICAL
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "bender")) {
            override fun nextQuestion(): Question = PROFESSION
            override fun validate(answer: String) = if (answer.isNotEmpty() && answer[0].isUpperCase()) {
                ""
            } else {
                "Имя должно начинаться с заглавной буквы"
            }
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validate(answer: String) = if (answer.isNotEmpty() && answer[0].isLowerCase()) {
                ""
            } else {
                "Профессия должна начинаться со строчной буквы"
            }
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun nextQuestion(): Question = BDAY
            override fun validate(answer: String) = if (answer.contains(Regex("\\d+"))) {
                "Материал не должен содержать цифр"
            } else {
                ""
            }
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun nextQuestion(): Question = SERIAL
            override fun validate(answer: String) = if (answer.contains(Regex("\\D+"))) {
                "Год моего рождения должен содержать только цифры"
            } else {
                ""
            }
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String) = if (answer.contains(Regex("\\D+")) || answer.length != 7) {
                "Серийный номер содержит только цифры, и их 7"
            } else {
                ""
            }
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(answer: String) = ""
        };

        abstract fun nextQuestion(): Question
        abstract fun validate(answer: String): String
    }
}