# 🚀 Automated Release Workflow Documentation

This document explains the automated release workflow for the Focus Android app.

## 📋 Overview

The Focus app uses GitHub Actions to automatically:
1. **Bump the version** with every commit to `app-main` branch
2. **Build a signed APK** (or debug APK if keystore is not configured)
3. **Create a GitHub release** with detailed release notes
4. **Tag the release** with semantic versioning

## 🔄 How It Works

### Trigger
The workflow is triggered automatically when:
- Code is pushed to the `app-main` branch
- Changes are NOT in documentation files (`.md`, `docs/`, etc.)

### Version Bumping Strategy
- **Version Code**: Incremented by 1 with each build
- **Version Name**: Follows semantic versioning (MAJOR.MINOR.PATCH)
  - **PATCH** version is automatically incremented with each commit
  - **MINOR** and **MAJOR** versions can be manually bumped using the script

### Workflow Steps

1. **Checkout Code**: Fetches the latest code from the repository
2. **Setup Environment**: Configures JDK 17 and Gradle
3. **Get Current Version**: Reads current version from `app/build.gradle`
4. **Bump Version**: Increments version code and patch version
5. **Commit Version Bump**: Commits the updated version back to the repository
6. **Decode Keystore**: Decodes the signing keystore from secrets (if available)
7. **Build APK**: Builds either release or debug APK
8. **Generate Release Notes**: Creates detailed release notes from commit history
9. **Create Git Tag**: Tags the release with version number
10. **Create GitHub Release**: Publishes the release with APK and notes
11. **Upload Artifact**: Stores APK as a workflow artifact (90 days retention)

## 🔐 Required Secrets

To build signed release APKs, configure these GitHub secrets:

### Setting Up Secrets

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add the following secrets:

| Secret Name | Description | How to Get |
|-------------|-------------|------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file | `base64 -i keystore.jks \| pbcopy` (macOS) or `base64 keystore.jks` (Linux) |
| `KEYSTORE_PASSWORD` | Password for the keystore | From your keystore creation |
| `KEY_ALIAS` | Alias of the signing key | From your keystore creation |
| `KEY_PASSWORD` | Password for the signing key | From your keystore creation |

### Creating a Keystore (if you don't have one)

```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias focus-key
```

Follow the prompts to set passwords and fill in certificate information.

### Converting Keystore to Base64

**macOS:**
```bash
base64 -i keystore.jks | pbcopy
```

**Linux:**
```bash
base64 keystore.jks
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore.jks")) | Set-Clipboard
```

## 📝 Manual Version Bumping

While the workflow automatically bumps the PATCH version, you can manually bump MAJOR or MINOR versions:

### Using the Script

```bash
# Bump patch version (1.2.3 → 1.2.4)
./scripts/bump_version.sh patch

# Bump minor version (1.2.3 → 1.3.0)
./scripts/bump_version.sh minor

# Bump major version (1.2.3 → 2.0.0)
./scripts/bump_version.sh major
```

### Manual Edit

Edit `app/build.gradle`:

```gradle
defaultConfig {
    applicationId "com.aryanvbw.focus"
    minSdk 26
    targetSdk 35
    versionCode 3        // Increment this
    versionName "2.1.0"  // Update semantic version
    
    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
}
```

Then commit and push:
```bash
git add app/build.gradle
git commit -m "chore: bump version to 2.1.0"
git push origin app-main
```

## 📦 Release Output

Each successful workflow run produces:

### 1. GitHub Release
- **Tag**: `v{version}` (e.g., `v2.0.1`)
- **Title**: Focus v{version}
- **Body**: Detailed release notes including:
  - Build number and type
  - APK size
  - Commit history since last release
  - Installation instructions
  - Required permissions

### 2. APK File
- **Release Build**: `Focus-v{version}-release.apk` (if keystore is configured)
- **Debug Build**: `Focus-v{version}-debug.apk` (fallback)

### 3. Workflow Artifact
- Stored for 90 days
- Accessible from the Actions tab
- Named: `Focus-v{version}-{type}`

## 🎯 Best Practices

### Commit Messages
Use conventional commit format for better release notes:

```
feat: add new blocking feature
fix: resolve crash on Android 14
chore: update dependencies
docs: improve README
refactor: optimize accessibility service
```

### Branch Protection
Consider setting up branch protection rules for `app-main`:
1. Require pull request reviews
2. Require status checks to pass
3. Require branches to be up to date

### Testing Before Release
The workflow builds on every push. For critical releases:
1. Create a feature branch
2. Test thoroughly
3. Merge to `app-main` when ready
4. The release will be created automatically

## 🐛 Troubleshooting

### Build Fails
- Check the Actions tab for detailed logs
- Verify all secrets are correctly configured
- Ensure `gradlew` has execute permissions

### Version Conflict
If the workflow fails due to version conflicts:
1. Pull the latest changes
2. Resolve any merge conflicts
3. Push again

### Keystore Issues
If release build fails but debug succeeds:
- Verify `KEYSTORE_BASE64` is correctly encoded
- Check that all keystore secrets match your actual keystore
- Ensure keystore passwords are correct

### Release Not Created
- Check if the tag already exists
- Verify GitHub token has write permissions
- Look for errors in the "Create GitHub Release" step

## 📊 Monitoring

### View Workflow Runs
1. Go to the **Actions** tab in your repository
2. Click on "Auto Release and APK Build"
3. View individual workflow runs and their status

### Download APKs
- From the **Releases** page (public)
- From the **Actions** tab → Workflow run → Artifacts (requires repository access)

## 🔄 Workflow Customization

### Change Version Bump Strategy
Edit `.github/workflows/auto-release.yml`:

```yaml
# For minor version bumps instead of patch
NEW_MINOR=$((MINOR + 1))
NEW_PATCH=0
NEW_VERSION_NAME="$MAJOR.$NEW_MINOR.$NEW_PATCH"
```

### Add Build Variants
Add additional build steps for different flavors:

```yaml
- name: Build Production APK
  run: ./gradlew assembleProductionRelease

- name: Build Staging APK
  run: ./gradlew assembleStagingRelease
```

### Customize Release Notes
Modify the "Generate Release Notes" step to change the format or content.

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Gradle Plugin](https://developer.android.com/studio/build)
- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)

## 🆘 Support

If you encounter issues with the workflow:
1. Check the workflow logs in the Actions tab
2. Review this documentation
3. Open an issue in the repository
4. Contact the maintainers

---

**Last Updated**: 2025-01-29  
**Workflow Version**: 1.0.0
