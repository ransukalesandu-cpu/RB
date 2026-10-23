# RB AI Assistant

A new Android-first personal AI assistant inspired by Jarvis-style assistants.

## Project goals

- Sinhala, Singlish, and English voice interaction
- AI conversation with privacy-conscious memory
- Voice-driven alarms, reminders, and tasks
- Optional hands-free mode, with clear microphone controls
- Safe Android actions using explicit permissions
- Modern Android UI and automated debug APK builds

## Architecture principles

1. Build a clean Android app rather than copying desktop-only code blindly.
2. Keep voice capture, speech recognition, text-to-speech, AI providers, memory, and actions in separate modules.
3. Never commit API keys, service-role keys, access tokens, or personal credentials.
4. Ask for confirmation before destructive or sensitive actions.
5. Test each feature and verify the debug build before calling it complete.

## Current status

Project initialized. Android app source and build workflow are the next milestones.
