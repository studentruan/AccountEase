package detectorTools;

public class KernelUtils {
    // 高斯核函数
    public static double gaussianKernel(double x) {
        return (1.0 / Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * x * x);
    }

    // 带带宽的核函数计算
    public static double scaledKernel(double x, double bandwidth) {
        return gaussianKernel(x / bandwidth) / bandwidth;
    }
}