@echo off
echo 🧹 Cleaning old compiled files...
if exist bin ( rmdir /s /q bin )
mkdir bin

echo 🔨 Compiling Java project...
:: Find all Java files and save their paths to a text file
dir /s /B src\*.java > sources.txt

:: Compile all files listed in the text file
javac -d bin @sources.txt

:: Clean up the text file
del sources.txt

:: Check if compilation was successful (Error level 0 means success)
if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful!
    echo 🚀 Starting the application...
    echo ------------------------------------------------
    java -cp bin com.auditorium.main.Main
) else (
    echo ❌ Compilation failed. Please check the errors above.
)
echo.
pause