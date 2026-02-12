package com.mintflow.personal.model

class Validations {
    public fun isEmailValid(email: String): Boolean {
        val emailRegex =
            Regex("^(?=.{1,64}@)[A-Za-z0-9+_-]+(\\.[A-Za-z0-9+_-]+)*@[^-][A-Za-z0-9+-]+(\\.[A-Za-z0-9+-]+)*(\\.[A-Za-z]{2,})$")
        return emailRegex.matches(email)
    }

    public fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        return passwordRegex.matches(password)
    }
    public fun isSpaceInBetween(text: String): Boolean {
        return text.contains(" ")
    }
}