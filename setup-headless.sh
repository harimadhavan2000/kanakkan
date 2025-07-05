#!/bin/bash

echo "=== UPI Tracker Headless Build Setup ==="
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Java
if command_exists java; then
    echo "✓ Java installed: $(java -version 2>&1 | head -n 1)"
else
    echo "✗ Java not found. Please install Java 17"
    exit 1
fi

# Set up Android SDK
ANDROID_HOME=~/Android
export ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Check if Android SDK is installed
if [ -d "$ANDROID_HOME/cmdline-tools" ]; then
    echo "✓ Android SDK found at $ANDROID_HOME"
else
    echo "✗ Android SDK not found. Installing..."
    mkdir -p ~/Android/cmdline-tools
    cd ~/Android/cmdline-tools
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
    unzip -q commandlinetools-linux-11076708_latest.zip
    mv cmdline-tools latest
    cd -
    
    # Accept licenses
    yes | sdkmanager --licenses
    
    # Install required components
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
fi

# Check model file
if [ -f "app/src/main/assets/gemma-3n-E2B-it-int4.task" ]; then
    echo "✓ Gemma model file found"
else
    echo "! Gemma model file not found"
    echo "  Please download from: https://huggingface.co/google/gemma-3n-E2B-it-litert-preview"
    echo "  And place in: app/src/main/assets/"
fi

echo ""
echo "Setup complete! To build the APK, run:"
echo "  ./build.sh"
echo ""
echo "The build process will take 5-10 minutes on first run."