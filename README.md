# AccountEase

# 中文
## 核心功能模块
### 1.混合阈值检测系统：
    采用统计方法(IQR)与核密度估计(KDE)的加权融合策略，基准阈值公式为：
      Q3 + 1.5*IQR $  与 $ 0.9*min(σ,IQR/1.34)*n^-0.2 的动态带宽计算。通过时间敏感调节器自动调整周末(未完成)/节假日的检测灵敏度。
### 2.多维度数据处理
    标准化预处理：Z-score归一化消除量纲差异
    白名单过滤：支持指定日期豁免检测（如促销活动）
    异构数据支持：自动识别Transaction对象与原始数值数据4
### 3.核函数计算
内置高斯核函数计算和带宽调节的核函数计算两种方法，为异常值检测提供了方便的工具

## 快速接入指南
```java

import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

// 数据加载
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// 异常检测
Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);

// 输出结果   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));

```

# English Version
## Core Features
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
