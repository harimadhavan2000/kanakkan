package com.upitracker.domain.parser

import com.upitracker.data.models.Transaction
import com.upitracker.data.models.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class TransactionParser {
    
    companion object {
        // Common UPI transaction patterns
        private val AMOUNT_PATTERNS = listOf(
            Pattern.compile("(?i)(?:RS\\.?|INR\\.?|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i)amount[:\\s]+(?:RS\\.?|INR\\.?|₹)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
        )
        
        private val DEBIT_KEYWORDS = listOf("debited", "paid", "sent", "transferred", "withdrawn", "debit")
        private val CREDIT_KEYWORDS = listOf("credited", "received", "deposited", "credit")
        
        private val UPI_ID_PATTERN = Pattern.compile("(?i)(?:to|from|UPI:?)\\s*([a-zA-Z0-9._-]+@[a-zA-Z0-9]+)")
        private val REF_NUMBER_PATTERN = Pattern.compile("(?i)(?:ref\\.?\\s*(?:no\\.?)?|reference|txn|transaction)\\s*[:#]?\\s*([A-Z0-9]+)")
        private val MERCHANT_PATTERNS = listOf(
            Pattern.compile("(?i)(?:to|at|merchant:?)\\s+([A-Za-z0-9\\s&.-]+?)(?:\\s+on|\\s+ref|\\s+txn|\\s+upi|$)"),
            Pattern.compile("(?i)paid\\s+to\\s+([A-Za-z0-9\\s&.-]+)")
        )
    }
    
    fun parseTransaction(message: String, timestamp: LocalDateTime = LocalDateTime.now()): Transaction? {
        if (!isValidTransactionMessage(message)) {
            return null
        }
        
        val amount = extractAmount(message) ?: return null
        val type = extractTransactionType(message)
        val upiId = extractUpiId(message)
        val refNumber = extractReferenceNumber(message)
        val merchant = extractMerchant(message)
        val description = generateDescription(type, amount, merchant, upiId)
        
        return Transaction(
            amount = amount,
            type = type,
            description = description,
            merchant = merchant,
            upiId = upiId,
            referenceNumber = refNumber,
            timestamp = timestamp,
            category = null,
            userNote = null,
            attachmentPath = null,
            rawMessage = message,
            isManuallyVerified = false
        )
    }
    
    private fun isValidTransactionMessage(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return (DEBIT_KEYWORDS.any { lowerMessage.contains(it) } || 
                CREDIT_KEYWORDS.any { lowerMessage.contains(it) }) &&
                (lowerMessage.contains("upi") || lowerMessage.contains("account") || 
                 lowerMessage.contains("wallet"))
    }
    
    private fun extractAmount(message: String): Double? {
        for (pattern in AMOUNT_PATTERNS) {
            val matcher = pattern.matcher(message)
            if (matcher.find()) {
                val amountStr = matcher.group(1)
                    ?.replace(",", "")
                    ?.trim()
                return amountStr?.toDoubleOrNull()
            }
        }
        return null
    }
    
    private fun extractTransactionType(message: String): TransactionType {
        val lowerMessage = message.lowercase()
        return if (DEBIT_KEYWORDS.any { lowerMessage.contains(it) }) {
            TransactionType.DEBIT
        } else {
            TransactionType.CREDIT
        }
    }
    
    private fun extractUpiId(message: String): String? {
        val matcher = UPI_ID_PATTERN.matcher(message)
        return if (matcher.find()) {
            matcher.group(1)?.trim()
        } else null
    }
    
    private fun extractReferenceNumber(message: String): String? {
        val matcher = REF_NUMBER_PATTERN.matcher(message)
        return if (matcher.find()) {
            matcher.group(1)?.trim()
        } else null
    }
    
    private fun extractMerchant(message: String): String? {
        for (pattern in MERCHANT_PATTERNS) {
            val matcher = pattern.matcher(message)
            if (matcher.find()) {
                return matcher.group(1)?.trim()
            }
        }
        
        // Fallback: try to extract from UPI ID
        val upiId = extractUpiId(message)
        return upiId?.substringBefore("@")?.replace(".", " ")?.capitalizeWords()
    }
    
    private fun generateDescription(type: TransactionType, amount: Double, merchant: String?, upiId: String?): String {
        val action = if (type == TransactionType.DEBIT) "Paid" else "Received"
        val target = merchant ?: upiId?.substringBefore("@") ?: "Unknown"
        return "$action ₹$amount ${if (type == TransactionType.DEBIT) "to" else "from"} $target"
    }
    
    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}