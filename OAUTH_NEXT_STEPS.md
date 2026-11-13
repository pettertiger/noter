# Dropbox OAuth - Nästa Steg

Jag har implementerat **Dropbox OAuth 2.0** i Noter-appen. Här är vad som gjordes och vad du behöver göra härnäst.

## ✅ Implementerat

### 1. **OAuth 2.0 Authorization Code Flow**
- `DropboxRepository.getOAuthStartUrl()` genererar OAuth login-URL
- Öppnar webbläsare med Dropbox login
- Hanterar callback via deep link `noter://oauth-callback`

### 2. **Secure Token Exchange**
- `DropboxRepository.handleOAuthCallback()` byter OAuth code mot access token
- `TokenManager` lagrar token säkert med EncryptedSharedPreferences (Android Keystore)
- Stöd för refresh tokens och expiration tracking

### 3. **CSRF Protection**
- OAuth state-parameter valideras för att förhindra CSRF-attacker
- Unika state-värden genereras med UUID

### 4. **UI Integration**
- Knappen "Connect Dropbox" initierar OAuth login
- Loading-state visas under inloggning
- Efter success: Visar PDF-listan från Dropbox automatiskt

## 🔧 Vad Du Behöver Göra

### Steg 1: Skapa Dropbox App
1. Gå till: https://www.dropbox.com/developers/apps
2. Logga in med **tiger.petter@gmail.com**
3. Klicka **"Create App"**
4. Välj:
   - API: **Scoped API**
   - Access Level: **Full Dropbox**  
   - App Name: **Noter**
5. Klicka **"Create App"**

### Steg 2: Konfigurera OAuth
1. Under **Settings** tab:
   - Gå till **OAuth 2** sektion
   - Lägg till Redirect URI: `noter://oauth-callback`
2. Under **Permissions**:
   - Kryscha: `files.metadata.read`
   - Kryscha: `files.content.read`
   - Kryscha: `files.content.write`
3. Klicka **Save**

### Steg 3: Hämta App Key & Secret
1. Under **Settings** tab, hitta:
   - **App Key** (kopiera)
   - **App Secret** (klicka "Show" och kopiera)

### Steg 4: Uppdatera gradle.dropbox.properties
Öppna filen: `gradle.dropbox.properties`

Ersätt dessa:
```properties
DROPBOX_APP_KEY=YOUR_APP_KEY        # ← Klistra in din App Key här
DROPBOX_APP_SECRET=YOUR_APP_SECRET  # ← Klistra in din App Secret här
```

### Steg 5: Bygg och Testa
1. I Android Studio: **Build → Rebuild Project**
2. Klicka **Run → Run 'app'** (eller Cmd+R)
3. På emulatorn:
   - Du ser "Welcome to Noter" skärm
   - Klicka **"Connect Dropbox"** knappen
   - Dropbox login-sida öppnas i webbläsare
   - Logga in med tiger.petter@gmail.com
   - Klicka **"Allow"** för att godkänna Noter-åtkomsten
   - Du återkommer till appen automatiskt
   - **✅ Appen visar nu dina PDF-filer från Dropbox!**

## 📋 Vad Händer Under Huven

### Login-flöde:
```
User clicks "Connect Dropbox"
    ↓
initiateDropboxLogin() → getOAuthStartUrl()
    ↓
Opens Dropbox OAuth consent screen in browser
    ↓
User logs in & authorizes Noter
    ↓
Dropbox redirects to: noter://oauth-callback?code=...&state=...
    ↓
MainActivity receives deep link intent
    ↓
handleOAuthCallback(code, state) validates state (CSRF protection)
    ↓
exchangeCodeForToken() makes HTTP POST to Dropbox token endpoint
    ↓
Receives access_token & refresh_token
    ↓
TokenManager saves tokens securely in EncryptedSharedPreferences
    ↓
DbxClientV2 initialized with access token
    ↓
listPdfs() loads PDFs from Dropbox
    ↓
UI updates to show "Your PDFs" with file list
```

## 🔒 Säkerhet

- **Tokens lagras krypterat** via Android Keystore (EncryptedSharedPreferences)
- **Aldrig loggade** i Logcat eller som plaintext
- **CSRF-protected** via OAuth state validation
- **Token expiration** spåras automatiskt
- **Deep links** valideras för `noter://oauth-callback`

## 🧪 Test-Checklist

Efter implementering:
- [ ] App startar utan crashes
- [ ] "Connect Dropbox" knappen öppnar Dropbox login
- [ ] Kan logga in med tiger.petter@gmail.com
- [ ] Kan godkänna Noter-åtkomsten
- [ ] Returnerar till appen efter godkännande
- [ ] "Your PDFs" skärm visar PDF-filer från din Dropbox
- [ ] "Sign Out" knappen loggar ut och visar login-skärm igen
- [ ] Logcat visar "OAuth successful - token saved"

## 📁 Ändrade Filer

1. **DropboxRepository.kt** - OAuth flow + token exchange
2. **TokenManager.kt** - OAuth state storage
3. **HomeViewModel.kt** - initiateDropboxLogin() + handleOAuthCallback()
4. **MainActivity.kt** - Deep link handling
5. **HomeScreen.kt** - Loading states
6. **build.gradle.kts** - BuildConfig för credentials
7. **gradle.dropbox.properties** - Credential storage
8. **DROPBOX_SETUP.md** - Setup documentation

## ⚠️ Troubleshooting

**Problem: "OAuth state mismatch"**
- Starta om appen och försök igen
- Kontrollera att `noter://oauth-callback` är registrerad i Dropbox settings

**Problem: "Failed to exchange code for token"**
- Dubbelkolla APP_KEY och APP_SECRET i gradle.dropbox.properties
- Kontrollera att du sparade filen och byggde om projektet

**Problem: "No PDFs found"**
- Lägg till PDF-filer i din Dropbox root eller `/Notes` folder
- Klicka "Reload" i appen för att uppdatera

**Problem: Browser öppnas inte**
- Kontrollera att Intent-filter är rätt i AndroidManifest.xml
- Använd en emulator med Google Play Services

## 🚀 Nästa Fas

Efter OAuth fungerar, nästa implementering blir:
- **PDF Viewer** - Visa PDF-fil i appen med PdfRenderer
- **Scroll Modes** - Smooth, 1/3-page, 1/2-page, full-page scroll
- **Annotations** - Lägga till anteckningar på PDF
- **Sidecar Model** - Spara anteckningar i .noter.json filer

## 💡 Tips

- Håll `gradle.dropbox.properties` privat (redan i .gitignore)
- Uppdatera aldrig APP_SECRET direkt i kod - använd gradle.properties
- För production: Använd CI/CD secrets istället för gradle.properties

---

Säg till när du är klar med Dropbox-konfigurationen och OAuth är körande! 🚀
