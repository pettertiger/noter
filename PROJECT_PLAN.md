# Noter - Projektplan & Milestones

**Datum:** 2026-11-13  
**Status:** Planning  
**Målgrupp:** Musikstudenter, musikanter  
**Primär plattform:** Android (7-13" läsplattor)

---

## 🎯 Projektmål

Skapa en enkel, intuitiv Android-app för att:
1. 📖 Läsa PDF-noter från Dropbox/Google Drive
2. ✍️ Anteckna direkt i noterna
3. 📜 Enkelt navigera med flexibel skrollning
4. ☁️ Synkronisera anteckningar automatiskt

---

## 📋 Arbetsflöde

### User Journey: Första gången
```
1. Användare öppnar Noter-app
2. Klickar "Connect Dropbox" → OAuth-dialog
3. Godkänner åtkomst → app listar PDF:er
4. Väljer en PDF att läsa
5. PDF öppnas med anteckningsverktyg
6. Läser, skrollar och antecknar
7. Anteckningar sparas lokalt och till Dropbox
```

### User Journey: Nästa gång
```
1. Användare öppnar Noter-app
2. Senaste PDF:er visas (från lokal cache)
3. Väljer en PDF
4. App synkroniserar eventuella nya anteckningar
5. Fortsätter läsa/anteckna från förra gången
```

---

## 🗺️ Implementation-vägen

### **Fas 1: MVP-grundstruktur** (v0.1)
**Mål:** Kunna läsa PDF:er från Dropbox och navigera med smooth scroll

#### Uppgifter:
- [x] Arkitektur dokumenterad
- [ ] Android-projekt setup (Kotlin + Gradle)
- [ ] Dropbox OAuth-integration
- [ ] Enkel PDF-viewer med zoom/pan
- [ ] Smooth scrolling implementerat
- [ ] Lokal Room-databas

**Deadline:** 1 vecka  
**Definition of Done:** App startar, kopplar till Dropbox, visar en PDF

---

### **Fas 2: Anteckningsfunktionalitet** (v0.2)
**Mål:** Kunna lägga till och redigera anteckningar

#### Uppgifter:
- [ ] Sidecar .noter.json-format implementerat
- [ ] Antecknings-UI komponenter (Compose)
- [ ] Lägga till/redigera/radera anteckningar
- [ ] Färgväljare för anteckningar
- [ ] Highlights på PDF
- [ ] Auto-save lokalt

**Deadline:** 1 vecka  
**Definition of Done:** Kan anteckna i PDF, anteckningar sparas lokalt

---

### **Fas 3: Synkronisering** (v0.3)
**Mål:** Synkronisera anteckningar automatiskt med Dropbox

#### Uppgifter:
- [ ] Upload .noter.json till Dropbox
- [ ] Konflikthantering (merge logic)
- [ ] Offline-läge med cache
- [ ] Sync-status indikator
- [ ] Timestamp-tracking

**Deadline:** 1 vecka  
**Definition of Done:** Anteckningar sparas både lokalt och i Dropbox

---

### **Fas 4: UX-förbättringar** (v0.4)
**Mål:** Polerad användarupplevelse och flerablägen

#### Uppgifter:
- [ ] 1/3-page scroll mode
- [ ] 1/2-page scroll mode
- [ ] Full-page scroll mode
- [ ] E-ink optimering (Boox-kompatibilitet)
- [ ] Anteckningsöversikt/gallery
- [ ] Sökfunktion i anteckningar

**Deadline:** 1 vecka  
**Definition of Done:** App fungerar bra på e-ink displays

---

### **Fas 5: Expansion** (v1.0+)
**Mål:** Google Drive, iOS, desktop

#### Uppgifter:
- [ ] Google Drive-integration
- [ ] Taggning/kategorisering av anteckningar
- [ ] Delning av anteckningar (framtida)
- [ ] Web-gränssnitt (Electron/Tauri)
- [ ] iOS-app (Swift)

**Deadline:** Framtida  
**Definition of Done:** Stöd för múltipla plattformar

---

## 📊 Milestones

| Milestone | Fas | Status | ETA |
|-----------|-----|--------|-----|
| 🚀 **v0.1** - PDF Reader + Dropbox | 1 | Pending | v45 |
| 📝 **v0.2** - Annotations | 2 | Pending | v46 |
| ☁️ **v0.3** - Cloud Sync | 3 | Pending | v47 |
| ✨ **v0.4** - UX Polish | 4 | Pending | v48 |
| 🎉 **v1.0** - Public Release | 5 | Pending | v50+ |

---

## 🔧 Tech Stack (redan godkänd)

### Android App
- **Språk:** Kotlin
- **UI:** Jetpack Compose
- **PDF:** PDFRenderer API
- **Database:** Room
- **Cloud:** Dropbox SDK, Google Drive SDK
- **Auth:** OAuth 2.0
- **Storage:** EncryptedSharedPreferences
- **Testing:** JUnit 4, Mockito, Espresso

### Development Tools
- **IDE:** Android Studio 2024.x
- **Gradle:** 8.5+
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 30 (Android 11)

---

## 📐 Arkitektur (kort sammanfattning)

**Se ARCHITECTURE.md för detaljer**

```
App (Compose UI)
    ↓
ViewModel (Business Logic)
    ↓
Repository (Cloud + Local Data)
    ↓
Room DB + EncryptedPrefs + Cloud APIs
```

**Key Design Pattern:** MVVM + Repository Pattern + Clean Architecture

---

## 🔐 Säkerhet

✅ OAuth 2.0 för cloud-autentisering  
✅ EncryptedSharedPreferences för token-lagring  
✅ Lokal kryptering av anteckningar  
✅ HTTPS för all cloud-kommunikation  
✅ Ingen plaintext-lagring av känslig data

---

## 📱 Target Devices

- Samsung Galaxy Tab S (7-13")
- Boox Page (e-ink 7.4")
- Boox Max 3 (e-ink 13.3")
- Andra Android 11+ enheter

**Test-enheter (rekommenderat):**
- 1x Samsung tablet för testing
- 1x Boox e-ink för e-ink optimering

---

## 👥 Roller & Ansvar

| Roll | Ansvar | Status |
|------|--------|--------|
| **Arkitekt** | Arkitektur, design decisions | Pettertiger ✓ |
| **Android Dev** | Implementation Fas 1-2 | TBD |
| **Backend Dev** | Cloud integration | TBD |
| **QA/Tester** | Testing, bug reports | TBD |
| **DevOps** | Build, release, deployment | TBD |

---

## 📅 Tidslinje (Estimated)

```
Nov 2026:
  - [x] Architecture planning
  - [ ] Fas 1: Android setup + Dropbox + PDF viewer (Week 1)
  - [ ] Fas 2: Annotations (Week 2)
  - [ ] Fas 3: Cloud sync (Week 3)
  
Dec 2026:
  - [ ] Fas 4: UX improvements (Week 1)
  - [ ] Testing & bug fixes (Week 2-3)
  - [ ] v1.0 release (Week 4)
```

---

## 📚 Dokumentation

- ✅ [ARCHITECTURE.md](./ARCHITECTURE.md) - Detaljerad arkitektur
- ⏳ [SETUP.md](./SETUP.md) - Setup-guide (kommer)
- ⏳ [API.md](./API.md) - API-dokumentation (kommer)
- ⏳ [TESTING.md](./TESTING.md) - Testing-guide (kommer)
- ⏳ [CHANGELOG.md](./CHANGELOG.md) - Version history (kommer)

---

## 🚀 Nästa Steg

1. **Godkänna denna plan** - Bekräfta tidslinjer och milestones
2. **Setup Android-projekt** - Börja Fas 1
3. **Installera dependencies** - Gradle + SDKs
4. **Skapa första branch** - `feature/android-setup`
5. **Hello World!** - Få app att starta

---

**Skapad av:** Copilot Architecture Planning  
**Senast uppdaterad:** 2026-11-13  
**Status:** Ready for implementation ✓
