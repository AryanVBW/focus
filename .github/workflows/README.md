# GitHub Actions Workflows

This directory contains automated workflows for the Focus Android app.

## Available Workflows

### 1. Auto Release and APK Build (`auto-release.yml`)

**Purpose**: Automatically build and release APK files to GitHub Releases

**Triggers**:
- Push to `app-main` branch (excluding documentation changes)

**What it does**:
- ✅ Automatically bumps version (patch increment)
- ✅ Builds signed APK (or debug if keystore not configured)
- ✅ Creates GitHub Release with detailed notes
- ✅ Tags the release with version number
- ✅ Uploads APK as workflow artifact (90-day retention)

**Required Secrets**:
- `KEYSTORE_BASE64` - Base64-encoded signing keystore
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias
- `KEY_PASSWORD` - Key password

**Documentation**: See [docs/RELEASE_WORKFLOW.md](../../docs/RELEASE_WORKFLOW.md)

---

### 2. Deploy to Google Play Console (`play-store-deploy.yml`)

**Purpose**: Build and deploy app to Google Play Console

**Triggers**:
- **Manual**: Workflow dispatch with track selection
- **Automatic**: Push to `release/internal`, `release/alpha`, or `release/beta` branches
- **Production**: Push tags matching `v*.*.*` pattern

**What it does**:
- ✅ Builds signed AAB (Android App Bundle) for Play Store
- ✅ Builds signed APK for backup
- ✅ Uploads to specified release track (internal/alpha/beta/production)
- ✅ Supports staged rollouts for production
- ✅ Uploads ProGuard mapping files for crash analysis
- ✅ Caches Gradle dependencies for faster builds

**Required Secrets**:
- `KEYSTORE_BASE64` - Base64-encoded signing keystore
- `KEYSTORE_PASSWORD` - Keystore password
- `KEY_ALIAS` - Key alias
- `KEY_PASSWORD` - Key password
- `PLAY_STORE_SERVICE_ACCOUNT_JSON` - Google Play service account credentials

**Documentation**: See [docs/PLAY_STORE_DEPLOYMENT.md](../../docs/PLAY_STORE_DEPLOYMENT.md)

---

## Quick Start Guide

### First-Time Setup

1. **Configure Secrets**:
   - Go to: Repository Settings → Secrets and variables → Actions
   - Add required secrets (see [PLAY_STORE_SECRETS.md](../PLAY_STORE_SECRETS.md))

2. **Verify Workflow Files**:
   ```bash
   ls -la .github/workflows/
   # Should show: auto-release.yml, play-store-deploy.yml
   ```

3. **Test GitHub Release Workflow**:
   ```bash
   # Make a change and push to app-main
   git checkout app-main
   echo "test" >> README.md
   git add README.md
   git commit -m "test: verify workflow"
   git push origin app-main
   ```

4. **Test Play Store Deployment**:
   - Go to: Actions → Deploy to Google Play Console
   - Click: Run workflow
   - Select: internal track
   - Monitor the deployment

### Daily Usage

#### Deploy to Internal Testing
```bash
git checkout -b release/internal
git push origin release/internal
```

#### Deploy to Alpha Testing
```bash
git checkout -b release/alpha
git push origin release/alpha
```

#### Deploy to Beta Testing
```bash
git checkout -b release/beta
git push origin release/beta
```

#### Deploy to Production
```bash
# Ensure version is bumped first!
git tag -a v2.0.5 -m "Release v2.0.5"
git push origin v2.0.5
```

---

## Workflow Comparison

| Feature | Auto Release | Play Store Deploy |
|---------|--------------|-------------------|
| **Output** | APK on GitHub | AAB on Play Store |
| **Trigger** | Auto on push | Manual or branch/tag |
| **Version Bump** | Automatic | Manual (before deploy) |
| **Review Required** | No | Yes (for production) |
| **Distribution** | GitHub Releases | Google Play Console |
| **Best For** | Development builds | Production releases |

---

## Common Tasks

### Bump Version Manually
```bash
./scripts/bump_version.sh patch  # 2.0.4 → 2.0.5
./scripts/bump_version.sh minor  # 2.0.5 → 2.1.0
./scripts/bump_version.sh major  # 2.1.0 → 3.0.0
```

### Update Release Notes
```bash
# Edit release notes for Play Store
nano .github/whatsnew/whatsnew-en-US

# Commit changes
git add .github/whatsnew/
git commit -m "docs: update release notes"
git push
```

### View Workflow Runs
1. Go to: Repository → Actions tab
2. Select workflow from left sidebar
3. Click on specific run to view details

### Download Build Artifacts
1. Go to: Actions → Select workflow run
2. Scroll to: Artifacts section
3. Click: Download artifact

---

## Troubleshooting

### Workflow Not Triggering

**Check**:
- Branch name matches trigger configuration
- Changes are not in ignored paths (`.md`, `docs/`)
- Workflow file is in `.github/workflows/` directory

