# 🚀 Deployment Quick Reference Card

Quick commands and steps for deploying the Focus Android app.

## 📋 Pre-Deployment Checklist

- [ ] All tests passing locally
- [ ] Version bumped in `app/build.gradle`
- [ ] Release notes updated in `.github/whatsnew/whatsnew-en-US`
- [ ] Changes committed and pushed
- [ ] All required secrets configured in GitHub

## 🎯 Deployment Commands

### Internal Testing (Fastest)
```bash
# Create and push to internal release branch
git checkout -b release/internal
git push origin release/internal

# Or use manual workflow dispatch:
# GitHub → Actions → Deploy to Google Play Console → Run workflow
# Select: internal track
```

**Timeline**: Instant deployment, no review required  
**Audience**: Up to 100 internal testers  
**Use for**: Daily builds, quick testing

---

### Alpha Testing
```bash
# Create and push to alpha release branch
git checkout -b release/alpha
git push origin release/alpha

# Or manual dispatch with track: alpha
```

**Timeline**: Instant deployment, no review required  
**Audience**: Unlimited testers via opt-in link  
**Use for**: Weekly builds, feature testing

---

### Beta Testing
```bash
# Create and push to beta release branch
git checkout -b release/beta
git push origin release/beta

# Or manual dispatch with track: beta
```

**Timeline**: Instant deployment, no review required  
**Audience**: Unlimited testers via opt-in or email list  
**Use for**: Release candidates, pre-production testing

---

### Production Release
```bash
# Ensure version is bumped first!
# Edit app/build.gradle: versionCode and versionName

# Create and push version tag
git tag -a v2.0.5 -m "Release version 2.0.5"
git push origin v2.0.5

# Or manual dispatch with track: production
```

**Timeline**: 1-7 days for Google review  
**Audience**: All users  
**Use for**: Stable releases only

---

### Staged Production Rollout
```bash
# Use manual workflow dispatch:
# GitHub → Actions → Deploy to Google Play Console → Run workflow
# Track: production
# Rollout: 10 (for 10%)

# Increase rollout gradually:
# 10% → 25% → 50% → 100%
```

**Best Practice**: Start with 10%, monitor for 24-48 hours, then increase

---

## 🔧 Version Management

### Bump Version
```bash
# Patch version (2.0.4 → 2.0.5)
./scripts/bump_version.sh patch

# Minor version (2.0.5 → 2.1.0)
./scripts/bump_version.sh minor

# Major version (2.1.0 → 3.0.0)
./scripts/bump_version.sh major

# Commit and push
git add app/build.gradle
git commit -m "chore: bump version to 2.0.5"
git push
```

### Manual Version Edit
```gradle
// app/build.gradle
defaultConfig {
    versionCode 7        // Increment by 1
    versionName "2.0.5"  // Semantic version
}
```

---

## 📝 Update Release Notes

```bash
# Edit release notes
nano .github/whatsnew/whatsnew-en-US

# Example content:
# • Fixed crash on Android 14
# • Improved blocking accuracy
# • Performance optimizations
# • Bug fixes and stability improvements

# Commit changes
git add .github/whatsnew/
git commit -m "docs: update release notes for v2.0.5"
git push
```

---

## 🔍 Monitor Deployment

### Check Workflow Status
```
1. Go to: GitHub → Actions tab
2. Select: "Deploy to Google Play Console"
3. Click: Latest workflow run
4. Monitor: Progress and logs
```

### Check Play Console
```
1. Go to: https://play.google.com/console
2. Navigate to: Focus app
3. Check: Release → Production/Testing
4. Monitor: Review status, rollout percentage
```

### View Crash Reports
```
1. Play Console → Focus app
2. Navigate to: Quality → Android vitals
3. Check: Crashes & ANRs
4. Monitor: Crash-free users percentage
```

---

## 🐛 Quick Troubleshooting

### Build Fails
```bash
# Test locally first
./gradlew clean
./gradlew bundleRelease

# Check logs
# GitHub → Actions → Failed run → View logs
```

### Version Already Exists
```bash
# Bump version code
./scripts/bump_version.sh patch
git add app/build.gradle
git commit -m "chore: bump version"
git push

# Retry deployment
```

