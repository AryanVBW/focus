# ✅ Google Play Store Deployment - Setup Checklist

Use this checklist to ensure all prerequisites are met before deploying to Google Play Console.

## 📋 Pre-Setup Requirements

### Google Play Console Account
- [ ] Google Play Developer account created ($25 one-time fee)
- [ ] Payment method added to account
- [ ] Developer profile completed
- [ ] App created in Play Console with package name: `com.aryanvbw.focus`

### Google Cloud Project
- [ ] Google Cloud project created
- [ ] Project linked to Play Console
- [ ] Billing enabled (if required)
- [ ] Google Play Android Developer API enabled

---

## 🔑 Service Account Setup

### Create Service Account
- [ ] Service account created in Google Cloud Console
- [ ] Service account name: `github-actions-play-deploy` (or similar)
- [ ] JSON key file downloaded and saved securely
- [ ] JSON key file backed up in secure location

### Grant Play Console Access
- [ ] Service account linked in Play Console → Settings → API access
- [ ] Permissions granted:
  - [ ] Admin (Releases)
  - [ ] View app information
  - [ ] Manage production releases
  - [ ] Manage testing track releases
- [ ] Invitation sent and accepted
- [ ] Access verified (wait 5-10 minutes after granting)

---

## 🔐 Keystore Setup

### Existing Keystore
- [ ] Release keystore file located
- [ ] Keystore password known and documented
- [ ] Key alias known and documented
- [ ] Key password known and documented
- [ ] Keystore backed up in secure location (password manager, encrypted drive)
- [ ] Keystore tested locally: `keytool -list -v -keystore keystore.jks`

### New Keystore (if needed)
- [ ] New keystore created using `keytool`
- [ ] Strong passwords used (minimum 12 characters)
- [ ] Certificate information filled out correctly
- [ ] Keystore file saved in secure location
- [ ] Passwords documented in password manager
- [ ] Keystore backed up in multiple secure locations
- [ ] **WARNING**: Keystore backed up - if lost, cannot update app!

---

## 🔧 GitHub Repository Setup

### Repository Access
- [ ] Admin access to GitHub repository
- [ ] Repository settings accessible
- [ ] Actions enabled in repository settings

### Workflow Files
- [ ] `.github/workflows/play-store-deploy.yml` exists
- [ ] Workflow file syntax validated
- [ ] Package name in workflow matches Play Console: `com.aryanvbw.focus`

### Secrets Configuration
Navigate to: **Repository → Settings → Secrets and variables → Actions**

- [ ] `KEYSTORE_BASE64` secret added
  - [ ] Keystore encoded to base64
  - [ ] Entire base64 string copied (no truncation)
  - [ ] Secret value pasted in GitHub
  - [ ] Secret saved successfully

- [ ] `KEYSTORE_PASSWORD` secret added
  - [ ] Correct password copied
  - [ ] No extra spaces or characters
  - [ ] Secret saved successfully

- [ ] `KEY_ALIAS` secret added
  - [ ] Correct alias name copied
  - [ ] Case matches keystore exactly
  - [ ] Secret saved successfully

- [ ] `KEY_PASSWORD` secret added
  - [ ] Correct key password copied
  - [ ] No extra spaces or characters
  - [ ] Secret saved successfully

- [ ] `PLAY_STORE_SERVICE_ACCOUNT_JSON` secret added
  - [ ] Entire JSON file content copied
  - [ ] JSON is valid (no syntax errors)
  - [ ] All fields present (type, project_id, private_key, etc.)
  - [ ] Secret saved successfully

### Verify All Secrets
- [ ] All 5 secrets appear in secrets list
- [ ] Secret names match exactly (case-sensitive)
- [ ] No typos in secret names

---

## 📱 App Configuration

### Version Management
- [ ] Current version in `app/build.gradle` noted
- [ ] Version bump script exists: `scripts/bump_version.sh`
- [ ] Version bump script is executable: `chmod +x scripts/bump_version.sh`
- [ ] Version bump tested locally

### Build Configuration
- [ ] `app/build.gradle` has signing configuration
- [ ] Signing config references `keystore.properties`
- [ ] Build types configured correctly
- [ ] ProGuard/R8 configuration set (if using minification)

### Release Notes
- [ ] `.github/whatsnew/` directory exists
- [ ] `whatsnew-en-US` file created
- [ ] Release notes template prepared
- [ ] Character limit understood (500 max)

---

## 🧪 Testing & Validation

### Local Build Testing
- [ ] Clean build successful: `./gradlew clean`
- [ ] Release APK builds: `./gradlew assembleRelease`
- [ ] Release AAB builds: `./gradlew bundleRelease`
- [ ] APK signed correctly (verified with `apksigner`)
- [ ] AAB signed correctly
- [ ] App installs on test device
- [ ] App functions correctly after installation

### Keystore Validation
- [ ] Keystore password verified: `keytool -list -v -keystore keystore.jks`
- [ ] Key alias exists in keystore
- [ ] Key password verified
- [ ] Certificate validity checked (should be valid for 25+ years)

### Service Account Validation
- [ ] Service account JSON is valid JSON
- [ ] Service account email matches Play Console
- [ ] Service account has correct permissions
- [ ] API access enabled and working

