package com.github.ekodomo3.browser;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import android.widget.Button;

import org.eu.thedoc.zettelnotes.plugins.base.BaseActivity;

public class SettingsActivity extends BaseActivity {

  public static final String PREFS = "_prefs_browser";
  public static final String PREF_IN_APP_BROWSER = "pref_in_app_browser";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppLogger.init(this);

    ScrollView scrollView = new ScrollView(this);
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    int p = dp(20);
    root.setPadding(p, p, p, p);
    scrollView.addView(root);

    TextView title = new TextView(this);
    title.setText("Open in Browser Settings");
    title.setTextSize(20);
    root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    TextView help = new TextView(this);
    help.setText("\nChoose how to open the Zettel Notes preview URL:\n");
    root.addView(help, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    RadioGroup radioGroup = new RadioGroup(this);

    RadioButton inAppRb = new RadioButton(this);
    inAppRb.setText("In-App Browser (Custom Tabs)");

    RadioButton externalRb = new RadioButton(this);
    externalRb.setText("External Browser");

    radioGroup.addView(inAppRb);
    radioGroup.addView(externalRb);

    boolean inAppBrowser = getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(PREF_IN_APP_BROWSER, true);
    if (inAppBrowser) {
        inAppRb.setChecked(true);
    } else {
        externalRb.setChecked(true);
    }

    radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
        boolean isInApp = (checkedId == inAppRb.getId());
        getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(PREF_IN_APP_BROWSER, isInApp)
            .apply();
        Toast.makeText(this, "Browser preference saved", Toast.LENGTH_SHORT).show();
    });

    root.addView(radioGroup, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    TextView logsTitle = new TextView(this);
    logsTitle.setText("\n\nLogs:");
    logsTitle.setTextSize(18);
    root.addView(logsTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    TextView logsView = new TextView(this);
    logsView.setText(AppLogger.getLogs());
    logsView.setTextIsSelectable(true);
    root.addView(logsView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    Button refreshBtn = new Button(this);
    refreshBtn.setText("Refresh Logs");
    refreshBtn.setOnClickListener(v -> logsView.setText(AppLogger.getLogs()));
    root.addView(refreshBtn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    Button clearBtn = new Button(this);
    clearBtn.setText("Clear Logs");
    clearBtn.setOnClickListener(v -> {
        AppLogger.clearLogs();
        logsView.setText(AppLogger.getLogs());
    });
    root.addView(clearBtn, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    setContentView(scrollView);
  }

  private int dp(int value) {
    return Math.round(value * getResources().getDisplayMetrics().density);
  }
}
