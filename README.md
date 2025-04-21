# AccountEase
This README is not complete

## Overall Project Description
JDK version: 21

fxml version: 21.0.7

We use Maven to build our project, so that other dependencies can be automatically downloaded

### Project Structure:

```text
|-- src
|   |-- main
|   |   |-- java
|   |   |   |-- AIUtilities # This part is implemented by Yu Ruan
|   |   |   |   |-- classification
|   |   |   |   |   |-- ClassificationXmlWriter.java
|   |   |   |   |   |-- DeepSeekONNXInference.java
|   |   |   |   |   |-- Transaction.java
|   |   |   |   |   |-- TransactionClassifier.java
|   |   |   |   |   `-- TransactionXmlParser.java
|   |   |   |   `-- prediction
|   |   |   |       |-- ARIMAModel.java
|   |   |   |       |-- ARMAMethod.java
|   |   |   |       |-- ARMAModel.java
|   |   |   |       |-- ARModel.java
|   |   |   |       |-- MAModel.java
|   |   |   |       `-- Test.java
|   |   |   |-- DataProcessor # This part is implemented by Zhou Fang and Canyang Yue
|   |   |   |   |-- CsvToXmlConverter.java
|   |   |   |   |-- DailyTransactionProcessor.java
|   |   |   |   |-- TransactionAnalyzer.java
|   |   |   |   `-- TransactionDataProcessor.java
|   |   |   |-- Main.java # Just a test entry
|   |   |   |-- com # This frontend part is implemented by Shang Shi and Jiayi Du
|   |   |   |   `-- myapp
|   |   |   |       |-- Main.java # This is the entry of our project
|   |   |   |       |-- controller
|   |   |   |       |   |-- AppearanceSettingsController.java
|   |   |   |       |   |-- ...
|   |   |   |       `-- model
|   |   |   |           |-- FinanceData.java
|   |   |   |           |-- Ledger.java
|   |   |   |           |-- LedgerManager.java
|   |   |   |           |-- TransactionLoader.java
|   |   |   |           `-- Transactions.java
|   |   |   `-- detectorTools # This part is implemented by Tianhao Yang
|   |   |       |-- AdvancedAnomalyDetector.java
|   |   |       |-- BandwidthCalculator.java
|   |   |       |-- KernelUtils.java
|   |   |       |-- OutlierDetector.java
|   |   |       |-- PureJavaKDEAnomalyDetector.java
|   |   |       |-- StatisticalThresholdCalculator.java
|   |   |       `-- Transaction.java
|   |   |-- output  # The storage of transactions record including 
|   |   |   |         classification of each transaction
|   |   |   `-- classified_transactions.xml
|   |   `-- resources
|   |       |-- Records.xml # Maybe useless
|   |       |-- Tokenizer # The tokenizer corresponds to the onnx model
|   |       |   |-- special_tokens_map.json
|   |       |   |-- tokenizer.json
|   |       |   |-- tokenizer_config.json
|   |       |   `-- vocab.txt
|   |       |-- bert_transaction_categorization.onnx # This is the model we used
|   |       |-- css
|   |       |   `-- style.css
|   |       |-- detailed_monthly_statement.csv # This is the raw csv record
|   |       |-- fxml
|   |       |   |-- ...
|   |       |-- images
|   |       |   |-- ...
|   |       |-- monthly_statement_custom_id.csv 
|   |       |-- transactions.xml  # This is the raw transaction record
|   |       `-- xml
|   |           |-- test.xml
|   |           `-- transactions1.xml


```

## AI-side Function Implementation: Transaction Classification, Expense Forecasting and Outlier Detection

Date Modified: 16 April

Version: 2.1

### Transaction Classification

The input is an XML file, structured as a series of `<transaction>` tags, where each transaction has the following format:

```xml
<transaction>
    <date>2025/03/01</date>
    <counterparty>Freelance</counterparty>
    <product>Project Fee</product>
    <type>Income</type>
    <amount>5204.03</amount>
</transaction>
```

Using a pre-trained Bert text classification model along with Java XML parsing tools, automatic classification into 25 transaction types can be achieved based on the transaction description text.

Usage is as follows:
#### 1.	Batch Classification (used for classifying batch transaction data from an XML file)

```java
// 1. Initialize classifier
Path tokenizerDir = Paths.get("src/main/resources/Tokenizer"); // Path to the model's tokenizer
String modelPath = "src/main/resources/bert_transaction_categorization.onnx"; // Path to the model
TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath);

