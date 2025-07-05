package com.upitracker.domain.parser

import android.content.Context
import com.upitracker.data.models.Transaction
import com.upitracker.data.models.TransactionType
import com.upitracker.ml.GemmaLLMManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionParser @Inject constructor(
    private val context: Context
) {
    private val llmManager = GemmaLLMManager(context)
    
    suspend fun parseTransaction(
        message: String, 
        timestamp: LocalDateTime = LocalDateTime.now()
    ): Transaction? = withContext(Dispatchers.Default) {
        try {
            // Initialize LLM if needed
            llmManager.initialize()
            
            // Use LLM to parse the transaction
            val prompt = buildParsingPrompt(message)
            val response = llmManager.parseTransactionMessage(prompt)
            
            if (response != null) {
                return@withContext parseTransactionFromResponse(response, message, timestamp)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return@withContext null
    }
    
    private fun buildParsingPrompt(message: String): String {
        return """
            Task: Extract transaction details from the following SMS message and return as JSON.
            
            SMS Message:
            "$message"
            
            Instructions:
            1. Identify if this is a valid UPI/bank transaction message
            2. Extract the following information:
               - amount: The transaction amount (number only, no currency symbols)
               - type: Either "DEBIT" or "CREDIT"
               - merchant: The merchant/person name (if available)
               - upiId: The UPI ID (if mentioned, format: xxx@yyy)
               - referenceNumber: Transaction reference/ID number
               - isValid: true if this is a valid transaction, false otherwise
            
            3. Return ONLY a JSON object with these fields. Example:
            {
                "isValid": true,
                "amount": 1500.00,
                "type": "DEBIT",
                "merchant": "Swiggy",
                "upiId": "swiggy@paytm",
                "referenceNumber": "123456789"
            }
            
            If not a valid transaction, return:
            {
                "isValid": false
            }
            
            JSON Response:
        """.trimIndent()
    }
    
    private fun parseTransactionFromResponse(
        jsonResponse: String,
        originalMessage: String,
        timestamp: LocalDateTime
    ): Transaction? {
        return try {
            val json = JSONObject(jsonResponse.trim())
            
            // Check if it's a valid transaction
            if (!json.optBoolean("isValid", false)) {
                return null
            }
            
            val amount = json.optDouble("amount", 0.0)
            if (amount <= 0) return null
            
            val typeStr = json.optString("type", "").uppercase()
            val type = when (typeStr) {
                "DEBIT" -> TransactionType.DEBIT
                "CREDIT" -> TransactionType.CREDIT
                else -> return null
            }
            
            val merchant = json.optString("merchant", null)?.takeIf { it.isNotBlank() }
            val upiId = json.optString("upiId", null)?.takeIf { it.isNotBlank() }
            val referenceNumber = json.optString("referenceNumber", null)?.takeIf { it.isNotBlank() }
            
            val description = generateDescription(type, amount, merchant, upiId)
            
            Transaction(
                amount = amount,
                type = type,
                description = description,
                merchant = merchant,
                upiId = upiId,
                referenceNumber = referenceNumber,
                timestamp = timestamp,
                category = null,
                userNote = null,
                attachmentPath = null,
                rawMessage = originalMessage,
                isManuallyVerified = false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun generateDescription(
        type: TransactionType, 
        amount: Double, 
        merchant: String?, 
        upiId: String?
    ): String {
        val action = if (type == TransactionType.DEBIT) "Paid" else "Received"
        val target = merchant ?: upiId?.substringBefore("@")?.replace(".", " ")?.capitalizeWords() ?: "Unknown"
        return "$action ₹$amount ${if (type == TransactionType.DEBIT) "to" else "from"} $target"
    }
    
    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}