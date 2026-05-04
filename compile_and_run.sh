#!/bin/bash

echo "🔨 Compiling Java project..."
mkdir -p bin
javac -d bin $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "🚀 Run with: java -cp bin com.auditorium.main.Main"
    echo ""
    echo "📋 Test credentials:"
    echo "   Manager: manager / admin123"
    echo "   Sales Person: rajesh / pass123"
else
    echo "❌ Compilation failed!"
fi
