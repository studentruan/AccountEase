package AIUtilities.prediction;

import java.util.Vector;

public class ARMAModel
{
    private double [] data = {};
    private int p;      //AR阶数
    private int q;      //MA阶数

    public ARMAModel(double [] data, int p, int q)
    {
        this.data = data;
        this.p = p;
        this.q = q;
    }

    public Vector<double []> solveCoeOfARMA()
    {
        Vector<double []>vec = new Vector<>();

        //ARMA模型
        double [] armaCoe = new ARMAMethod().computeARMACoe(this.data, this.p, this.q);
        //AR系数
        double [] arCoe = new double[this.p + 1];
        System.arraycopy(armaCoe, 0, arCoe, 0, arCoe.length);
        //MA系数
        double [] maCoe = new double[this.q + 1];
        System.arraycopy(armaCoe, (this.p + 1), maCoe, 0, maCoe.length);

        vec.add(arCoe);
        vec.add(maCoe);

        return vec;
    }
}
