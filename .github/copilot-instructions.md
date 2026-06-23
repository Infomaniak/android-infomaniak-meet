# Copilot Coding Agent Onboarding — android-infomaniak-meet

## Overview
Infomaniak Meet (kMeet) — a privacy-friendly Android video-conferencing app. **View-based UI only** (no Compose, no Hilt). It is a thin wrapper around the Jitsi Meet SDK, which is distributed via a custom Maven repository (`https://github.com/Infomaniak/jitsi-maven-repository`). Core Legacy submodule is the only Core dependency used.

## One-Time Environment Setup
```bash
git submodule update --init --recursive   # Core submodule (Legacy module)
```

## Build
There is **no `android.yml` CI workflow** in this repo. Build validation is manual:
```bash
./gradlew assembleDebug
./gradlew build
```

## Project Layout
```
app/
├── src/main/java/com/infomaniak/meet/
│   ├── ui/              # Activities + Fragments (View-based, XML layouts)
│   └── utils/           # Helpers
Core/                     # Git submodule — only Legacy module is used
gradle/libs.versions.toml
settings.gradle.kts
```

## Critical Architecture Notes
- **No Hilt**: manual dependency wiring only.
- **No Compose**: all UI is XML + ViewBinding. Do not introduce Compose.
- Jitsi SDK Firebase is explicitly excluded: `exclude(group = "com.google.firebase")` — do not re-add Firebase.
- The Jitsi SDK JAR/AAR comes from the custom Maven repo; see `build.gradle.kts` for repo URL.
- `Core:Legacy` provides auth and shared utilities; do not introduce new Core modules.
- CI only enforces semantic commit messages (`.github/workflows/semantic-commit.yml`).
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
