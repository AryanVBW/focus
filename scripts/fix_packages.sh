#!/bin/bash

# Fix all package names from com.aryanvbw.focus to com.focus.app
find /Volumes/DATA_vivek/GITHUB/focus/Focus-app/app/src/main/java -name "*.kt" -type f -exec sed -i '' 's/com\.aryanvbw\.focus/com.focus.app/g' {} \;

echo "Package names fixed!"