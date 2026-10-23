package com.rb.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_SPEECH = 101;
    private static final int REQUEST_AUDIO_PERMISSION = 102;
    private TextView status;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(9, 14, 27));
        getWindow().setNavigationBarColor(Color.rgb(9, 14, 27));
        buildScreen();
        tts = new TextToSpeech(this, result -> {
            ttsReady = result == TextToSpeech.SUCCESS;
            if (ttsReady) tts.setLanguage(new Locale("en", "US"));
        });
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(28, 52, 28, 28);
        root.setBackgroundColor(Color.rgb(9, 14, 27));

        TextView brand = new TextView(this);
        brand.setText("RB");
        brand.setTextSize(48);
        brand.setTypeface(Typeface.DEFAULT_BOLD);
        brand.setTextColor(Color.rgb(87, 205, 255));
        brand.setGravity(Gravity.CENTER);
        root.addView(brand, new LinearLayout.LayoutParams(-1, -2));

        TextView heading = new TextView(this);
        heading.setText("Your personal AI assistant");
        heading.setTextSize(20);
        heading.setTextColor(Color.WHITE);
        heading.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = 8;
        root.addView(heading, hp);

        status = new TextView(this);
        status.setText("RB is ready. Tap the microphone to begin.");
        status.setTextSize(16);
        status.setTextColor(Color.rgb(190, 203, 222));
        status.setGravity(Gravity.CENTER);
        status.setPadding(8, 32, 8, 32);
        root.addView(status, new LinearLayout.LayoutParams(-1, -2));

        Button listen = new Button(this);
        listen.setText("🎙  TALK TO RB");
        listen.setAllCaps(false);
        listen.setOnClickListener(v -> startListening());
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 16;
        root.addView(listen, bp);

        TextView note = new TextView(this);
        note.setText("Starter build · Voice recognition uses your device's available speech service. AI chat and phone actions will be added in later stages.");
        note.setTextSize(12);
        note.setTextColor(Color.rgb(135, 151, 175));
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2);
        np.topMargin = 28;
        root.addView(note, np);
        setContentView(root);
    }

    private void startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to RB");
        try {
            startActivityForResult(intent, REQUEST_SPEECH);
        } catch (Exception e) {
            status.setText("Speech recognition is unavailable on this device.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spoken = results.get(0);
                status.setText("You said: " + spoken + "\n\nAI connection is not configured yet.");
                if (ttsReady) tts.speak("I heard you say " + spoken, TextToSpeech.QUEUE_FLUSH, null, "rb-response");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else if (requestCode == REQUEST_AUDIO_PERMISSION) {
            status.setText("Microphone permission is needed for voice input.");
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
