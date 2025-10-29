# 🔐 Google Play Store Deployment - Secrets Configuration

This document provides a quick reference for configuring GitHub Secrets required for automated Play Store deployment.

## Required Secrets

Configure these secrets in: **Settings → Secrets and variables → Actions → New repository secret**

### 1. KEYSTORE_BASE64

**Description**: Base64-encoded Android signing keystore file

**How to Generate**:
```bash
# macOS/Linux
base64 -i keystore/focus-release-key.jks > keystore_base64.txt

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore\focus-release-key.jks")) > keystore_base64.txt
```

**Value**: Copy the entire content of `keystore_base64.txt`

**Validation**: Should be a very long string (several thousand characters)

---

### 2. KEYSTORE_PASSWORD

**Description**: Password for the keystore file

**How to Get**: This is the password you set when creating the keystore

**Value**: Plain text password (e.g., `MySecurePassword123!`)

**Validation**: Test locally with:
```bash
keytool -list -v -keystore keystore/focus-release-key.jks
# Enter this password when prompted
```

---

### 3. KEY_ALIAS

**Description**: Alias name of the signing key within the keystore

**How to Get**: This is the alias you specified when creating the key

**Value**: Plain text alias (e.g., `focus-key`)

**Validation**: List all aliases in keystore:
```bash
keytool -list -v -keystore keystore/focus-release-key.jks
# Look for "Alias name:" in the output
```

---

### 4. KEY_PASSWORD

**Description**: Password for the specific signing key (may differ from keystore password)

**How to Get**: This is the key password you set when creating the key

**Value**: Plain text password (e.g., `MyKeyPassword456!`)

**Note**: Often the same as KEYSTORE_PASSWORD, but can be different

---

### 5. PLAY_STORE_SERVICE_ACCOUNT_JSON

**Description**: Google Cloud service account credentials for Play Console API access

**How to Generate**:

1. **Create Service Account** (if not already created):
   - Go to: https://console.cloud.google.com/
   - Navigate to: IAM & Admin → Service Accounts
   - Click: Create Service Account
   - Name: `github-actions-play-deploy`
   - Click: Create and Continue → Done

2. **Create JSON Key**:
   - Click on the service account
   - Go to: Keys tab
   - Click: Add Key → Create new key
   - Select: JSON
   - Click: Create (downloads JSON file)

3. **Grant Play Console Access**:
   - Go to: https://play.google.com/console/
   - Navigate to: Settings → API access
   - Find your service account
   - Click: Grant access
   - Select permissions:
     - ✅ Admin (Releases)
     - ✅ View app information
     - ✅ Manage production releases
     - ✅ Manage testing track releases
   - Click: Invite user → Send invitation

**Value**: Copy the **entire content** of the downloaded JSON file

**IMPORTANT**:
- Copy the complete JSON file content, including all braces `{}` and quotes
- Do NOT encode to base64 (unlike the keystore)
- The JSON should be copied as-is, in plain text
- The workflow uses `serviceAccountJsonPlainText` parameter to pass this directly to the upload action

**Example Format**:
```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "abc123...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "github-actions-play-deploy@your-project.iam.gserviceaccount.com",
  "client_id": "123456789",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

**Validation**: Ensure the JSON is valid and complete (no truncation)

**How to Add to GitHub**:
```bash
# Open the downloaded JSON file
cat ~/Downloads/your-project-abc123.json

# Copy the ENTIRE output (Cmd+A, Cmd+C on macOS)
# Then paste into GitHub Secrets as PLAY_STORE_SERVICE_ACCOUNT_JSON
```

---

## Quick Setup Checklist

- [ ] Create or locate Android signing keystore
- [ ] Encode keystore to base64
- [ ] Note keystore password and key alias
- [ ] Create Google Cloud service account
- [ ] Download service account JSON key
- [ ] Grant service account access in Play Console
- [ ] Add all 5 secrets to GitHub repository
- [ ] Verify secrets are saved (they should appear in the list)
- [ ] Test deployment with manual workflow trigger

---

## Security Best Practices

### ✅ DO:
- Store keystore backup in secure password manager
- Use strong, unique passwords for keystore
- Rotate service account keys annually
- Limit service account permissions to minimum required
- Use GitHub's encrypted secrets (never commit to code)
- Keep offline backups of keystore

### ❌ DON'T:
- Commit keystore files to Git
- Commit service account JSON to Git
- Share keystore passwords in plain text
- Use weak or common passwords
- Store secrets in code or configuration files
- Lose your keystore (you cannot recover it!)

---

## Verification Commands

### Test Keystore Locally
```bash
# List keystore contents
keytool -list -v -keystore keystore/focus-release-key.jks

# Verify key alias exists
keytool -list -v -keystore keystore/focus-release-key.jks -alias focus-key

# Test signing locally
./gradlew assembleRelease
```

### Test Service Account JSON Validity
```bash
# Validate JSON syntax
cat ~/Downloads/your-service-account.json | jq .

# If jq is not installed, use python
python3 -m json.tool ~/Downloads/your-service-account.json

# Test authentication with gcloud CLI (optional)
gcloud auth activate-service-account --key-file=~/Downloads/your-service-account.json
gcloud projects list
```

### Verify Base64 Encoding
```bash
# Encode and decode to verify
base64 -i keystore.jks > encoded.txt
base64 -d -i encoded.txt > decoded.jks

# Compare files (should be identical)
diff keystore.jks decoded.jks
```

---

## Common Issues

### Issue: "Keystore was tampered with, or password was incorrect"
**Solution**: Verify KEYSTORE_PASSWORD matches the actual keystore password

### Issue: "Cannot recover key"
**Solution**: Verify KEY_PASSWORD is correct for the specific key alias

### Issue: "Alias does not exist"
**Solution**: List aliases with `keytool -list` and update KEY_ALIAS secret

### Issue: "Service account not authorized"
**Solution**: 
1. Verify service account has Play Console access
2. Check permissions include "Manage releases"
3. Wait 5-10 minutes for permissions to propagate

### Issue: "Invalid JSON"
**Solution**: Ensure entire JSON file content is copied, including all braces and quotes

---

## Emergency Recovery

### Lost Keystore Password
**Impact**: Cannot sign new releases with existing keystore  
**Solution**: 
- If app not yet published: Create new keystore
- If app already published: Cannot recover - must create new app listing

### Lost Service Account JSON
**Impact**: Cannot deploy via automation  
**Solution**: 
1. Go to Google Cloud Console
2. Navigate to service account
3. Create new JSON key
4. Update GitHub secret

### Compromised Secrets
**Impact**: Security risk  
**Solution**:
1. Immediately delete compromised secrets from GitHub
2. Revoke service account key in Google Cloud Console
3. Create new keystore (if keystore was compromised)
4. Generate new service account key
5. Update all secrets
6. Review access logs

---

## Support

For detailed setup instructions, see: [docs/PLAY_STORE_DEPLOYMENT.md](../docs/PLAY_STORE_DEPLOYMENT.md)

For workflow documentation, see: [.github/workflows/play-store-deploy.yml](workflows/play-store-deploy.yml)

---

**Last Updated**: 2025-10-29  
**Document Version**: 1.0.0

