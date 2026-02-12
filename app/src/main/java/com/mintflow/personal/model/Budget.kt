package com.mintflow.personal.model

import java.util.Calendar

data class Budget(
    var spent: Double,
    var budget: Double,
    var month: Int = Calendar.getInstance().get(Calendar.MONTH),
    var year: Int = Calendar.getInstance().get(Calendar.YEAR),
)