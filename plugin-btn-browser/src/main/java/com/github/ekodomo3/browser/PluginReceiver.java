package com.github.ekodomo3.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.eu.thedoc.zettelnotes.broadcasts.AbstractPluginReceiver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PluginReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Not used as we use Button API instead.
    }
}
