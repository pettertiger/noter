# Noter - Systemarkitektur

**Version:** 1.0  
**Datum:** 2026-11-13  
**Status:** Arkitektur etablerad, implementering påbörjas

---

## 1. Systemöversikt

### 1.1 Vision
Noter är en Android-applikation för musikstudenter och musikanter som möjliggör:
- 📖 Läsning av PDF-noter från Dropbox/Google Docs
- 🎵 Anteckning direkt i noterna
- 📜 Flexibel skrollning (smooth, 1/3, 1/2, helsidig)
- ☁️ Automatisk synkronisering via sidecar-modellen

### 1.2 Målplattform
- **Primär:** Android 10+ (Samsung Galaxy Tab, Boox e-ink tablets)
- **Skärmstorlek:** 7-13 tum (läsplattor)
- **Connectivity:** Online och offline-lägen

### 1.3 Arkitekturöversikt
```
┌─────────────────────────────────────────────────────┐
│         Noter Android-app (Kotlin)                  │
│  ┌──────────────────────────────────────────────┐   │
│  │  UI-lager (Jetpack Compose)                  │   │
│  │  - PDF-viewer                                │   │
│  │  - Scroller-kontroller                       │   │
│  │  - Antecknings-redigerare                    │   │
│  └──────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────┐   │
│  │  Business Logic Layer                        │   │
│  │  - Dropbox/Google OAuth                      │   │
│  │  - PDF-rendering (PDFBox/iText)              │   │
│  │  - Antecknings-hantering                     │   │
│  │  - Synkronisering                            │   │
│  └──────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────┐   │
│  │  Data Layer (Room Database + Local Storage)  │   │
│  │  - Metadata om PDF:er                        │   │
│  │  - Cached PDF:er                             │   │
│  │  - Anteckningar (.noter.json)                │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
        ↓              ↓              ↓
   Dropbox API    Google Drive API   Local Storage
```

---

## 2. Datamodell

### 2.1 Anteckningsfil (.noter.json)

**Placering:** Samma katalog som PDF, med samma namn  
**Exempel:** `Mozarts_Symphony_40.pdf` → `Mozarts_Symphony_40.noter.json`

```json
{
  "version": "1.0",
  "document_id": "uuid-of-pdf",
  "document_name": "Mozarts_Symphony_40.pdf",
  "document_hash": "sha256-hash-of-pdf",
  "created_at": "2026-11-13T14:51:38Z",
  "last_modified": "2026-11-13T14:51:38Z",
  "pages": {
    "1": {
      "page_number": 1,
      "annotations": [
        {
          "id": "ann-uuid-001",
          "type": "text-note",
          "content": "Adagio - lugnt tempo",
          "position": {
            "x": 150,
            "y": 200,
            "page": 1
          },
          "color": "#FFD700",
          "created_at": "2026-11-13T14:51:38Z",
          "modified_at": "2026-11-13T14:51:38Z"
        },
        {
          "id": "ann-uuid-002",
          "type": "highlight",
          "start_line": 3,
          "end_line": 5,
          "color": "#FF6B6B",
          "created_at": "2026-11-13T14:51:38Z"
        }
      ]
    },
    "2": {
      "page_number": 2,
      "annotations": []
    }
  },
  "metadata": {
    "total_pages": 10,
    "last_viewed_page": 2,
    "reading_progress": 20
  }
}
```

### 2.2 Lokalt cachekopior

**Katalogstruktur:**
```
Android Internal Storage
├── /data/com.noter.app/files/
│   ├── downloads/
│   │   ├── Mozarts_Symphony_40.pdf
│   │   ├── Mozarts_Symphony_40.noter.json
│   │   └── [md5-checksums.db]
│   ├── cache/
│   │   ├── [temporary-pdfs]
│   └── database/
│       └── [Room-database]
```

---

## 3. Funktionalitet per Modul

### 3.1 Autentisering & Cloud-integration

**Dropbox OAuth Flow:**
```
1. Användare klickar "Connect Dropbox"
2. App öppnar Dropbox OAuth-dialog
3. Dropbox token sparas säkert (EncryptedSharedPreferences)
4. Refresh-token cachelagras för offline-åtkomst
5. App listar filer från /Music/Scores (eller användarens val)
```

**Google Drive OAuth Flow:**
```
Samma som Dropbox, men använder Google Drive SDK
- Stöder MIME-type: application/pdf
- Mappar "Noter"-folder i Google Drive
```

**Token-hantering:**
```kotlin
// Pseudokod
val tokenManager = TokenManager(context)
tokenManager.saveToken(
    provider = "dropbox",
    accessToken = token,
    refreshToken = refreshToken,
    expiresAt = expiry,
    encrypted = true
)
```

### 3.2 PDF-hantering

