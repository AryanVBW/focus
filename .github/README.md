# 📚 GitHub Configuration & Workflows Documentation

Welcome to the Focus app's GitHub configuration documentation. This directory contains all workflow files, deployment guides, and automation documentation.

## 🎯 Quick Navigation

### 🚀 For Deploying the App
- **[Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)** - Quick commands and steps
- **[Play Store Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md)** - Complete setup checklist
- **[Workflows Summary](WORKFLOWS_SUMMARY.md)** - Overview of all workflows

### 🔐 For Setting Up Secrets
- **[Play Store Secrets Guide](PLAY_STORE_SECRETS.md)** - How to configure GitHub Secrets
- **[Setup Release Automation](../docs/SETUP_RELEASE_AUTOMATION.md)** - GitHub releases setup

### 📖 For Understanding Workflows
- **[Workflows README](workflows/README.md)** - Detailed workflow documentation
- **[Play Store Deployment Guide](../docs/PLAY_STORE_DEPLOYMENT.md)** - Complete deployment guide
- **[Release Workflow Guide](../docs/RELEASE_WORKFLOW.md)** - GitHub releases workflow

### 📝 For Writing Release Notes
- **[Release Notes Guide](whatsnew/README.md)** - How to write release notes
- **[Release Checklist](RELEASE_CHECKLIST.md)** - Pre-release checklist

---

## 📁 Directory Structure

```
.github/
├── workflows/                          # GitHub Actions workflow files
│   ├── auto-release.yml               # Automatic GitHub releases
│   ├── play-store-deploy.yml          # Google Play Console deployment
│   └── README.md                      # Workflows documentation
│
├── whatsnew/                          # Play Store release notes
│   ├── whatsnew-en-US                 # English release notes
│   └── README.md                      # Release notes guide
│
├── DEPLOYMENT_QUICK_REFERENCE.md      # Quick deployment commands
├── PLAY_STORE_SECRETS.md              # Secrets configuration guide
├── PLAY_STORE_SETUP_CHECKLIST.md      # Complete setup checklist
├── RELEASE_CHECKLIST.md               # Pre-release checklist
├── WORKFLOWS_SUMMARY.md               # Workflows overview
└── README.md                          # This file
```

---

## 🚀 Available Workflows

### 1. Auto Release and APK Build
**File**: `workflows/auto-release.yml`

**Purpose**: Automatically build and publish APK to GitHub Releases

**Triggers**:
- Push to `app-main` branch (excluding docs)

**Features**:
- ✅ Automatic version bumping
- ✅ Signed APK building
- ✅ GitHub Release creation
- ✅ Git tagging
- ✅ Artifact upload

**Documentation**: [Release Workflow Guide](../docs/RELEASE_WORKFLOW.md)

---

### 2. Deploy to Google Play Console
**File**: `workflows/play-store-deploy.yml`

**Purpose**: Build and deploy AAB to Google Play Console

**Triggers**:
- Manual workflow dispatch
- Push to `release/internal`, `release/alpha`, `release/beta`
- Push tags matching `v*.*.*`

**Features**:
- ✅ AAB + APK building
- ✅ Multi-track deployment
- ✅ Staged rollouts
- ✅ ProGuard mapping upload
- ✅ Build caching

**Documentation**: [Play Store Deployment Guide](../docs/PLAY_STORE_DEPLOYMENT.md)

---

## 🎓 Getting Started

### First-Time Setup

1. **Read the Setup Checklist**
   - Start with: [PLAY_STORE_SETUP_CHECKLIST.md](PLAY_STORE_SETUP_CHECKLIST.md)
   - Follow all steps carefully
   - Check off each item as you complete it

2. **Configure Secrets**
   - Guide: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)
   - Location: Repository Settings → Secrets and variables → Actions
   - Required: 5 secrets total

3. **Test Workflows**
   - Test GitHub releases first
   - Then test Play Store deployment to internal track
   - Verify everything works before production

### Daily Usage

1. **Quick Deployment**
   - Reference: [DEPLOYMENT_QUICK_REFERENCE.md](DEPLOYMENT_QUICK_REFERENCE.md)
   - Choose your deployment method
   - Follow the commands

2. **Update Release Notes**
   - Edit: `.github/whatsnew/whatsnew-en-US`
   - Guide: [whatsnew/README.md](whatsnew/README.md)
   - Keep under 500 characters

3. **Monitor Deployments**
   - GitHub: Actions tab
   - Play Console: https://play.google.com/console
   - Check crash reports and reviews

---

## 📊 Deployment Tracks

| Track | Audience | Review | Speed | Use Case |
|-------|----------|--------|-------|----------|
| **Internal** | Up to 100 testers | None | Instant | Daily builds, team testing |
| **Alpha** | Unlimited (opt-in) | None | Instant | Early adopters, feature testing |
| **Beta** | Unlimited (opt-in/list) | None | Instant | Pre-release, wider testing |
| **Production** | All users | Yes (1-7 days) | After review | Stable releases only |

---

## 🔐 Required Secrets

### For Both Workflows
- `KEYSTORE_BASE64` - Base64-encoded signing keystore
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias
- `KEY_PASSWORD` - Key password

