# CI/CD Quick Reference Card

## 🚀 Quick Commands

### Local Testing Before PR

```bash
# Android App
cd Focus-app
./gradlew clean assembleDebug testDebugUnitTest lintDebug

# Website
cd Focus-site
npm ci && npm run lint && npm run build && npm run preview
```

### Create PR

```bash
git checkout -b feature/your-feature
# Make changes
git add .
git commit -m "feat: your description"
git push origin feature/your-feature
# Create PR on GitHub
```

## 📊 Workflow Jobs

| Job | Duration | Purpose |
|-----|----------|---------|
| 🤖 Android Build & Test | 5-8 min | Build APK, run tests, lint |
| 🌐 Website Build & Test | 2-3 min | Build site, run ESLint |
| 📸 Screenshot Capture | 2-3 min | Capture UI screenshots |
| 📋 Final Status | <1 min | Post summary report |

**Total: ~10-15 minutes**

## 💬 PR Comments You'll See

| Comment | Meaning | Action |
|---------|---------|--------|
| ❌ Android Build Failed | Compilation error | Fix build errors, push again |
| ⚠️ Unit Tests Failed | Test failures | Fix failing tests |
| ⚠️ ESLint Issues | Code quality issues | Run `npm run lint -- --fix` |
| 📸 Screenshots Ready | UI captured | Download from artifacts |
| 📊 Final Summary | Overall status | Review and proceed |

## 📦 Artifacts

| Name | What | Retention |
|------|------|-----------|
| focus-app-debug | APK file | 7 days |
| android-test-results | Test reports | 7 days |
| android-lint-results | Lint reports | 7 days |
| focus-site-dist | Built website | 7 days |
| ui-screenshots | UI screenshots | 30 days |

## 🔍 Troubleshooting

### Build Fails in CI

```bash
# Clear caches
cd Focus-app && ./gradlew clean --stop
rm -rf ~/.gradle/caches/

# Rebuild
./gradlew assembleDebug
```

### Tests Fail in CI

```bash
# Run tests locally
./gradlew testDebugUnitTest --info

# Check test reports
open app/build/reports/tests/testDebugUnitTest/index.html
```

### Website Build Fails

```bash
# Clean install
cd Focus-site
rm -rf node_modules package-lock.json
npm install
npm run build
```

## 📸 Screenshot Viewports

- 🖥️ Desktop Full: 1920x1080 (full page)
- 🖥️ Desktop Viewport: 1920x1080 (above fold)
- 📱 Mobile: 375x812 (iPhone)
- 📱 Tablet: 768x1024 (iPad)

## ✅ Pre-PR Checklist

- [ ] Code compiles locally
- [ ] Tests pass locally
- [ ] Lint checks pass
- [ ] No console errors
- [ ] Documentation updated
- [ ] PR template filled

## 🎯 Status Indicators

| Icon | Meaning |
|------|---------|
| ✅ | Success |
| ❌ | Failed |
| ⚠️ | Warning |
| ⏭️ | Skipped |
| 🔄 | In Progress |

## 🔗 Quick Links

- [Workflow File](.github/workflows/pr-check.yml)
- [Full Documentation](.github/workflows/README.md)
- [Workflow Diagram](.github/workflows/WORKFLOW_DIAGRAM.md)
- [Contributing Guide](.github/CONTRIBUTING_CI.md)
- [Setup Summary](.github/CI_CD_SETUP_SUMMARY.md)

## 🆘 Need Help?

1. Check workflow logs in Actions tab
2. Review error comments on PR
3. Read full documentation
4. Create an issue with workflow run link

## 📝 Workflow Triggers

**Runs on:**
- PR opened
- PR synchronized (new commits)
- PR reopened

**Target branches:**
- main
- develop

## 🔧 Environment

- Java: 17 (Temurin)
- Node.js: 18
- Gradle: 8.1.1
- npm: Latest with Node 18

## 💡 Pro Tips

1. **Test locally first** - Saves CI time
2. **Fix issues quickly** - Don't wait for review
3. **Download artifacts** - Test APK before merge
4. **Review screenshots** - Catch UI regressions
5. **Read error messages** - Usually very helpful

## 🎓 Common Scenarios

### Scenario 1: All Checks Pass ✅

```
✅ Android: Build and tests passed
✅ Website: Build passed
✅ Screenshots: Captured successfully
```

**Action:** Request review, merge when approved

### Scenario 2: Build Fails ❌

```
❌ Android: Build failed
✅ Website: Build passed
⏭️ Screenshots: Skipped
```

**Action:** Fix build errors, push changes

### Scenario 3: Tests Fail ⚠️

```
✅ Android: Build passed
⚠️ Android: Tests failed
✅ Website: Build passed
```

**Action:** Fix failing tests, push changes

## 📊 Workflow File Structure

```yaml
name: PR Check
on: pull_request
env: JAVA_VERSION, NODE_VERSION
jobs:
  - android-build-test
  - website-build-test
  - capture-screenshots (conditional)
  - final-status (always)
```

## 🔄 Re-running Checks

**Via GitHub UI:**
1. Go to PR → Checks tab
2. Click "Re-run jobs"
3. Select "Re-run failed jobs"

**Via GitHub CLI:**
```bash
gh run rerun <run-id> --failed
```

## 📈 Success Metrics

- ✅ All jobs complete
- ✅ No error comments
- ✅ Artifacts uploaded
- ✅ Screenshots captured
- ✅ Final summary shows success

---

**Last Updated:** 2025-10-07  
**Version:** 1.0  
**Status:** Production Ready

