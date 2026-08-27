package com.github.ekodomo3.browser;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppLogger {
    private static File logFile;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    public static void init(Context context) {
        if (logFile == null && context != null) {
            logFile = new File(context.getApplicationContext().getFilesDir(), "plugin_logs.txt");
        }
    }

    private static void writeLog(String level, String tag, String msg) {
        if (logFile == null) return;
        String time = sdf.format(new Date());
        String logLine = time + " " + level + "/" + tag + ": " + msg + "\n";
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            fos.write(logLine.getBytes());
        } catch (IOException e) {
            Log.e("AppLogger", "Failed to write log", e);
        }
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        writeLog("D", tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        writeLog("E", tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
        writeLog("E", tag, msg + "\n" + Log.getStackTraceString(t));
    }

    public static String getLogs() {
        if (logFile == null || !logFile.exists()) return "No logs found.";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Failed to read logs: " + e.getMessage();
        }
        return sb.toString();
    }

    public static void clearLogs() {
        if (logFile != null && logFile.exists()) {
            logFile.delete();
        }
    }
}
