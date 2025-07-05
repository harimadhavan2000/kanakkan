package com.upitracker

/**
 * Examples of how the LLM-based parser handles various SMS formats
 * The LLM can understand and extract information from diverse message formats
 * without needing specific regex patterns for each bank
 */
class LLMParserExamples {
    
    companion object {
        // The LLM-based parser can handle all these formats and more:
        
        val exampleMessages = listOf(
            // Standard UPI messages
            "Dear Customer, Rs.1,500.00 debited from your a/c XXXXXXX1234 on 15-12-23 to UPI ID swiggy@paytm. Ref No. 334512345678. Call 18001234567 for dispute. -SBI",
            
            // Different format from HDFC
            "Rs 2,345.67 debited frm a/c **3456 on 15-DEC-23 to VPA amazon@ybl (UPI Ref No 345612345678). Not you? Call 18002586161. -HDFC Bank",
            
            // ICICI format
            "ICICI Bank Acc XX892 debited with Rs 500.00 on 15-Dec-23; UPI: grocerystore@paytm. UPI Ref: 456723456789. Call 18001080 for dispute.",
            
            // Paytm format
            "Paid Rs.750 to Uber India via Paytm UPI. Transaction ID: 567834567890. Your Paytm balance is Rs.1,234.56",
            
            // Google Pay format
            "You paid ₹123.45 to PhonePe Recharge using Google Pay. UPI transaction ID: 678945678901",
            
            // Credit transaction
            "Your a/c XX1234 is credited with INR 5,000.00 on 15-12-23 by UPI Ref no. 789056789012. -Kotak Bank",
            
            // Complex merchant names
            "Rs.890.50 debited from your account ending 5678 for payment to The Coffee House & Bakery Pvt Ltd via UPI. Ref: 890167890123",
            
            // Mixed language
            "आपके खाते XX789 से Rs.350 डेबिट किया गया है। UPI: medicalstore@okaxis Ref: 901278901234",
            
            // Wallet transactions
            "Payment of Rs.456 to Swiggy from your MobiKwik wallet is successful. Order ID: FOOD123456789",
            
            // Different date formats
            "Amount of Rs 234.00 has been debited from your account on 15/12/2023 14:30:45 to zomato@paytm. Transaction ref: 012389012345"
        )
        
        /**
         * The LLM understands context and can extract:
         * - Amount in various formats (Rs., Rs, ₹, INR)
         * - Transaction type (debit/credit/paid/received)
         * - Merchant names (even complex ones)
         * - UPI IDs in different formats
         * - Reference numbers with various prefixes
         * - Dates and times
         * 
         * It can also:
         * - Handle typos and abbreviations
         * - Understand mixed languages
         * - Extract partial information when complete data isn't available
         * - Distinguish between actual transactions and promotional messages
         */
    }
}