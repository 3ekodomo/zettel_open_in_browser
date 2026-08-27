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

        Intent intent = getIntent();
        
        // Attempt to get arguments passed directly by Zettel Notes Intent
        String uriString = intent != null ? intent.getStringExtra(AbstractPluginReceiver.EXTRAS_URI) : null;
        String repositoryString = intent != null ? intent.getStringExtra(AbstractPluginReceiver.EXTRAS_REPOSITORY) : null;

        // Fallback to SharedPreferences if intent extras are missing (populated by PluginReceiver)
        if (uriString == null) {
            SharedPreferences notePrefs = getSharedPreferences(PluginReceiver.PREFS_NOTE_DATA, MODE_PRIVATE);
            uriString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_URI, null);
            repositoryString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_REPOSITORY, null);
        }

        Log.d(TAG, "uri: " + uriString + ", repository: " + repositoryString);

        if (uriString == null) {
            Log.e(TAG, "No URI found in intent extras or SharedPreferences");
            Toast.makeText(this, "No note data found. Please open a note first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String filename = uriString;
        
        // Parse filename out of the provided URI
        if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
            filename = Uri.parse(uriString).getLastPathSegment();
            if (filename == null) {
                filename = uriString;
            }
        } else if (uriString.startsWith("https://thedoc.eu.org/app-links/zettel-notes/")) {
            Uri uri = Uri.parse(uriString);
            List<String> segments = uri.getPathSegments();
            // Expected segments: [app-links, zettel-notes, repository_name, folder1, folder2, ..., note.md]
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
            Log.e(TAG, "Filename is empty");
            Toast.makeText(this, "Could not determine filename", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Sanitize leading slash to avoid double '%2F' encoding
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        String url = BASE_URL;

        try {
            if (repositoryString != null && !repositoryString.isEmpty()) {
                String repo = repositoryString;
                // Sanitize trailing slash
                if (repo.endsWith("/")) {
                    repo = repo.substring(0, repo.length() - 1);
                }
                url += URLEncoder.encode(repo, "UTF-8").replace("+", "%20") + "%2F";
            }
            url += URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "URL Encoding failed", e);
            finish();
            return;
        }

        Log.d(TAG, "Opening URL: " + url);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        boolean inAppBrowser = prefs.getBoolean(SettingsActivity.PREF_IN_APP_BROWSER, true);

        try {
            // Attempt configured launch technique
            if (inAppBrowser) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
            } else {
                throw new Exception("User preferred external browser");
            }
        } catch (Exception e) {
            // Fallback robustly to the standard external intent if Custom Tabs crash or are manually bypassed
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to launch external browser", ex);
                Toast.makeText(this, "No browser app found to handle this link.", Toast.LENGTH_SHORT).show();
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
