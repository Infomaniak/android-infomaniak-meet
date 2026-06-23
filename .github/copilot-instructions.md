# Copilot Coding Agent Onboarding — android-infomaniak-meet

## Overview
Infomaniak Meet (kMeet) — a privacy-friendly Android video-conferencing app. **View-based UI only** (no Compose, no Hilt). It is a thin wrapper around the Jitsi Meet SDK distributed via a custom Maven repository. Core Legacy submodule is the only Core dependency used.

## One-Time Environment Setup
```bash
git submodule update --init --recursive   # Core submodule (Legacy module)
```

## Build
No `android.yml` CI — build validation is manual:
```bash
./gradlew assembleDebug
./gradlew build
```

## Project Layout
```
app/src/main/java/com/infomaniak/meet/
├── ui/              # Activities + Fragments (View-based, XML layouts)
└── utils/           # Helpers
Core/                # Git submodule — Legacy module only
gradle/libs.versions.toml
```

## PR Review Instructions

- Ensure strings are localized via `strings.xml` resources.
- **No Hilt, no Compose** — do not introduce either. All UI is XML + ViewBinding.
- Jitsi SDK Firebase is explicitly excluded via `exclude(group = "com.google.firebase")` — do not re-add Firebase.
- `Core:Legacy` is the only Core module in use — do not introduce new Core modules.
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
