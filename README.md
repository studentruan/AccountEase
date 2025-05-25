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
git clone --depth 1 https://github.com/studentruan/AccountEase.git
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
|   |   |   |-- Backend        #
|   |   |   |-- DataProcessor  #
|   |   |   |-- com            # Implementation of Frontend Java code. By Shang Shi and Jiayi Du
|   |   |   |   `-- myapp
|   |   |   |       |-- config
|   |   |   |       |-- controller
|   |   |   |       `-- util
|   |   |   `-- detectorTools  # Implementation of Anomalies Detection to finance data. By Tianhao Yang
|   |   |-- output
|   |   `-- resources
|   |       |-- Tokenizer      # Bert model use this
|   |       |-- Transactions_Record_CSV  # 
|   |       |-- Transactions_Record_XML  #
|   |       |-- css
|   |       |-- fourthlevel_xml          #
|   |       |-- fxml
|   |       |-- images
|   |       |-- lang                     #
|   |       |-- secondlevel_json         #
|   |       |   `-- images
|   |       |-- thirdlevel_json          #
|   |       `-- xml
|   `-- test
|       `-- java
|           |-- AIUtilities              
|           |   |-- classification
|           |   `-- prediction
|           |-- DataProcessor             
|           `-- detectorTools
```