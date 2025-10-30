# 🎯 Google Play Console Setup Guide - Step by Step

This guide will walk you through setting up Google Play Console for automated deployments of the Focus Android app.

---

## 📋 Prerequisites

Before starting, ensure you have:
- [ ] Google Play Developer account ($25 one-time fee if you don't have one)
- [ ] Access to Google Cloud Console
- [ ] The signed AAB file (already built: `app/build/outputs/bundle/release/app-release.aab`)

---

## Part 1: Create App in Google Play Console

### Step 1: Access Play Console

1. Go to https://play.google.com/console
2. Sign in with your Google account
3. If this is your first time, you'll need to:
   - Pay the $25 one-time registration fee
   - Accept the Developer Distribution Agreement

### Step 2: Create New App

1. Click **"Create app"** button (top right)
2. Fill in the details:
   - **App name:** Focus - Productivity & App Blocker
   - **Default language:** English (United States)
   - **App or game:** App
   - **Free or paid:** Free
3. Check the boxes:
   - ✓ Developer Program Policies
   - ✓ US export laws
4. Click **"Create app"**

### Step 3: Set Up App Dashboard

You'll be taken to the app dashboard. You need to complete several sections:

#### A. App Access
1. Go to **Policy → App access**
2. Select: "All functionality is available without special access"
3. Click **Save**

#### B. Ads
1. Go to **Policy → Ads**
2. Select: "No, my app does not contain ads" (or "Yes" if it does)
3. Click **Save**

#### C. Content Rating
1. Go to **Policy → Content rating**
2. Click **Start questionnaire**
3. Enter your email address
4. Select category: **Utility, Productivity, Communication, or Other**
5. Answer the questionnaire (all "No" for a productivity app)
6. Click **Submit**
7. Click **Apply rating**

#### D. Target Audience
1. Go to **Policy → Target audience and content**
2. Select target age group: **18 and over** (recommended for productivity apps)
3. Click **Next** and **Save**

#### E. News App
1. Go to **Policy → News app**
2. Select: "No, it's not a news app"
3. Click **Save**

#### F. COVID-19 Contact Tracing and Status Apps
1. Go to **Policy → COVID-19 contact tracing and status apps**
2. Select: "No"
3. Click **Save**

#### G. Data Safety
1. Go to **Policy → Data safety**
2. Click **Start**
3. Answer questions about data collection:
   - Does your app collect or share user data? (Select based on your app)
   - For a basic productivity app: Select "No"
4. Click **Next** → **Submit**

---

## Part 2: Upload First Release (Manual)

**Important:** The first release MUST be uploaded manually. After this, automated deployments will work.

### Step 1: Create Internal Testing Release

1. In the left sidebar, go to **Testing → Internal testing**
2. Click **Create new release**
3. You'll see "App bundles" section

### Step 2: Upload Your AAB

1. Click **Upload** button
2. Navigate to your AAB file:
   ```
   /Volumes/DATA_vivek/GITHUB/focus/app/build/outputs/bundle/release/app-release.aab
   ```
3. Upload the file (8.5M)
4. Wait for upload to complete

### Step 3: Fill Release Details

1. **Release name:** 2.0.7 (or leave auto-generated)
2. **Release notes:** Copy from `.github/whatsnew/whatsnew-en-US`:
   ```
   🎯 Focus v2.0.7 - Enhanced Productivity Features
   
   • Improved app blocking accuracy and reliability
   • Enhanced focus mode with better distraction detection  
   • Performance optimizations for smoother experience
   • Bug fixes and stability improvements
   • Updated UI/UX for better user experience
   
   Test build for internal/beta track validation.
   ```
3. Click **Save**
4. Click **Review release**
5. Click **Start rollout to Internal testing**

### Step 4: Create Testers List

1. Go to **Testing → Internal testing → Testers** tab
2. Click **Create email list**
3. Name it: "Internal Testers"
4. Add email addresses (your email and team members)
5. Click **Save changes**

---

## Part 3: Set Up Service Account for API Access

This is crucial for automated deployments via GitHub Actions.

### Step 1: Access Google Cloud Console

1. Go to https://console.cloud.google.com/
2. Sign in with the same Google account
3. Create a new project or select existing one:
   - Click project dropdown (top left)
   - Click **"New Project"**
   - Name: "Focus Android Deployment"
   - Click **Create**

### Step 2: Enable Google Play Developer API

1. In Google Cloud Console, go to **APIs & Services → Library**
2. Search for: "Google Play Android Developer API"
3. Click on it
4. Click **Enable**
5. Wait for it to enable (may take a minute)

### Step 3: Create Service Account

1. Go to **APIs & Services → Credentials**
2. Click **Create Credentials → Service Account**
3. Fill in details:
   - **Service account name:** focus-play-deploy
   - **Service account ID:** focus-play-deploy (auto-filled)
   - **Description:** Service account for automated Play Store deployments
4. Click **Create and Continue**
5. **Grant this service account access to project:**
   - Skip this (click **Continue**)
6. **Grant users access to this service account:**
   - Skip this (click **Done**)

### Step 4: Create Service Account Key

1. You'll see your service account in the list
2. Click on the service account email (focus-play-deploy@...)
3. Go to **Keys** tab
4. Click **Add Key → Create new key**
5. Select **JSON** format
6. Click **Create**
7. A JSON file will download - **SAVE THIS FILE SECURELY**
8. Rename it to something like: `focus-play-service-account.json`

### Step 5: Link Service Account to Play Console

1. Go back to **Play Console** (https://play.google.com/console)
2. Click **Settings** (gear icon, bottom left)
3. Go to **API access**
4. You should see your Google Cloud project linked
   - If not, click **Link** and select your project
5. Scroll down to **Service accounts**
6. Find your service account: `focus-play-deploy@...`
7. Click **Grant access**
8. Set permissions:
   - **Account permissions:**
     - ✓ View app information and download bulk reports (read-only)
     - ✓ Manage production releases
     - ✓ Manage testing track releases
     - ✓ Release to production, exclude devices, and use Play App Signing
   - **Or simply select:** "Admin (all permissions)" for testing
9. Click **Invite user**
10. Click **Send invite**

---

## Part 4: Update GitHub Secret

Now you need to update the GitHub secret with the service account JSON.

### Step 1: Prepare the JSON

1. Open the downloaded JSON file: `focus-play-service-account.json`
2. Copy the ENTIRE contents (it should look like this):
   ```json
   {
     "type": "service_account",
     "project_id": "...",
     "private_key_id": "...",
     "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
     "client_email": "focus-play-deploy@....iam.gserviceaccount.com",
     "client_id": "...",
     "auth_uri": "https://accounts.google.com/o/oauth2/auth",
     "token_uri": "https://oauth2.googleapis.com/token",
     "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
     "client_x509_cert_url": "..."
   }
   ```

### Step 2: Update GitHub Secret

```bash
# Method 1: Using gh CLI (recommended)
gh secret set PLAY_STORE_SERVICE_ACCOUNT_JSON < focus-play-service-account.json

# Method 2: Via GitHub Web UI
# 1. Go to: https://github.com/AryanVBW/focus/settings/secrets/actions
# 2. Click on PLAY_STORE_SERVICE_ACCOUNT_JSON
# 3. Click "Update secret"
# 4. Paste the entire JSON content
# 5. Click "Update secret"
```

---

## Part 5: Complete Store Listing (Required before going live)

Before you can publish to production, you need to complete the store listing.

### Step 1: Main Store Listing

1. Go to **Grow → Main store listing**
2. Fill in required fields:

   **App name:** Focus - Productivity & App Blocker
   
   **Short description (80 chars max):**
   ```
   Block distracting apps and boost your productivity with smart focus modes
   ```
   
   **Full description (4000 chars max):**
   ```
   🎯 Focus - Your Ultimate Productivity Companion
   
   Take control of your digital life with Focus, the powerful app blocker designed to help you stay productive and focused on what matters most.
   
   ✨ KEY FEATURES:
   
   📱 Smart App Blocking
   • Block distracting apps during focus sessions
   • Customizable blocklists for different scenarios
   • Intelligent detection of productivity-killing apps
   
   ⏰ Focus Modes
   • Work Mode - Block social media and games
   • Study Mode - Block everything except educational apps
   • Sleep Mode - Block all apps for better rest
   • Custom Modes - Create your own focus profiles
   
   📊 Productivity Insights
   • Track your focus time and patterns
   • See which apps distract you most
   • Monitor your productivity improvements
   
   🔒 Powerful Blocking
   • Prevent app uninstallation during focus
   • Lock settings to avoid temptation
   • Schedule automatic focus sessions
   
   🎨 Beautiful Design
   • Clean, modern interface
   • Dark mode support
   • Intuitive controls
   
   WHY CHOOSE FOCUS?
   
   Unlike other app blockers, Focus uses advanced techniques to ensure you stay focused. It's not just about blocking apps - it's about building better digital habits.
   
   Perfect for:
   • Students preparing for exams
   • Professionals working from home
   • Anyone struggling with phone addiction
   • People wanting better work-life balance
   
   Download Focus today and reclaim your time! 🚀
   ```

### Step 2: Graphics Assets

You need to provide screenshots and graphics:

1. **App icon:** 512 x 512 px (already in your app)
2. **Feature graphic:** 1024 x 500 px
3. **Phone screenshots:** At least 2, up to 8 (1080 x 1920 px or similar)
4. **7-inch tablet screenshots:** Optional
5. **10-inch tablet screenshots:** Optional

**Quick tip:** You can use Android Studio's screenshot tool or an emulator to capture screenshots.

### Step 3: Categorization

1. **App category:** Productivity
2. **Tags:** productivity, focus, app blocker, time management

### Step 4: Contact Details

1. **Email:** Your support email
2. **Phone:** Optional
3. **Website:** Optional (but recommended)
4. **Privacy Policy URL:** Required if you collect any data

---

## Part 6: Test Automated Deployment

Now that everything is set up, let's test the automated deployment!

### Step 1: Trigger Deployment

```bash
# Make sure you're in the project directory
cd /Volumes/DATA_vivek/GITHUB/focus

# Trigger internal track deployment
gh workflow run play-store-deploy.yml \
  --ref release/internal \
  -f track=internal \
  -f rollout_percentage=100
```

### Step 2: Monitor the Deployment

```bash
# Watch the workflow run
gh run watch

# Or list recent runs
gh run list --workflow=play-store-deploy.yml --limit 3
```

### Step 3: Verify in Play Console

1. Go to **Play Console → Testing → Internal testing**
2. You should see a new release appear
3. Check that:
   - Version code is 9
   - Version name is 2.0.7
   - Release notes are correct
   - Status shows "Available to testers"

---

## Part 7: Test Beta Track

Once internal testing works, test the beta track:

### Step 1: Set Up Beta Track

1. In Play Console, go to **Testing → Open testing** (or **Closed testing** for beta)
2. Click **Create new release**
3. You can promote from internal or wait for automated deployment

### Step 2: Trigger Beta Deployment

```bash
# Push to beta branch
git checkout release/beta
git merge release/internal
git push origin release/beta

# Or trigger manually
gh workflow run play-store-deploy.yml \
  --ref release/beta \
  -f track=beta \
  -f rollout_percentage=100
```

---

## 🎉 Success Checklist

Once everything is working, you should have:

- [x] App created in Play Console
- [x] All policy sections completed
- [x] First release uploaded manually to internal track
- [x] Service account created and configured
- [x] Service account linked to Play Console with proper permissions
- [x] GitHub secret updated with service account JSON
- [x] Automated deployment tested and working
- [x] Internal track deployment successful
- [x] Beta track deployment successful
- [x] Store listing completed (for production)

---

## 🆘 Troubleshooting

### Error: "The caller does not have permission"

**Solution:** 
- Go to Play Console → Settings → API access
- Find your service account
- Click "Grant access" and ensure it has "Release Manager" or "Admin" permissions

### Error: "Package not found"

**Solution:**
- Ensure you've created the app in Play Console
- Verify the package name matches: `com.aryanvbw.focus`
- Make sure you've uploaded at least one release manually first

### Error: "Version code X has already been used"

**Solution:**
- Increment `versionCode` in `app/build.gradle`
- Each release must have a unique, incrementing version code

### Error: "APK or Android App Bundle file is malformed"

**Solution:**
- Rebuild the AAB: `./gradlew clean bundleRelease`
- Verify signing: `jarsigner -verify app/build/outputs/bundle/release/app-release.aab`

---

## 📞 Quick Reference Commands

```bash
# Test local build
./test-play-store-deployment.sh

# Deploy to internal
gh workflow run play-store-deploy.yml --ref release/internal -f track=internal

# Deploy to beta
gh workflow run play-store-deploy.yml --ref release/beta -f track=beta

# Monitor deployment
gh run watch

# Check Play Console
open https://play.google.com/console
```

---

**Need Help?** Check the detailed logs in GitHub Actions or Play Console for specific error messages.

**Next Steps:** Once automated deployment works, you can set up production releases with staged rollouts!

