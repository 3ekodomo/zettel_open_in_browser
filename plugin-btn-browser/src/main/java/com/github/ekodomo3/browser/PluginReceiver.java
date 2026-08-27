package com.github.ekodomo3.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.eu.thedoc.zettelnotes.broadcasts.AbstractPluginReceiver;

public class PluginReceiver extends BroadcastReceiver {
    private static final String TAG = "PluginReceiver";
    public static final String PREFS_NOTE_DATA = "note_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppLogger.init(context);
        AppLogger.d(TAG, "Received broadcast action: " + intent.getAction());
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                AppLogger.d(TAG, "Extra: " + key + " = " + intent.getExtras().get(key));
            }
        }

        String uri = null;
        Object uriObj = intent.getExtras() != null ? intent.getExtras().get(AbstractPluginReceiver.EXTRAS_URI) : null;
        if (uriObj != null) {
            uri = uriObj.toString();
        } else if (intent.getData() != null) {
            uri = intent.getData().toString();
        } else if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            uri = intent.getStringExtra(Intent.EXTRA_TEXT);
        } else if (intent.getParcelableExtra(Intent.EXTRA_STREAM) != null) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM).toString();
        }

        if (uri == null && intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object val = intent.getExtras().get(key);
                if (val != null) {
                    String strVal = val.toString();
                    if (strVal.startsWith("content://") || strVal.startsWith("file://") || strVal.endsWith(".md") || strVal.endsWith(".txt")) {
                        uri = strVal;
                        break;
                    }
                }
            }
        }

        String repo = null;
        Object repoObj = intent.getExtras() != null ? intent.getExtras().get(AbstractPluginReceiver.EXTRAS_REPOSITORY) : null;
        if (repoObj != null) {
            repo = repoObj.toString();
        } else if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                if (key.toLowerCase().contains("repo")) {
                    Object val = intent.getExtras().get(key);
                    if (val != null) repo = val.toString();
                }
            }
        }

        AppLogger.d(TAG, "Parsed broadcast URI: " + uri + ", Repo: " + repo);

        if (uri != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NOTE_DATA, Context.MODE_PRIVATE);
            prefs.edit()
                 .putString(AbstractPluginReceiver.EXTRAS_URI, uri)
                 .putString(AbstractPluginReceiver.EXTRAS_REPOSITORY, repo)
                 .commit();
            AppLogger.d(TAG, "Saved URI and Repo to SharedPreferences.");
        } else {
            AppLogger.e(TAG, "URI was null, not saving to SharedPreferences.");
        }
    }
}
