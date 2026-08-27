package com.github.ekodomo3.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import org.eu.thedoc.zettelnotes.broadcasts.AbstractPluginReceiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "BrowserPlugin";
    private static final String BASE_URL = "https://3ekodomo.github.io/site/markdown?open=";

    private boolean mBrowserLaunched = false;
    private boolean mIsFirstResume = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.init(this);

        Intent intent = getIntent();
        
        if (intent != null && intent.getAction() != null) {
            AppLogger.d(TAG, "onCreate intent action: " + intent.getAction());
        }
        if (intent != null && intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                AppLogger.d(TAG, "onCreate extra: " + key + " = " + intent.getExtras().get(key));
            }
        }

        // Attempt to get arguments passed directly by Zettel Notes Intent
        String uriString = null;
        String repositoryString = null;

        if (intent != null && intent.getExtras() != null) {
            Object uriObj = intent.getExtras().get(AbstractPluginReceiver.EXTRAS_URI);
            if (uriObj != null) uriString = uriObj.toString();

            Object repoObj = intent.getExtras().get(AbstractPluginReceiver.EXTRAS_REPOSITORY);
            if (repoObj != null) repositoryString = repoObj.toString();
        }

        // Fallback to SharedPreferences if intent extras are missing (populated by PluginReceiver)
        if (uriString == null) {
            SharedPreferences notePrefs = getSharedPreferences(PluginReceiver.PREFS_NOTE_DATA, MODE_PRIVATE);
            uriString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_URI, null);
            repositoryString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_REPOSITORY, null);
            AppLogger.d(TAG, "Loaded from SharedPreferences - uri: " + uriString + ", repository: " + repositoryString);
        } else {
            AppLogger.d(TAG, "Loaded from Intent - uri: " + uriString + ", repository: " + repositoryString);
        }

        // ABORT TRIGGER: If the plugin missed the NOTE_OPENED broadcast
        if (uriString == null) {
            AppLogger.e(TAG, "No URI found in intent extras or SharedPreferences");
            Toast.makeText(this, "Note location not found!\nPlease close this note in Zettel Notes and open it again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String filename = uriString;
        String decodedUri = Uri.decode(uriString);
        
        // Smarter URI parsing to extract the exact relative file path (preserves subfolders)
        if (decodedUri.startsWith("content://") || decodedUri.startsWith("file://")) {
            if (repositoryString != null && !repositoryString.isEmpty() && decodedUri.contains(repositoryString + "/")) {
                // Extract everything AFTER the repository name
                filename = decodedUri.substring(decodedUri.indexOf(repositoryString + "/") + repositoryString.length() + 1);
            } else {
                // Fallback basic segment extraction
                String lastSegment = Uri.parse(decodedUri).getLastPathSegment();
                if (lastSegment != null) {
                    if (lastSegment.contains("/")) {
                        filename = lastSegment.substring(lastSegment.lastIndexOf('/') + 1);
                    } else if (lastSegment.contains(":")) {
                        filename = lastSegment.substring(lastSegment.lastIndexOf(':') + 1);
                    } else {
                        filename = lastSegment;
                    }
                }
            }
        } else if (decodedUri.startsWith("https://thedoc.eu.org/app-links/zettel-notes/")) {
            Uri uri = Uri.parse(decodedUri);
            List<String> segments = uri.getPathSegments();
            if (segments.size() > 2) {
                repositoryString = segments.get(2); 
                if (segments.size() > 3) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < segments.size(); i++) {
                        if (i > 3) sb.append("/");
                        sb.append(segments.get(i));
                    }
                    filename = sb.toString();
                } else {
                    filename = "";
                }
            }
        }

        if (filename == null || filename.isEmpty()) {
            AppLogger.e(TAG, "Filename is empty");
            Toast.makeText(this, "Could not extract filename from location", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Sanitize leading slash
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        String url = BASE_URL;

        try {
            // Append Filename (e.g., "B2/Note_1.md" becomes "B2%2FNote_1.md")
            // Zettel repo (repositoryString) should be treated as root and left out of the url
            url += URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            AppLogger.e(TAG, "URL Encoding failed", e);
            finish();
            return;
        }

        AppLogger.d(TAG, "Opening URL: " + url);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        boolean inAppBrowser = prefs.getBoolean(SettingsActivity.PREF_IN_APP_BROWSER, true);

        try {
            // Attempt Custom Tabs (In-App)
            if (inAppBrowser) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
            } else {
                throw new Exception("External browser requested");
            }
        } catch (Exception e) {
            // Fallback robustly to standard External intent
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } catch (Exception ex) {
                AppLogger.e(TAG, "Failed to launch external browser", ex);
                Toast.makeText(this, "No web browser app found on device.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        mBrowserLaunched = true;
        setResult(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsFirstResume) {
            mIsFirstResume = false;
        } else if (mBrowserLaunched) {
            // Close this transparent activity once we return from the browser screen
            finish(); 
        }
    }
}
