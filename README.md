# Noter - Musik Noter App 🎵

En modern Android-app för att läsa, anteckna och synkronisera musiknoter från Dropbox och Google Drive.

## 🎯 Vision

Noter gör det enkelt för musikstudenter och musikanter att:
- 📖 Läsa PDF-noter från sina molnkonton (Dropbox/Google Drive)
- ✍️ Lägga till anteckningar direkt i noterna
- 📜 Navigera med flexibel scrollning (smooth, 1/3, 1/2, full-page)
- ☁️ Synkronisera anteckningar automatiskt mellan enheter
- 📱 Använda på Android läsplattor (Samsung, Boox e-ink)

## 🚀 Status

**Aktuell fas:** Planning & Architecture ✅  
**Version:** 0.0.1  
**Nästa steg:** Implementering av Fas 1 (Android Setup + PDF Viewer)

### Etablerade dokument
- ✅ [ARCHITECTURE.md](./ARCHITECTURE.md) - Detaljerad systemarkitektur
- ✅ [PROJECT_PLAN.md](./PROJECT_PLAN.md) - Implementeringsplan & milestones
- ✅ [DECISIONS.md](./DECISIONS.md) - Arkitekturbeslut & motivering

## 🏗️ Tech Stack

| Komponent | Val |
|-----------|-----|
| **Språk** | Kotlin |
| **UI-framework** | Jetpack Compose |
| **Database** | Room |
| **Cloud** | Dropbox SDK + Google Drive SDK |
| **PDF-rendering** | Android PDFRenderer API |
| **Auth** | OAuth 2.0 |
| **Secure Storage** | EncryptedSharedPreferences |
| **Testing** | JUnit 4, Mockito, Espresso |

## 📋 Implementation-vägen (5 faser)

### Fas 1: MVP-grundstruktur (v0.1)
- Android-projekt setup
- Dropbox OAuth-integration  
- PDF-viewer med scrolling
- Lokal Room-databas

### Fas 2: Anteckningar (v0.2)
- Sidecar .noter.json-format
- Antecknings-UI (Compose)
- Auto-save lokalt

### Fas 3: Cloud Sync (v0.3)
- Synkronisering med Dropbox
- Offline-läge
- Konflikthantering

### Fas 4: UX-förbättringar (v0.4)
- 1/3/1/2-page scroll modes
- E-ink optimering (Boox)
- Sökfunktion

### Fas 5: Expansion (v1.0+)
- Google Drive full support
- Taggning & kategorisering
- Web + iOS-versioner (framtida)

Se [PROJECT_PLAN.md](./PROJECT_PLAN.md) för detaljer.

## 🔐 Säkerhet

- ✅ OAuth 2.0 (ingen lösenord i app)
- ✅ Encrypted token storage
- ✅ HTTPS för cloud-kommunikation
- ✅ Local encryption av anteckningar
- ✅ Offline-mode med lokal cache

## 📱 Target Devices

- Samsung Galaxy Tab (7-13")
- Boox e-ink tablets (7.4" - 13.3")
- Andra Android 11+ enheter

## 📖 Dokumentation

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Systemdesign & arkitektur
- [PROJECT_PLAN.md](./PROJECT_PLAN.md) - Timeline & milestones
- [DECISIONS.md](./DECISIONS.md) - Arkitekturbeslut & motivering
- SETUP.md - (kommer) Installation & development setup
- API.md - (kommer) API-dokumentation
- TESTING.md - (kommer) Testing-strategi

## 🛠️ Getting Started

*Instruktioner för lokal setup kommer när Fas 1 starter.*

```bash
# Förutsättningar:
# - Android Studio 2024.x
# - JDK 17+
# - Git

# Setup (framtida):
git clone https://github.com/pettertiger/noter.git
cd noter
# Följ SETUP.md
```

## 👥 Bidrag

Noter är ett privat projekt för Lantero AB.

## 📄 Licens

*Licensinformation kommer senare*

---

**Senast uppdaterad:** 2026-11-13  
**Arkitektur:** Godkänd ✓  
**Nästa milestone:** Fas 1 implementation (v0.1)
