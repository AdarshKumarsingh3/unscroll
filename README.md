# 📱 Unscroll — Native Android App (Kotlin & Jetpack Compose)

This is the production-ready **Native Android Mobile Application** for the **Anti-Doomscroll Platform**, built strictly following the 90-Day Build Plan & Business Proposal.

---

## 🏗️ Architecture & Android Tech Stack

- **Language**: Kotlin 2.1.0
- **UI Framework**: Modern Jetpack Compose & Material 3 (Dark calm mindfulness theme)
- **Navigation**: Jetpack Navigation Compose
- **Notifications**: Android `NotificationManager` with 3 dedicated channels:
  1. `channel_mindfulness`: Gentle morning and evening reflection nudges.
  2. `channel_focus`: Ongoing sticky notification in notification shade during active 25m Pomodoro sessions.
  3. `channel_streak`: Celebrations when reclaiming hours from feeds.
- **Feed Interception**: Native Android `AccessibilityService` (`DoomscrollInterceptorService`) that intercepts Instagram Reels, TikTok, YouTube Shorts, X/Twitter, and Reddit with a 5-second mindfulness pause.
- **Haptic Tactile Engine**: Real phone vibration feedback via Android `Vibrator` / `VibrationEffect` during the 90-second Urge Surfer tap pacer.
- **Audio Synthesis**: Pure Kotlin `AudioTrack` procedural synthesizer for 432Hz/528Hz harmonic singing bowl chimes and rain soundscapes (zero external audio file dependencies).
- **Quick Settings Tile**: `QuickFocusTileService` allowing users to toggle Unscroll focus mode directly from Android's top status bar shade.

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio**.
2. Select **Open** (or File $\rightarrow$ Open).
3. Choose the folder: `c:\Users\adars\Downloads\New folder\android-app`
4. Android Studio will automatically sync the Gradle files using the provided Version Catalog (`gradle/libs.versions.toml`).
5. Connect your Android phone via USB (or start an Android Emulator) and click the green **Run (▶)** button!

---

## 📋 Features Implemented (Matching the Proposal PDF)

| Feature | Android Implementation | Impact |
| :--- | :--- | :--- |
| **1. Screen-Time Cost Calculator** | `CalculatorScreen.kt` with live interactive sliders and native Android `Intent.ACTION_SEND` share chooser. | Viral top-of-funnel acquisition. |
| **2. Reality-Check Interceptor** | `DoomscrollInterceptorService.kt` + `InterceptorScreen.kt` with Accessibility service integration and 5s pause simulation. | Breaks unconscious automatic app opening loops. |
| **3. 90-Second Urge Surfer** | `UrgeSurferScreen.kt` with animated breathing scale, tactile phone vibration, singing bowl chimes, and craving rating gauge. | Clinically dissolves craving peaks within 90s. |
| **4. Micro-Replacement Bites** | `ReplacementsScreen.kt` covering all 6 triggers (Bored, Stressed, Restless, Lonely, Productive, Learn) with mini-games, trivia, squat counter, and SMS launcher. | Dopamine substitution without endless scrolling. |
| **5. Live Focus Rooms** | `FocusRoomsScreen.kt` with 25/5 Pomodoro timer, background ongoing notification, and live peer presence. | Social accountability and focus community. |
| **6. Saved-Hours Wall** | `DashboardScreen.kt` tracking hours reclaimed, active flame streak, and dopamine rewiring milestones. | Habit loop retention. |
| **7. Founder & Investor Deck** | `FounderDeckScreen.kt` with the 90-day phase gates, Year 1 revenue scenarios, and action items. | Executive pitch ready. |

---

## 🔒 Permissions Used
- `POST_NOTIFICATIONS`: Android 13+ runtime notification display.
- `VIBRATE`: Physical tactile feedback for Urge Surfer.
- `BIND_ACCESSIBILITY_SERVICE`: Automatic detection of social feed opens.
- `SYSTEM_ALERT_WINDOW`: Displaying the mindfulness pause screen over distracting apps.
