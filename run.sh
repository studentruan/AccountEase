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
        if ! curl -# -L "$MODEL_URL" -o "$MODEL_FILE"; then
            echo "[WARN] Primary download failed, trying mirror site..."

            MIRROR_URL="${MODEL_URL/huggingface.co/hf-mirror.com}"

            if ! curl -# -L "$MIRROR_URL" -o "$MODEL_FILE"; then
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
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_osx-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/Library/Java/JavaVirtualMachines/javafx-sdk-$JAVAFX_VERSION"
        ;;
esac

if [ ! -d "$INSTALL_DIR" ]; then
    echo "[INFO] JavaFX SDK not found. Installing..."
    # 定义多个可能的下载镜像
    JAVAFX_MIRRORS=(
        $JAVAFX_URL
    )

    download_and_validate() {
        local url="$1"
        echo "[INFO] Trying to download from: $url"
        if curl -# -L "$url" -o /tmp/javafx-sdk.zip; then
            if unzip -tq /tmp/javafx-sdk.zip >/dev/null 2>&1; then
                echo "[INFO] ZIP file validation successful"
                return 0
            else
                echo "[WARN] Downloaded ZIP file is corrupt: $url"
                rm -f /tmp/javafx-sdk.zip
                return 1
            fi
        else
            echo "[WARN] Download failed from: $url"
            return 1
        fi
    }

    download_success=false
    for mirror_url in "${JAVAFX_MIRRORS[@]}"; do
        # 每个镜像最多尝试3次
        for attempt in {1..3}; do
            if download_and_validate "$mirror_url"; then
                download_success=true
                break 2  # 跳出两层循环
            fi
            echo "[INFO] Retrying download (attempt $attempt/3)..."
            sleep 1
        done
    done

    if [ "$download_success" = false ]; then
        echo "[ERROR] All JavaFX download attempts failed or produced invalid ZIP files!"
        exit 1
    fi

    echo "[INFO] Installing to $INSTALL_DIR..."
    sudo mkdir -p "$INSTALL_DIR"

    # 更安全的解压方式
    if ! sudo unzip -q /tmp/javafx-sdk.zip -d "$INSTALL_DIR"; then
        echo "[ERROR] Failed to unzip JavaFX SDK"
        exit 1
    fi

    # 处理可能的嵌套目录结构
    if [ -d "$INSTALL_DIR/javafx-sdk-$JAVAFX_VERSION" ]; then
        sudo mv "$INSTALL_DIR"/javafx-sdk-"$JAVAFX_VERSION"/* "$INSTALL_DIR"
        sudo rmdir "$INSTALL_DIR"/javafx-sdk-"$JAVAFX_VERSION"
    elif [ -d "$INSTALL_DIR/javafx-sdk" ]; then
        sudo mv "$INSTALL_DIR"/javafx-sdk/* "$INSTALL_DIR"
        sudo rmdir "$INSTALL_DIR"/javafx-sdk
    fi

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