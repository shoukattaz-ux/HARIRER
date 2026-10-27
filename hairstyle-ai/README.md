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

## Google Play Billing (Premium)

Premium is a Google Play purchase. It is never stored as a local flag: on every launch and resume the
app re-reads purchases from Google Play, verifies their signature, acknowledges new ones (required
within 3 days or Google refunds them), and grants Premium only while a valid purchase exists.
Premium raises the daily AI preview limit from 3 to 30 (`AiTryOnConfig`).

1. **Upload the app to Play Console.** Billing only works for apps uploaded to a testing track:
   upload a signed release AAB to **Internal testing**.
2. **Create the product.** Monetize with Play → Products → **Subscriptions** → create
   `hairlook_premium` with an auto-renewing monthly base plan, set the price, and **activate** it.
   (For a one-time purchase instead, create an in-app product with the same ID and set
   `BillingConfig.PREMIUM_PRODUCT_TYPE = BillingClient.ProductType.INAPP`.)
3. **Add the license key.** Monetization setup → Licensing → copy the Base64 RSA public key into
   `.env` as `PLAY_LICENSE_KEY=...`. It is a public key (safe in the app) and is used to reject
   forged purchases. Without it, signature verification is skipped.
4. **Test without being charged.** Settings → License testing → add the testers' Google accounts,
   install the app from the internal testing link, and buy Premium with the test card. Also test
   cancel, a declined card, and a "slow" (pending) payment.
5. **Recommended before scaling:** verify purchase tokens on a server with the Google Play Developer
   API and subscribe to Real-time developer notifications so refunds and chargebacks are handled
   immediately. Firebase App Check (above) already blocks modified apps from using the AI backend.

Users can manage or cancel Premium from the Premium card (opens Google Play → Subscriptions), and
"Restore Purchase" in the upgrade dialog re-checks Google Play after a reinstall.

## App updates

The app checks for updates on launch.

- **Installed from Google Play:** uses Google Play **In-App Updates**, the only update method Play
  allows. Normal updates download in the background and show a "Restart" banner; releases with
  in-app update priority ≥ 4 (set when you roll out the release) or ignored for 14+ days block the app
  until updated. Nothing to host: just upload the new version to Play.
- **Installed from an APK (sideloaded):** reads a public `update.json` and offers the new APK as a
  download. Releases below `minSupportedVersionCode` must be installed before the app can be used.
  Android only accepts the APK as an update if it is signed with the same key.

### Publishing a sideloaded update to Cloudflare R2

1. In Cloudflare → R2 → your bucket → **Settings**, connect a **custom domain** (recommended) or
   enable the **Public Development URL** (`r2.dev`, rate-limited). This gives a public, read-only URL.
2. Put `UPDATE_MANIFEST_URL=https://<public-url>/update.json` in `.env` and build the release APK
   with a higher `versionCode` in `app/build.gradle.kts`.
3. Create an R2 API token with **Object Read & Write** limited to this bucket. Keep its keys in your
   environment only — never in the app, `.env`, or git — and roll them immediately if they leak.
4. Publish:

   ```bash
   # macOS/Linux (PowerShell: $env:R2_ACCESS_KEY_ID = "...")
   export R2_ACCESS_KEY_ID=...  R2_SECRET_ACCESS_KEY=...
   export R2_ENDPOINT=https://<ACCOUNT_ID>.r2.cloudflarestorage.com
   export R2_PUBLIC_BASE_URL=https://<public-url>
   pip install boto3
   python scripts/publish_update.py app/build/outputs/apk/release/app-release.apk \
       --version-code 4 --version-name 1.3 --notes "New AI looks"   # add --dry-run to preview
   ```

   The APK is uploaded first and `update.json` last, so users never see an update that isn't there yet.
   Use `--min-supported <versionCode>` to force old versions to update.
