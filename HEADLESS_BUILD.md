# Building UPI Tracker on Linux Server (Headless)

This guide explains how to build the UPI Tracker Android app on a Linux server without GUI access.

## Prerequisites

- Linux server (Ubuntu/Debian tested)
- Java 17 installed
- At least 4GB RAM
- ~2GB disk space

## Quick Start

```bash
# 1. Run the setup script
./setup-headless.sh

# 2. Download the Gemma model
# Visit: https://huggingface.co/google/gemma-3n-E2B-it-litert-preview
# Download: gemma-3n-E2B-it-int4.task
# Place in: app/src/main/assets/

# 3. Build the APK
./build.sh
```

## Detailed Steps

### 1. Install Java (if not installed)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### 2. Set up Android SDK
The setup script automatically:
- Downloads Android command-line tools
- Installs Android SDK components
- Accepts licenses
- Sets up environment variables

### 3. Build the APK
```bash
./build.sh
```

First build takes 5-10 minutes. Subsequent builds are faster (~1-2 minutes).

### 4. Find the APK
After successful build:
```bash
ls -la app/build/outputs/apk/debug/app-debug.apk
```

## Transferring the APK

### Option 1: SCP to local machine
```bash
scp user@server:/path/to/UpiTracker/app/build/outputs/apk/debug/app-debug.apk .
```

### Option 2: Start simple HTTP server
```bash
cd app/build/outputs/apk/debug/
python3 -m http.server 8000
# Access at http://server-ip:8000/app-debug.apk
```

### Option 3: Upload to file sharing service
```bash
curl -F "file=@app/build/outputs/apk/debug/app-debug.apk" https://file.io
```

## Installing on Android Device

1. Enable "Unknown Sources" in Android settings
2. Transfer APK to device
3. Open APK file to install

Or with ADB:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### Out of Memory
Add to `gradle.properties`:
```
org.gradle.jvmargs=-Xmx2048m
```

### Build Fails
```bash
# Clean and rebuild
./gradlew clean
./build.sh
```

### Check Logs
```bash
./gradlew assembleDebug --info
```

## Build Variants

### Debug Build (default)
```bash
./gradlew assembleDebug
```

### Release Build (requires signing)
```bash
./gradlew assembleRelease
```

## CI/CD Integration

Example GitHub Actions workflow:
```yaml
name: Build APK
on: push
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./setup-headless.sh
      - run: ./build.sh
      - uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

## Notes

- Model file (300MB) not included in repo
- First build downloads ~1GB of dependencies
- Gradle daemon keeps running; stop with: `./gradlew --stop`
- Built APK is ~50MB without model, ~350MB with model