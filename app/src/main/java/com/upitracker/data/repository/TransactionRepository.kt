package com.upitracker.data.repository

import com.upitracker.data.database.dao.CategoryDao
import com.upitracker.data.database.dao.TransactionDao
import com.upitracker.data.database.entities.CategoryEntity
import com.upitracker.data.database.entities.TransactionEntity
import com.upitracker.data.models.Category
import com.upitracker.data.models.Transaction
import com.upitracker.data.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }
    
    fun getTransactionsBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetween(startDate, endDate).map { entities ->
            entities.map { it.toTransaction() }
        }
    }
    
    fun searchTransactions(query: String): Flow<List<Transaction>> {
        return transactionDao.searchTransactions(query).map { entities ->
            entities.map { it.toTransaction() }
        }
    }
    
    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)?.toTransaction()
    }
    
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }
    
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }
    
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }
    
    suspend fun getTotalSpent(startDate: LocalDateTime, endDate: LocalDateTime): Double {
        return transactionDao.getTotalAmountByType("DEBIT", startDate, endDate) ?: 0.0
    }
    
    suspend fun getTotalReceived(startDate: LocalDateTime, endDate: LocalDateTime): Double {
        return transactionDao.getTotalAmountByType("CREDIT", startDate, endDate) ?: 0.0
    }
    
    // Category operations
    fun getActiveCategories(): Flow<List<Category>> {
        return categoryDao.getActiveCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }
    
    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toCategory()
    }
    
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }
    
    // Extension functions for conversion
    private fun TransactionEntity.toTransaction(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            type = TransactionType.valueOf(type),
            description = description,
            merchant = merchant,
            upiId = upiId,
            referenceNumber = referenceNumber,
            timestamp = timestamp,
            category = categoryId?.toString(),
            userNote = userNote,
            attachmentPath = attachmentPath,
            rawMessage = rawMessage,
            isManuallyVerified = isManuallyVerified,
            createdAt = createdAt
        )
    }
    
    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            type = type.name,
            description = description,
            merchant = merchant,
            upiId = upiId,
            referenceNumber = referenceNumber,
            timestamp = timestamp,
            categoryId = category?.toLongOrNull(),
            userNote = userNote,
            attachmentPath = attachmentPath,
            rawMessage = rawMessage,
            isManuallyVerified = isManuallyVerified,
            createdAt = createdAt
        )
    }
    
    private fun CategoryEntity.toCategory(): Category {
        return Category(
            id = id,
            name = name,
            icon = icon,
            color = color,
            monthlyBudget = monthlyBudget,
            isActive = isActive
        )
    }
    
    private fun Category.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            icon = icon,
            color = color,
            monthlyBudget = monthlyBudget,
            isActive = isActive
        )
    }
}