# 🚀 Google Play Store Deployment - Quick Start Guide

## Prerequisites Checklist

Before deploying, ensure:

- [ ] App listing created in Google Play Console for `com.aryanvbw.focus`
- [ ] Service account has "Release Manager" or "Admin" permissions
- [ ] Internal testing track is enabled in Play Console
- [ ] All GitHub secrets are configured (run `gh secret list` to verify)
- [ ] Local build test passes (run `./test-play-store-deployment.sh`)

---

## Method 1: Manual Deployment (Recommended for Testing)

### Deploy to Internal Track

```bash
gh workflow run play-store-deploy.yml \
  --ref release/internal \
  -f track=internal \
  -f rollout_percentage=100
```

### Deploy to Beta Track

```bash
gh workflow run play-store-deploy.yml \
  --ref release/beta \
  -f track=beta \
  -f rollout_percentage=100
```

### Deploy to Production (with staged rollout)

```bash
gh workflow run play-store-deploy.yml \
  --ref main \
  -f track=production \
  -f rollout_percentage=20
```

---

## Method 2: Automatic Branch-Based Deployment

### Internal Track
```bash
git checkout release/internal
git merge main  # or your feature branch
git push origin release/internal
```

### Beta Track
```bash
git checkout release/beta
git merge release/internal
git push origin release/beta
```

---

## Method 3: Tag-Based Production Deployment

```bash
# Ensure version is bumped in app/build.gradle
git tag -a v2.0.7 -m "Release version 2.0.7"
git push origin v2.0.7
```

---

## Monitoring Deployments

### List Recent Runs
```bash
gh run list --workflow=play-store-deploy.yml --limit 5
```

### Watch Latest Run
```bash
gh run watch
```

### View Run Details
```bash
gh run view <RUN_ID>
```

### View Run Logs
```bash
gh run view <RUN_ID> --log
```

---

## Testing the Build Locally

### Run Full Test Suite
```bash
./test-play-store-deployment.sh
```

### Build AAB Only
```bash
./gradlew clean bundleRelease
```

### Build APK Only
```bash
./gradlew clean assembleRelease
```

### Verify Signing
```bash
# For AAB
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab

# For APK
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

---

## Troubleshooting

### Build Fails Locally

1. **Clean and rebuild:**
   ```bash
   ./gradlew clean
   ./gradlew bundleRelease --stacktrace
   ```

2. **Check keystore:**
   ```bash
   keytool -list -v -keystore keystore/focus-release-key.jks
   ```

3. **Verify keystore.properties:**
   ```bash
   cat keystore.properties
   ```

### Workflow Fails

1. **Check workflow syntax:**
   ```bash
   gh workflow view play-store-deploy.yml
   ```

2. **View failure logs:**
   ```bash
   gh run list --workflow=play-store-deploy.yml --limit 1
   gh run view <RUN_ID> --log-failed
   ```

3. **Verify secrets:**
   ```bash
   gh secret list
   ```

### Deployment Fails

1. **Service Account Issues:**
   - Go to Play Console → Settings → API access
   - Verify service account has proper permissions
   - Re-grant access if needed

2. **Version Code Conflict:**
   - Increment `versionCode` in `app/build.gradle`
   - Commit and push changes

3. **App Not Found:**
   - Ensure app listing exists in Play Console
   - Verify package name matches: `com.aryanvbw.focus`
   - May need to manually upload first AAB

---

## Version Management

### Bump Version

```bash
# Patch version (2.0.7 → 2.0.8)
./scripts/bump_version.sh patch

# Minor version (2.0.8 → 2.1.0)
./scripts/bump_version.sh minor

# Major version (2.1.0 → 3.0.0)
./scripts/bump_version.sh major
```

### Manual Version Update

Edit `app/build.gradle`:
```gradle
versionCode 10      // Must increment for each release
versionName "2.0.8" // Semantic versioning
```

---

## Release Notes

Edit `.github/whatsnew/whatsnew-en-US`:

```
🎯 Focus v2.0.8 - New Features

• Feature 1 description
• Feature 2 description
• Bug fixes and improvements

Maximum 500 characters for Play Store.
```

---

## Common Commands Reference

```bash
# Test everything locally
./test-play-store-deployment.sh

# Deploy to internal track
gh workflow run play-store-deploy.yml --ref release/internal -f track=internal

# Check deployment status
gh run list --workflow=play-store-deploy.yml

# View latest run
gh run view

# Open Play Console
open https://play.google.com/console

# Check secrets
gh secret list

# Build locally
./gradlew clean bundleRelease assembleRelease
```

---

## Release Tracks Explained

| Track | Purpose | Audience | Rollout |
|-------|---------|----------|---------|
| **Internal** | Quick testing | Team members only | Immediate |
| **Alpha** | Early testing | Closed testers | Immediate |
| **Beta** | Pre-release | Open/closed testers | Immediate |
| **Production** | Public release | All users | Staged (optional) |

---

## Best Practices

1. **Always test locally first:**
   ```bash
   ./test-play-store-deployment.sh
   ```

2. **Use internal track for testing:**
   - Deploy to internal first
   - Verify in Play Console
   - Then promote to beta/production

3. **Increment version codes:**
   - Never reuse a version code
   - Use semantic versioning for version names

4. **Review release notes:**
   - Keep under 500 characters
   - Highlight key changes
   - Use clear, user-friendly language

5. **Monitor deployments:**
   - Watch workflow runs
   - Check Play Console for errors
   - Review crash reports after release

---

## Emergency Rollback

If you need to rollback a production release:

1. **In Play Console:**
   - Go to Production track
   - Click "Manage" on the problematic release
   - Select "Halt rollout" or "Roll back"

2. **Deploy previous version:**
   ```bash
   git checkout v2.0.6  # Previous stable version
   gh workflow run play-store-deploy.yml --ref v2.0.6 -f track=production
   ```

---

## Support

- **Documentation:** See `docs/PLAY_STORE_DEPLOYMENT.md`
- **Testing Report:** See `PLAY_STORE_TESTING_REPORT.md`
- **Workflow Details:** `.github/workflows/play-store-deploy.yml`
- **Secrets Setup:** `.github/PLAY_STORE_SECRETS.md`

---

**Last Updated:** October 29, 2025  
**Version:** 1.0  
**Tested With:** Focus Android v2.0.7 (Build 9)

