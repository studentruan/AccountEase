package AIUtilities.prediction;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

/**
 * ARIMA (AutoRegressive Integrated Moving Average) model for time series forecasting.
 * This class supports both single-step and multi-step prediction with square root transformation
 * to prevent negative values, differencing to achieve stationarity, and ARMA parameter estimation.
 */
public class ARIMAModel {
    private final double[] originalData;
    private final int period;
    private final int p; // AR order
    private final int q; // MA order
    private double[] arCoefficients;
    private double[] maCoefficients;

    /**
     * Constructs an ARIMA model with the given data and parameters.
     * Applies square root transformation to original data to prevent negative predictions.
     *
     * @param data   The original time series data.
     * @param period The differencing period.
     * @param p      The order of the autoregressive (AR) part.
     * @param q      The order of the moving average (MA) part.
     */
    public ARIMAModel(double[] data, int period, int p, int q) {
        this.originalData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            this.originalData[i] = Math.sqrt(data[i]); // Square root transform to avoid negative values
        }
        this.period = period;
        this.p = p;
        this.q = q;

        // Parameter validation
        if (p < 0 || q < 0) {
            throw new IllegalArgumentException("p and q must be non-negative");
        }
        if (period < 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
    }

    /**
     * Performs one-step ahead prediction using ARIMA model.
     *
     * @return The predicted next value (after inverse square root transformation).
     */
    public int predict() {
        if (this.originalData.length <= 0) {
            throw new IllegalArgumentException("There must be some input data");
        }

        // 1. Apply differencing
        double[] diffData = preDealDiff(period);

        // 2. Compute AR and MA coefficients
        calculateCoefficients(diffData);

        // 3. Predict differenced value
        int predictDiff = predictValue(p, q, period);

        // 4. Invert differencing
        int results = aftDeal(predictDiff, period);

        // Invert square root transformation
        return (int) Math.pow(results, 2);
    }

    /**
     * Performs multi-step prediction using ARIMA model.
     *
     * @param steps Number of future steps to predict.
     * @return An array of predicted values (after inverse square root transformation).
     */
    public int[] predict(int steps) {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }

        if (this.originalData.length <= 0) {
            throw new IllegalArgumentException("There must be some input data");
        }

        int[] predictions = new int[steps];
        double[] extendedData = originalData.clone();

        for (int i = 0; i < steps; i++) {
            // 1. Apply differencing
            double[] diffData = preDealDiff(period);

            // 2. Compute AR and MA coefficients
            calculateCoefficients(diffData);

            // 3. Predict differenced value
            int predictDiff = predictValue(p, q, period);

            // 4. Invert differencing
            int prediction = aftDeal(predictDiff, period);
            predictions[i] = prediction;

            // 5. Append prediction for next-step use
            extendedData = Arrays.copyOf(extendedData, extendedData.length + 1);
            extendedData[extendedData.length - 1] = prediction;
        }

        // Invert square root transformation
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = (int) Math.pow(predictions[i], 2);
        }

        return predictions;
    }

    /**
     * Calculates AR or MA or ARMA model coefficients based on differenced data.
     *
     * @param diffData the differenced data series
     */
    private void calculateCoefficients(double[] diffData) {
        if (p > 0 && q > 0) {
            // ARMA model
            ARMAModel arma = new ARMAModel(diffData, p, q);
            Vector<double[]> coe = arma.solveCoeOfARMA();
            this.arCoefficients = coe.get(0);
            this.maCoefficients = coe.get(1);
        } else if (p > 0) {
            // Pure AR model
            ARModel ar = new ARModel(diffData, p);
            Vector<double[]> coe = ar.solveCoeOfAR();
            this.arCoefficients = coe.get(0);
        } else if (q > 0) {
            // Pure MA model
            MAModel ma = new MAModel(diffData, q);
            Vector<double[]> coe = ma.solveCoeOfMA();
            this.maCoefficients = coe.get(0);
        } else {
            throw new IllegalStateException("Both p and q cannot be zero");
        }
    }

    /**
     * Handles differencing process based on the specified period.
     *
     * @param period the differencing period (0: none, 1: first-order, >1: seasonal)
     * @return differenced data
     */
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

    /**
     * Applies first-order differencing to the input data.
     *
     * @param preData the original data array
     * @return first-order differenced data
     */
    private double[] preFirDiff(double[] preData) {
        double[] tmpData = new double[preData.length - 1];
        for (int i = 0; i < preData.length - 1; ++i) {
            tmpData[i] = preData[i + 1] - preData[i];
        }
        return tmpData;
    }

    /**
     * Applies seasonal differencing with fixed season length (7).
     *
     * @param preData the original data array
     * @return seasonally differenced data
     */
    private double[] preSeasonDiff(double[] preData) {
        double[] tmpData = new double[preData.length - 7];
        for (int i = 0; i < preData.length - 7; ++i) {
            tmpData[i] = preData[i + 7] - preData[i];
        }
        return tmpData;
    }

    /**
     * Inverse differencing to restore the original scale after prediction.
     *
     * @param predictValue the predicted value in differenced form
     * @param period the differencing period
     * @return value after inverse differencing
     */
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

    /**
     * Predicts the next differenced value using AR, MA or ARMA model.
     *
     * @param p AR order
     * @param q MA order
     * @param period differencing period
     * @return predicted differenced value
     */
    private int predictValue(int p, int q, int period) {
        double[] data = preDealDiff(period);
        int n = data.length;
        int predict = 0;
        double tmpAR = 0.0, tmpMA = 0.0;
        double[] errData = new double[q + 1];
        Random random = new Random();

        if (p == 0) {
            // MA model
            for (int k = q; k < n; ++k) {
                tmpMA = 0;
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoefficients[i] * errData[i];
                }
                // Update noise
                for (int j = q; j > 0; --j) {
                    errData[j] = errData[j - 1];
                }
                errData[0] = random.nextGaussian() * Math.sqrt(maCoefficients[0]);
            }
            predict = (int) tmpMA;
        } else if (q == 0) {
            // AR model
            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoefficients[i] * data[k - i - 1];
                }
            }
            predict = (int) tmpAR;
        } else {
            // ARMA model
            for (int k = p; k < n; ++k) {
                tmpAR = 0;
                tmpMA = 0;
                for (int i = 0; i < p; ++i) {
                    tmpAR += arCoefficients[i] * data[k - i - 1];
                }
                for (int i = 1; i <= q; ++i) {
                    tmpMA += maCoefficients[i] * errData[i];
                }
                // Update noise
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