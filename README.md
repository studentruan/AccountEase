# AICount

## AI端功能实现：交易分类 + 支出预测

更改日期：4.15   
版本：2.1

### 交易分类

输入为xml文件，结构为一系列`<transaction>`标签的交易记录，其中具体的构成结构为：

```xml
<transaction>
    <date>2025/03/01</date>
    <counterparty>Freelance</counterparty>
    <product>Project Fee</product>
    <type>Income</type>
    <amount>5204.03</amount>
</transaction>
```

使用预训练Bert文本分类模型及java的xml解析工具，可以实现对交易描述文本的自动化25种交易类型分类。

调用方式如下：

1）批量分类（用于对xml文件中的批量交易数据分类）
```java
// 1. 初始化分类器
Path tokenizerDir = Paths.get("src/main/resources/Tokenizer"); // 模型Tokenizer的路径
String modelPath = "src/main/resources/bert_transaction_categorization.onnx"; // 模型路径
TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath);

// 2. 解析 XML 文件
String xmlFilePath = "src/main/resources/transactions.xml"; //交易记录文件路径
List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

// 3. 批量分类
Map<Transaction, String> categorized = classifier.classifyBatch(transactions);
```

最终使用字典保存每个交易的分类结果。

2）单个交易描述的分类（用于手动输入交易数据时的分类）
```java
//单个分类（比如手动输入数据）
String transaction = "";
String category = classifier.classify(transaction);
```

下一步实现功能：xml结构添加一个<id>作为交易记录的唯一标识，将分类结果结合之前的xml数据，写入到一个新的xml文件中。

### 支出预测
输入为一个double类型的数组，长度任意，但为了预测性能应当进行截断。 其代表一段时间的每日支出。

使用ARIMA模型，对支出数据进行预测，调用方式如下：

```java
// ARIMAModel(inputData, period(若数据有周期性因素),p,q)
ARIMAModel model = new ARIMAModel(testData, 7, 4, 5);

// 预测未来任意天
int steps = 20;
int[] forecasts = model.predict(steps);
```

返回预测结果数组，长度=steps。

下一步实现功能：后处理预测结果，对偏移太多的进行截断处理（已实现）





