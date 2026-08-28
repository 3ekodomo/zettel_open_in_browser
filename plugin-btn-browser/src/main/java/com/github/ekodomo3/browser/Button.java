package com.github.ekodomo3.browser;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import org.eu.thedoc.zettelnotes.interfaces.ButtonInterface;

public class Button extends ButtonInterface {

  public static final String INTENT_ACTION = "org.eu.thedoc.zettelnotes.intent.buttons.browser.open";
  public static final String INTENT_ACTION_SETTINGS = "org.eu.thedoc.zettelnotes.intent.buttons.browser.settings";

  private final Listener mListener = new Listener() {
    @Override
    public void onClick() {
      if (mCallback != null) {
        Intent intent = new Intent(INTENT_ACTION);
        
        // Pass the filename and relative filename using the updated API
        String fileName = null;
        String relativeFileName = null;
        try {
            fileName = mCallback.getFileName();
            relativeFileName = mCallback.getRelativeFileName();
        } catch (Throwable t) {
            Log.e("BrowserPlugin", "Method not found in callback", t);
        }

        if (fileName != null) {
            intent.putExtra("EXTRAS_FILE_NAME", fileName);
        }
        if (relativeFileName != null) {
            intent.putExtra("EXTRAS_RELATIVE_FILE_NAME", relativeFileName);
        }
        
        mCallback.setActivityResultListener(result -> { /* no result expected */ });
        mCallback.startActivityForResult(intent);
      }
    }

    @Override
    public boolean onLongClick() {
      if (mCallback != null) {
        mCallback.setActivityResultListener(result -> { /* no result expected */ });
        mCallback.startActivityForResult(new Intent(INTENT_ACTION_SETTINGS));
        return true;
      }
      return false;
    }
  };

  @Override
  public String getName() {
    return "Open in Browser";
  }

  @Override
  public Listener getListener() {
    return mListener;
  }
}
