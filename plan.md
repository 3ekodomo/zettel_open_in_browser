1. **Refactor Directory Structure and Package Name:**
   - Run `mkdir -p plugin-btn-browser/src/main/java/com/github/ekodomo3/browser/`.
   - Run `rm -rf plugin-btn-browser/src/main/java/com/github/ekodomo3/imgbb/`.
   - Edit `settings.gradle` to change `:plugin-btn-imgbb` to `:plugin-btn-browser`.
   - Wait, `settings.gradle` was already changed but I'll make sure to verify it using `read_file` or `cat`.
2. **Update Build Configurations:**
   - Edit `plugin-btn-browser/build.gradle` to change `namespace` to `com.github.ekodomo3.browser`.
   - Add `androidx.browser:browser:1.8.0` dependency to `build.gradle` for Custom Tabs.
   - Verify changes using `cat plugin-btn-browser/build.gradle`.
3. **Write Button.java:**
   - Create `plugin-btn-browser/src/main/java/com/github/ekodomo3/browser/Button.java` using `write_file`.
   - Implement `ButtonInterface` with `getName()` returning "Open in Browser".
   - `onClick()` will call `mCallback.startActivityForResult(new Intent("org.eu.thedoc.zettelnotes.intent.buttons.browser.open"))`.
   - `onLongClick()` will call `mCallback.startActivityForResult(new Intent("org.eu.thedoc.zettelnotes.intent.buttons.browser.settings"))` and return true.
   - Verify creation using `read_file`.
4. **Write MainActivity.java (Open Action):**
   - Create `plugin-btn-browser/src/main/java/com/github/ekodomo3/browser/MainActivity.java` using `write_file`.
   - In `onCreate()`, read `arg-repository` and `arg-uri` from the intent extras.
   - Construct the URL: `https://3ekodomo.github.io/site/markdown?open=[URL_ENCODED_FOLDER]%2F[URL_ENCODED_FILE]`.
   - Read SharedPreferences (`SettingsActivity.PREFS`, `SettingsActivity.PREF_BROWSER_TYPE`) to determine "In-App Browser" (default) or "External Browser".
   - Launch the URL using either `CustomTabsIntent` (In-App) or `Intent.ACTION_VIEW` (External).
   - Call `finish()` to close the transparent activity.
   - Verify creation using `read_file`.
5. **Write SettingsActivity.java:**
   - Create `plugin-btn-browser/src/main/java/com/github/ekodomo3/browser/SettingsActivity.java` using `write_file`.
   - Implement a simple UI programmatically (like the original SettingsActivity) with RadioButtons to select "In-App Browser" or "External Browser".
   - Save the selection to SharedPreferences.
   - Verify creation using `read_file`.
6. **Update AndroidManifest.xml:**
   - Edit `plugin-btn-browser/src/main/AndroidManifest.xml` using `write_file`.
   - Declare `MainActivity` with intent filters for `org.eu.thedoc.zettelnotes.intent.buttons` and `org.eu.thedoc.zettelnotes.intent.buttons.browser.open` and `android.intent.action.MAIN` (INFO category).
   - Declare `SettingsActivity` with intent filter for `org.eu.thedoc.zettelnotes.intent.buttons.browser.settings` and `android.intent.action.MAIN` (LAUNCHER category).
   - Verify changes using `read_file`.
7. **Create GitHub Action Workflow:**
   - Create `.github/workflows/build.yml` using `write_file`.
   - The workflow should run on push, setup JDK 17, run `./gradlew :plugin-btn-browser:assembleRelease`, and upload the APK as an artifact. Also it should create a GitHub Release with the APK if tags are pushed or just upload artifact if we can't easily create a release. The prompt says "Also add GitHub action to compile the apk and Release the ZN open in browser.apk in release of GitHub." We will use `softprops/action-gh-release@v1` or similar to create a release.
   - Verify creation using `read_file`.
8. **Build and Test Compilation:**
   - Run `./gradlew :plugin-btn-browser:assembleRelease` to verify everything compiles successfully.
9. **Complete pre-commit steps:**
   - Complete pre-commit steps to ensure proper testing, verification, review, and reflection are done.
10. **Submit changes:**
    - Use `submit` tool to finalize.
