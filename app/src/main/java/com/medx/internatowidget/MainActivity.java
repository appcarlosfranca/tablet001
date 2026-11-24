package com.medx.internatowidget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final Calendar target = Calendar.getInstance();
    private TextView dateValue;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        target.setTimeInMillis(WidgetPrefs.getTargetMillis(this));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(28), dp(42), dp(28), dp(28));
        root.setBackgroundResource(R.drawable.app_background);

        TextView title = new TextView(this);
        title.setText("MED X");
        title.setTextColor(Color.rgb(0, 234, 242));
        title.setTextSize(36);
        title.setTypeface(null, 1);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("CONTAGEM INTERNATO • WIDGET");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(12);
        subtitle.setLetterSpacing(0.16f);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sp.setMargins(0, dp(6), 0, dp(36));
        root.addView(subtitle, sp);

        TextView label = new TextView(this);
        label.setText("Fim do internato");
        label.setTextColor(Color.LTGRAY);
        label.setTextSize(14);
        root.addView(label);

        dateValue = new TextView(this);
        dateValue.setTextColor(Color.WHITE);
        dateValue.setTextSize(24);
        dateValue.setTypeface(null, 1);
        LinearLayout.LayoutParams dpv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dpv.setMargins(0, dp(8), 0, dp(26));
        root.addView(dateValue, dpv);
        refreshDateLabel();

        Button dateBtn = button("ALTERAR DATA");
        dateBtn.setOnClickListener(v -> pickDate());
        root.addView(dateBtn, buttonParams());

        Button timeBtn = button("ALTERAR HORÁRIO");
        timeBtn.setOnClickListener(v -> pickTime());
        root.addView(timeBtn, buttonParams());

        Button save = button("SALVAR E ATUALIZAR WIDGET");
        save.setOnClickListener(v -> {
            WidgetPrefs.setTargetMillis(this, target.getTimeInMillis());
            MedXWidgetProvider.updateAll(this);
            Toast.makeText(this, "Widget atualizado.", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams saveParams = buttonParams();
        saveParams.setMargins(0, dp(20), 0, 0);
        root.addView(save, saveParams);

        TextView hint = new TextView(this);
        hint.setText("Na tela inicial do Samsung: toque e segure em uma área vazia → Widgets → MED X • Contagem Internato.");
        hint.setTextColor(Color.rgb(174, 180, 194));
        hint.setTextSize(13);
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hp.setMargins(0, dp(36), 0, 0);
        root.addView(hint, hp);

        setContentView(root);
    }

    private void pickDate() {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    target.set(Calendar.YEAR, year);
                    target.set(Calendar.MONTH, month);
                    target.set(Calendar.DAY_OF_MONTH, day);
                    refreshDateLabel();
                },
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void pickTime() {
        new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    target.set(Calendar.HOUR_OF_DAY, hour);
                    target.set(Calendar.MINUTE, minute);
                    target.set(Calendar.SECOND, 0);
                    target.set(Calendar.MILLISECOND, 0);
                    refreshDateLabel();
                },
                target.get(Calendar.HOUR_OF_DAY),
                target.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void refreshDateLabel() {
        dateValue.setText(fmt.format(new Date(target.getTimeInMillis())));
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        return b;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52));
        p.setMargins(0, dp(8), 0, 0);
        return p;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
