package com.github.ekodomo3.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.eu.thedoc.zettelnotes.broadcasts.AbstractPluginReceiver;

public class PluginReceiver extends BroadcastReceiver {
    private static final String TAG = "PluginReceiver";
    public static final String PREFS_NOTE_DATA = "note_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast action: " + intent.getAction());
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Log.d(TAG, "Extra: " + key + " = " + intent.getExtras().get(key));
            }
        }

        String uri = null;
        Object uriObj = intent.getExtras() != null ? intent.getExtras().get(AbstractPluginReceiver.EXTRAS_URI) : null;
        if (uriObj != null) {
            uri = uriObj.toString();
        }

        String repo = null;
        Object repoObj = intent.getExtras() != null ? intent.getExtras().get(AbstractPluginReceiver.EXTRAS_REPOSITORY) : null;
        if (repoObj != null) {
            repo = repoObj.toString();
        }

        Log.d(TAG, "Parsed broadcast URI: " + uri + ", Repo: " + repo);

        if (uri != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NOTE_DATA, Context.MODE_PRIVATE);
            prefs.edit()
                 .putString(AbstractPluginReceiver.EXTRAS_URI, uri)
                 .putString(AbstractPluginReceiver.EXTRAS_REPOSITORY, repo)
                 .apply();
            Log.d(TAG, "Saved URI and Repo to SharedPreferences.");
        } else {
            Log.e(TAG, "URI was null, not saving to SharedPreferences.");
        }
    }
}
