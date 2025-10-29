#!/bin/bash

# Setup and Monitor Script for Focus App Workflow
# This script helps you set up secrets and monitor workflows

set -e

REPO="AryanVBW/focus"
WORKFLOW_FILE=".github/workflows/auto-release.yml"

echo "🚀 Focus App - Workflow Setup & Monitor"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo ""
    echo "⚠️  GitHub CLI (gh) is not installed!"
    echo ""
    echo "📦 To install GitHub CLI:"
    echo "   brew install gh"
    echo ""
    echo "🔐 After installation, authenticate with:"
    echo "   gh auth login"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo ""
    echo "⚠️  Not authenticated with GitHub!"
    echo ""
    echo "🔐 Please authenticate with:"
    echo "   gh auth login"
    echo ""
    exit 1
fi

echo ""
echo "✅ GitHub CLI is installed and authenticated"
echo ""

# Function to show menu
show_menu() {
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📋 What would you like to do?"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "1) 🔑 Setup GitHub Secrets"
    echo "2) 📊 List Workflow Runs"
    echo "3) 👁️  Watch Latest Workflow Run"
    echo "4) 📋 View Failed Workflow Logs"
    echo "5) 🔄 Re-run Failed Workflow"
    echo "6) 🚀 Trigger New Workflow Run"
    echo "7) 📦 List Releases"
    echo "8) ❌ Exit"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -n "Enter your choice [1-8]: "
}

# Function to setup secrets
setup_secrets() {
    echo ""
    echo "🔑 Setting up GitHub Secrets"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # Check if keystore exists
    if [ ! -f "keystore/focus-release-key.jks" ]; then
        echo "❌ Keystore not found at: keystore/focus-release-key.jks"
        echo "Please ensure your keystore is in the correct location."
        return
    fi
    
    echo ""
    echo "📝 Current secrets:"
    gh secret list --repo "$REPO"
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "Setting up KEYSTORE_BASE64..."
    base64 -i keystore/focus-release-key.jks | gh secret set KEYSTORE_BASE64 --repo "$REPO"
    echo "✅ KEYSTORE_BASE64 set"
    
    echo ""
    echo "Setting up KEYSTORE_PASSWORD..."
    echo -n "Enter keystore password: "
    read -s KEYSTORE_PASS
    echo ""
    echo "$KEYSTORE_PASS" | gh secret set KEYSTORE_PASSWORD --repo "$REPO"
    echo "✅ KEYSTORE_PASSWORD set"
    
    echo ""
    echo "Setting up KEY_PASSWORD..."
    echo -n "Enter key password: "
    read -s KEY_PASS
    echo ""
    echo "$KEY_PASS" | gh secret set KEY_PASSWORD --repo "$REPO"
    echo "✅ KEY_PASSWORD set"
    
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "✅ All secrets have been set successfully!"
    echo ""
    echo "📝 Updated secrets:"
    gh secret list --repo "$REPO"
}

# Function to list workflow runs
list_runs() {
    echo ""
    echo "📊 Recent Workflow Runs"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    gh run list --repo "$REPO" --workflow "auto-release.yml" --limit 10
}

# Function to watch latest run
watch_latest() {
    echo ""
    echo "👁️  Watching Latest Workflow Run"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    RUN_ID=$(gh run list --repo "$REPO" --workflow "auto-release.yml" --limit 1 --json databaseId --jq '.[0].databaseId')
    
    if [ -z "$RUN_ID" ]; then
        echo "❌ No workflow runs found"
        return
    fi
    
    echo "Watching run ID: $RUN_ID"
    echo ""
    gh run watch "$RUN_ID" --repo "$REPO" --exit-status
    
    STATUS=$(gh run view "$RUN_ID" --repo "$REPO" --json conclusion --jq '.conclusion')
    
    echo ""
    if [ "$STATUS" = "success" ]; then
        echo "✅ Workflow completed successfully!"
    else
        echo "❌ Workflow status: $STATUS"
    fi
}

# Function to view failed logs
view_failed_logs() {
    echo ""
    echo "📋 Failed Workflow Logs"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    RUN_ID=$(gh run list --repo "$REPO" --workflow "auto-release.yml" --limit 1 --json databaseId --jq '.[0].databaseId')
    
    if [ -z "$RUN_ID" ]; then
        echo "❌ No workflow runs found"
        return
    fi
    
    echo "Viewing logs for run ID: $RUN_ID"
    echo ""
    gh run view "$RUN_ID" --repo "$REPO" --log-failed
}

# Function to rerun failed workflow
rerun_workflow() {
    echo ""
    echo "🔄 Re-running Failed Workflow"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    RUN_ID=$(gh run list --repo "$REPO" --workflow "auto-release.yml" --limit 1 --json databaseId --jq '.[0].databaseId')
    
    if [ -z "$RUN_ID" ]; then
        echo "❌ No workflow runs found"
        return
    fi
    
    echo "Re-running workflow ID: $RUN_ID"
    gh run rerun "$RUN_ID" --repo "$REPO"
    echo "✅ Workflow re-run triggered"
    echo ""
    echo "Watch the run with option 3"
}

# Function to trigger new run
trigger_run() {
    echo ""
    echo "🚀 Triggering New Workflow Run"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    echo "This will trigger the workflow on the 'main' branch"
    echo -n "Continue? (y/n): "
    read -r confirm
    
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        gh workflow run auto-release.yml --repo "$REPO" --ref main
        echo "✅ Workflow triggered on main branch"
        echo ""
        echo "Watch the run with option 3"
    else
        echo "❌ Cancelled"
    fi
}

# Function to list releases
list_releases() {
    echo ""
    echo "📦 Recent Releases"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    gh release list --repo "$REPO" --limit 10
}

# Main loop
while true; do
    show_menu
    read -r choice
    
    case $choice in
        1) setup_secrets ;;
        2) list_runs ;;
        3) watch_latest ;;
        4) view_failed_logs ;;
        5) rerun_workflow ;;
        6) trigger_run ;;
        7) list_releases ;;
        8) 
            echo ""
            echo "👋 Goodbye!"
            exit 0
            ;;
        *)
            echo "❌ Invalid option. Please choose 1-8"
            ;;
    esac
    
    echo ""
    echo -n "Press Enter to continue..."
    read -r
done
