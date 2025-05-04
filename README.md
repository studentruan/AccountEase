# AccountEase

## AI-side Function Implementation: Transaction Classification and Expense Forecasting

Date Modified: 1 Mar

Version: 3.0

by Ruan Yu

### Transaction Classification

The input is an XML file, structured as a series of `<transaction>` tags, where each transaction has the following format:

```xml
<transaction>
    <id>250301001</id>
    <date>2025/03/01</date>
    <counterparty>Freelance</counterparty>
    <product>Consulting Fee</product>
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
Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json"); //Path to the merchant description
TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

// 2. Parse XML file
String xmlFilePath = "src/main/resources/transactions.xml"; // Path to transaction record file
List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

// 3. Batch classification
Map<Transaction, Map<String,String>> categorized = classifier.classifyBatch(transactions);
```
The final result is stored in a dictionary with each transaction and its classified type.

If you want to get the category of each transaction, you can do this:
```java
categorized.forEach((tx, cate) -> {
    String enrichedText = cate.keySet().iterator().next();   // input text to the model
    String predictedCategory = cate.get(enrichedText);       // category
});
```

If you want to store the result in another XML file, then you can do this:
```java
ClassificationXmlWriter.writeClassifications(xmlFilePath, "src/main/output/classified_transactions.xml" ,categorized);
```
And the new XML file will have an additional tag <class> like this:
```xml
<transaction>
    <id>250301002</id>
    <date>2025/03/01</date>
    <counterparty>Salary</counterparty>
    <product>Monthly Salary</product>
    <type>Income</type>
    <amount>9090.3</amount>
    <class>Salary</class>
</transaction>
```

#### 2.	Classification of a Single Transaction Description (used when inputting a transaction manually)

```java
// Single classification (e.g., for manually entered data)
String transaction = "Some description";
Map<String,String> category = classifier.classify(transaction);
```
The value of this map is the category

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

## My Test Methods

This section includes comprehensive unit tests to ensure the correctness of transaction classification and expense prediction.

### Tested Components

The following core components are tested:

•	TransactionClassifier: Verifies correct classification for both normal and edge-case transactions.

•	TransactionXmlParser: Ensures proper parsing of input transaction XML.

•	ClassificationXmlWriter: Verifies output XML is correctly structured and matches classification results.

•   ARIMAModel： Ensures that the ARIMA-based forecasting model performs accurately and robustly under various input conditions

### Test Scenarios

The two test class  covers:
#### 1.	Full Classification Workflow
•	Parse transactions from XML

•	Perform batch classification using BERT-based model

•	Write categorized results into a new XML

•	Validate the written XML file structure and content

#### 2.	Edge Cases
•	Empty Input: Ensures no crash or incorrect output for an empty transaction list.

•	Corrupted Input: Validates classification still returns a result even for empty or malformed transaction text.

#### 3.	Valid Data Forecasting
Generates random historical expense data. Performs multi-step forecasts using the ARIMA model 
Validates that:
-	Forecast array is not null
-	Output length matches the number of forecast steps
-	All predicted values are non-negative
#### 4.	Invalid Data Handling

Tests behavior with:
- Empty input time series
- Negative forecast step count
- Ensures the model throws IllegalArgumentException with clear error messages