**Rendering:**
- Använd **PDFRenderer (Android Framework)** eller **Mozilla PDF.js (WebView)**
- Cachelagra rendererad PDF i lokal lagring
- Support för e-ink displays (Boox) - anpassa färger efter skärm

**Skrollning-lägen:**
```
┌─────────────────────────────┐
│  Smooth Scroll              │ (Kontinuerlig)
│  - Pixelbaserad scrolling   │
│  - Smooth animations        │
│  - Finger-tracking          │
├─────────────────────────────┤
│  1/3-Page Scroll            │ (Page Down = 1/3 sida)
│  - Beräkna 1/3 av sidans    │
│    höjd (pixel)             │
│  - Animerad övergång        │
├─────────────────────────────┤
│  1/2-Page Scroll            │ (Page Down = 1/2 sida)
│  - Beräkna 1/2 av sidans    │
│    höjd (pixel)             │
│  - Animerad övergång        │
├─────────────────────────────┤
│  Full-Page Scroll           │ (Page Down = nästa sida)
│  - Helt sida-baserad        │
│  - Snabb övergång           │
└─────────────────────────────┘
```

### 3.3 Anteckningshantering

**Sidecar-integration:**
```
1. PDF öppnas → app söker efter .noter.json
2. Om .noter.json finns:
   - Ladda anteckningar från fil
   - Visa anteckningar på rätt position
3. Användaren lägger till/redigerar anteckningar
4. Auto-save till .noter.json var 5:e sekund
5. Vid nästa synkronisering: uppdatera Dropbox/Google
```

**UI för anteckningar:**
```
┌─────────────────────────────────┐
│  Antecknings-toolbar            │
├─────────────────────────────────┤
│ [Sticky Note] [Highlight] [Delete]
│ [Color Picker] [Font Size]      │
├─────────────────────────────────┤
│  PDF med synliga anteckningar    │
│  (overlay-baserat)              │
└─────────────────────────────────┘
```

### 3.4 Synkronisering

**Auto-sync strategi:**
```
Varje 5 minuter (när appen är aktiv):
1. Jämför lokal .noter.json med remote
2. Om remote är nyare: downlad och slå ihop
3. Om lokal är nyare: uppdatera remote
4. Vid konflikt: använd "local wins" (användaren kan manuellt välja senare)
5. Logg ändringar för spårning
```

**Offline-läge:**
```
- Alla PDF:er cachelagras lokalt
- Anteckningar lagras lokalt
- Vid nästa online: synkronisera automatiskt
- Visa "Syncing..." indikator
```

---

## 4. Teknikstack

### 4.1 Backend (Minimal)
**Eftersom vi använder Dropbox/Google-API direkt, behövs inte en egen backend för MVP.**

*Framtida: Om vi behöver centraliserad synkronisering eller serverlogik kan vi lägga till:*
- Node.js/Express eller Python/FastAPI
- PostgreSQL för metadata
- Redis för caching

### 4.2 Android Frontend
| Komponent | Val | Anledning |
|-----------|-----|----------|
| Språk | Kotlin | Modern, null-safe, Android-först |
| UI-framework | Jetpack Compose | Moderna UI, reactive, e-ink friendly |
| PDF-rendering | PDFRenderer API | Built-in, lättvikt |
| Database | Room | Type-safe, offline-first |
| Storage | EncryptedSharedPreferences | Säker token-lagring |
| Cloud SDK | Dropbox SDK, Google Drive SDK | Officiella, väl-underhållen |
| Async/Coroutines | Kotlin Coroutines | Asynchron kodning |
| Dependency Injection | Hilt | Enkel, Android-fokuserad |
| Testing | JUnit 4, Mockito | Standard Android testing |

### 4.3 Projektstruktur
```
noter-android/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/noter/
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── PdfViewerScreen.kt
│   │   │   │   │   └── AnnotationEditorScreen.kt
│   │   │   │   └── components/
│   │   │   ├── viewmodel/
│   │   │   │   ├── PdfViewerViewModel.kt
│   │   │   │   └── AnnotationViewModel.kt
│   │   │   ├── repository/
│   │   │   │   ├── DropboxRepository.kt
│   │   │   │   ├── GoogleDriveRepository.kt
│   │   │   │   └── AnnotationRepository.kt
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   └── [DAO:er]
│   │   │   │   └── remote/
│   │   │   ├── auth/
│   │   │   │   ├── OAuthManager.kt
│   │   │   │   └── TokenManager.kt
│   │   │   └── MainActivity.kt
│   │   └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── README.md
└── ARCHITECTURE.md (denna fil)
```

---

## 5. API-Design (Cloud-integration)

