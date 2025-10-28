# GitHub Actions Workflows

This directory contains automated workflows for the Focus Android app.

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
