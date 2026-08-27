package com.github.ekodomo3.browser;

import android.content.Context;
import org.eu.thedoc.zettelnotes.interfaces.ScanInterface;

public class Scanner extends ScanInterface {
    // Shared state running inside the Zettel Notes process
    public static String currentUri = null;
    public static String currentRepository = null;

    @Override
    public String getName() {
        return "Browser URI Tracker";
    }

    @Override
    public Listener getListener() {
        return new Listener() {
            @Override
            public void onScanText(Context context, String category, String fileUri, String fileTitle, String text) {
                // Intercept the metadata when Zettel Notes loads the note
                currentRepository = category;
                currentUri = fileUri;
            }
        };
    }
}
