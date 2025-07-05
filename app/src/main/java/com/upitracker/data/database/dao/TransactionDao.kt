package com.upitracker.data.database.dao

import androidx.room.*
import com.upitracker.data.database.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getTransactionsBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' OR merchant LIKE '%' || :query || '%' OR userNote LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getTotalAmountByType(type: String, startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :categoryId AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getTotalAmountByCategory(categoryId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT * FROM transactions WHERE referenceNumber = :refNumber LIMIT 1")
    suspend fun getTransactionByReference(refNumber: String): TransactionEntity?
}