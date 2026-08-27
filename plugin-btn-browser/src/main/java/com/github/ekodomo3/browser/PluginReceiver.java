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
        String uri = intent.getStringExtra(AbstractPluginReceiver.EXTRAS_URI);
        String repo = intent.getStringExtra(AbstractPluginReceiver.EXTRAS_REPOSITORY);

        Log.d(TAG, "Received broadcast with URI: " + uri + " Repo: " + repo);

        if (uri != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NOTE_DATA, Context.MODE_PRIVATE);
            prefs.edit()
                 .putString(AbstractPluginReceiver.EXTRAS_URI, uri)
                 .putString(AbstractPluginReceiver.EXTRAS_REPOSITORY, repo)
                 .apply();
        }
    }
}
