package com.upitracker.ml

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GemmaLLMManager(private val context: Context) {
    
    private var llmInference: LlmInference? = null
    private var isInitialized = false
    
    companion object {
        private const val MODEL_PATH = "gemma-2b-it-gpu-int4.bin"
        private const val MAX_TOKENS = 512
        private const val TEMPERATURE = 0.7f
        private const val TOP_K = 40
        private const val RANDOM_SEED = 101
    }
    
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext
        
        try {
            val modelPath = copyModelToInternalStorage()
            
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(MAX_TOKENS)
                .setTemperature(TEMPERATURE)
                .setTopK(TOP_K)
                .setRandomSeed(RANDOM_SEED)
                .build()
            
            llmInference = LlmInference.createFromOptions(context, options)
            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    suspend fun categorizeTransaction(
        transaction: String,
        merchant: String?,
        amount: Double,
        availableCategories: List<String>
    ): String? = withContext(Dispatchers.Default) {
        if (!isInitialized) {
            initialize()
        }
        
        val prompt = buildCategorizationPrompt(transaction, merchant, amount, availableCategories)
        
        return try {
            val response = llmInference?.generateResponse(prompt)
            parseCategorizationResponse(response, availableCategories)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun buildCategorizationPrompt(
        transaction: String,
        merchant: String?,
        amount: Double,
        categories: List<String>
    ): String {
        return """
            Task: Categorize the following transaction into one of the given categories.
            
            Transaction Details:
            - Description: $transaction
            - Merchant: ${merchant ?: "Unknown"}
            - Amount: ₹$amount
            
            Available Categories:
            ${categories.joinToString("\n") { "- $it" }}
            
            Rules:
            1. Choose ONLY from the given categories
            2. If uncertain, choose the most likely category
            3. Reply with just the category name, nothing else
            
            Category:
        """.trimIndent()
    }
    
    private fun parseCategorizationResponse(response: String?, availableCategories: List<String>): String? {
        if (response.isNullOrBlank()) return null
        
        val cleanResponse = response.trim().lowercase()
        
        // Find the best matching category
        return availableCategories.find { category ->
            cleanResponse.contains(category.lowercase())
        } ?: availableCategories.firstOrNull { category ->
            // Fuzzy match - check if any word in the response matches category
            val categoryWords = category.lowercase().split(" ")
            categoryWords.any { word -> cleanResponse.contains(word) }
        }
    }
    
    private suspend fun copyModelToInternalStorage(): String = withContext(Dispatchers.IO) {
        val modelFile = File(context.filesDir, MODEL_PATH)
        
        if (!modelFile.exists()) {
            // In production, download from a secure source
            // For now, assume the model is in assets
            try {
                context.assets.open(MODEL_PATH).use { input ->
                    modelFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                // Model not in assets, will need to be downloaded
                throw Exception("Model file not found. Please download the Gemma 2B model.")
            }
        }
        
        return modelFile.absolutePath
    }
    
    fun release() {
        llmInference?.close()
        llmInference = null
        isInitialized = false
    }
}