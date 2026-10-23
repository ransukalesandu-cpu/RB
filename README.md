# RB AI Assistant

An Android-first personal assistant inspired by Jarvis-style desktop assistants. This repository is an independent Android implementation; desktop-only Python modules are not copied into the Android app.

## Current Android features

- Dark, Jarvis-inspired assistant interface
- Speech recognition with Sinhala, Singlish-oriented, and English mode choices (actual recognition languages depend on the speech service installed on the phone)
- Android text-to-speech replies; voice and language availability depend on the device's installed TTS voices
- Local command handling for greetings, date/time, and study motivation
- Voice or typed reminders such as: `remind me in 10 minutes to drink water`
- Reminder notifications through Android's alarm and notification APIs
- Recent chat transcript stored locally on the device
- GitHub Actions debug APK build

## Planned upgrades

- Secure connection to an AI provider (not configured yet)
- More robust Sinhala/Singlish intent recognition
- Editable tasks and reminder management
- Optional hands-free mode with visible microphone controls
- Expanded Android actions with explicit permissions and confirmation for sensitive operations

## Build

GitHub Actions builds a debug APK on pushes to `main`. Open the **Actions** tab, select **RB Android APK**, and download the `RB-debug-apk` artifact from a successful run.

## Privacy and security

- Do not commit API keys, tokens, or private credentials.
- Current chat history is stored in app-local preferences on the device.
- Voice input is initiated by tapping the microphone button; always-on wake-word listening is not implemented yet.
- Reminders use Android notifications; allow notifications in system settings for visible alerts.
- Sensitive Android actions should request explicit user permission and confirmation.

## Project inspiration

The feature direction is inspired by the public Jarvis-AI project by Ninja2007S. This Android implementation is written for this repository rather than copying desktop-only modules. The original project states that its source is licensed CC BY-NC 4.0; consult the original repository's license before reusing its source or assets.
