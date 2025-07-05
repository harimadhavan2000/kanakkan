# UPI Transaction Tracker

An Android app that automatically tracks UPI transactions from bank SMS notifications, categorizes them using on-device AI (Gemma 3N 2B), and provides comprehensive reporting features.

## Features

- **Automatic Transaction Detection**: Uses NotificationListenerService to capture bank SMS notifications
- **AI-Powered SMS Parsing**: Uses Gemma 3N 2B LLM to intelligently parse transaction details from any SMS format
- **AI-Powered Categorization**: On-device Gemma 3N 2B LLM for automatic transaction categorization
- **Manual Category Override**: Users can correct/change categories
- **OCR Bill Scanning**: ML Kit integration for scanning and attaching receipts
- **Comprehensive Search**: Full-text search across transactions
- **Detailed Reports**: Visual reports with charts and category breakdowns
- **Privacy-First**: All processing happens on-device, no data leaves the phone

## Architecture

- **MVVM Pattern** with Repository layer
- **Jetpack Compose** for modern UI
- **Room Database** for local storage
- **MediaPipe LLM API** with Gemma 3N 2B for AI operations
- **ML Kit** for OCR functionality

## Setup Instructions

1. Clone the repository
2. Open in Android Studio (Arctic Fox or later)
3. Download the Gemma 3N E2B model file (gemma-3n-E2B-it-int4.task) from Hugging Face
4. Place the model file in `app/src/main/assets/`
5. Build and run the app

## Permissions Required

- **Notification Access**: To read bank SMS notifications
- **Camera**: For scanning bills and receipts

## Key Components

### LLM-Based Transaction Parser
- Uses Gemma 3N 2B to intelligently parse SMS messages
- Understands context and handles any bank format
- No need for bank-specific regex patterns
- Extracts: amount, type (debit/credit), merchant, UPI ID, reference number
- Handles typos, abbreviations, and mixed languages

### AI Categorizer
- Uses Gemma 3N 2B (4-bit quantized) for on-device inference
- Falls back to rule-based categorization
- Learns from user feedback

### Database Schema
- **Transactions**: Stores all transaction details
- **Categories**: User-defined categories with budgets
- Room database with type converters for LocalDateTime

### UI Screens
- **Home**: Transaction list with search and summary
- **Transaction Detail**: Edit category, add notes, attach bills
- **Categories**: Manage categories and budgets
- **Reports**: Visual analytics and export options

## Future Enhancements

- Support for more languages
- Multimodal capabilities when Gemma 3N multimodal is available
- Cloud backup option
- Budget alerts
- Expense predictions
- Multi-user support

## Development Notes

- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Kotlin version: 1.9.20
- Compose BOM: 2023.10.01

## License

This project is for demonstration purposes.