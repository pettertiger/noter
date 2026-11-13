# Noter - Decision Log

**Dokumentet håller reda på alla strategiska beslut och deras motivering**

---

## ✅ Godkända Beslut

### 1. Platform: Android (Mobiltelefon/Läsplatta)
**Datum:** 2026-11-13  
**Beslut:** Primär plattform = Android 10+ (Samsung/Boox tablets)  
**Motivation:**
- Användaren har redan Android läsplattor (Samsung/Boox)
- E-ink Boox passar perfekt för noter-läsning
- MVP fokuserar på en plattform för snabbare leverans
- Kan senare expandera till iOS, web

**Status:** APPROVED ✓

---

### 2. Data Format: Sidecar Model (.noter.json)
**Datum:** 2026-11-13  
**Beslut:** Anteckningar lagras i separata .noter.json-filer bredvid PDFs  
**Motivation:**
- Säker - PDFs förblir orörda
- Versionskontrollbar
- Enkel att synkronisera mellan enheter
- Möjliggör offline-redigering
- Kan sparas tillbaka tillsammans med PDF senare om önskad

**Alternativ övervägt:**
- Embedded i PDF → Kan krasha PDF om fel
- Backend-lagring → Kräver server som inte finns än

**Status:** APPROVED ✓

---

### 3. Cloud Storage: Dropbox + Google Drive
**Datum:** 2026-11-13  
**Beslut:** Integrera direkt mot Dropbox och Google Drive via OAuth  
**Motivation:**
- Användaren har redan PDF:er där
- OAuth 2.0 = ingen lösenord i app
- Dropbox/Google hanterar säkerheten
- Användaren behöver inte ny lagring
- Direktåtkomst till användarens filstruktur

**Alternativ övervägt:**
- Egen backend → Mer komplext, lagringskostnad
- OneDrive → Kan läggas till senare
- iCloud Drive → Bara iOS

**Status:** APPROVED ✓

---

### 4. Authentication: OAuth 2.0 (Stateless)
**Datum:** 2026-11-13  
**Beslut:** Användare autentiserar direkt mot Dropbox/Google  
**Motivation:**
- Ingen egen server behövs för MVP
- Secure tokens från Dropbox/Google
- Refresh tokens för långsiktig åtkomst
- Offline-läge möjligt med lokal cache

**Alternativ övervägt:**
- API-nycklar → Mindre säkert
- Backend login → Kräver server

**Status:** APPROVED ✓

---

### 5. UI Framework: Jetpack Compose
**Datum:** 2026-11-13  
**Beslut:** Använd Jetpack Compose för Android UI  
**Motivation:**
- Modern, declarative UI framework
- Enklare än XML layouts
- Bättre för komplexe interactions (scrolling, annotations)
- Google-rekommenderad standard
- E-ink-friendly med bra performance

**Alternativ övervägt:**
- Traditional XML layouts → Gammal teknik
- React Native → Overkill, inte Android-native

**Status:** APPROVED ✓

---

### 6. Database: Room (Local-first)
**Datum:** 2026-11-13  
**Beslut:** Använd Room för lokal lagring av metadata  
**Motivation:**
- Android standard
- Type-safe SQL
- Offline-first
- Caching av PDFs och anteckningar
- Enkelt att migratera senare

**Alternativ övervägt:**
- Realm → Tredje-parts databas
- SharedPreferences → Bara för små data
- Firebase → Kräver backend

**Status:** APPROVED ✓

---

### 7. PDF Rendering: PDFRenderer API
**Datum:** 2026-11-13  
**Beslut:** Använd Android PDFRenderer för att rendera PDFs  
**Motivation:**
- Built-in i Android framework
- Lättvikt och snabbt
- Inget extra beroende
- E-ink-kompatibelt
- Cachelagring möjlig

**Alternativ övervägt:**
- iText/PDFBox → Externa bibliotek, större storlek
- Mozilla PDF.js → WebView-baserat, långsammare
- Foxit SDK → Kommersiellt, licensutveckling

**Status:** APPROVED ✓

---

