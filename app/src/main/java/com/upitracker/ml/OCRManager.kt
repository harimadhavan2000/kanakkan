package com.upitracker.ml

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRManager(private val context: Context) {
    
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    data class OCRResult(
        val fullText: String,
        val lines: List<String>,
        val extractedAmount: Double?,
        val extractedDate: String?,
        val extractedMerchant: String?
    )
    
    suspend fun processImage(imageUri: Uri): OCRResult? {
        return try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            recognizeText(inputImage)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun processImage(bitmap: Bitmap): OCRResult? {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            recognizeText(inputImage)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun recognizeText(inputImage: InputImage): OCRResult = 
        suspendCancellableCoroutine { continuation ->
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val result = parseTextResult(visionText.text)
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    
    private fun parseTextResult(text: String): OCRResult {
        val lines = text.split("\n").filter { it.isNotBlank() }
        
        val amount = extractAmount(text)
        val date = extractDate(text)
        val merchant = extractMerchant(lines)
        
        return OCRResult(
            fullText = text,
            lines = lines,
            extractedAmount = amount,
            extractedDate = date,
            extractedMerchant = merchant
        )
    }
    
    private fun extractAmount(text: String): Double? {
        // Common patterns for amounts in bills
        val patterns = listOf(
            Regex("(?i)total[:\\s]*(?:RS\\.?|INR\\.?|₹)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)"),
            Regex("(?i)amount[:\\s]*(?:RS\\.?|INR\\.?|₹)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)"),
            Regex("(?i)grand\\s*total[:\\s]*(?:RS\\.?|INR\\.?|₹)?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)"),
            Regex("₹\\s*([0-9,]+(?:\\.[0-9]{1,2})?)")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "")
                return amountStr.toDoubleOrNull()
            }
        }
        
        return null
    }
    
    private fun extractDate(text: String): String? {
        // Common date patterns
        val patterns = listOf(
            Regex("\\d{2}/\\d{2}/\\d{4}"),
            Regex("\\d{2}-\\d{2}-\\d{4}"),
            Regex("\\d{1,2}\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{4}", RegexOption.IGNORE_CASE)
        )
        
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return match.value
            }
        }
        
        return null
    }
    
    private fun extractMerchant(lines: List<String>): String? {
        // Usually the merchant name is in the first few lines
        if (lines.isEmpty()) return null
        
        // Take the first non-empty line that looks like a merchant name
        return lines.take(5).firstOrNull { line ->
            line.length > 3 && !line.matches(Regex("^[0-9\\s.,/-]+$"))
        }
    }
    
    fun release() {
        // ML Kit manages its own resources
    }
}