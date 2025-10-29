# ⚡ Quick Start - Google Play Store Deployment

Get your app deployed to Google Play Console in 5 steps!

## 🎯 Prerequisites

Before you start, ensure you have:
- [ ] Google Play Developer account ($25 one-time fee)
- [ ] App created in Play Console (package: `com.aryanvbw.focus`)
- [ ] Release keystore file with passwords
- [ ] Admin access to this GitHub repository

## 🚀 5-Step Setup

### Step 1: Create Service Account (10 minutes)

1. **Go to Google Cloud Console**: https://console.cloud.google.com/
2. **Create/Select Project**: Link it to your Play Console
3. **Enable API**:
   - Navigate to: APIs & Services → Library
   - Search: "Google Play Android Developer API"
   - Click: Enable
4. **Create Service Account**:
   - Navigate to: IAM & Admin → Service Accounts
   - Click: Create Service Account
   - Name: `github-actions-play-deploy`
   - Click: Create → Done
5. **Download JSON Key**:
   - Click on service account
   - Keys tab → Add Key → Create new key
   - Format: JSON → Create
   - **Save this file securely!**

### Step 2: Grant Play Console Access (5 minutes)

1. **Go to Play Console**: https://play.google.com/console/
2. **Navigate to**: Settings → API access
3. **Link Project** (if not already linked)
4. **Find Service Account** in the list
5. **Click**: Grant access
6. **Select Permissions**:
   - ✅ Admin (Releases)
   - ✅ View app information
   - ✅ Manage production releases
   - ✅ Manage testing track releases
7. **Click**: Invite user → Send invitation
8. **Wait**: 5-10 minutes for permissions to propagate

### Step 3: Prepare Keystore (5 minutes)

**If you have a keystore:**
```bash
# Encode to base64
base64 -i keystore/focus-release-key.jks > keystore_base64.txt

# Copy the content
cat keystore_base64.txt | pbcopy  # macOS
# or
cat keystore_base64.txt  # Copy manually
```

**If you need to create a keystore:**
```bash
keytool -genkey -v \
  -keystore focus-release-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias focus-key

# Follow prompts and SAVE THE PASSWORDS!
# Then encode as shown above
```

### Step 4: Configure GitHub Secrets (5 minutes)

1. **Go to**: Repository → Settings → Secrets and variables → Actions
2. **Click**: New repository secret
3. **Add these 5 secrets**:

| Secret Name | Value | Where to Get |
|-------------|-------|--------------|
| `KEYSTORE_BASE64` | Content of `keystore_base64.txt` | From Step 3 |
| `KEYSTORE_PASSWORD` | Your keystore password | From keystore creation |
| `KEY_ALIAS` | Your key alias | Usually `focus-key` |
| `KEY_PASSWORD` | Your key password | From keystore creation |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Entire JSON file content | From Step 1 |

**Important**: Copy entire values, no extra spaces!

### Step 5: Test Deployment (5 minutes)

1. **Bump Version** (if needed):
   ```bash
   ./scripts/bump_version.sh patch
   git add app/build.gradle
   git commit -m "chore: bump version"
   git push
   ```

2. **Trigger Workflow**:
   - Go to: Actions → Deploy to Google Play Console
   - Click: Run workflow
   - Branch: `main` or `app-main`
   - Track: `internal`
   - Click: Run workflow

3. **Monitor**:
   - Watch workflow progress in Actions tab
   - Should complete in ~5-10 minutes

4. **Verify**:
   - Go to: Play Console → Release → Testing → Internal testing
   - You should see your new release!

## ✅ Success!

If you see your app in Play Console, you're done! 🎉

## 📚 Next Steps

### Learn More
- **Full Setup Guide**: [PLAY_STORE_SETUP_CHECKLIST.md](PLAY_STORE_SETUP_CHECKLIST.md)
- **Deployment Guide**: [../docs/PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md)
- **Quick Reference**: [DEPLOYMENT_QUICK_REFERENCE.md](DEPLOYMENT_QUICK_REFERENCE.md)

### Deploy to Other Tracks

**Alpha Testing**:
```bash
git checkout -b release/alpha
git push origin release/alpha
```

**Beta Testing**:
```bash
git checkout -b release/beta
git push origin release/beta
```

**Production**:
```bash
# Ensure version is bumped!
git tag -a v2.0.5 -m "Release v2.0.5"
git push origin v2.0.5
```

## 🐛 Common Issues

### "Service account not authorized"
**Solution**: Wait 10 minutes after granting permissions, then retry

### "Keystore was tampered with"
**Solution**: Verify KEYSTORE_PASSWORD is correct

### "Version code already exists"
**Solution**: Bump version code in `app/build.gradle`

### "Invalid JSON"
**Solution**: Ensure entire service account JSON is copied

## 🆘 Need Help?

1. **Check**: Workflow logs in Actions tab
2. **Review**: [Troubleshooting Guide](../docs/PLAY_STORE_DEPLOYMENT.md#troubleshooting)
3. **Test**: Build locally with `./gradlew bundleRelease`
4. **Verify**: All secrets are configured correctly

## 📞 Support

- **Full Documentation**: [.github/README.md](README.md)
- **Setup Checklist**: [PLAY_STORE_SETUP_CHECKLIST.md](PLAY_STORE_SETUP_CHECKLIST.md)
- **Secrets Guide**: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)

---

**Total Setup Time**: ~30 minutes  
**Difficulty**: Intermediate  
**One-time Setup**: Yes (then deploy anytime!)

**Happy Deploying! 🚀**

