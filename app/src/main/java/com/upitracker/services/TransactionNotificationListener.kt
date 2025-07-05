package com.upitracker.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.upitracker.data.database.TransactionDatabase
import com.upitracker.data.database.entities.TransactionEntity
import com.upitracker.domain.parser.TransactionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TransactionNotificationListener : NotificationListenerService() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var database: TransactionDatabase
    private lateinit var parser: TransactionParser
    
    companion object {
        // Bank app package names
        private val BANK_PACKAGES = setOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "net.one97.paytm", // Paytm
            "com.phonepe.app", // PhonePe
            "in.amazon.mShop.android.shopping", // Amazon Pay
            "com.icicibank.imobile", // ICICI
            "com.sbi.upi", // SBI
            "com.hdfc.bank", // HDFC
            "com.axis.mobile", // Axis
            "com.csam.icici.bank.imobile", // ICICI iMobile
            "com.mobikwik_new", // MobiKwik
            "com.freecharge.android", // FreeCharge
            "com.myairtelapp" // Airtel
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        database = TransactionDatabase.getDatabase(this)
        parser = TransactionParser(this)
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        
        // Check if notification is from a bank app
        if (sbn.packageName !in BANK_PACKAGES) {
            return
        }
        
        val notification = sbn.notification
        val extras = notification.extras
        
        // Extract notification text
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
        val bigText = extras.getString(Notification.EXTRA_BIG_TEXT) ?: text
        
        // Combine all text for parsing
        val fullMessage = "$title $bigText".trim()
        
        if (fullMessage.isNotEmpty()) {
            processNotification(fullMessage)
        }
    }
    
    private fun processNotification(message: String) {
        serviceScope.launch {
            try {
                // Parse the transaction
                val transaction = parser.parseTransaction(message) ?: return@launch
                
                // Check if transaction already exists (by reference number)
                transaction.referenceNumber?.let { refNum ->
                    val existing = database.transactionDao().getTransactionByReference(refNum)
                    if (existing != null) {
                        return@launch // Transaction already exists
                    }
                }
                
                // Convert to entity and save
                val entity = TransactionEntity(
                    amount = transaction.amount,
                    type = transaction.type.name,
                    description = transaction.description,
                    merchant = transaction.merchant,
                    upiId = transaction.upiId,
                    referenceNumber = transaction.referenceNumber,
                    timestamp = transaction.timestamp,
                    categoryId = null, // Will be categorized later
                    userNote = null,
                    attachmentPath = null,
                    rawMessage = transaction.rawMessage,
                    isManuallyVerified = false
                )
                
                database.transactionDao().insertTransaction(entity)
                
                // TODO: Trigger categorization using LLM
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up if needed
    }
}