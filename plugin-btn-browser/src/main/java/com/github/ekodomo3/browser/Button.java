package com.github.ekodomo3.browser;

import android.content.Intent;
import org.eu.thedoc.zettelnotes.interfaces.ButtonInterface;

public class Button extends ButtonInterface {

  public static final String INTENT_ACTION = "org.eu.thedoc.zettelnotes.intent.buttons.browser.open";
  public static final String INTENT_ACTION_SETTINGS = "org.eu.thedoc.zettelnotes.intent.buttons.browser.settings";

  private final Listener mListener = new Listener() {
    @Override
    public void onClick() {
      if (mCallback != null) {
        Intent intent = new Intent(INTENT_ACTION);
        
        try {
            // Retrieve data using the new API methods
            String relativeFileName = mCallback.getRelativeFileName();
            if (relativeFileName != null) {
                intent.putExtra("relativeFileName", relativeFileName);
            }
        } catch (Throwable t) {
            AppLogger.e("Button", "Failed to retrieve relative file name. Ensure Zettel Notes is updated.", t);
        }
        
        mCallback.setActivityResultListener((resultCode, data) -> { /* no result expected */ });
        mCallback.startActivityForResult(intent);
      }
    }

    @Override
    public boolean onLongClick() {
      if (mCallback != null) {
        mCallback.setActivityResultListener((resultCode, data) -> { /* no result expected */ });
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
