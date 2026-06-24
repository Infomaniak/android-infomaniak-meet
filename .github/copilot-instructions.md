# Copilot Coding Agent Onboarding — android-infomaniak-meet

## Overview
Infomaniak Meet (kMeet) — a privacy-friendly Android video-conferencing app. **View-based UI only** (no Compose, no Hilt). It is a thin wrapper around the Jitsi Meet SDK distributed via a custom Maven repository. Core Legacy submodule is the only Core dependency used.

## One-Time Environment Setup
```bash
git submodule update --init --recursive   # Core submodule (Legacy module) — requires GitHub SSH access (submodule URL is git@github.com:...; rewrite to HTTPS or configure SSH credentials if needed)
```

## Build
No GitHub Actions build workflow is configured in this repo (there is no `android.yml`). For a manual build check:
```bash
./gradlew assembleDebug
./gradlew build
```

## Test / Lint
No repository CI tasks are currently configured for tests or linting.

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
