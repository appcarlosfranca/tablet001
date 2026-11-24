package com.medx.internatowidget;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.Build;
import android.view.View;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.SystemClock;
import android.widget.RemoteViews;

import java.util.Locale;

public class MedXWidgetProvider extends AppWidgetProvider {
    private static final String BOUNDARY = "com.medx.internatowidget.BOUNDARY";
    private static final long DAY_MS = 24L * 60L * 60L * 1000L;

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) updateWidget(context, manager, id);
        scheduleBoundary(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (BOUNDARY.equals(action) || Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_DATE_CHANGED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            updateAll(context);
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, MedXWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(component);
        for (int id : ids) updateWidget(context, manager, id);
        scheduleBoundary(context);
    }

    public static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        long now = System.currentTimeMillis();
        long target = WidgetPrefs.getTargetMillis(context);
        long diff = Math.max(0L, target - now);

        long totalDays = diff / DAY_MS;
        long weeks = totalDays / 7L;
        long days = totalDays % 7L;
        

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_medx);
        views.setTextViewText(R.id.weeksValue, String.format(Locale.getDefault(), "%02d", weeks));
        views.setTextViewText(R.id.daysValue, String.format(Locale.getDefault(), "%02d", days));
        views.setTextViewText(R.id.daysPrefix, "FALTAM: " + totalDays + "d ");

        // The timer uses the complete remaining interval, so it never crosses zero at a day boundary.
        long base = SystemClock.elapsedRealtime() + diff;
        views.setViewVisibility(R.id.remainderChrono, diff > 0 ? View.VISIBLE : View.GONE);
        views.setTextViewText(R.id.daysPrefix, diff > 0 ? "FALTAM: " + totalDays + "d • TOTAL " : "INTERNATO CONCLUÍDO!");
        views.setChronometer(R.id.remainderChrono, base, null, diff > 0L);
        views.setChronometerCountDown(R.id.remainderChrono, true);

        float weekArc = diff == 0 ? 0f : Math.min(1f, weeks / 52f);
        float dayArc = days / 7f;
        views.setImageViewBitmap(R.id.ringWeeks, createRingBitmap(220, weekArc, true));
        views.setImageViewBitmap(R.id.ringDays, createRingBitmap(220, dayArc, false));

        Intent launch = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(
                context,
                0,
                launch,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetRoot, pending);

        manager.updateAppWidget(widgetId, views);
    }

    private static PendingIntent boundaryIntent(Context context) {
        Intent intent = new Intent(context, MedXWidgetProvider.class).setAction(BOUNDARY);
        return PendingIntent.getBroadcast(context, 42, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void scheduleBoundary(Context context) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms == null) return;
        PendingIntent pending = boundaryIntent(context);
        alarms.cancel(pending);
        if (AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MedXWidgetProvider.class)).length == 0) return;
        long diff = WidgetPrefs.getTargetMillis(context) - System.currentTimeMillis();
        if (diff <= 0) return;
        long delay = Math.min(diff, diff % DAY_MS + 1000L);
        long when = SystemClock.elapsedRealtime() + delay;
        if (Build.VERSION.SDK_INT < 31 || alarms.canScheduleExactAlarms()) {
            try { alarms.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, when, pending); return; }
            catch (SecurityException ignored) { }
        }
        alarms.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, when, pending);
    }

    @Override public void onDisabled(Context context) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms != null) alarms.cancel(boundaryIntent(context));
    }

    private static Bitmap createRingBitmap(int size, float progress, boolean purple) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        float c = size / 2f;
        float pad = size * 0.13f;
        RectF oval = new RectF(pad, pad, size - pad, size - pad);

        Paint base = new Paint(Paint.ANTI_ALIAS_FLAG);
        base.setStyle(Paint.Style.STROKE);
        base.setStrokeWidth(size * 0.075f);
        base.setStrokeCap(Paint.Cap.ROUND);
        base.setColor(Color.argb(42, 255, 255, 255));
        canvas.drawArc(oval, 0, 360, false, base);

        int cyan = Color.rgb(0, 234, 242);
        int violet = Color.rgb(198, 44, 255);
        int[] colors = purple
                ? new int[]{violet, Color.rgb(121, 54, 255), violet}
                : new int[]{cyan, Color.rgb(69, 102, 255), cyan};

        Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
        glow.setStyle(Paint.Style.STROKE);
        glow.setStrokeWidth(size * 0.11f);
        glow.setStrokeCap(Paint.Cap.ROUND);
        glow.setColor(purple ? Color.argb(70, 198, 44, 255) : Color.argb(70, 0, 234, 242));
        canvas.drawArc(oval, -90f, 360f * progress, false, glow);

        Paint arc = new Paint(Paint.ANTI_ALIAS_FLAG);
        arc.setStyle(Paint.Style.STROKE);
        arc.setStrokeWidth(size * 0.065f);
        arc.setStrokeCap(Paint.Cap.ROUND);
        SweepGradient shader = new SweepGradient(c, c, colors, null);
        arc.setShader(shader);

        canvas.save();
        canvas.rotate(-90f, c, c);
        canvas.drawArc(oval, 0f, 360f * progress, false, arc);
        canvas.restore();

        Paint outer = new Paint(Paint.ANTI_ALIAS_FLAG);
        outer.setStyle(Paint.Style.STROKE);
        outer.setStrokeWidth(1.5f);
        outer.setColor(Color.argb(45, 255, 255, 255));
        canvas.drawCircle(c, c, c - 4f, outer);

        return bmp;
    }
}