**Solution**:
```bash
# Verify workflow file syntax
cat .github/workflows/play-store-deploy.yml | grep "on:"

# Check current branch
git branch --show-current

# Force trigger (for manual workflows)
# Use GitHub UI: Actions → Select workflow → Run workflow
```

### Build Fails

**Common Causes**:
1. Missing or incorrect secrets
2. Version code already exists (for Play Store)
3. Keystore password mismatch
4. Gradle build errors

**Solution**:
```bash
# Test build locally first
./gradlew clean
./gradlew assembleRelease

# Check workflow logs
# Go to: Actions → Failed run → View logs

# Verify secrets are set
# Go to: Settings → Secrets and variables → Actions
```

### Deployment Fails

**Common Causes**:
1. Service account not authorized
2. Version code already uploaded
3. Invalid AAB/APK signature
4. Play Console API issues

**Solution**:
1. Verify service account has Play Console access
2. Bump version code in `app/build.gradle`
3. Check keystore configuration
4. Review Play Console error messages

---

## Best Practices

### Version Management
- ✅ Always bump version before Play Store deployment
- ✅ Use semantic versioning (MAJOR.MINOR.PATCH)
- ✅ Keep version code incrementing sequentially
- ✅ Tag production releases in Git

### Testing Strategy
```
Development → GitHub Release (auto-release.yml)
    ↓
Internal Testing → Play Store internal track
    ↓
Alpha Testing → Play Store alpha track
    ↓
Beta Testing → Play Store beta track
    ↓
Production → Play Store production track (staged rollout)
```

### Security
- ✅ Never commit secrets to repository
- ✅ Use GitHub encrypted secrets
- ✅ Rotate service account keys annually
- ✅ Backup keystore securely
- ✅ Use strong passwords

### Monitoring
- ✅ Check workflow runs regularly
- ✅ Monitor Play Console for review status
- ✅ Track crash reports after deployment
- ✅ Review user feedback
- ✅ Monitor staged rollout metrics

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Google Play Console](https://play.google.com/console)
- [Play Console API](https://developers.google.com/android-publisher)

---

## Support

For detailed documentation:
- GitHub Releases: [docs/RELEASE_WORKFLOW.md](../../docs/RELEASE_WORKFLOW.md)
- Play Store: [docs/PLAY_STORE_DEPLOYMENT.md](../../docs/PLAY_STORE_DEPLOYMENT.md)
- Secrets Setup: [PLAY_STORE_SECRETS.md](../PLAY_STORE_SECRETS.md)

For issues:
- Check workflow logs in Actions tab
- Review documentation
- Open an issue in the repository

---

**Last Updated**: 2025-10-29
**Maintained by**: Focus Development Team

## 📋 Available Workflows

### 🚀 Auto Release and APK Build (`auto-release.yml`)

**Trigger**: Push to `app-main` branch (excluding documentation files)

**What it does**:
1. Automatically bumps app version (patch increment)
2. Builds signed release APK (or debug if keystore not configured)
3. Creates GitHub release with detailed notes
4. Tags the release with semantic version
5. Uploads APK as artifact (90-day retention)

**Requirements**:
- GitHub secrets configured (see setup guide)
- Valid `app/build.gradle` with version info
- Android project structure

**Output**:
- GitHub Release with APK
- Version tag (e.g., `v2.0.1`)
- Workflow artifact
- Updated version in code

## 🔧 Configuration

### Required Secrets

Set these in **Settings → Secrets and variables → Actions**:

| Secret | Description |
|--------|-------------|
| `KEYSTORE_BASE64` | Base64-encoded Android keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias name |
| `KEY_PASSWORD` | Key password |

### Optional Configuration

Edit `auto-release.yml` to customize:
- Version bump strategy (patch/minor/major)
- Release note format
- Build variants
- Artifact retention period

## 📚 Documentation

- [Complete Workflow Documentation](../../docs/RELEASE_WORKFLOW.md)
- [Setup Guide](../../docs/SETUP_RELEASE_AUTOMATION.md)
- [Release Checklist](../RELEASE_CHECKLIST.md)

## 🔍 Monitoring

View workflow runs:
1. Go to the **Actions** tab
2. Select "Auto Release and APK Build"
3. View individual runs and logs

## 🆘 Troubleshooting

Common issues and solutions:

### Workflow doesn't trigger
- Verify branch name is `app-main`
- Check if changes are in ignored paths
- Ensure workflow file is valid YAML

### Build fails
- Check Actions logs for details
- Verify secrets are correctly set
- Test build locally: `./gradlew assembleRelease`

### Release not created
- Check if tag already exists
- Verify GitHub token permissions
- Review "Create GitHub Release" step logs

## 🔗 Resources

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Android Build Guide](https://developer.android.com/studio/build)
- [Semantic Versioning](https://semver.org/)

---

For detailed setup instructions, see [SETUP_RELEASE_AUTOMATION.md](../../docs/SETUP_RELEASE_AUTOMATION.md)
