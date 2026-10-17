#!/bin/bash
# ==============================================================================
# run.sh – Launch Smart Mess Card & Dining Management System
# Developed by: Abhishek Pathak (https://github.com/abhishekpathak6941)
# ==============================================================================

# Search for working java executable
JAVA=""
for cand in "$JAVA_HOME/bin/java" \
            "/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home/bin/java" \
            /Library/Java/JavaVirtualMachines/*/Contents/Home/bin/java \
            "$(which java 2>/dev/null)"; do
    if [ -x "$cand" ]; then
        if "$cand" -version >/dev/null 2>&1; then
            JAVA="$cand"
            break
        fi
    fi
done

if [ -z "$JAVA" ]; then
    JAVA="java"
fi

CP="out:lib/sqlite-jdbc.jar:lib/zxing-core.jar:lib/zxing-javase.jar"

echo "============================================="
echo " Starting Smart Mess Card System..."
echo " Java Runtime: $($JAVA -version 2>&1 | head -n 1)"
echo "============================================="

$JAVA -cp "$CP" MessCardSystem
