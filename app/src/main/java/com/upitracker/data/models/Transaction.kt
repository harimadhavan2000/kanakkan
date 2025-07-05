package com.upitracker.data.models

import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val description: String,
    val merchant: String?,
    val upiId: String?,
    val referenceNumber: String?,
    val timestamp: LocalDateTime,
    val category: String?,
    val userNote: String?,
    val attachmentPath: String?,
    val rawMessage: String,
    val isManuallyVerified: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    DEBIT,
    CREDIT
}

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String?,
    val color: String,
    val monthlyBudget: Double?,
    val isActive: Boolean = true
)

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
)