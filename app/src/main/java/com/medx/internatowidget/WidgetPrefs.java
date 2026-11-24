package com.medx.internatowidget;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public final class WidgetPrefs {
    private static final String PREFS = "medx_widget_prefs";
    private static final String KEY_TARGET = "target_millis";

    private WidgetPrefs() {}

    public static long getTargetMillis(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long saved = prefs.getLong(KEY_TARGET, 0L);
        if (saved > 0L) return saved;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2027);
        c.set(Calendar.MONTH, Calendar.MARCH);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static void setTargetMillis(Context context, long millis) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_TARGET, millis)
                .apply();
    }
}
