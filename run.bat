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
        curl -L --retry 3 --retry-delay 5 "%MODEL_URL%" -o "%MODEL_FILE%"
        if !errorlevel! neq 0 (
            echo [WARN] Primary download failed, trying mirror site...
            set "MIRROR_URL=%MODEL_URL:huggingface.co=hf-mirror.com%"
            curl -L --retry 3 --retry-delay 5 "%MIRROR_URL%" -o "%MODEL_FILE%"
            if !errorlevel! neq 0 (
                echo [ERROR] All download attempts failed!
                echo Possible solutions:
                echo 1. Check your internet connection
                echo 2. Manually download from:
                echo    Original: %MODEL_URL%
                echo    Mirror: %MIRROR_URL%
                del "%MODEL_FILE%" 2>nul
                exit /b 1
            ) else (
                echo [INFO] Successfully downloaded via mirror site
            )
        )
    ) else (
        :: Fallback to bitsadmin (older Windows)
        echo [INFO] Using bitsadmin as fallback...
        bitsadmin /transfer downloadModel /download /priority normal "%MODEL_URL%" "%MODEL_FILE%"
        if !errorlevel! neq 0 (
            echo [WARN] Primary download failed, trying mirror site...
            set "MIRROR_URL=%MODEL_URL:huggingface.co=hf-mirror.com%"
            bitsadmin /transfer downloadModel /download /priority normal "%MIRROR_URL%" "%MODEL_FILE%"
            if !errorlevel! neq 0 (
                echo [ERROR] All download attempts failed!
                echo Try:
                echo 1. Install curl from https://curl.se/windows/
                echo 2. Manually download from:
                echo    Original: %MODEL_URL%
                echo    Mirror: %MIRROR_URL%
                del "%MODEL_FILE%" 2>nul
                exit /b 1
            ) else (
                echo [INFO] Mirror site download succeeded
            )
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
set INSTALL_DIR=%ProgramFiles%\javafx-sdk-%JAVAFX_VERSION%

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
:: 3. Auto Build (新增的自动编译逻辑)
:: ==============================================
echo [INFO] Checking build status...
if not exist "target\classes" (
    echo [INFO] Compiled classes not found, running 'mvn clean compile'...
    mvn clean compile
    if !errorlevel! neq 0 (
        echo [ERROR] Compilation failed!
        exit /b 1
    )
)

if not exist "target\dependency" (
    echo [INFO] Dependencies not found, preparing them...
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