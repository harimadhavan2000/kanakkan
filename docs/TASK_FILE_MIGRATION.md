# Migration Guide: From .bin to .task Format

## Overview

The Gemma 3N models use a new `.task` file format instead of the traditional `.bin` format. This guide explains the changes needed to support .task files.

## Key Differences

### .bin Format (Old)
- Required copying from assets to internal storage
- Larger file size
- Manual tokenizer configuration
- Used with older Gemma models

### .task Format (New)
- Direct loading from assets folder
- Smaller size due to PLE optimization
- Pre-compiled with all components
- Mobile-optimized format

## Code Changes

### Before (with .bin files):
```kotlin
private const val MODEL_PATH = "gemma-2b-it-gpu-int4.bin"

suspend fun initialize() {
    val modelPath = copyModelToInternalStorage()
    val options = LlmInference.LlmInferenceOptions.builder()
        .setModelPath(modelPath)
        // ... other options
        .build()
}

private suspend fun copyModelToInternalStorage(): String {
    // Complex file copying logic
}
```

### After (with .task files):
```kotlin
private const val MODEL_PATH = "gemma-3n-E2B-it-int4.task"

suspend fun initialize() {
    val modelAssetPath = "file:///android_asset/$MODEL_PATH"
    val options = LlmInference.LlmInferenceOptions.builder()
        .setModelPath(modelAssetPath)
        // ... other options
        .build()
}
// No file copying needed!
```

## Benefits

1. **Simpler Code**: No need for file copying logic
2. **Faster Loading**: Direct asset loading
3. **Less Storage**: No duplicate files in internal storage
4. **Better Performance**: Pre-optimized for mobile

## Troubleshooting

### Common Issues

1. **File Not Found**
   - Ensure the .task file is in `app/src/main/assets/`
   - Check the exact filename matches

2. **Out of Memory**
   - The .task format uses PLE to reduce memory usage
   - Ensure device has at least 2GB RAM available

3. **Initialization Failure**
   - Update MediaPipe to latest version
   - Verify the .task file isn't corrupted

## Resources

- [Gemma 3N Model Card](https://huggingface.co/google/gemma-3n-E2B-it-litert-preview)
- [MediaPipe LLM Inference Guide](https://developers.google.com/mediapipe/solutions/genai/llm_inference)
- [Edge AI Gallery App](https://github.com/google-ai-edge/gallery)