---

## 🚀 First Deployment Test

### Internal Track Test
- [ ] Version bumped in `app/build.gradle`
- [ ] Changes committed and pushed
- [ ] Manual workflow dispatch triggered
- [ ] Track selected: `internal`
- [ ] Workflow started successfully
- [ ] Workflow completed without errors
- [ ] AAB uploaded to Play Console
- [ ] Release visible in Play Console → Release → Testing → Internal testing
- [ ] Internal testers can see the release

### Verify Deployment
- [ ] Play Console shows new version
- [ ] Version code matches `app/build.gradle`
- [ ] Version name matches `app/build.gradle`
- [ ] Release notes appear correctly
- [ ] AAB size is reasonable
- [ ] No errors in Play Console

---

## 📊 Post-Setup Configuration

### Play Console Settings
- [ ] App information completed (title, description, screenshots)
- [ ] Content rating questionnaire completed
- [ ] Target audience and content settings configured
- [ ] Privacy policy URL added (if required)
- [ ] App category selected
- [ ] Contact details added

### Testing Tracks Setup
- [ ] Internal testing track configured
  - [ ] Testers added (up to 100)
  - [ ] Tester list created
  
- [ ] Alpha testing track configured (optional)
  - [ ] Opt-in URL created
  - [ ] Testers invited
  
- [ ] Beta testing track configured (optional)
  - [ ] Opt-in URL created
  - [ ] Tester list created

### Production Preparation
- [ ] Store listing completed
- [ ] Screenshots uploaded (phone, tablet, etc.)
- [ ] Feature graphic created and uploaded
- [ ] App icon verified
- [ ] Short description written (80 characters max)
- [ ] Full description written (4000 characters max)
- [ ] Promotional video added (optional)

---

## 🔄 Workflow Testing

### Manual Deployment
- [ ] Manual workflow dispatch works
- [ ] Can select different tracks
- [ ] Can set rollout percentage
- [ ] Workflow completes successfully

### Branch-Based Deployment
- [ ] `release/internal` branch triggers internal deployment
- [ ] `release/alpha` branch triggers alpha deployment
- [ ] `release/beta` branch triggers beta deployment
- [ ] Deployments complete successfully

### Tag-Based Deployment
- [ ] Version tag triggers production deployment
- [ ] Tag format `v*.*.*` recognized
- [ ] Production deployment works
- [ ] Tag appears in GitHub releases

---

## 📚 Documentation Review

- [ ] Read: [PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md)
- [ ] Read: [PLAY_STORE_SECRETS.md](PLAY_STORE_SECRETS.md)
- [ ] Read: [DEPLOYMENT_QUICK_REFERENCE.md](DEPLOYMENT_QUICK_REFERENCE.md)
- [ ] Read: [workflows/README.md](workflows/README.md)
- [ ] Understand deployment workflow
- [ ] Understand version management
- [ ] Understand release tracks
- [ ] Understand staged rollouts

---

## 🛡️ Security Verification

- [ ] Keystore NOT committed to Git
- [ ] Service account JSON NOT committed to Git
- [ ] `keystore.properties` in `.gitignore`
- [ ] All secrets stored in GitHub Secrets only
- [ ] Keystore backed up in secure location
- [ ] Service account JSON backed up securely
- [ ] Passwords stored in password manager
- [ ] No sensitive data in workflow files
- [ ] No sensitive data in commit history

---

## 📝 Final Checks

- [ ] All team members aware of deployment process
- [ ] Deployment documentation accessible to team
- [ ] Emergency rollback procedure understood
- [ ] Monitoring plan in place
- [ ] Crash reporting configured
- [ ] Analytics configured (if using)
- [ ] Support email configured in Play Console
- [ ] Release schedule planned

---

## 🎉 Ready to Deploy!

If all items above are checked, you're ready to deploy to Google Play Console!

### Next Steps:
1. Bump version: `./scripts/bump_version.sh patch`
2. Update release notes: `.github/whatsnew/whatsnew-en-US`
3. Commit and push changes
4. Trigger deployment workflow
5. Monitor deployment in Actions tab
6. Verify in Play Console
7. Test with internal testers
8. Gradually promote through tracks
9. Monitor crash reports and reviews
10. Celebrate your successful deployment! 🎊

---

## 🆘 If Something Goes Wrong

### Workflow Fails
1. Check workflow logs in Actions tab
2. Verify all secrets are correct
3. Test build locally
4. Review error messages
5. Consult troubleshooting guide

### Deployment Fails
1. Check Play Console for error messages
2. Verify service account permissions
3. Check version code is unique
4. Verify AAB is signed correctly
5. Review Play Console requirements

### Need Help?
- Review documentation in `docs/` directory
- Check workflow logs for specific errors
- Verify all checklist items above
- Open an issue in repository
- Contact Play Console support (for Play Console issues)

---

**Setup Date**: _______________  
**Completed By**: _______________  
**First Deployment Date**: _______________  
**Notes**: 

_______________________________________________
_______________________________________________
_______________________________________________

---

**Last Updated**: 2025-10-29  
**Checklist Version**: 1.0.0

