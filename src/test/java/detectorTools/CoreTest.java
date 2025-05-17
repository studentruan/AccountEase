package detectorTools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CoreTest {
    @Test
    void testBandwidthCalculation() {
        List<Double> data = List.of(1.0, 2.0, 3.0, 4.0, 5.0);
        double bw = BandwidthCalculator.calculateBandwidth(data);
        /* 验证Silverman规则：0.9 * min(std, IQR/1.34) * n^(-1/5) 使用线性插值
               mean = 3.0   v=sqrt((4+1+0+1+4)/5)=sqrt(2)≈1.4142
                Q1: pos=(5-1)*0.25=1.0
                lowerIndex=1，upperIndex=1，fraction=0
                Q1 = sorted[1] = 2.0
                Q2: pos=(5-1)*0.75=3.0
                lowerIndex=3，upperIndex=3，fraction=0
                Q3 = sorted[3] = 4.0
                IQR=Q3−Q1=4.0−2.0=2.0
                0.9*min(1.4142, 2/1.34≈1.4925)*n^(-1/5)=0.9*1.3132*5^(-1/5)≈0.9225
        */
        Assertions.assertEquals(0.9225, bw, 0.01);
    }
    @Test
    void testGaussianKernel() {
        double value = KernelUtils.gaussianKernel(0);
        Assertions.assertEquals(1/Math.sqrt(2*Math.PI), value, 1e-6);
    }

    @Test
    void testDynamicThreshold() {
        List<Double> data = IntStream.rangeClosed(1, 100).asDoubleStream().boxed().collect(Collectors.toList());
        StatisticalThresholdCalculator calc = new StatisticalThresholdCalculator(data);
        Assertions.assertEquals(149.5, calc.calculateDynamicThreshold()); // Q3=75.5 + 1.5*IQR(49.5)
    }

}
