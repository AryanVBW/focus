# ✅ Play Console Setup - Quick Action Checklist

Use this checklist to quickly set up Play Console for automated deployments.

---

## 🎯 Phase 1: Play Console Account (5 minutes)

- [ ] **1.1** Go to https://play.google.com/console
- [ ] **1.2** Sign in with Google account
- [ ] **1.3** Pay $25 registration fee (if first time)
- [ ] **1.4** Accept Developer Distribution Agreement

---

## 📱 Phase 2: Create App (10 minutes)

- [ ] **2.1** Click "Create app" button
- [ ] **2.2** Enter app details:
  - Name: `Focus - Productivity & App Blocker`
  - Language: `English (United States)`
  - Type: `App`
  - Free/Paid: `Free`
- [ ] **2.3** Check policy agreements
- [ ] **2.4** Click "Create app"

---

## 📋 Phase 3: Complete Policy Sections (15 minutes)

- [ ] **3.1** App access → "All functionality available" → Save
- [ ] **3.2** Ads → Select "No ads" (or "Yes" if applicable) → Save
- [ ] **3.3** Content rating → Start questionnaire → Complete → Apply rating
- [ ] **3.4** Target audience → Select "18 and over" → Save
- [ ] **3.5** News app → "No" → Save
- [ ] **3.6** COVID-19 apps → "No" → Save
- [ ] **3.7** Data safety → Answer questions → Submit

---

## 📦 Phase 4: First Manual Upload (10 minutes)

- [ ] **4.1** Go to Testing → Internal testing
- [ ] **4.2** Click "Create new release"
- [ ] **4.3** Upload AAB file:
  ```
  /Volumes/DATA_vivek/GITHUB/focus/app/build/outputs/bundle/release/app-release.aab
  ```
- [ ] **4.4** Add release notes (copy from `.github/whatsnew/whatsnew-en-US`)
- [ ] **4.5** Click "Save" → "Review release" → "Start rollout"
- [ ] **4.6** Create testers list with your email
- [ ] **4.7** Verify upload succeeded

---

## ☁️ Phase 5: Google Cloud Setup (15 minutes)

### 5A: Create Project
- [ ] **5.1** Go to https://console.cloud.google.com/
- [ ] **5.2** Create new project: "Focus Android Deployment"

### 5B: Enable API
- [ ] **5.3** Go to APIs & Services → Library
- [ ] **5.4** Search "Google Play Android Developer API"
- [ ] **5.5** Click "Enable"

### 5C: Create Service Account
- [ ] **5.6** Go to APIs & Services → Credentials
- [ ] **5.7** Create Credentials → Service Account
- [ ] **5.8** Name: `focus-play-deploy`
- [ ] **5.9** Click "Create and Continue" → Skip permissions → Done

### 5D: Download Key
- [ ] **5.10** Click on service account email
- [ ] **5.11** Keys tab → Add Key → Create new key
- [ ] **5.12** Select JSON → Create
- [ ] **5.13** Save downloaded JSON file securely
- [ ] **5.14** Rename to: `focus-play-service-account.json`

---

## 🔗 Phase 6: Link Service Account (10 minutes)

- [ ] **6.1** Go back to Play Console → Settings → API access
- [ ] **6.2** Link Google Cloud project (if not linked)
- [ ] **6.3** Find service account: `focus-play-deploy@...`
- [ ] **6.4** Click "Grant access"
- [ ] **6.5** Select permissions:
  - Option A: "Admin (all permissions)" ← Recommended for testing
  - Option B: Select individual permissions:
    - ✓ View app information
    - ✓ Manage production releases
    - ✓ Manage testing track releases
    - ✓ Release to production
- [ ] **6.6** Click "Invite user" → "Send invite"
- [ ] **6.7** Verify service account appears in list with correct permissions

---

## 🔐 Phase 7: Update GitHub Secret (5 minutes)

### Method 1: Using CLI (Recommended)
```bash
cd /Volumes/DATA_vivek/GITHUB/focus
gh secret set PLAY_STORE_SERVICE_ACCOUNT_JSON < focus-play-service-account.json
```

- [ ] **7.1** Run the command above
- [ ] **7.2** Verify: `gh secret list | grep PLAY_STORE`

### Method 2: Using Web UI
- [ ] **7.1** Go to https://github.com/AryanVBW/focus/settings/secrets/actions
- [ ] **7.2** Click "PLAY_STORE_SERVICE_ACCOUNT_JSON"
- [ ] **7.3** Click "Update secret"
- [ ] **7.4** Paste entire JSON content
- [ ] **7.5** Click "Update secret"

---

## 🚀 Phase 8: Test Automated Deployment (5 minutes)