// 2. Parse XML file
String xmlFilePath = "src/main/resources/transactions.xml"; // Path to transaction record file
List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

// 3. Batch classification
Map<Transaction, String> categorized = classifier.classifyBatch(transactions);
```

The final result is stored in a dictionary with each transaction and its classified type.

#### 2.	Classification of a Single Transaction Description (used when inputting a transaction manually)

```java
// Single classification (e.g., for manually entered data)
String transaction = "";
String category = classifier.classify(transaction);
```

Next step: Add an <id> field in the XML structure to serve as a unique identifier for each transaction. Combine the classification result with the original XML data and write it into a new XML file. (Implemented)

### Expense Forecasting

The input is an array of doubles with arbitrary length, but truncation is recommended for better prediction performance. It represents daily expenses over a period.

An ARIMA model is used to predict expense data. Usage is as follows:

```java
// ARIMAModel(inputData, period (if data has cyclic patterns), p, q)
ARIMAModel model = new ARIMAModel(testData, 7, 4, 5);

// Predict expenses for any number of future days
int steps = 20;
int[] forecasts = model.predict(steps);
```

The prediction result is an array of length = steps.

Next step: Post-process the prediction results to trim any values that deviate too much. (Implemented)

### Outlier Detection
### 1.Hybrid Threshold Detection
    Combines statistical method (IQR) with KDE through weighted fusion:
      Q3 + 1.5*IQR and dynamic bandwidth 0.9*min(σ,IQR/1.34)*n^-0.26. Time-sensitive adjustment for weekends(not implement)/holidays.
### 2.Multi-dimensional Processing
    Normalization: Z-score standardization
    Whitelist Filtering: Date-based exemption (e.g., promotions)
    Heterogeneous Data Support: Auto-recognizes Transaction objects4
### 3.Kernel function calculation
Two methods are provided for anomaly detection, namely the calculation based on built-in Gaussian kernel function and the calculation of
kernel function with bandwidth adjustment. These methods offer convenient tools for anomaly detection.
## Quick Integration Guide

```java
import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

// Load data
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// Detect anomalies
Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);

// Output results   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
```

## Local-Side Function Implementation: CSV to XML Conversion + Income & Expense Analysis
**Date Modified:** 20 April  
**Version:** 1.3

---

###  CSV to XML Converter

###  描述
该模块负责将 `.csv` 格式的账单文件转换为结构化的 `.xml` 文件，便于后续处理和展示。

###  输入
标准 CSV 文件，每行包含如下字段：

```
id,date,counterparty,product,type,amount
250201001,2025-03-01,Starbucks,coffee,Expense,28.00
250201002,2025-03-01,Company,salary,Income,5000.00
```

###  输出（XML 格式）

```xml
<transaction>
    <id>250201001</id>
    <date>2025/03/01</date>
    <counterparty>Starbucks</counterparty>
    <product>coffee</product>
    <type>Expense</type>
    <amount>28.00</amount>
</transaction>
```

###  使用方式（批处理转换）

```java
String csvPath = "C:/Users/ASUS/Desktop/CSVXML/detailed_monthly_statement.csv";
String xmlPath = "C:/Users/ASUS/Desktop/CSVXML/transactions1.xml";

CsvToXmlConverter.convert(csvPath, xmlPath);
```

---

### TransactionAnalyzer

###  描述
从 `.xml` 文件中加载交易记录，并输出每日 / 每月 / 每年净收支、每日总支出等统计结果。

###  输入
来自 XML 文件的交易结构，包含 `<transaction>` 标签。

###  输出示例：

```
 每日净收支:
2025-03-01: 4972.00

 每月净收支:
2025-03: 14600.00

 每年净收支:
2025: 78200.00

 每日支出:
2025-03-01: 28.00
```

###  使用方式

```java
TransactionAnalyzer analyzer = new TransactionAnalyzer("C:/.../transactions.xml");

analyzer.getDailySummary();           // 每日净收支
analyzer.getMonthlySummary();         // 每月净收支
analyzer.getYearlySummary();          // 每年净收支
analyzer.getExpenseDailySummary();    // 每日总支出
```

---

### ⚠️ 注意事项

- XML 文件中 `date` 字段会被标准化为 `yyyy/MM/dd` 格式；
- `Expense` 类型的金额会自动转换为负数进行计算；
- 分析器默认不包含分类等标签，仅处理金额与时间。


