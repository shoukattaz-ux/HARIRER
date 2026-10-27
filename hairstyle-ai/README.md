<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/242f5ab2-12ed-40b3-92f6-aa6e97b15b39

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## Secure AI setup (required for release builds)

Release builds do **not** contain the Gemini API key. AI previews (4 AI Looks, Style Score scenes)
go through **Firebase AI Logic**, which keeps the key on Google's servers, and every request is
verified with **Firebase App Check**, so only the genuine app can use your quota.

1. **Create a Firebase project** at https://console.firebase.google.com and add an Android app with
   package name `com.aistudio.hairstyleai.vqzpmk`. Download `google-services.json` into `app/`.
2. **Enable Firebase AI Logic** (Build → AI Logic → Get started) with the *Gemini Developer API*.
3. **Set up App Check** (Build → App Check):
   - Register the Android app with **Play Integrity** and add the SHA-256 of your app signing key
     (Play Console → App integrity).
   - Debug builds use the App Check *debug provider*: run the app, copy the debug token printed in
     Logcat (`DebugAppCheckProvider`) and add it under App Check → Manage debug tokens.
   - After verifying requests succeed, click **Enforce** for Firebase AI Logic.
4. **Set usage limits** for the Gemini API in Google Cloud Console (APIs & Services → Quotas) so a
   bug or abuse can't run up costs. The in-app daily limit (3 free previews) is only a client-side guard.

Without `google-services.json`:
- **Debug builds** fall back to calling Gemini directly with `GEMINI_API_KEY` from `.env` (development only).
- **Release builds** strip the key and show "AI previews are not set up yet". As a temporary escape
  hatch you can add `hairlook.allowClientGeminiKeyInRelease=true` to `gradle.properties`, but anyone
  can extract a key shipped inside an APK.

### Privacy & data handling
- The app asks for consent before a photo is sent for AI processing; face scan and style advice run on-device.
- Dedicated REST API tokens (Paid AI Studio) are encrypted with an Android Keystore key and excluded from backups.
- Custom try-on endpoints must use `https://`; there is no default third-party endpoint.
- HTTP logging is disabled in release builds and never logs request bodies, API keys or tokens.

### Not done yet
- **Premium purchases are not real payments.** Integrate Google Play Billing (ideally with server-side
  purchase verification) before charging users, then set `AiTryOnConfig.PREMIUM_UNLIMITED_AI_ENABLED = true`.
