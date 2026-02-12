package com.mintflow.personal.model

import java.util.Date

data class Transaction(
    val id: String,
    val title: String,
    val category: String,
    val price: Double,
    val date: Date,
    var type: String
)