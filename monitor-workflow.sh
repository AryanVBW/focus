#!/bin/bash

# Monitor GitHub Actions workflow runs
# Usage: ./monitor-workflow.sh

set -e

REPO="AryanVBW/focus"
WORKFLOW_NAME="auto-release.yml"

echo "🔍 Monitoring workflow runs for $REPO..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Get the latest workflow run
echo ""
echo "📊 Latest Workflow Runs:"
gh run list --repo "$REPO" --workflow "$WORKFLOW_NAME" --limit 5

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Get the most recent run ID
RUN_ID=$(gh run list --repo "$REPO" --workflow "$WORKFLOW_NAME" --limit 1 --json databaseId --jq '.[0].databaseId')

if [ -z "$RUN_ID" ]; then
    echo "❌ No workflow runs found"
    exit 1
fi

echo ""
echo "🔎 Watching latest run (ID: $RUN_ID)..."
echo ""

# Watch the workflow run
gh run watch "$RUN_ID" --repo "$REPO" --exit-status

# Get the final status
STATUS=$(gh run view "$RUN_ID" --repo "$REPO" --json conclusion --jq '.conclusion')

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

if [ "$STATUS" = "success" ]; then
    echo "✅ Workflow completed successfully!"
    echo ""
    echo "📦 Checking for release artifacts..."
    gh run view "$RUN_ID" --repo "$REPO" --log-failed
elif [ "$STATUS" = "failure" ]; then
    echo "❌ Workflow failed!"
    echo ""
    echo "📋 Failed job logs:"
    gh run view "$RUN_ID" --repo "$REPO" --log-failed
    exit 1
else
    echo "⚠️  Workflow status: $STATUS"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
