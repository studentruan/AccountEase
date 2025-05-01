package AIUtilities.prediction;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class ARIMAModel {
    private final double[] originalData;
    private final int period;
    private final int p; // 固定AR阶数
    private final int q; // 固定MA阶数
    private double[] arCoefficients;
    private double[] maCoefficients;

    public ARIMAModel(double[] data, int period, int p, int q) {

        //防止负数值预测，采取平方根变换
        this.originalData = new double[data.length];
        for(int i = 0;i < data.length;i++) {
            this.originalData[i] = Math.sqrt(data[i]);
        }
        this.period = period;
        this.p = p;
        this.q = q;

        // 参数校验
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException("p and q must be non-negative");
        }
        if (period < 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
    }

    // 单步预测
    public int predict() {
        // 1. 差分处理
        double[] diffData = preDealDiff(period);

        // 2. 计算模型系数
        calculateCoefficients(diffData);

        // 3. 预测差分值
        int predictDiff = predictValue(p, q, period);

        // 4. 逆差分处理
        int results = aftDeal(predictDiff, period);

        return (int) Math.pow(results, 2);
    }

    //多步预测
    public int[] predict(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }

        int[] predictions = new int[steps];
        double[] extendedData = originalData.clone();

        for (int i = 0; i < steps; i++) {
            // 1. 差分处理
            double[] diffData = preDealDiff(period);

            // 2. 计算模型系数
            calculateCoefficients(diffData);

            // 3. 预测差分值
            int predictDiff = predictValue(p, q, period);

            // 4. 逆差分处理
            int prediction = aftDeal(predictDiff, period);
            predictions[i] = prediction;

            // 5. 将预测值加入数据末尾，用于下一步预测
            extendedData = Arrays.copyOf(extendedData, extendedData.length + 1);
            extendedData[extendedData.length - 1] = prediction;
        }

        //对预测值作逆平方根变换
        for(int i = 0;i < predictions.length; i++) {
            predictions[i] = (int) Math.pow(predictions[i], 2);
        }

        return predictions;
    }

    // 计算ARMA系数
    private void calculateCoefficients(double[] diffData) {
        if (p > 0 && q > 0) {
            // ARMA模型
            ARMAModel arma = new ARMAModel(diffData, p, q);
            Vector<double[]> coe = arma.solveCoeOfARMA();
            this.arCoefficients = coe.get(0);
            this.maCoefficients = coe.get(1);
        } else if (p > 0) {
            // 纯AR模型
            ARModel ar = new ARModel(diffData, p);
            Vector<double[]> coe = ar.solveCoeOfAR();
            this.arCoefficients = coe.get(0);
        } else if (q > 0) {
            // 纯MA模型
            MAModel ma = new MAModel(diffData, q);
            Vector<double[]> coe = ma.solveCoeOfMA();
            this.maCoefficients = coe.get(0);
        } else {
            throw new IllegalStateException("Both p and q cannot be zero");
        }
    }

    // 差分处理
    private double[] preDealDiff(int period) {
        if (period >= originalData.length - 1) {
            period = 0;
        }

        switch (period) {
            case 0:
                return this.originalData;
            case 1:
                return preFirDiff(this.originalData);
            default:
                return preSeasonDiff(this.originalData);
        }
    }

    // 一阶差分
    private double[] preFirDiff(double[] preData) {
        double[] tmpData = new double[preData.length - 1];
        for (int i = 0; i < preData.length - 1; ++i) {
            tmpData[i] = preData[i + 1] - preData[i];
        }
        return tmpData;
    }

    // 季节性差分
    private double[] preSeasonDiff(double[] preData) {
        double[] tmpData = new double[preData.length - 7];
        for (int i = 0; i < preData.length - 7; ++i) {
            tmpData[i] = preData[i + 7] - preData[i];
        }
        return tmpData;
    }

    // 逆差分处理
    private int aftDeal(int predictValue, int period) {
        if (period >= originalData.length) {
            period = 0;
        }

        switch (period) {
            case 0:
                return predictValue;
            case 1:
                return (int)(predictValue + originalData[originalData.length - 1]);
            default:
                return (int)(predictValue + originalData[originalData.length - 7]);
        }
    }

    // 预测差分值
    private int predictValue(int p, int q, int period) {
        double[] data = preDealDiff(period);
        int n = data.length;
        int predict = 0;
        double tmpAR = 0.0, tmpMA = 0.0;
        double[] errData = new double[q + 1];
        Random random = new Random();

        if (p == 0) {
            // 纯MA模型
            for (int k = q; k < n; ++k) {
                tmpMA = 0;
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoefficients[i] * errData[i];
                }
                // 更新噪声
                for (int j = q; j > 0; --j) {
                    errData[j] = errData[j - 1];
                }
                errData[0] = random.nextGaussian() * Math.sqrt(maCoefficients[0]);
            }
            predict = (int) tmpMA;
        } else if (q == 0) {
            // 纯AR模型
            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoefficients[i] * data[k - i - 1];
                }
            }
            predict = (int) tmpAR;
        } else {
            // ARMA模型
            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                tmpMA = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoefficients[i] * data[k - i - 1];
                }
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoefficients[i] * errData[i];
                }
                // 更新噪声
                for (int j = q; j > 0; --j) {
                    errData[j] = errData[j - 1];
                }
                errData[0] = random.nextGaussian() * Math.sqrt(maCoefficients[0]);
            }
            predict = (int) (tmpAR + tmpMA);
        }
        return predict;
    }
}