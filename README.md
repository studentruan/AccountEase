# AccountEase
核心功能模块
混合阈值检测系统
采用统计方法(IQR)与核密度估计(KDE)的加权融合策略，基准阈值公式为：
Q3 + 1.5*IQR 与 0.9*min(σ,IQR/1.34)*n^-0.2 的动态带宽计算6。通过时间敏感调节器自动调整周末/节假日的检测灵敏度3。
多维度数据处理
标准化预处理：Z-score归一化消除量纲差异
白名单过滤：支持指定日期豁免检测（如促销活动）
异构数据支持：自动识别Transaction对象与原始数值数据4
可视化诊断接口
内置KDE密度曲线绘制模块，可通过KernelUtils.gaussianKernel()生成概率分布图，支持异常点可视化标注。
快速接入指南

import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

// 数据加载
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// 异常检测
Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);

// 输出结果   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));

English Version
Core Features
Hybrid Threshold Detection
Combines statistical method (IQR) with KDE through weighted fusion:
Q3 + 1.5*IQR and dynamic bandwidth 0.9*min(σ,IQR/1.34)*n^-0.26. Time-sensitive adjustment for weekends/holidays3.
Multi-dimensional Processing
Normalization: Z-score standardization
Whitelist Filtering: Date-based exemption (e.g., promotions)
Heterogeneous Data Support: Auto-recognizes Transaction objects4
Visual Diagnostics
Built-in KDE density plotter via KernelUtils.gaussianKernel(), supports anomaly visualization.
Quick Integration

import TransactionAnalyzer.TransactionAnalyzer;
import detectorTools.AdvancedAnomalyDetector;

// Load data
TransactionAnalyzer analyzer = new TransactionAnalyzer("path/to/transaction.xml");

// Detect anomalies
Map<String, Double> anomalies =  outputOutliers(detectAnomalies(analyzer), 1.5);

// Output results   date: cvalue
anomalies.forEach((date, cvalue) -> System.out.printf(date + ": %.2f%n", cvalue));
