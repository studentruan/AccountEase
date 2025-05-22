/**
 * Contains kernel functions for density estimation.
 */

package detectorTools;

public class KernelUtils {
    /**
     * Standard Gaussian kernel function.
     * <p>
     * Formula: {@code (1/√(2π)) * e^(-x²/2)}
     *
     * @param x input value
     * @return kernel density at x
     */
    // 高斯核函数
    public static double gaussianKernel(double x) {
        return (1.0 / Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * x * x);
    }
    /**
     * Scaled kernel function incorporating bandwidth.
     * <p>
     * Implements bandwidth-adjusted kernel: {@code K(x/h) / h}
     *
     * @param x input value
     * @param bandwidth smoothing parameter
     * @return scaled kernel value
     */
    // 带带宽的核函数计算
    public static double scaledKernel(double x, double bandwidth) {
        return gaussianKernel(x / bandwidth) / bandwidth;
    }
}