### 5.1 Dropbox API-anrop
```kotlin
// Pseudokod för implementering
class DropboxRepository(private val client: DbxClientV2) {
    
    // Lista PDF:er i en mapp
    suspend fun listPdfs(folderPath: String): List<FileMetadata>
    
    // Ladda ner PDF
    suspend fun downloadPdf(filePath: String): ByteArray
    
    // Ladda upp anteckningsfil (.noter.json)
    suspend fun uploadAnnotations(
        filePath: String, 
        notesJson: String
    ): FileMetadata
    
    // Synkronisera anteckningar
    suspend fun syncAnnotations(
        pdfPath: String, 
        localNotes: NotesFile
    ): SyncResult
}
```

### 5.2 Google Drive API-anrop
```kotlin
class GoogleDriveRepository(private val service: Drive) {
    
    // Samma interface som Dropbox
    suspend fun listPdfs(folderId: String): List<FileMetadata>
    suspend fun downloadPdf(fileId: String): ByteArray
    suspend fun uploadAnnotations(fileId: String, notesJson: String): FileMetadata
}
```

---

## 6. Säkerhet

### 6.1 Token-hantering
```
✓ Access tokens lagras i EncryptedSharedPreferences (Android Keystore)
✓ Refresh tokens roteras regelbundet
✓ Tokens sparas ALDRIG i plaintext
✓ Tokens sparas ALDRIG i loggar
✓ Session timeout efter 1 timme inaktivitet
```

### 6.2 PDF & Anteckningar
```
✓ Anteckningar krypteras vid vila (lokal lagring)
✓ HTTPS för all cloud-kommunikation
✓ PDF:er cachelagras säkert (intern lagring, inte SD-kort)
✓ Användare kan radera all lokal data när som helst
```

### 6.3 Behörigheter
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

---

## 7. MVP - Implementeringsfaser

### Fas 1: Grundstruktur (v1)
- [ ] Android-projekt setup (Kotlin + Compose)
- [ ] Dropbox OAuth-integration
- [ ] PDF-viewer med smooth scrolling
- [ ] Lokal Room-databas

### Fas 2: Anteckningar (v2)
- [ ] Sidecar .noter.json-format
- [ ] Anteckningsvisning på PDF
- [ ] Anteckningsredigering (text + highlights)
- [ ] Auto-save lokalt

### Fas 3: Synkronisering (v3)
- [ ] Upload .noter.json till Dropbox
- [ ] Konflikthantering
- [ ] Offline-läge
- [ ] Sync-status indikator

### Fas 4: UX-förbättringar (v4)
- [ ] 1/3, 1/2, helsidig scrollning
- [ ] E-ink optimering (Boox)
- [ ] Google Drive-stöd
- [ ] Anteckningsöversikt

### Fas 5: Avancerat (v5+)
- [ ] Sök i anteckningar
- [ ] Taggning/kategorisering
- [ ] Delning av anteckningar
- [ ] Samarbete i realtid

---

## 8. Utvecklingsmiljö

### 8.1 Verktyg
- **IDE:** Android Studio 2024.x
- **Gradle:** 8.5+
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 30 (Android 11)
- **JDK:** 17+

### 8.2 Beroenden (build.gradle.kts)
```kotlin
dependencies {
    // Jetpack
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.ui:ui:1.6.0")
    
    // PDF Rendering
    implementation("androidx.graphics:graphics-core:1.0.0-alpha03")
    
    // Database
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Cloud SDKs
    implementation("com.dropbox.core:dropbox-core-sdk:6.0.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20231211-2.0.0")
    
    // Async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
```

---

## 9. Deployment & Distribution

### 9.1 Build-process
```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease --minifyEnabled true
```

### 9.2 Distribution-kanaler
- **Google Play Store** (primär)
- **GitHub Releases** (alternativ)
- **Direct install** (APK-fil för Boox/Samsung)

### 9.3 Versioning
- **Semantic Versioning:** `major.minor.patch` (t.ex., `1.0.0`)
- **Build metadata:** Git commit hash
- **Changelog:** CHANGELOG.md

---

## 10. Framtida Utökningar

### 10.1 Backend-tjänst (Post-MVP)
Om vi senare behöver:
- Centraliserad synkronisering mellan enheter
- Serverbaserad backup
- Samarbete mellan användare
- Analys av användardata

**Stack-förslag:**
```
Node.js + Express + PostgreSQL + Redis
eller
Python + FastAPI + PostgreSQL + Redis
```

### 10.2 Andra plattformar
- **Web-gränssnitt** (React/Vue.js för desktop-användning)
- **iOS-app** (Swift, framtida)
- **Desktop** (Electron/Tauri, framtida)

---

## 11. Kontaktpunkter & Ansvar

| Område | Ansvar |
|--------|--------|
| Arkitektur | Pettertiger (denna dokument) |
| Android-implementering | TBD |
| Cloud-integration | TBD |
| Testing & QA | TBD |
| Deployment | TBD |

---

**Nästa steg:** Godkänna denna arkitektur och påbörja implementering av Fas 1 (grundstruktur).