### Service Account Error
```
1. Go to: Play Console → Settings → API access
2. Find: Service account
3. Verify: Permissions include "Manage releases"
4. Wait: 5-10 minutes for propagation
5. Retry: Deployment
```

### Keystore Error
```bash
# Verify keystore locally
keytool -list -v -keystore keystore/focus-release-key.jks

# Re-encode and update secret
base64 -i keystore/focus-release-key.jks > keystore_base64.txt
# Copy content to GitHub secret: KEYSTORE_BASE64
```

---

## 📊 Deployment Timeline

| Track | Review Time | Rollout Time | Total Time |
|-------|-------------|--------------|------------|
| Internal | None | Instant | ~5 minutes |
| Alpha | None | Instant | ~5 minutes |
| Beta | None | Instant | ~5 minutes |
| Production | 1-7 days | Instant | 1-7 days |
| Production (Staged) | 1-7 days | 3-7 days | 4-14 days |

---

## 🎯 Recommended Workflow

### For Regular Updates
```
1. Develop feature on feature branch
2. Merge to main/app-main
3. Deploy to internal → Test 1-2 days
4. Deploy to alpha → Test 3-5 days
5. Deploy to beta → Test 1-2 weeks
6. Deploy to production (staged):
   - 10% rollout → Monitor 24-48 hours
   - 25% rollout → Monitor 24-48 hours
   - 50% rollout → Monitor 24-48 hours
   - 100% rollout → Full release
```

### For Hotfixes
```
1. Create hotfix branch
2. Fix critical issue
3. Test locally
4. Deploy to internal → Quick test
5. Deploy to production (100% rollout)
6. Monitor closely for 24 hours
```

### For Major Releases
```
1. Complete all features
2. Deploy to internal → Team testing
3. Deploy to alpha → Early adopters (1 week)
4. Deploy to beta → Wider testing (2-3 weeks)
5. Deploy to production (staged):
   - 10% → 3 days
   - 25% → 3 days
   - 50% → 3 days
   - 100% → Full release
```

---

## 🔐 Required Secrets

Quick checklist of GitHub Secrets needed:

- [ ] `KEYSTORE_BASE64` - Base64-encoded keystore
- [ ] `KEYSTORE_PASSWORD` - Keystore password
- [ ] `KEY_ALIAS` - Key alias (e.g., "focus-key")
- [ ] `KEY_PASSWORD` - Key password
- [ ] `PLAY_STORE_SERVICE_ACCOUNT_JSON` - Service account JSON

**Setup Guide**: See [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)

---

## 📞 Emergency Contacts

### Halt Production Rollout
```
1. Go to: Play Console → Release → Production
2. Click: Halt rollout
3. Investigate: Issue causing problems
4. Fix: Create hotfix
5. Resume: After fix is deployed
```

### Rollback Release
```
Note: Google Play doesn't support rollback
Alternative:
1. Fix the issue
2. Bump version
3. Deploy new version immediately
4. Use 100% rollout for critical fixes
```

---

## 📚 Documentation Links

- **Full Play Store Guide**: [docs/PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md)
- **Secrets Setup**: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)
- **Workflow Details**: [workflows/play-store-deploy.yml](workflows/play-store-deploy.yml)
- **Release Workflow**: [docs/RELEASE_WORKFLOW.md](../docs/RELEASE_WORKFLOW.md)

---

## 💡 Pro Tips

1. **Always test on internal first** - Catch issues before wider release
2. **Use staged rollouts** - Minimize impact of critical bugs
3. **Monitor crash rates** - Halt rollout if crash rate increases
4. **Keep mapping files** - Essential for debugging production crashes
5. **Update release notes** - Users appreciate knowing what changed
6. **Backup keystore** - Store in multiple secure locations
7. **Test locally first** - Run `./gradlew bundleRelease` before pushing
8. **Version consistently** - Follow semantic versioning
9. **Tag releases** - Makes it easy to track what's in production
10. **Document changes** - Maintain a changelog for reference

---

**Last Updated**: 2025-10-29  
**Quick Reference Version**: 1.0.0

**Print this page and keep it handy for quick deployments!**

