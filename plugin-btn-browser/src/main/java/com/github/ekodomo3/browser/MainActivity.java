package com.github.ekodomo3.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        String filename = null;

        if (intent != null && intent.hasExtra("relativeFileName")) {
            filename = intent.getStringExtra("relativeFileName");
        }

        if (filename == null || filename.isEmpty()) {
            AppLogger.e(TAG, "Filename is empty.");
            Toast.makeText(this, "Could not extract filename. Please update Zettel Notes.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        String url = BASE_URL;

        try {
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
            if (inAppBrowser) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                customTabsIntent.launchUrl(this, Uri.parse(url));
            } else {
                throw new Exception("External browser requested");
            }
        } catch (Exception e) {
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
            finish(); 
        }
    }
}
