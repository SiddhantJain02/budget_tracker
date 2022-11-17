package com.example.budgettracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable
import java.util.*

@Entity(tableName = "transactions")

class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label: String,
    val amount: Double,
    val description: String,
    @TypeConverters(DateTypeConverter::class)
    val date: Date,


    ): Serializable {
}