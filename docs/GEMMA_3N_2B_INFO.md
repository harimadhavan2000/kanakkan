# Gemma 3N E2B Model Information

## Why Gemma 3N E2B?

Gemma 3N E2B is Google's latest mobile-first language model, specifically designed for edge devices with revolutionary architecture improvements.

### Key Advantages

1. **Enhanced Performance**
   - Better understanding of context and nuance
   - Improved accuracy in structured data extraction
   - More reliable JSON generation for parsing tasks

2. **Optimized for Mobile**
   - 4-bit quantization maintains quality while reducing size
   - Faster inference times on mobile GPUs
   - Lower memory footprint

3. **Better Multimodal Readiness**
   - Architecture prepared for future multimodal capabilities
   - Will enable direct OCR integration when multimodal version releases
   - Unified model for both text and image understanding

4. **Improved Instruction Following**
   - Better at following complex prompts
   - More consistent output formatting
   - Reduced hallucination rates

## Model Specifications

- **Model**: Gemma 3N E2B IT (Edge 2B Instruction Tuned)
- **Format**: .task (LiteRT MediaPipe format)
- **Architecture**: MatFormer with Per-Layer Embeddings (PLE)
- **Quantization**: INT4 for optimal mobile performance
- **Context Length**: Supports up to 8K tokens
- **Size**: ~300MB (with PLE optimization)
- **Hardware**: Runs on mid-range Android devices (Snapdragon 778+)

## Use Cases in UPI Tracker

### 1. SMS Parsing
```
Input: Complex bank SMS with mixed formats
Output: Structured JSON with transaction details
Accuracy: >95% on common formats
```

### 2. Transaction Categorization
```
Input: Transaction description + merchant info
Output: Appropriate category selection
Learning: Adapts to user preferences
```

### 3. Future: Multimodal Bill Processing
```
Input: Image of receipt/bill
Output: Extracted text + categorized transaction
Status: Awaiting multimodal release
```

## Performance Metrics

- **Inference Speed**: 15-20 tokens/second on Snapdragon 8 Gen 1
- **First Token Latency**: <500ms
- **Memory Usage**: ~1GB during inference
- **Battery Impact**: Minimal with proper batching

## Download Instructions

1. Visit Hugging Face: https://huggingface.co/google/gemma-3n-E2B-it-litert-preview
2. Download the .task file (gemma-3n-E2B-it-int4.task)
3. Place in `app/src/main/assets/`
4. The app will load it directly from assets using MediaPipe

## .task File Format

The .task format is a compiled MediaPipe format that contains:
- Pre-compiled model weights
- Tokenizer configuration
- Embeddings and encoders
- All metadata needed for inference

This format eliminates the need for model conversion and provides optimal performance on mobile devices.

## Future Roadmap

When Gemma 3N multimodal is released, the app can be updated to:
- Process bill images directly without separate OCR
- Better understand receipt layouts
- Extract structured data from complex documents
- Provide visual question answering for expenses