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

public class MainActivity extends Activity {

    private static final String TAG = "BrowserPlugin";
    private static final String BASE_URL = "https://3ekodomo.github.io/site/markdown?open=";

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

        Log.d(TAG, "uri: " + uriString + ", repository: " + repositoryString);

        if (uriString == null) {
            Log.e(TAG, "No URI found in intent extras");
            finish();
            return;
        }

        String filename = uriString;
        if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
            filename = Uri.parse(uriString).getLastPathSegment();
            if (filename == null) {
                filename = uriString;
            }
        }

        String url = BASE_URL;

        try {
            if (repositoryString != null && !repositoryString.isEmpty()) {
                url += URLEncoder.encode(repositoryString, "UTF-8") + "%2F";
            }
            url += URLEncoder.encode(filename, "UTF-8");
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
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch browser", e);
        }

        setResult(RESULT_OK);
        finish();
    }
}
