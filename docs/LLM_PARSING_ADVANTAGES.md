# LLM-Based Transaction Parsing vs Regex

## Why LLM-Based Parsing?

### 1. **Universal Format Support**
- **Regex**: Requires specific patterns for each bank's SMS format
- **LLM**: Understands any format automatically through context

### 2. **Maintenance-Free**
- **Regex**: Needs updates when banks change their SMS formats
- **LLM**: Adapts to new formats without code changes

### 3. **Intelligent Context Understanding**
- **Regex**: Can only match exact patterns
- **LLM**: Understands context, handles typos, abbreviations

### 4. **Multi-Language Support**
- **Regex**: Complex patterns needed for each language
- **LLM**: Naturally handles mixed languages and scripts

## Examples

### Example 1: Standard Format
```
SMS: "Rs.500 debited from a/c XX1234 to paytm-grocery@paytm Ref: 123456"
LLM extracts:
- Amount: 500
- Type: DEBIT
- Merchant: paytm-grocery
- UPI ID: paytm-grocery@paytm
- Reference: 123456
```

### Example 2: Complex Format
```
SMS: "Payment of ₹1,234.56 made to The Coffee House & Restaurant Pvt Ltd successfully via UPI. Transaction ID: ABC123XYZ"
LLM extracts:
- Amount: 1234.56
- Type: DEBIT
- Merchant: The Coffee House & Restaurant Pvt Ltd
- Reference: ABC123XYZ
```

### Example 3: Typos and Variations
```
SMS: "Amt Rs 750/- debitd frm ur acc for Big Bazaar payment. UPI ref 789012"
LLM understands despite:
- "Amt" instead of "Amount"
- "debitd" instead of "debited"
- "frm" instead from "from"
- "ur" instead of "your"
```

### Example 4: New Bank Format
```
SMS: "Transaction Alert: You've sent 2000 rupees to phonepe@ybl using NewBank UPI. Reference: NBK2023121500001"
LLM works immediately without needing regex updates
```

## Technical Implementation

The LLM receives a carefully crafted prompt that instructs it to:
1. Identify if the message is a valid transaction
2. Extract specific fields in JSON format
3. Handle edge cases gracefully

This approach ensures:
- Consistent output format
- Reliable parsing across diverse inputs
- Easy integration with the rest of the app
- Future-proof architecture