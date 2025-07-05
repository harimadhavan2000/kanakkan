package com.upitracker.domain.categorizer

import android.content.Context
import com.upitracker.data.models.Transaction
import com.upitracker.data.repository.TransactionRepository
import com.upitracker.ml.GemmaLLMManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionCategorizer @Inject constructor(
    private val context: Context,
    private val repository: TransactionRepository
) {
    private val llmManager = GemmaLLMManager(context)
    
    // Fallback rule-based categories
    private val keywordCategories = mapOf(
        "Food & Dining" to listOf("swiggy", "zomato", "restaurant", "cafe", "food", "dining", "pizza", "burger"),
        "Shopping" to listOf("amazon", "flipkart", "myntra", "shop", "store", "mart", "mall"),
        "Transportation" to listOf("uber", "ola", "rapido", "cab", "taxi", "bus", "metro", "fuel", "petrol"),
        "Bills & Utilities" to listOf("electricity", "water", "gas", "internet", "mobile", "recharge", "bill"),
        "Entertainment" to listOf("movie", "netflix", "prime", "spotify", "game", "play", "book"),
        "Healthcare" to listOf("medical", "doctor", "hospital", "pharmacy", "medicine", "health"),
        "Education" to listOf("school", "college", "course", "book", "tuition", "education"),
        "Groceries" to listOf("grocery", "vegetable", "fruit", "milk", "bigbasket", "grofers", "dunzo"),
        "Investment" to listOf("mutual fund", "stock", "invest", "sip", "trading"),
        "Others" to listOf() // Default category
    )
    
    suspend fun categorizeTransaction(transaction: Transaction): String {
        try {
            // Get available categories from database
            val categories = repository.getActiveCategories().first()
            val categoryNames = categories.map { it.name }.ifEmpty { 
                keywordCategories.keys.toList() 
            }
            
            // First try LLM categorization
            val llmCategory = llmManager.categorizeTransaction(
                transaction = transaction.description,
                merchant = transaction.merchant,
                amount = transaction.amount,
                availableCategories = categoryNames
            )
            
            if (llmCategory != null) {
                return llmCategory
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Fallback to rule-based categorization
        return categorizeByRules(transaction)
    }
    
    private fun categorizeByRules(transaction: Transaction): String {
        val searchText = "${transaction.description} ${transaction.merchant ?: ""}".lowercase()
        
        for ((category, keywords) in keywordCategories) {
            if (keywords.any { keyword -> searchText.contains(keyword) }) {
                return category
            }
        }
        
        return "Others"
    }
    
    suspend fun learnFromUserFeedback(transaction: Transaction, correctCategory: String) {
        // Store user feedback for future improvements
        // This could be used to fine-tune the model or improve rules
        // For now, just update the transaction
        val updatedTransaction = transaction.copy(
            category = correctCategory,
            isManuallyVerified = true
        )
        repository.updateTransaction(updatedTransaction)
    }
    
    fun getDefaultCategories(): List<String> {
        return keywordCategories.keys.toList()
    }
}