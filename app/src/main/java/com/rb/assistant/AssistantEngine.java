package com.rb.assistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Local, offline-safe command layer. A remote AI provider can be connected later. */
public final class AssistantEngine {
    private AssistantEngine() {}

    public static String respond(Context context, String input) {
        String text = input == null ? "" : input.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        if (text.isEmpty()) return "Tell me what you need, and I'll try to help.";

        if (lower.matches(".*\\b(hello|hi|hey|good morning|good evening)\\b.*")) {
            return "Hello! RB is ready. What shall we work on today?";
        }
        if (lower.contains("what time") || lower.contains("time now") || lower.equals("time")) {
            return "It's " + new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date()) + ".";
        }
        if (lower.contains("what date") || lower.contains("today's date") || lower.equals("date")) {
            return "Today is " + new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(new Date()) + ".";
        }
        if (lower.contains("motivate") || lower.contains("motivation") || lower.contains("give me motivation")) {
            return "Small steps, repeated every day, create big results. Focus on the next 25 minutes. You don't need a perfect day; you need to begin.";
        }
        if (lower.contains("study") && (lower.contains("plan") || lower.contains("focus"))) {
            return "Try one focused 25-minute session, then take a 5-minute break. Start with the hardest Maths, Physics, or ICT task and keep your phone away during the session.";
        }

        Pattern reminderPattern = Pattern.compile(".*?(?:remind me|remind|set a reminder).*?\\bin\\s+(\\d+)\\s*(minutes?|mins?|hours?|hrs?)\\s+(?:to\\s+)?(.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = reminderPattern.matcher(text);
        if (matcher.matches()) {
            try {
                int amount = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2).toLowerCase(Locale.ROOT);
                String task = matcher.group(3).trim();
                if (amount < 1 || task.isEmpty()) {
                    return "Try: remind me in 10 minutes to drink water.";
                }
                long multiplier = (unit.startsWith("h") ? 60L : 1L) * 60L * 1000L;
                long triggerAt = System.currentTimeMillis() + amount * multiplier;
                int requestCode = (int) (System.currentTimeMillis() & 0x7fffffff);
                Intent intent = new Intent(context, ReminderReceiver.class);
                intent.putExtra(ReminderReceiver.EXTRA_TITLE, "RB Reminder");
                intent.putExtra(ReminderReceiver.EXTRA_MESSAGE, task);
                PendingIntent pending = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarms == null) return "I couldn't access Android's alarm service.";
                alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending);
                return "Reminder set for " + amount + " " + unit + ": " + task + ". Keep notifications enabled so RB can alert you.";
            } catch (Exception e) {
                return "I couldn't set that reminder. Try: remind me in 10 minutes to drink water.";
            }
        }

        return "I heard you: “" + text + "”. My local command system is active, but the online AI brain isn't connected yet. The next upgrade will add a secure AI provider connection.";
    }
}
