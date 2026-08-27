package com.github.ekodomo3.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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
        if (intent == null) {
            finish();
            return;
        }

        String uriString = intent.getStringExtra(AbstractPluginReceiver.EXTRAS_URI);
        String repositoryString = intent.getStringExtra(AbstractPluginReceiver.EXTRAS_REPOSITORY);

        // Fallback to SharedPreferences if extras are missing
        if (uriString == null) {
            SharedPreferences notePrefs = getSharedPreferences(PluginReceiver.PREFS_NOTE_DATA, MODE_PRIVATE);
            uriString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_URI, null);
            repositoryString = notePrefs.getString(AbstractPluginReceiver.EXTRAS_REPOSITORY, null);
        }

        Log.d(TAG, "uri: " + uriString + ", repository: " + repositoryString);

        if (uriString == null) {
            Log.e(TAG, "No URI found in intent extras or SharedPreferences");
            finish();
            return;
        }

        String filename = uriString;
        
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
                // Correctly extract the repository name instead of clearing it
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
            finish();
            return;
        }

        // Sanitize leading slash to avoid double '%2F' encoding later
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
            if (inAppBrowser) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
                mBrowserLaunched = true;
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                mBrowserLaunched = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch browser", e);
            finish();
            return;
        }

        setResult(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsFirstResume) {
            mIsFirstResume = false;
        } else if (mBrowserLaunched) {
            finish(); // Close this transparent activity once we return from the browser
        }
    }
}
