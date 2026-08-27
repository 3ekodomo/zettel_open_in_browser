package com.github.ekodomo3.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Entry point used by Zettel Notes to discover this button plugin.
 * This router forwards the incoming click intent directly to MainActivity.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Forward the intent and its extras (like arg-uri, arg-repository) to MainActivity
        Intent targetIntent = new Intent(this, MainActivity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            targetIntent.putExtras(getIntent().getExtras());
        }
        startActivity(targetIntent);
        
        // Close this router activity immediately
        finish();
    }
}
