# 🚀 GitHub Actions Workflows - Complete Summary

This document provides an overview of all automated workflows for the Focus Android app.

## 📦 Available Workflows

### 1. Auto Release and APK Build
**File**: `.github/workflows/auto-release.yml`  
**Purpose**: Automatically build and publish APK to GitHub Releases  
**Status**: ✅ Active

### 2. Deploy to Google Play Console
**File**: `.github/workflows/play-store-deploy.yml`  
**Purpose**: Build and deploy AAB to Google Play Console  
**Status**: ✅ Active (requires setup)

---

## 🎯 Workflow Comparison

| Feature | Auto Release | Play Store Deploy |
|---------|--------------|-------------------|
| **Trigger** | Auto on push to `app-main` | Manual, branch, or tag |
| **Output** | APK file | AAB + APK |
| **Destination** | GitHub Releases | Google Play Console |
| **Version Bump** | Automatic | Manual (before deploy) |
| **Signing** | Release or Debug | Release only |
| **Review** | None | Yes (production only) |
| **Caching** | Gradle cache | Gradle + dependencies |
| **Artifacts** | 90 days | 90 days (AAB/APK) + 365 days (mapping) |
| **Best For** | Development builds | Production releases |

---

## 📋 Quick Start

### First-Time Setup

1. **Configure Secrets** (for both workflows):
   ```
   Settings → Secrets and variables → Actions → New repository secret
   ```
   
   Required for both workflows:
   - `KEYSTORE_BASE64`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
   
   Additional for Play Store:
   - `PLAY_STORE_SERVICE_ACCOUNT_JSON`

2. **Verify Workflow Files**:
   ```bash
   ls -la .github/workflows/
   # Should show: auto-release.yml, play-store-deploy.yml
   ```

3. **Test Auto Release**:
   ```bash
   git checkout app-main
   echo "test" >> README.md
   git add README.md
   git commit -m "test: verify auto release"
   git push origin app-main
   # Check Actions tab for workflow run
   ```

4. **Test Play Store Deploy**:
   ```
   GitHub → Actions → Deploy to Google Play Console
   → Run workflow → Select: internal track
   ```

---

## 🔄 Deployment Workflows

### Development Cycle
```
Feature Branch
    ↓
Merge to app-main
    ↓
Auto Release Workflow (automatic)
    ↓
APK on GitHub Releases
```

### Production Cycle
```
Version Bump
    ↓
Update Release Notes
    ↓
Push to release/internal
    ↓
Play Store Deploy (internal track)
    ↓
Test with team
    ↓
Push to release/alpha
    ↓
Play Store Deploy (alpha track)
    ↓
Test with early adopters
    ↓
Push to release/beta
    ↓
Play Store Deploy (beta track)
    ↓
Test with larger audience
    ↓
Create version tag (v2.0.5)
    ↓
Play Store Deploy (production track)
    ↓
Staged rollout: 10% → 25% → 50% → 100%
```

---

## 📚 Documentation Index

### Setup Guides
- **[Play Store Setup Checklist](.github/PLAY_STORE_SETUP_CHECKLIST.md)** - Complete setup checklist
- **[Play Store Secrets](PLAY_STORE_SECRETS.md)** - Secrets configuration guide
- **[Setup Release Automation](../docs/SETUP_RELEASE_AUTOMATION.md)** - GitHub releases setup

### Usage Guides
- **[Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)** - Quick command reference
- **[Play Store Deployment](../docs/PLAY_STORE_DEPLOYMENT.md)** - Complete deployment guide
- **[Release Workflow](../docs/RELEASE_WORKFLOW.md)** - GitHub releases workflow

### Technical Documentation
- **[Workflows README](workflows/README.md)** - Detailed workflow documentation
- **[Release Notes Guide](whatsnew/README.md)** - How to write release notes

---

## 🎯 Common Tasks

### Deploy to Internal Testing
```bash
git checkout -b release/internal
git push origin release/internal
```

### Deploy to Production
```bash
# 1. Bump version
./scripts/bump_version.sh patch

# 2. Update release notes
nano .github/whatsnew/whatsnew-en-US

# 3. Commit changes
git add app/build.gradle .github/whatsnew/
git commit -m "chore: prepare release v2.0.5"
git push

# 4. Create and push tag
git tag -a v2.0.5 -m "Release v2.0.5"
git push origin v2.0.5
```

### Manual Deployment
```
1. Go to: Actions → Deploy to Google Play Console
2. Click: Run workflow
3. Select: Branch and track
4. Set: Rollout percentage (if production)
5. Click: Run workflow
6. Monitor: Workflow progress
```

---

## 🔐 Required Secrets

### For Auto Release Workflow
- `KEYSTORE_BASE64` (optional - falls back to debug build)
- `KEYSTORE_PASSWORD` (optional)
- `KEY_ALIAS` (optional)
- `KEY_PASSWORD` (optional)

