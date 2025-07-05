package com.upitracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // DEBIT or CREDIT
    val description: String,
    val merchant: String?,
    val upiId: String?,
    val referenceNumber: String?,
    val timestamp: LocalDateTime,
    val categoryId: Long?,
    val userNote: String?,
    val attachmentPath: String?,
    val rawMessage: String,
    val isManuallyVerified: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String?,
    val color: String,
    val monthlyBudget: Double?,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)