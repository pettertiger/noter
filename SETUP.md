# Noter - Development Setup Guide

**Version:** 1.0  
**Fase:** 1 (Android Setup + PDF Viewer)  
**Status:** Initial setup for development

---

## Prerequisites

### Required Software
- **Android Studio:** 2024.1.1 or newer
- **JDK:** 17 or newer
- **Git:** Latest version
- **Gradle:** 8.5+ (bundled with Android Studio)

### System Requirements
- **OS:** macOS 12+, Windows 10+, Ubuntu 20.04+
- **RAM:** 8GB minimum (16GB recommended)
- **Disk Space:** 10GB for Android SDK + project

---

## Installation

### 1. Clone Repository
```bash
git clone https://github.com/pettertiger/noter.git
cd noter
```

### 2. Open in Android Studio
```bash
# Open the project
open -a "Android Studio" .
```

Or in Android Studio:
1. File → Open
2. Select `noter` directory
3. Wait for Gradle sync to complete

### 3. Configure Android SDK
- Android Studio will automatically suggest missing SDK components
- Accept all prompts to install:
  - Android SDK 35 (Target SDK)
  - Android SDK 30 (Min SDK)
  - Emulator images (optional for testing)

### 4. Set Up Dropbox OAuth

**Generate API Key/Secret:**
1. Go to [Dropbox Developer Console](https://www.dropbox.com/developers/apps)
2. Create a new app (Scoped app)
   - **API:** Scoped API
   - **Access type:** Full Dropbox
   - **App name:** Noter Dev
3. Copy `App Key` and `App Secret`

**Add to project** (app/local.properties):
```properties
dropbox.app_key=YOUR_APP_KEY_HERE
dropbox.app_secret=YOUR_APP_SECRET_HERE
```

### 5. Build Project
```bash
# From project root
./gradlew build

# Or in Android Studio:
# Build → Make Project
```

### 6. Run App
```bash
# On connected device or emulator
./gradlew installDebug

# Or in Android Studio:
# Run → Run 'app'
```

---

## Development Workflow

### Branching Strategy
```
main (production)
  └─ feature/phase1-android-setup
     └─ feature/phase2-annotations
     └─ fix/xyz-bugfix
```

### Create Feature Branch
```bash
git checkout -b feature/xyz-feature-name
```

### Commit Changes
```bash
git add .
git commit -m "Description of changes"
```

### Push & Create PR
```bash
git push origin feature/xyz-feature-name
# Create PR on GitHub
```

---

## Gradle Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run Android tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check dependencies
./gradlew dependencies

# Format code
./gradlew spotlessApply
```

---

## Project Structure

```
noter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/noter/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── NoterApplication.kt
│   │   │   │   ├── auth/
│   │   │   │   │   ├── TokenManager.kt
│   │   │   │   │   └── AuthModels.kt
│   │   │   │   ├── data/
│   │   │   │   ├── repository/
│   │   │   │   │   ├── DropboxRepository.kt
│   │   │   │   │   └── PdfRepository.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   └── HomeScreen.kt
│   │   │   │   │   └── theme/
│   │   │   │   └── di/
│   │   │   │       └── RepositoryModule.kt
│   │   │   └── res/
│   │   ├── test/ (Unit tests)
│   │   └── androidTest/ (Instrumented tests)
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts (Root)
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## Debugging

### Logcat
```bash
# View logs for all processes
./gradlew logcat

# Or in Android Studio:
# View → Tool Windows → Logcat
```

### Using Timber Logging
All logging should use Timber:

```kotlin
import timber.log.Timber

// Debug
Timber.d("Message")

// Error
Timber.e(exception, "Message")

// Warning
Timber.w("Message")
```

### Breakpoints
1. Click on line number to add breakpoint
2. Run in debug mode (Shift+F9)
3. App will pause at breakpoint

---

## Testing

### Unit Tests
```bash
./gradlew test
```

### Android Tests (on device/emulator)
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
```bash
./gradlew testDebugUnitTestCoverage
```

---

## Emulator Setup

### Create Virtual Device
1. Android Studio → Tools → Device Manager
2. Create Virtual Device
3. Choose Pixel 6 Pro (for tablet simulation: Pixel Tablet)
4. Select Android 15 (API 35)
5. Launch emulator

### Useful Emulator Commands
```bash
# List available devices
emulator -list-avds

# Start emulator
emulator -avd Pixel_6_Pro_API_35

# Install app
adb install app/build/outputs/apk/debug/app-debug.apk

# Clear app data
adb shell pm clear com.noter
```

---

## Troubleshooting

### Gradle Sync Issues
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Or invalidate cache
File → Invalidate Caches → Invalidate and Restart
```

### Missing SDK Components
- Android Studio will prompt automatically
- Or manually: Settings → Appearance & Behavior → Android SDK

### Dropbox Auth Issues
- Ensure App Key/Secret are correct in local.properties
- Check redirect URI matches in Dropbox console
- Clear app cache: `adb shell pm clear com.noter`

### Build Failures
1. Check Gradle version compatibility
2. Update dependencies: `./gradlew refreshVersions`
3. Check JDK version (should be 17+)
4. Clean: `./gradlew clean`

---

## Environment Variables (Optional)

```bash
# Set ANDROID_HOME (macOS/Linux)
export ANDROID_HOME=~/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/tools/bin

# Or add to ~/.zshrc or ~/.bashrc for persistence
```

---

## Next Steps

1. ✅ Setup complete
2. Review [ARCHITECTURE.md](./ARCHITECTURE.md) for system design
3. Review [PROJECT_PLAN.md](./PROJECT_PLAN.md) for implementation phases
4. Start coding! Begin with:
   - [ ] Complete Dropbox OAuth flow
   - [ ] Implement PDF viewer
   - [ ] Add Room database
   - [ ] Create annotation system

---

## Resources

- [Android Documentation](https://developer.android.com)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Dropbox SDK Documentation](https://www.dropbox.com/developers/documentation/kotlin)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Hilt Dependency Injection](https://dagger.dev/hilt/)

---

**Last Updated:** 2026-11-13  
**Status:** Ready for development ✓