### 8. Scrolling: Multiple Modes
**Datum:** 2026-11-13  
**Beslut:** Implementera 4 scroll-lägen: smooth, 1/3-page, 1/2-page, full-page  
**Motivation:**
- Smooth scroll = normal användning
- 1/3/1/2-page = snabbare navigation för noter
- Full-page = snabbt skifta mellan sidor
- E-ink tablets gynnas av diskreta jumps (reducerar flicker)
- Användare väljer själva preferens

**Status:** APPROVED ✓

---

### 9. Offline Support: Immediate
**Datum:** 2026-11-13  
**Beslut:** Offline-läge implementeras från start (Fas 3)  
**Motivation:**
- PDFs kan cachelagras lokalt
- Anteckningar kan redigeras offline
- Synkroniserar när nätet är tillbaka
- Användaren kan öva utan internet

**Status:** APPROVED ✓

---

### 10. No Backend Required (Initially)
**Datum:** 2026-11-13  
**Beslut:** MVP behöver INGEN egen backend-server  
**Motivation:**
- Dropbox/Google hanterar cloud-logik
- Room-databas för lokal data
- Kan lägga till backend senare för sync mellan enheter
- Snabbare MVP-leverans
- Lägre kostnad

**Framtida:** Backend kan läggas till för:
- Centraliserad synkronisering mellan Android + Web + iOS
- Backup av anteckningar
- Collaboration features

**Status:** APPROVED ✓

---

### 11. Security: Encrypted Storage + HTTPS
**Datum:** 2026-11-13  
**Beslut:** Säker lagring av tokens och anteckningar  
**Motivation:**
- EncryptedSharedPreferences för OAuth tokens
- Local encryption för anteckningar
- HTTPS för all cloud-kommunikation
- Android Keystore för nyckelhantering
- Användare kan radera data när som helst

**Status:** APPROVED ✓

---

### 12. Testing Framework: JUnit + Mockito + Espresso
**Datum:** 2026-11-13  
**Beslut:** Standard Android testing stack  
**Motivation:**
- JUnit 4 = standard unit testing
- Mockito = mocking för repos och cloud-APIs
- Espresso = UI testing
- Well-documented, community-supported

**Status:** APPROVED ✓

---

### 13. Version Control: Git + GitHub
**Datum:** 2026-11-13  
**Beslut:** Använd Git för version control, GitHub för hosting  
**Motivation:**
- Standard i industrin
- Redan setup i detta repo
- GitHub Actions för CI/CD senare
- Issue tracking för bug management

**Status:** APPROVED ✓

---

## 🔄 Framtida Beslut (Att Fattas)

| Fråga | Kontext | Datum |
|-------|---------|-------|
| Ska vi lägga till Google Drive-stöd i v0.3 eller v0.4? | Cloud integration | 2026-11-20 |
| Vilken e-ink platta ska vi prioritera: Samsung eller Boox? | Device testing | 2026-11-20 |
| Ska vi skapa en web-app (React) parallellt eller efter Android v1.0? | Platform expansion | 2026-12-01 |
| Ska vi integrera en backend för cross-device sync från start? | Architecture | 2026-12-01 |
| Hur hanterar vi multi-device sync av anteckningar? | Sync strategy | 2026-12-01 |

---

## 🚫 Beslut vi INTE tar

### Inte: Backend från dag 1
**Anledning:** Kan integreras senare när det behövs

### Inte: iOS-app från dag 1
**Anledning:** Android först för MVP

### Inte: Samarbete/Sharing från dag 1
**Anledning:** Enkel-användare MVP är enklare

### Inte: Server-baserad PDF-rendering
**Anledning:** Local rendering på enheten är snabbare + offline

---

## 📝 Anteckningar

- **Alla arkitekturbeslut är reversibla** - Vi kan byta lagring, UI-framework senare om behövs
- **MVP = Minimal Viable Product** - Bara de viktigaste funktionerna
- **Phase-driven implementation** - Varje fas kan testas och deployeras separat
- **User feedback-driven** - Inleda testing med riktiga användare ASAP

---

**Senast uppdaterad:** 2026-11-13  
**Nästa review:** 2026-11-20 (efter Fas 1 start)
