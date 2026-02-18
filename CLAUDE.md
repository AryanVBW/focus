# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Focus is an Android app that blocks distracting content (Instagram Reels, YouTube Shorts, Snapchat Stories, adult websites) using Android's Accessibility Service. It operates in two modes: **Normal Mode** (tracks usage, enforces time limits) and **Focus Mode** (actively blocks distracting content and apps).

Package: `com.aryanvbw.focus` | Min SDK 26 | Target SDK 35 | Kotlin + Java 17

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (requires keystore.properties)
./gradlew bundleRelease          # Build AAB for Play Store
./gradlew lint                   # Run lint checks
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumentation tests
```

Release signing requires `keystore.properties` in project root with `storeFile`, `storePassword`, `keyAlias`, `keyPassword`.

## Architecture

**Single-module Gradle project** (`app/`) using MVVM with View Binding/Data Binding.

### Core Service Pipeline

The app's blocking functionality flows through a layered detection and blocking system:

1. **`FocusAccessibilityService`** — Entry point. Receives all accessibility events, routes to detection system, handles event throttling (300ms per package).
2. **`ContentDetectionCoordinator`** — Orchestrates legacy (`ContentDetector`) and enhanced (`VideoContentDetector`) detectors. Determines content type and blocking strategy.
3. **`EnhancedBlockingManager`** — Executes blocking via multiple strategies: overlay blocking, UI traversal, gesture injection, disruption activity.
4. **`ScrollBlockingHandler`** — Handles counter-scrolling to prevent users from scrolling through blocked content.
5. **`BlockingActionHandler`** — Executes the final blocking action (close player, close app, or lock screen).

### Key Packages

- **`service/`** — `FocusAccessibilityService` (core blocking), `FocusMonitorService` (foreground service for Focus Mode), `UsageAnalyticsService` (Normal Mode usage tracking)
- **`detection/`** — `VideoContentDetector`, `ContentDetectionCoordinator`, detection models
- **`blocking/`** — `EnhancedBlockingManager`, `OverlayBlockingManager`, `ScrollBlockingHandler`
- **`data/`** — Room database (`AppDatabase`, v2), DAOs, entities, `FocusRepository`
- **`util/`** — `AppSettings` (SharedPreferences wrapper, all feature flags), `ContentDetector` (legacy), `NotificationHelper`, `BlockingActionHandler`
- **`ui/`** — Activities and Fragments using Navigation Component with bottom nav (Home, Timer, Block, Progress, Settings)

### Data Layer

- **Room database** (`focus_database`, version 2) with entities: `BlockedContentEvent`, `AppUsageEvent`, `AppLimit`, `NotificationEvent`, `AppMode`
- **`AppSettings`** — Central SharedPreferences wrapper controlling all feature flags, blocking preferences, and app state. Most settings default to `true`/enabled.
- **`FocusRepository`** — Repository pattern over Room DAOs for usage analytics, app limits, notifications, and blocked content events.

### Navigation

Bottom navigation with 5 tabs: Home → Timer → Block → Progress → Settings. Navigation graph at `res/navigation/mobile_navigation.xml`. Entry flow: `SplashActivity` → `OnboardingActivity` (first launch) → `MainActivity`.

### Content Detection

Social app content is detected via accessibility node tree traversal, matching against known view IDs and content descriptions. These identifiers are hardcoded and may break when target apps update their UI. Key constants are in `FocusAccessibilityService` companion object and `AppSettings`.

## CI/CD

- **`auto-release.yml`** — Triggers on push to `app-main` branch. Auto-bumps patch version, builds APK, creates GitHub Release.
- **`play-store-deploy.yml`** — Deploys AAB to Google Play Console. Triggers on `release/*` branches or version tags. Supports internal/alpha/beta/production tracks.

## Branch Strategy

- `app-main` — Primary development branch (triggers auto-release)
- `main` — Base branch for PRs
- `release/internal`, `release/alpha`, `release/beta` — Trigger Play Store deployments
