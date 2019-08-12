package com.xjb.NN;

public class OutputPerceptron extends Perceptron {
  private double[] exp_i_arr; // 此神经元的输入值对应的exp(i)
  private double sum; // 此神经元的输入和参数乘积的和

  /**
   * 构造函数，主要是随机生成指定数量的本神经元的参数
   * 
   * @param input_c
   *          参数数量，等于输入数量
   */
  public OutputPerceptron(int input_c, int index) {
    super();
    i_arr = new double[input_c];
    di_arr = new double[input_c];
    exp_i_arr = new double[input_c];
    this.index = index;
  }

  /**
   * 反向传播，调整参数值
   * 
   * @param step
   */
  public void dwork(double dcost_out) {
    for (int i = 0; i < di_arr.length; i++) {
      double dout_i = 0;
      if (i == index) {
        dout_i = out * (1 - out);
      } else {
        dout_i = -out * (exp_i_arr[i] / sum);
      }

      di_arr[i] = dcost_out * dout_i;
    }
  }

  /**
   * 激活神经元，往前传递信息
   */
  public void work() {
    this.sum = 0.0;
    for (int i = 0; i < i_arr.length; i++) {
      double d = Math.exp(i_arr[i]);
      exp_i_arr[i] = d;
      sum += d;
    }

    this.out = exp_i_arr[index] / sum;
  }

  @Override
  public String toString() {
    String res = "[";
    for (double i : i_arr) {
      res += i + ", ";
    }
    res = res.substring(0, res.length() - 2) + "], index=" + index + ", out=" + this.out;
    return res;
  }

  @Override
  protected double activation(double x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected double dout_x(double x) {
    // TODO Auto-generated method stub
    return 0;
  }
}