### For Play Store Deploy Workflow
- `KEYSTORE_BASE64` (required)
- `KEYSTORE_PASSWORD` (required)
- `KEY_ALIAS` (required)
- `KEY_PASSWORD` (required)
- `PLAY_STORE_SERVICE_ACCOUNT_JSON` (required)

**Setup Guide**: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)

---

## 🐛 Troubleshooting

### Workflow Not Triggering
- Verify branch name matches trigger
- Check paths-ignore filters
- Ensure workflow file is valid YAML

### Build Fails
- Test locally: `./gradlew clean bundleRelease`
- Check workflow logs in Actions tab
- Verify all secrets are configured

### Deployment Fails
- Check Play Console for errors
- Verify service account permissions
- Ensure version code is unique
- Check AAB signature

**Full Troubleshooting**: [PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md#troubleshooting)

---

## ✅ Best Practices

### Version Management
- ✅ Always bump version before Play Store deployment
- ✅ Use semantic versioning (MAJOR.MINOR.PATCH)
- ✅ Keep version code incrementing
- ✅ Tag production releases

### Testing Strategy
- ✅ Test on internal track first
- ✅ Use alpha for early adopters
- ✅ Use beta for wider testing
- ✅ Use staged rollouts for production

### Security
- ✅ Never commit keystore to Git
- ✅ Never commit service account JSON
- ✅ Use GitHub Secrets for all credentials
- ✅ Backup keystore securely
- ✅ Rotate service account keys annually

### Monitoring
- ✅ Check workflow runs regularly
- ✅ Monitor Play Console for review status
- ✅ Track crash reports
- ✅ Review user feedback
- ✅ Monitor rollout metrics

---

## 📊 Workflow Features

### Auto Release Workflow
- ✅ Automatic version bumping
- ✅ Signed or debug APK building
- ✅ GitHub Release creation
- ✅ Git tagging
- ✅ Detailed release notes
- ✅ Artifact upload (90 days)
- ✅ Gradle caching

### Play Store Deploy Workflow
- ✅ Multi-track deployment
- ✅ AAB + APK building
- ✅ Staged rollouts
- ✅ ProGuard mapping upload
- ✅ Release notes upload
- ✅ Service account authentication
- ✅ Build caching
- ✅ Artifact upload (90 days AAB/APK, 365 days mapping)
- ✅ Automatic cleanup of sensitive files

---

## 🎓 Learning Resources

### GitHub Actions
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)
- [Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

### Android Development
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Android App Bundle](https://developer.android.com/guide/app-bundle)
- [ProGuard/R8](https://developer.android.com/studio/build/shrink-code)

### Google Play Console
- [Play Console Documentation](https://support.google.com/googleplay/android-developer)
- [Play Console API](https://developers.google.com/android-publisher)
- [Release Tracks](https://support.google.com/googleplay/android-developer/answer/9845334)

---

## 📞 Support

### Documentation
- Check relevant documentation in `docs/` and `.github/` directories
- Review workflow files for inline comments
- Consult troubleshooting sections

### Issues
- Check workflow logs in Actions tab
- Review Play Console error messages
- Test builds locally
- Open GitHub issue with details

### Emergency
- Halt production rollout in Play Console
- Revert to previous version if needed
- Deploy hotfix immediately
- Monitor crash reports

---

## 🎉 Success Metrics

Track these metrics to measure deployment success:

### Workflow Metrics
- ✅ Workflow success rate
- ✅ Average build time
- ✅ Artifact size trends
- ✅ Deployment frequency

### App Metrics
- ✅ Crash-free users percentage
- ✅ ANR (App Not Responding) rate
- ✅ User ratings and reviews
- ✅ Install/uninstall rates
- ✅ Rollout completion time

---

## 🔄 Maintenance

### Regular Tasks
- [ ] Review workflow runs weekly
- [ ] Update dependencies monthly
- [ ] Rotate service account keys annually
- [ ] Backup keystore regularly
- [ ] Review and update documentation

### Workflow Updates
- [ ] Monitor GitHub Actions updates
- [ ] Update action versions when available
- [ ] Test workflow changes on feature branch
- [ ] Document any workflow modifications

---

## 📈 Future Enhancements

Potential improvements to consider:

- [ ] Add automated testing before deployment
- [ ] Implement changelog generation
- [ ] Add Slack/Discord notifications
- [ ] Create deployment dashboard
- [ ] Add performance benchmarking
- [ ] Implement automatic rollback on high crash rate
- [ ] Add multi-language release notes
- [ ] Create deployment analytics

---

**Last Updated**: 2025-10-29  
**Document Version**: 1.0.0  
**Maintained by**: Focus Development Team

---

## Quick Links

- 📖 [Complete Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md)
- 🔐 [Secrets Configuration](PLAY_STORE_SECRETS.md)
- 🚀 [Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)
- 📱 [Play Store Guide](../docs/PLAY_STORE_DEPLOYMENT.md)
- 🔄 [Release Workflow](../docs/RELEASE_WORKFLOW.md)
- 📝 [Release Notes](whatsnew/README.md)

