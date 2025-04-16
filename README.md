明白了，以下是完整的英文翻译，保持了你原来的 Markdown 格式：

# AICount

## AI-side Function Implementation: Transaction Classification + Expense Forecasting

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

