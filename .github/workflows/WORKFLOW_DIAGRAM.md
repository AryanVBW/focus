# PR Check Workflow Diagram

## 🔄 Workflow Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    PR Opened/Updated/Reopened                    │
│                    (on main or develop branch)                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
        ┌────────────────────────────────────────────┐
        │         Parallel Execution Starts          │
        └────────────┬───────────────────┬───────────┘
                     │                   │
         ┌───────────▼──────────┐   ┌───▼────────────────────┐
         │  Job 1: Android App  │   │  Job 2: Website Build  │
         │   Build & Test       │   │      & Test            │
         └───────────┬──────────┘   └───┬────────────────────┘
                     │                   │
    ┌────────────────┼───────────────────┼────────────────┐
    │                │                   │                │
    ▼                ▼                   ▼                ▼
┌────────┐      ┌─────────┐        ┌────────┐      ┌─────────┐
│ Setup  │      │  Build  │        │ Setup  │      │  Build  │
│ JDK 17 │──────▶  Debug  │        │Node 18 │──────▶ Website │
│Gradle  │      │   APK   │        │  npm   │      │  Vite  │
└────────┘      └────┬────┘        └────────┘      └────┬────┘
                     │                                   │
                     ▼                                   ▼
                ┌─────────┐                         ┌─────────┐
                │   Run   │                         │   Run   │
                │  Unit   │                         │ ESLint  │
                │  Tests  │                         │  Check  │
                └────┬────┘                         └────┬────┘
                     │                                   │
                     ▼                                   ▼
                ┌─────────┐                         ┌─────────┐
                │   Run   │                         │ Upload  │
                │  Lint   │                         │  Build  │
                │  Check  │                         │Artifact │
                └────┬────┘                         └────┬────┘
                     │                                   │
                     ▼                                   │
                ┌─────────┐                             │
                │ Upload  │                             │
                │  APK &  │                             │
                │ Reports │                             │
                └────┬────┘                             │
                     │                                   │
                     └───────────┬───────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Both Jobs Complete    │
                    │   (success or failure)  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Job 3: Capture         │
                    │  UI Screenshots         │
                    │  (only if website OK)   │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Download Website Build │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Install Playwright     │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Start Preview Server   │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Capture Screenshots:   │
                    │  • Desktop (Full)       │
                    │  • Desktop (Viewport)   │
                    │  • Mobile (375x812)     │
                    │  • Tablet (768x1024)    │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Upload Screenshots     │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Post Screenshot        │
                    │  Comment on PR          │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Job 4: Final Status    │
                    │  Report (always runs)   │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Aggregate All Results  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Post Comprehensive     │
                    │  Status Report on PR    │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Set Final Status       │
                    │  ✅ Pass or ❌ Fail     │
                    └─────────────────────────┘
```

## 📊 Job Dependencies

```
android-build-test ──┐
                     ├──▶ capture-screenshots ──┐
website-build-test ──┘                          ├──▶ final-status
                                                 │
                     (always runs) ─────────────┘
```

## 💬 PR Comments Timeline

```
Time ──────────────────────────────────────────────────────────▶

  │
  ├─ [If Android Build Fails]
  │  └─ ❌ Android Build Failed Comment
  │
  ├─ [If Android Tests Fail]
  │  └─ ⚠️ Android Unit Tests Failed Comment
  │
  ├─ [If Website Build Fails]
  │  └─ ❌ Website Build Failed Comment
  │
  ├─ [If ESLint Issues]
  │  └─ ⚠️ ESLint Issues Found Comment
  │
  ├─ [If Screenshots Captured]
  │  └─ 📸 UI Screenshots Captured Comment
  │
  └─ [Always]
     └─ 📊 PR Check Summary Comment (Final Status)
```

## 🎯 Decision Points

```
┌─────────────────────────────────────────────────────────────┐
│                    Android Build Result                      │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
┌─────────┐      ┌─────────┐
│ Success │      │ Failure │
└────┬────┘      └────┬────┘
     │                │
     │                └──▶ Post Error Comment
     │                     Upload Logs
     │                     Mark Job as Failed
     │
     └──▶ Run Tests
          │
    ┌─────┴─────┐
    │           │
    ▼           ▼
┌─────────┐ ┌─────────┐
│  Pass   │ │  Fail   │
└────┬────┘ └────┬────┘
     │           │
     │           └──▶ Post Warning Comment
     │                Upload Test Reports
     │                Continue (don't fail job)
     │
     └──▶ Upload APK
          Mark as Success
```

```
┌─────────────────────────────────────────────────────────────┐
│                    Website Build Result                      │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
┌─────────┐      ┌─────────┐
│ Success │      │ Failure │
└────┬────┘      └────┬────┘
     │                │
     │                └──▶ Post Error Comment
     │                     Skip Screenshots
     │                     Mark Job as Failed
     │
     └──▶ Upload Build Artifacts
          Enable Screenshot Job
          Mark as Success
```

```
┌─────────────────────────────────────────────────────────────┐
│                  Screenshot Capture Logic                    │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────▼────────┐
    │ Website Build   │
    │   Successful?   │
    └────────┬────────┘
             │
    ┌────────┴────────┐
    │                 │
    ▼                 ▼
┌─────────┐      ┌─────────┐
│   Yes   │      │   No    │
└────┬────┘      └────┬────┘
     │                │
     │                └──▶ Skip Screenshot Job
     │
     └──▶ Download Build
          Start Server
          Capture Screenshots
          Upload & Comment
```

## 📦 Artifact Flow

```
┌──────────────────┐
│  Android Build   │
└────────┬─────────┘
         │
         ├──▶ focus-app-debug.apk (7 days)
         ├──▶ android-test-results/ (7 days)
         └──▶ android-lint-results.html (7 days)

┌──────────────────┐
│  Website Build   │
└────────┬─────────┘
         │
         └──▶ focus-site-dist/ (7 days)
              │
              └──▶ [Used by Screenshot Job]

┌──────────────────┐
│   Screenshots    │
└────────┬─────────┘
         │
         └──▶ ui-screenshots/ (30 days)
              ├──▶ homepage-full.png
              ├──▶ homepage-viewport.png
              ├──▶ homepage-mobile.png
              └──▶ homepage-tablet.png
```

## 🔄 Error Handling Flow

```
┌─────────────────────────────────────────────────────────────┐
│                      Error Detected                          │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────▼────────┐
    │  Capture Error  │
    │   Information   │
    └────────┬────────┘
             │
    ┌────────▼────────┐
    │  Upload Logs &  │
    │  Error Reports  │
    └────────┬────────┘
             │
    ┌────────▼────────┐
    │  Post Detailed  │
    │  Error Comment  │
    │  on PR          │
    └────────┬────────┘
             │
    ┌────────▼────────┐
    │  Continue with  │
    │  Other Jobs     │
    │  (if possible)  │
    └────────┬────────┘
             │
    ┌────────▼────────┐
    │  Final Status   │
    │  Shows Failure  │
    └─────────────────┘
```

---

*This diagram represents the complete flow of the PR Check workflow.*

