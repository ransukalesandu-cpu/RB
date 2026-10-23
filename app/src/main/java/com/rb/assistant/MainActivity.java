package com.rb.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_SPEECH = 101;
    private static final int REQUEST_AUDIO_PERMISSION = 102;
    private static final int REQUEST_NOTIFICATIONS = 103;
    private static final String PREFS = "rb_local_memory";
    private static final String HISTORY = "history";
    private TextView status;
    private TextView transcript;
    private EditText input;
    private ScrollView scroll;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private String languageMode = "en";
    private final int bg = Color.rgb(8, 13, 27);
    private final int panel = Color.rgb(17, 27, 47);
    private final int cyan = Color.rgb(75, 210, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildScreen();
        restoreHistory();
        tts = new TextToSpeech(this, result -> {
            ttsReady = result == TextToSpeech.SUCCESS;
            if (ttsReady) configureVoice();
        });
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(16));
        root.setBackgroundColor(bg);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        TextView brand = new TextView(this);
        brand.setText("R B  /  J A R V I S   M O D E");
        brand.setTextSize(24);
        brand.setTypeface(Typeface.DEFAULT_BOLD);
        brand.setTextColor(cyan);
        brand.setGravity(Gravity.CENTER);
        header.addView(brand, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("YOUR PERSONAL AI ASSISTANT");
        subtitle.setTextSize(11);
        subtitle.setTextColor(Color.rgb(145, 165, 195));
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(-1, -2);
        subParams.topMargin = dp(7);
        header.addView(subtitle, subParams);
        root.addView(header);

        status = new TextView(this);
        status.setText("SYSTEM ONLINE  •  LOCAL COMMANDS READY");
        status.setTextColor(Color.rgb(104, 225, 179));
        status.setTextSize(11);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(6), dp(15), dp(6), dp(12));
        root.addView(status);

        LinearLayout modes = new LinearLayout(this);
        modes.setGravity(Gravity.CENTER);
        modes.setOrientation(LinearLayout.HORIZONTAL);
        modes.addView(modeButton("සිංහල", "si"), new LinearLayout.LayoutParams(0, dp(42), 1));
        modes.addView(modeButton("Singlish", "sg"), new LinearLayout.LayoutParams(0, dp(42), 1));
        modes.addView(modeButton("English", "en"), new LinearLayout.LayoutParams(0, dp(42), 1));
        root.addView(modes);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(-1, 0, 1);
        scrollParams.topMargin = dp(14);
        scrollParams.bottomMargin = dp(12);
        transcript = new TextView(this);
        transcript.setTextColor(Color.rgb(223, 233, 247));
        transcript.setTextSize(15);
        transcript.setLineSpacing(dp(4), 1f);
        transcript.setPadding(dp(15), dp(15), dp(15), dp(15));
        transcript.setBackground(roundDrawable(panel, dp(16)));
        scroll.addView(transcript, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, scrollParams);

        input = new EditText(this);
        input.setSingleLine(false);
        input.setMinLines(1);
        input.setMaxLines(3);
        input.setTextSize(15);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.rgb(130, 148, 177));
        input.setHint("Ask RB or type a command…");
        input.setPadding(dp(14), dp(10), dp(14), dp(10));
        input.setBackground(roundDrawable(panel, dp(12)));
        root.addView(input, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams controlsParams = new LinearLayout.LayoutParams(-1, -2);
        controlsParams.topMargin = dp(10);
        Button talk = actionButton("🎙  TALK");
        talk.setOnClickListener(v -> startListening());
        Button send = actionButton("SEND  ➤");
        send.setOnClickListener(v -> sendTypedMessage());
        controls.addView(talk, new LinearLayout.LayoutParams(0, dp(48), 1));
        LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(0, dp(48), 1);
        sendParams.leftMargin = dp(10);
        controls.addView(send, sendParams);
        root.addView(controls, controlsParams);

        TextView footer = new TextView(this);
        footer.setText("LOCAL MEMORY ON DEVICE  •  AI CLOUD CONNECTION NOT SET UP YET");
        footer.setTextSize(9);
        footer.setTextColor(Color.rgb(113, 131, 159));
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(12), 0, 0);
        root.addView(footer);

        setContentView(root);
    }

    private Button modeButton(String label, String mode) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(11);
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(v -> {
            languageMode = mode;
            if (ttsReady) configureVoice();
            status.setText("VOICE MODE: " + (mode.equals("si") ? "SINHALA" : mode.equals("sg") ? "SINGLISH" : "ENGLISH"));
        });
        return button;
    }

    private Button actionButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setTextColor(Color.WHITE);
        button.setBackground(roundDrawable(Color.rgb(20, 105, 148), dp(12)));
        return button;
    }

    private GradientDrawable roundDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        drawable.setStroke(dp(1), Color.rgb(36, 62, 91));
        return drawable;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void restoreHistory() {
        String saved = getSharedPreferences(PREFS, MODE_PRIVATE).getString(HISTORY, "");
        if (saved == null || saved.trim().isEmpty()) {
            transcript.setText("RB: Hello! I'm your personal assistant.\n\nTry: “What time is it?” or “Remind me in 10 minutes to drink water.”\n\n");
        } else {
            transcript.setText(saved);
        }
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private void addMessage(String who, String message) {
        String current = transcript.getText().toString();
        String updated = current + who + ": " + message + "\n\n";
        String[] lines = updated.split("\n\n");
        if (lines.length > 24) {
            StringBuilder recent = new StringBuilder();
            for (int i = Math.max(0, lines.length - 24); i < lines.length; i++) {
                if (!lines[i].isEmpty()) recent.append(lines[i]).append("\n\n");
            }
            updated = recent.toString();
        }
        transcript.setText(updated);
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(HISTORY, updated).apply();
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private void sendTypedMessage() {
        String message = input.getText().toString().trim();
        if (message.isEmpty()) return;
        input.setText("");
        handleMessage(message);
    }

    private void handleMessage(String message) {
        addMessage("YOU", message);
        String response = AssistantEngine.respond(this, message);
        addMessage("RB", response);
        if (ttsReady) tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, "rb-reply");
        if (response.startsWith("Reminder set") && Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }

    private void startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String locale = languageMode.equals("si") ? "si-LK" : languageMode.equals("sg") ? "en-IN" : "en-US";
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, languageMode.equals("si") ? "සිංහලෙන් කතා කරන්න" : "Speak to RB");
        try {
            startActivityForResult(intent, REQUEST_SPEECH);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not available on this device.", Toast.LENGTH_LONG).show();
        }
    }

    private void configureVoice() {
        if (tts == null || !ttsReady) return;
        Locale locale = languageMode.equals("si") ? new Locale("si", "LK") : languageMode.equals("sg") ? new Locale("en", "IN") : Locale.US;
        int result = tts.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale.US);
            status.setText("Selected voice unavailable on device; using the default English voice.");
        }
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                for (Voice voice : tts.getVoices()) {
                    String name = voice.getName().toLowerCase(Locale.ROOT);
                    if (voice.getLocale().getLanguage().equals(locale.getLanguage()) &&
                        (name.contains("female") || name.contains("fem") || name.contains("woman"))) {
                        tts.setVoice(voice);
                        break;
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) handleMessage(results.get(0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION && grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else if (requestCode == REQUEST_AUDIO_PERMISSION) {
            status.setText("Microphone permission is needed for voice input.");
        } else if (requestCode == REQUEST_NOTIFICATIONS &&
            (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Enable notifications in Android Settings for reminder alerts.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
