# AccountEase

# 中文
## 核心功能模块
### 1.混合阈值检测系统：
    采用统计方法(IQR)与核密度估计(KDE)的加权融合策略，基准阈值公式为：
      Q3 + 1.5*IQR $  与 $ 0.9*min(σ,IQR/1.34)*n^-0.2 的动态带宽计算，其中IQR采用插值法来降低对数据量的基本要求。再通过时间敏感调节器降低周末/节假日的灵敏度。
      最终加权得到混合阈值
      hybirdThreshold = 0.4 * dynamicThreshold + 0.6 * kde.estimateDensity(amount)
### 2.多维度数据处理
    标准化预处理：Z-score归一化消除量纲差异
    白名单过滤：支持指定日期豁免检测（如促销活动）
    异构数据支持：自动识别Transaction对象与原始数值数据4
### 3.核函数计算
内置高斯核函数计算和带宽调节的核函数计算两种方法，为异常值检测提供了方便的工具

## 快速接入指南
```java

import DataProcessor.TransactionAnalyzer;
import static detectorTools.OutlierDetector.detectAnomalies;

// 数据加载
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// 异常检测 holidayList 为String类型的列表，元素里面储存MM-DD格式的日期作为节日，无节日参数中不输入holidayList即可
Map<String, Double> anomalies =  detectAnomalies(analyzer, holidayList);

// 输出结果   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));

```

# English Version
## Core Features
### 1.Hybrid Threshold Detection
    Uses a weighted fusion strategy combining statistical methods (IQR) and kernel density estimation (KDE). The baseline threshold formula is:  
        Q3 + 1.5*IQR and dynamic bandwidth calculation 0.9*min(σ,IQR/1.34)*n^-0.2, 
        where IQR uses interpolation to reduce basic requirements for data volume. 
        Then reduces sensitivity on weekends/holidays through a time-sensitive adjuster.
    The final weighted hybrid threshold is:  
        hybridThreshold = 0.4 * dynamicThreshold + 0.6 * kde.estimateDensity(amount)
### 2.Multi-dimensional Processing
    Normalization: Z-score standardization
    Whitelist Filtering: Date-based exemption (e.g., promotions)
    Heterogeneous Data Support: Auto-recognizes Transaction objects4
### 3.Kernel function calculation
Two methods are provided for anomaly detection, namely the calculation based on built-in Gaussian kernel function and the calculation of 
kernel function with bandwidth adjustment. These methods offer convenient tools for anomaly detection.
## Quick Integration Guide

```java
import DataProcessor.TransactionAnalyzer;
import static detectorTools.OutlierDetector.detectAnomalies;

// Load data
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// Detect anomalies ,holidayList is a list of type String, where elements store dates in MM-DD format as holidays. 
// If there are no holidays, simply omit the holidayList parameter in the input.
Map<String, Double> anomalies =  detectAnomalies(analyzer, holidayList);

// Output results   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
```