### Additional for Play Store
- `PLAY_STORE_SERVICE_ACCOUNT_JSON` - Service account credentials

**Setup Guide**: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)

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

# 3. Commit and tag
git add app/build.gradle .github/whatsnew/
git commit -m "chore: prepare release v2.0.5"
git tag -a v2.0.5 -m "Release v2.0.5"
git push origin v2.0.5
```

### Manual Deployment
```
GitHub → Actions → Deploy to Google Play Console
→ Run workflow → Select track → Run
```

---

## 🐛 Troubleshooting

### Quick Fixes

**Workflow not triggering?**
- Check branch name matches trigger
- Verify changes aren't in ignored paths
- Ensure workflow file is valid YAML

**Build fails?**
- Test locally: `./gradlew bundleRelease`
- Check workflow logs in Actions tab
- Verify all secrets are configured

**Deployment fails?**
- Check Play Console for errors
- Verify service account permissions
- Ensure version code is unique

**Full Troubleshooting**: [PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md#troubleshooting)

---

## ✅ Best Practices

### Version Management
- ✅ Always bump version before Play Store deployment
- ✅ Use semantic versioning (MAJOR.MINOR.PATCH)
- ✅ Keep version code incrementing sequentially
- ✅ Tag production releases in Git

### Testing Strategy
```
Development → GitHub Release
    ↓
Internal Testing (1-2 days)
    ↓
Alpha Testing (3-5 days)
    ↓
Beta Testing (1-2 weeks)
    ↓
Production (staged: 10% → 25% → 50% → 100%)
```

### Security
- ✅ Never commit keystore to Git
- ✅ Never commit service account JSON
- ✅ Use GitHub Secrets for all credentials
- ✅ Backup keystore in secure location
- ✅ Rotate service account keys annually

---

## 📚 Documentation Index

### Setup & Configuration
- [Play Store Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md)
- [Play Store Secrets Guide](PLAY_STORE_SECRETS.md)
- [Setup Release Automation](../docs/SETUP_RELEASE_AUTOMATION.md)

### Usage & Deployment
- [Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)
- [Play Store Deployment Guide](../docs/PLAY_STORE_DEPLOYMENT.md)
- [Release Workflow Guide](../docs/RELEASE_WORKFLOW.md)

### Technical Documentation
- [Workflows README](workflows/README.md)
- [Workflows Summary](WORKFLOWS_SUMMARY.md)
- [Release Notes Guide](whatsnew/README.md)

### Checklists
- [Play Store Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md)
- [Release Checklist](RELEASE_CHECKLIST.md)

---

## 🎓 Learning Path

### For New Team Members
1. Read: [Workflows Summary](WORKFLOWS_SUMMARY.md)
2. Read: [Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)
3. Practice: Deploy to internal track
4. Review: [Play Store Deployment Guide](../docs/PLAY_STORE_DEPLOYMENT.md)

### For Setting Up Automation
1. Read: [Play Store Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md)
2. Follow: [Play Store Secrets Guide](PLAY_STORE_SECRETS.md)
3. Test: Deploy to internal track
4. Verify: Check Play Console

### For Production Releases
1. Review: [Release Checklist](RELEASE_CHECKLIST.md)
2. Follow: [Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md)
3. Monitor: Actions tab and Play Console
4. Track: Crash reports and reviews

---

## 🔄 Workflow Diagram

The deployment workflow follows this pattern:

```
Developer Changes
    ↓
Push to Branch
    ↓
┌─────────────────┬──────────────────┬──────────────────┐
│                 │                  │                  │
app-main      release/*         tag v*.*.*
│                 │                  │
Auto Release   Play Store       Play Store
│              (testing)        (production)
│                 │                  │
GitHub         Internal/          Production
Releases       Alpha/Beta         Release
```

---

## 📞 Support & Resources

### Documentation
- All guides in `.github/` and `docs/` directories
- Inline comments in workflow files
- Troubleshooting sections in guides

### External Resources
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Play Console Docs](https://support.google.com/googleplay/android-developer)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)

### Getting Help
1. Check relevant documentation
2. Review workflow logs
3. Test locally
4. Open GitHub issue with details

---

## 🎉 Success Checklist

After setup, you should be able to:

- [ ] Push to `app-main` and get automatic GitHub release
- [ ] Deploy to internal track manually
- [ ] Deploy to alpha/beta via branch push
- [ ] Deploy to production via tag
- [ ] View releases in Play Console
- [ ] Monitor crash reports
- [ ] Update release notes
- [ ] Perform staged rollouts

---

## 📈 Metrics to Monitor

### Workflow Health
- Workflow success rate
- Average build time
- Deployment frequency

### App Health
- Crash-free users percentage
- ANR rate
- User ratings
- Install/uninstall rates

---

**Last Updated**: 2025-10-29  
**Documentation Version**: 1.0.0  
**Maintained by**: Focus Development Team

---

## 🚀 Ready to Deploy?

Start with the [Deployment Quick Reference](DEPLOYMENT_QUICK_REFERENCE.md) for quick commands, or follow the [Play Store Setup Checklist](PLAY_STORE_SETUP_CHECKLIST.md) for first-time setup.

**Happy Deploying! 🎊**

