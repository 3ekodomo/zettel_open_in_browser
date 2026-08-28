package org.eu.thedoc.zettelnotes.interfaces;

import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

public abstract class ButtonInterface {

    protected Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public abstract String getName();

    public abstract Listener getListener();

    public interface Listener {
        void onClick();
        boolean onLongClick();
    }

    public interface Callback {

        void startActivityForResult(Intent intent);

        void setActivityResultListener(ActivityResultListener result);

        void insertText(String text);

        void replaceTextSelected(String text);

        Pair<Integer, String> getCurrentLine();

        void replaceCurrentLine(Pair<Integer, String> pair);

        String getTextSelected(boolean returnAllIfEmpty);

        void insertUri(Uri uri);

        // Added methods to directly expose note paths
        String getFileName();

        String getRelativeFileName();
    }

    public interface ActivityResultListener {
        void onActivityResult(int resultCode, Intent data);
    }
}
