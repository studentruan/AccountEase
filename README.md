# AccountEase

### 🛠️ Environment Setup

Prerequisites
JDK 21 

Maven 

### 🚀 Running the Application

#### Firstly, clone the repository to your local
```bash
git clone --depth 1 https://github.com/studentruan/AccountEase.git
```

However, if clone through https failed, you can use ssh:
```bash
git clone --depth 1 git@github.com:studentruan/AccountEase.git
```

Next, you need to do:
```bash
mvn clean compile
```

#### Then you can use the script to automatically start the app
- macOS/Linux：

```bash
./run.sh
```

- Windows:
You can just click the **run.bat** file or use command:
```powershell
./run.bat
```
(If you're in Windows, you may meet an "[ERROR] Failed to prepare dependencies!“ 
or success but nothing is displayed while you first run this script.
You can try to run it again and our app is expected to be launched)

This script will:
- Automatically download JavaFX dependencies

- Automatically download Bert model for classification
- Build the project

- Launch the application

### 🔧 Technical Details

#### JavaFX Installation Paths

The script installs JavaFX SDK to these locations:

| OS      | Installation Path                                  | Download URL Pattern                                                                 |
|---------|----------------------------------------------------|-------------------------------------------------------------------------------------|
| Linux   | `/opt/javafx-sdk-{version}`                        | `https://download2.gluonhq.com/openjfx/{version}/openjfx-{version}_linux-x64_bin-sdk.zip`     |
| macOS   | `/Library/Java/JavaVirtualMachines/javafx-sdk-{version}` | `https://download2.gluonhq.com/openjfx/{version}/openjfx-{version}_mac-x64_bin-sdk.zip`   |                     
| Windows | `C:\Program Files\javafx-sdk-{version}`            | `https://download2.gluonhq.com/openjfx/{version}/openjfx-{version}_windows-x64_bin-sdk.zip` |                      

MODEL_URL="https://huggingface.co/softfish666/bert_transaction_classifer/resolve/main/bert_transaction_categorization.onnx"

#### Dependencies

All required dependencies will be automatically installed during the Maven build process.

## Project Structure

```text
|-- src
|   |-- main
|   |   |-- java
|   |   |   |-- AIUtilities    # Implementation of transaction classification and 
|   |   |   |   |             Expense predication. By Yu Ruan        
|   |   |   |   |-- classification
|   |   |   |   `-- prediction
|   |   |   |-- Backend        # Core business logic - manages ledgers and processes ledger data. By Zhou Fang ,CanYang Yue and Shang Shi.
|   |   |   |-- DataProcessor  # Data processing utilities. By Zhou Fang and CanYang Yue.
|   |   |   |-- com            # Implementation of Frontend Java code. By Shang Shi and Jiayi Du
|   |   |   |   `-- myapp
|   |   |   |       |-- config
|   |   |   |       |-- controller
|   |   |   |       `-- util
|   |   |   `-- detectorTools  # Implementation of Anomalies Detection to finance data. By Tianhao Yang
|   |   |-- output
|   |   `-- resources
|   |       |-- Tokenizer      # Bert model use this
|   |       |-- Transactions_Record_CSV  # User-imported CSV billing files
|   |       |-- Transactions_Record_XML  # XML files converted from user-imported CSV billing files
|   |       |-- css
|   |       |-- fourthlevel_xml          # Level-4 directory: Stores categorized XML files
|   |       |-- fxml
|   |       |-- images
|   |       |-- lang                     # Language configuration files
|   |       |-- secondlevel_json         # Level-2 directory: Ledger data storage
|   |       |   `-- images
|   |       |-- thirdlevel_json          # Level-3 directory: Financial data storage
|   |       `-- xml
|   `-- test                   # Unit tests
|       `-- java
|           |-- AIUtilities              
|           |   |-- classification
|           |   `-- prediction
|           |-- DataProcessor             
|           `-- detectorTools
```