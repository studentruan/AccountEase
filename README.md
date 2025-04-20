
### `AICount`
**Local-Side Function Implementation: CSV to XML Conversion + Income & Expense Analysis**  
**Date Modified:** 20 April  
**Version:** 1.3

---

##  CSV to XML Converter

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

##  TransactionAnalyzer

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

## ⚠️ 注意事项

- XML 文件中 `date` 字段会被标准化为 `yyyy/MM/dd` 格式；
- `Expense` 类型的金额会自动转换为负数进行计算；
- 分析器默认不包含分类等标签，仅处理金额与时间。