### 8A: Trigger Deployment
```bash
cd /Volumes/DATA_vivek/GITHUB/focus
gh workflow run play-store-deploy.yml --ref release/internal -f track=internal
```

- [ ] **8.1** Run the command above
- [ ] **8.2** Verify: "✓ Created workflow_dispatch event"

### 8B: Monitor Deployment
```bash
gh run watch
```

- [ ] **8.3** Watch the workflow run
- [ ] **8.4** Wait for completion (usually 3-5 minutes)

### 8C: Verify in Play Console
- [ ] **8.5** Go to Play Console → Testing → Internal testing
- [ ] **8.6** Verify new release appears
- [ ] **8.7** Check version: 2.0.7 (Build 9)
- [ ] **8.8** Verify release notes are correct
- [ ] **8.9** Status shows "Available to testers"

---

## 🎉 Phase 9: Test Beta Track (5 minutes)

```bash
cd /Volumes/DATA_vivek/GITHUB/focus
gh workflow run play-store-deploy.yml --ref release/beta -f track=beta
```

- [ ] **9.1** Trigger beta deployment
- [ ] **9.2** Monitor with `gh run watch`
- [ ] **9.3** Verify in Play Console → Testing → Open testing (or Closed testing)
- [ ] **9.4** Confirm successful deployment

---

## 📊 Verification Commands

Run these to verify everything is set up:

```bash
# 1. Verify local build works
./test-play-store-deployment.sh

# 2. Verify GitHub secrets
gh secret list

# 3. Verify workflow file
gh workflow view play-store-deploy.yml

# 4. Check recent runs
gh run list --workflow=play-store-deploy.yml --limit 5

# 5. Interactive verification
./verify-play-console-setup.sh
```

---

## 🆘 Quick Troubleshooting

### If deployment fails with "Permission denied":
```bash
# Check service account permissions in Play Console
# Settings → API access → Verify "Admin" or "Release Manager"
```

### If deployment fails with "Package not found":
```bash
# Ensure you've uploaded first release manually
# Play Console → Testing → Internal testing → Create new release
```

### If deployment fails with "Version code already used":
```bash
# Increment version in app/build.gradle
# versionCode 10  (was 9)
# Commit and push changes
```

### If workflow doesn't trigger:
```bash
# Check workflow syntax
gh workflow view play-store-deploy.yml

# Try manual trigger
gh workflow run play-store-deploy.yml --ref release/internal -f track=internal
```

---

## 📈 Success Indicators

You'll know everything is working when:

✅ **Local builds succeed:**
```bash
./test-play-store-deployment.sh
# All tests pass
```

✅ **Workflow triggers successfully:**
```bash
gh workflow run play-store-deploy.yml --ref release/internal -f track=internal
# ✓ Created workflow_dispatch event
```

✅ **Deployment completes:**
```bash
gh run watch
# Status: ✓ Completed successfully
```

✅ **Release appears in Play Console:**
- Go to Play Console → Testing → Internal testing
- See version 2.0.7 (Build 9)
- Status: "Available to testers"

---

## ⏱️ Total Time Estimate

| Phase | Time | Cumulative |
|-------|------|------------|
| 1. Account | 5 min | 5 min |
| 2. Create App | 10 min | 15 min |
| 3. Policy Sections | 15 min | 30 min |
| 4. Manual Upload | 10 min | 40 min |
| 5. Google Cloud | 15 min | 55 min |
| 6. Link Service Account | 10 min | 65 min |
| 7. GitHub Secret | 5 min | 70 min |
| 8. Test Internal | 5 min | 75 min |
| 9. Test Beta | 5 min | 80 min |

**Total: ~80 minutes (1 hour 20 minutes)**

---

## 🎯 Next Steps After Setup

Once everything is working:

1. **Document the process** for your team
2. **Set up production track** with staged rollouts
3. **Complete store listing** (screenshots, description, etc.)
4. **Submit for review** (first production release)
5. **Monitor deployments** and set up notifications

---

## 📚 Reference Documents

- **Detailed Guide:** `PLAY_CONSOLE_SETUP_GUIDE.md`
- **Quick Start:** `DEPLOYMENT_QUICK_START.md`
- **Test Report:** `PLAY_STORE_TESTING_REPORT.md`
- **Summary:** `TESTING_SUMMARY.md`

---

## 🔄 Regular Deployment Workflow (After Setup)

Once setup is complete, deploying is simple:

```bash
# Internal testing
git push origin release/internal

# Beta testing
git push origin release/beta

# Production (with tag)
git tag -a v2.0.8 -m "Release 2.0.8"
git push origin v2.0.8
```

---

**Last Updated:** October 29, 2025  
**Status:** Ready for Setup  
**Estimated Completion:** 80 minutes

