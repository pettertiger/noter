# Dropbox OAuth Setup

## 1. Skapa Dropbox App

1. Gå till: https://www.dropbox.com/developers/apps
2. Logga in med tiger.petter@gmail.com
3. Klicka **"Create App"**
4. Välj:
   - **API**: Scoped API
   - **Access Level**: Full Dropbox
   - **App Name**: `Noter`
5. Klicka **"Create App"**

## 2. Konfigurera App

1. I app-inställningar, under **OAuth 2**, sätt:
   - **Redirect URIs**: 
     ```
     noter://oauth-callback
     ```
2. Under **Permissions**, kryscha:
   - `files.metadata.read`
   - `files.content.read`
   - `files.content.write`

## 3. Få App Key & Secret

1. Under **Settings** tab, hitta:
   - **App Key**: `xxxxxxxxxxxxx`
   - **App Secret**: `xxxxxxxxxxxxx` (visa med knappen)
2. **Kopiera dessa värden**

## 4. Uppdatera Noter-appen

### I Android Studio:

1. Öppna filen:
   ```
   app/src/main/java/com/noter/repository/DropboxRepository.kt
   ```

2. Hitta dessa rader (bottom of file):
   ```kotlin
   companion object {
       private const val PROVIDER_NAME = "dropbox"
       // These values will be set from Dropbox Developer Console
       const val APP_KEY = "YOUR_APP_KEY"
       const val APP_SECRET = "YOUR_APP_SECRET"
       const val REDIRECT_URI = "noter://oauth-callback"
   }
   ```

3. **Ersätt YOUR_APP_KEY och YOUR_APP_SECRET** med de värden du kopierade

### Exempel:
```kotlin
const val APP_KEY = "abcd1234efgh5678"
const val APP_SECRET = "xyz9876uvwt5432abcd"
```

## 5. Testa OAuth-flödet

1. I Android Studio, kör appen igen
2. Klicka **"Connect Dropbox"** knappen
3. Din webbläsare öppnas med Dropbox login
4. Logga in med tiger.petter@gmail.com
5. Godkänn app-åtkomsten
6. Du kommer tillbaka till Noter-appen
7. Appen visar nu dina PDF-filer från Dropbox! ✅

## Troubleshooting

**"OAuth state mismatch"**
- Starta om appen och försök igen

**"Failed to exchange code for token"**
- Kontrollera att APP_KEY och APP_SECRET är korrekta
- Säkerställ att redirect URI är korrekt registrerad

**"No PDFs found"**
- Lägg till PDF-filer i din Dropbox
- Försök **Reload** knappen i appen
