package detectorTools;

public class KernelUtils {
    // 高斯核函数（网页2、网页3公式转Java实现）
    public static double gaussianKernel(double x) {
        return (1.0 / Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * x * x);
    }

    // 带带宽的核函数计算（网页4参数化扩展）
    public static double scaledKernel(double x, double bandwidth) {
        return gaussianKernel(x / bandwidth) / bandwidth;
    }
}