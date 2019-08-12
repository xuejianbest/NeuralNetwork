package com.xjb.NN;

import com.xjb.util.DataUtil;

public abstract class Perceptron {
  private double[] w_arr; // 此神经元的参数
  protected double[] i_arr; // 此神经元的输入值

  private double[] dw_arr; // 此神经元的参数的偏导
  protected double[] di_arr; // 此神经元的输入的偏导

  private double x; // 此神经元的输入和参数乘积的和
  protected double out; // 此神经元的输出，即：activation(x)

  private Perceptron[] nexts = null; // 此神经元连接的下一层神经元
  protected int index = 0; // 此神经元在本层的编号

  /**
   * 构造函数，主要是随机生成指定数量的本神经元的参数
   * 
   * @param input_c
   *          参数数量，等于输入数量
   */
  public Perceptron(int input_c) {
    w_arr = new double[input_c];
    i_arr = new double[input_c];
    for (int i = 0; i < input_c; i++) {
      w_arr[i] = DataUtil.randomNum(-0.05, 0.05);
    }
  }

  protected Perceptron() {
  }

  public Perceptron[] getNext() {
    return nexts;
  }

  public double[] getDw_arr() {
    return dw_arr;
  }

  public double[] getW_arr() {
    return w_arr;
  }

  public void setW_arr(double[] w_arr) {
    if (this.w_arr.length != w_arr.length) {
      throw new RuntimeException("setW_arr error: 权重数量不正确!");
    }
    this.w_arr = w_arr;
  }

  public double[] getDi_arr() {
    return di_arr;
  }

  public int args_c() {
    return w_arr.length;
  }

  public void link(Perceptron[] nexts, int index) {
    this.nexts = nexts;
    this.index = index;
  }

  public void setIn(int index, double in_value) {
    this.i_arr[index] = in_value;
  }

  public void setIn(double[] in_values) {
    for (int i = 0; i < in_values.length; i++) {
      setIn(i, in_values[i]);
    }
  }

  /**
   * activation function，激活函数
   * 
   * @param x
   *          输入值
   * @return 输出out
   */
  protected abstract double activation(double x);

  /**
   * 激活函数的导函数
   * 
   * @param x
   *          输入值
   * @return 输出x点的导数值
   */
  protected abstract double dout_x(double x);

  private double dx_i(int n) {
    return w_arr[n];
  }

  private double dx_w(int n) {
    return i_arr[n];
  }

  /**
   * 反向传播，调整参数值
   * 
   * @param step
   */
  public void dwork(double step) {
    dw_arr = new double[w_arr.length];
    di_arr = new double[i_arr.length];
    double DO_X = dout_x(x);
    for (int i = 0; i < dw_arr.length; i++) {
      dw_arr[i] = di_arr[i] = 0;
      for (Perceptron next : nexts) {
        double dcost_out = next.getDi_arr()[index];

        dw_arr[i] += dcost_out * DO_X * dx_w(i);
        di_arr[i] += dcost_out * DO_X * dx_i(i);
      }

      w_arr[i] -= step * dw_arr[i];
    }
  }

  /**
   * 反向传播，调整参数值。 只对输出层的神经元调用此方法，其他层调用dwork(double step)
   * 
   * @param dcost_out
   *          损失函数关于本神经元输出的导数值
   * @param step
   *          调整幅度
   */
  public void dwork(double dcost_out, double step) {
    dw_arr = new double[w_arr.length];
    di_arr = new double[i_arr.length];
    double DO_X = dout_x(x);
    for (int i = 0; i < dw_arr.length; i++) {
      dw_arr[i] = dcost_out * DO_X * dx_w(i);
      di_arr[i] = dcost_out * DO_X * dx_i(i);

      w_arr[i] -= step * dw_arr[i];
    }
  }

  /**
   * 激活神经元，往前传递信息
   */
  public void work() {
    this.x = 0.0;
    for (int i = 0; i < w_arr.length; i++) {
      this.x += this.w_arr[i] * this.i_arr[i];
    }

    this.out = activation(this.x);

    if (nexts != null) {
      for (Perceptron next : nexts) {
        next.setIn(index, out);
      }
    }
  }

  public double getOut() {
    return this.out;
  }

  @Override
  public String toString() {
    String res = "[";
    for (double w : w_arr) {
      res += w + ", ";
    }
    res = res.substring(0, res.length() - 2) + "], out=" + this.out;
    return res;
  }
}
