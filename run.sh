#!/bin/bash

# ==============================================
# Config
# ==============================================
JAVAFX_VERSION="21.0.7"
MAIN_CLASS="com.myapp.Main"
MODEL_URL="https://huggingface.co/softfish666/bert_transaction_classifer/resolve/main/bert_transaction_categorization.onnx"
MODEL_DIR="src/main/resources"
MODEL_FILE="$MODEL_DIR/bert_transaction_categorization.onnx"

# ==============================================
# OS Detection
# ==============================================
OS_NAME=$(uname -s)
ARCH=$(uname -m)

case "$OS_NAME" in
    Linux*)     OS="linux";;
    Darwin*)    OS="mac";;
    *)          echo "[ERROR] Unsupported OS: $OS_NAME"; exit 1;;
esac

case "$ARCH" in
    x86_64)  ARCH="x64";;
    arm64)   ARCH="aarch64";;
    *)       echo "[ERROR] Unsupported architecture: $ARCH"; exit 1;;
esac

# ==============================================
# 1. Download Model
# ==============================================
echo "[INFO] Checking model file..."
if [ ! -f "$MODEL_FILE" ]; then
    echo "[INFO] Downloading model from Hugging Face..."

    if command -v wget &> /dev/null; then
        if ! wget -q --show-progress -O "$MODEL_FILE" "$MODEL_URL"; then
            echo "[ERROR] Model download failed!"
            rm -f "$MODEL_FILE" 2>/dev/null
            exit 1
        fi
    elif command -v curl &> /dev/null; then
        if ! curl -# -L --retry 3 --retry-delay 5 "$MODEL_URL" -o "$MODEL_FILE"; then
            echo "[WARN] Primary download failed, trying mirror site..."

            MIRROR_URL="${MODEL_URL/huggingface.co/hf-mirror.com}"

            if ! curl -# -L --retry 3 --retry-delay 5 "$MIRROR_URL" -o "$MODEL_FILE"; then
                echo "[ERROR] All download attempts failed!"
                echo "Possible solutions:"
                echo "1. Check your internet connection"
                echo "2. Manually download from:"
                echo "   Original: $MODEL_URL"
                echo "   Mirror: $MIRROR_URL"
                rm -f "$MODEL_FILE" 2>/dev/null
                exit 1
            else
                echo "[INFO] Successfully downloaded via mirror site"
            fi
        fi
    else
        echo "[ERROR] Required: wget or curl. Please install one."
        exit 1
    fi

    if [ -f "$MODEL_FILE" ]; then
        echo "[SUCCESS] Model saved to: $MODEL_FILE"
    else
        echo "[ERROR] Model file not found after download!"
        exit 1
    fi
else
    echo "[INFO] Model already exists: $MODEL_FILE"
fi

# ==============================================
# 2. Install JavaFX
# ==============================================
case "$OS" in
    linux)
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${OS}-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/opt/javafx-sdk-$JAVAFX_VERSION"
        ;;
    mac)
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${OS}-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/Library/Java/JavaVirtualMachines/javafx-sdk-$JAVAFX_VERSION"
        ;;
esac

if [ ! -d "$INSTALL_DIR" ]; then
    echo "[INFO] JavaFX SDK not found. Installing..."

    echo "[INFO] Downloading JavaFX SDK..."
    if ! wget -q --show-progress -O /tmp/javafx-sdk.zip "$JAVAFX_URL"; then
        echo "[ERROR] JavaFX download failed!"
        exit 1
    fi

    echo "[INFO] Installing to $INSTALL_DIR..."
    sudo mkdir -p "$INSTALL_DIR"
    sudo unzip -q /tmp/javafx-sdk.zip -d "$INSTALL_DIR"
    sudo mv "$INSTALL_DIR"/javafx-sdk-*/* "$INSTALL_DIR" 2>/dev/null
    sudo rm -rf "$INSTALL_DIR"/javafx-sdk-* 2>/dev/null

    rm -f /tmp/javafx-sdk.zip
    echo "[SUCCESS] JavaFX installed to: $INSTALL_DIR"
else
    echo "[INFO] JavaFX already installed: $INSTALL_DIR"
fi

# ==============================================
# 3. Check Build Status
# ==============================================
if [ ! -d "target/classes" ]; then
    echo "[INFO] 检测到未编译项目，正在自动执行编译 (mvn clean compile)..."
    if ! mvn clean compile; then
        echo "[ERROR] 编译失败！请检查错误日志。"
        exit 1
    fi
fi

if [ ! -d "target/dependency" ]; then
    echo "[INFO] 检测到依赖未复制，正在自动处理 (mvn dependency:copy-dependencies)..."
    if ! mvn dependency:copy-dependencies; then
        echo "[ERROR] 依赖复制失败！"
        exit 1
    fi
fi

# ==============================================
# 4. Run Application
# ==============================================
echo "[INFO] Starting Application..."
java --module-path "$INSTALL_DIR/lib" \
     --add-modules javafx.controls,javafx.fxml \
     --enable-native-access=javafx.graphics \
     -cp "target/classes:target/dependency/*" \
     "$MAIN_CLASS"

echo "[INFO] Application exited"