#!/bin/bash
# ==============================================================================
# compile.sh – Build & compile Smart Mess Card & Dining System
# Developed by: Abhishek Pathak (https://github.com/abhishekpathak6941)
# ==============================================================================

# Search for working javac executable
JAVAC=""
for cand in "$JAVA_HOME/bin/javac" \
            "/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home/bin/javac" \
            /Library/Java/JavaVirtualMachines/*/Contents/Home/bin/javac \
            "$(which javac 2>/dev/null)"; do
    if [ -x "$cand" ]; then
        # Ensure it actually executes without "Unable to locate Java Runtime"
        if "$cand" -version >/dev/null 2>&1; then
            JAVAC="$cand"
            break
        fi
    fi
done

if [ -z "$JAVAC" ]; then
    JAVAC="javac"
fi

mkdir -p out

CP="lib/sqlite-jdbc.jar:lib/zxing-core.jar:lib/zxing-javase.jar"

echo "============================================="
echo " Building Smart Mess Card System..."
echo " Compiler: $($JAVAC -version 2>&1)"
echo "============================================="

$JAVAC -cp "$CP" -d out src/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo " [✓] Build successful! Run application with: ./run.sh"
    echo "============================================="
else
    echo ""
    echo " [✗] Compilation failed. Please check errors above."
    echo "============================================="
    exit 1
fi
