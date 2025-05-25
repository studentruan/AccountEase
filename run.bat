@echo off
setlocal enabledelayedexpansion

:: ==============================================
:: Config
:: ==============================================
set JAVAFX_VERSION=21.0.7
set MAIN_CLASS=com.myapp.Main
set MODEL_URL=https://huggingface.co/softfish666/bert_transaction_classifer/resolve/main/bert_transaction_categorization.onnx
set MODEL_DIR=src\main\resources
set MODEL_FILE=%MODEL_DIR%\bert_transaction_categorization.onnx

:: ==============================================
:: 1. Download Model
:: ==============================================
echo [INFO] Checking model file...
if not exist "%MODEL_FILE%" (
    echo [INFO] Downloading model from Hugging Face...

    if not exist "%MODEL_DIR%" mkdir "%MODEL_DIR%"

    :: Try using curl (Windows 10+ built-in)
    where curl >nul 2>&1
    if !errorlevel! equ 0 (
        curl -L "%MODEL_URL%" -o "%MODEL_FILE%"
        if !errorlevel! neq 0 (
            echo [ERROR] Model download failed!
            del "%MODEL_FILE%" 2>nul
            exit /b 1
        )
    ) else (
        :: Fallback to bitsadmin (older Windows)
        bitsadmin /transfer downloadModel /download /priority normal "%MODEL_URL%" "%MODEL_FILE%"
        if !errorlevel! neq 0 (
            echo [ERROR] Model download failed! Install curl or use better internet connection.
            del "%MODEL_FILE%" 2>nul
            exit /b 1
        )
    )

    if exist "%MODEL_FILE%" (
        echo [SUCCESS] Model saved to: %MODEL_FILE%
    ) else (
        echo [ERROR] Model file not found after download!
        exit /b 1
    )
) else (
    echo [INFO] Model already exists: %MODEL_FILE%
)

:: ==============================================
:: 2. Install JavaFX
:: ==============================================
set JAVAFX_URL=https://download2.gluonhq.com/openjfx/%JAVAFX_VERSION%/openjfx-%JAVAFX_VERSION%_windows-x64_bin-sdk.zip
set INSTALL_DIR=%~dp0javafx-sdk-%JAVAFX_VERSION%

if not exist "%INSTALL_DIR%" (
    echo [INFO] JavaFX SDK not found. Installing...

    echo [INFO] Downloading JavaFX SDK...
    powershell -Command "Invoke-WebRequest -Uri '%JAVAFX_URL%' -OutFile '%TEMP%\javafx-sdk.zip'"
    if !errorlevel! neq 0 (
        echo [ERROR] JavaFX download failed!
        exit /b 1
    )

    echo [INFO] Installing to %INSTALL_DIR%...
    mkdir "%INSTALL_DIR%" 2>nul
    powershell -Command "Expand-Archive -Path '%TEMP%\javafx-sdk.zip' -DestinationPath '%INSTALL_DIR%'"
    move "%INSTALL_DIR%\javafx-sdk-%JAVAFX_VERSION%\*" "%INSTALL_DIR%\" >nul 2>&1
    rmdir /s /q "%INSTALL_DIR%\javafx-sdk-%JAVAFX_VERSION%" 2>nul

    del "%TEMP%\javafx-sdk.zip"
    echo [SUCCESS] JavaFX installed to: %INSTALL_DIR%
) else (
    echo [INFO] JavaFX already installed: %INSTALL_DIR%
)

:: ==============================================
:: 3. Check Build Status
:: ==============================================
if not exist "target\classes" (
    echo [ERROR] Compiled classes not found! Run first:
    echo   mvn clean compile
    exit /b 1
)

if not exist "target\dependency" (
    echo [INFO] Preparing dependencies...
    mvn dependency:copy-dependencies -DoutputDirectory=target\dependency
    if !errorlevel! neq 0 (
        echo [ERROR] Failed to prepare dependencies!
        exit /b 1
    )
)

:: ==============================================
:: 4. Run Application
:: ==============================================
echo [INFO] Starting Application...
java --module-path "%INSTALL_DIR%\lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     --enable-native-access=javafx.graphics ^
     -cp "target\classes;target\dependency\*" ^
     %MAIN_CLASS%

echo [INFO] Application exited
endlocal

pause