#!/bin/bash

# Define variables
JAVAFX_VERSION="21.0.7"
MAIN_CLASS="com.myapp.Main"  # Replace with your main class

# Detect OS and architecture
OS_NAME=$(uname -s)
ARCH=$(uname -m)

case "$OS_NAME" in
    Linux*)     OS="linux";;
    Darwin*)    OS="mac";;
    CYGWIN*|MINGW*) OS="windows";;
    *)          echo "Unsupported OS"; exit 1;;
esac

case "$ARCH" in
    x86_64)  ARCH="x64";;
    arm64)   ARCH="aarch64";;
    *)       echo "Unsupported architecture"; exit 1;;
esac

# Set installation paths
case "$OS" in
    linux)
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${OS}-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/opt/javafx-sdk-$JAVAFX_VERSION"
        ;;
    mac)
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${OS}-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/Library/Java/JavaVirtualMachines/javafx-sdk-$JAVAFX_VERSION"
        ;;
    windows)
        JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${OS}-${ARCH}_bin-sdk.zip"
        INSTALL_DIR="/c/Program Files/javafx-sdk-$JAVAFX_VERSION"
        ;;
esac

# Check if JavaFX SDK is installed
if [ ! -d "$INSTALL_DIR" ]; then
    echo "JavaFX SDK not found. Starting installation..."

    # Download JavaFX SDK
    echo "Downloading JavaFX SDK..."
    if ! wget -O /tmp/javafx-sdk.zip "$JAVAFX_URL"; then
        echo "Error: Failed to download JavaFX SDK!"
        exit 1
    fi

    # Install to target directory
    echo "Installing to $INSTALL_DIR..."
    sudo mkdir -p "$INSTALL_DIR"
    sudo unzip -q /tmp/javafx-sdk.zip -d "$INSTALL_DIR"
    sudo mv "$INSTALL_DIR"/javafx-sdk-*/* "$INSTALL_DIR"
    sudo rm -rf "$INSTALL_DIR"/javafx-sdk-*

    # Cleanup
    rm /tmp/javafx-sdk.zip
    echo "JavaFX SDK installation completed!"
else
    echo "JavaFX SDK already installed. Skipping download."
fi

# Check for compiled classes
if [ ! -d "target/classes" ]; then
    echo "Error: Compiled classes not found!"
    echo "Please run 'mvn compile' first."
    exit 1
fi

# Prepare dependencies
if [ ! -d "target/dependency" ]; then
    echo "Preparing dependencies..."
    if ! mvn dependency:copy-dependencies -DoutputDirectory=target/dependency; then
        echo "Error: Failed to prepare dependencies!"
        exit 1
    fi
fi

# Run the application
echo "Starting Application..."
java --module-path "$INSTALL_DIR/lib" \
     --add-modules javafx.controls,javafx.fxml \
     --enable-native-access=javafx.graphics \
     -cp "target/classes:target/dependency/*" \
     "$MAIN_CLASS"

echo "Application exited"