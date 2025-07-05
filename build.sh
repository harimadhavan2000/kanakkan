#!/bin/bash

# Set up Android SDK environment
export ANDROID_HOME=~/Android
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

echo "Building UPI Tracker APK..."
echo "This may take several minutes on first build..."

# Build debug APK
./gradlew assembleDebug

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install on a connected device:"
    echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build failed. Check the error messages above."
fi