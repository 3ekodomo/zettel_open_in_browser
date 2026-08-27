# Zettel Notes Open in Browser Plugin

This project is a standalone buildable version of the Open in Browser (plugin-btn-browser) Zettel Notes plugin.

## How it works internally

This plugin opens the currently viewed markdown note in a web browser. It does this by capturing the current note's metadata and passing it to the target activity.

1.  **Capturing Note Data with `ScanInterface`**:
    The plugin uses the Zettel Notes API's `ScanInterface` to capture the current note's URI and repository data. Because the plugin shares memory space with Zettel Notes, implementing `Listener.onScanText` allows the plugin to intercept the `fileUri` and `category` (repository) in real-time when a note is loaded.
2.  **`Scanner.java`**:
    This class extends `ScanInterface` and intercepts the metadata. It stores the `fileUri` and `category` in static variables (`currentUri` and `currentRepository`).
3.  **`Button.java`**:
    When the plugin button is clicked, this class retrieves the static variables from `Scanner.java` and injects them as extras (`EXTRAS_URI`, `EXTRAS_REPOSITORY`) into the Intent before calling `startActivityForResult`. This ensures the target activity receives the necessary data, solving issues with blank intents.
4.  **`AndroidManifest.xml`**:
    To ensure the main app registers the new interface, the `org.eu.thedoc.zettelnotes.intent.scanner` action is added to the manifest.

## GitHub Actions

Push the project to GitHub. The included workflow:

1. Installs JDK 17.
2. Uses the included Gradle wrapper.
3. Builds `:plugin-btn-browser:assembleRelease`.
4. Uploads the release APK as the `zn-open-in-browser-apk` workflow artifact.

No `local.properties`, signing key, external Zettel Notes source checkout, or local version catalog is required.

The plugin uses the official Zettel Notes Plugin API dependency `com.github.damionx7:Zettel-Notes-Plugin-Api:28`.
