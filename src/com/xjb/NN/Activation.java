package com.xjb.NN;

@SuppressWarnings("unused")
public class Activation {
  private double sigmoid(double x) {
    return 1.0 / (1.0 + Math.exp(-x));
  }

  private double dSigmoid(double x) {
    double e_x = Math.exp(-x);
    Double res = e_x / ((1 + e_x) * (1 + e_x));
    if (res.isNaN()) {
      res = 0.0;
    }
    return res;
  }

  double B = 0.6666;
  double A = 1.7159;

  private double tanh(double x) {
    return A * Math.tanh(B * x);
  }

  private double dTanh(double x) {
    double t = tanh(B * x);
    double dt = 1 - t * t;
    return A * B * dt;
  }

  private double reLu(double x) {
    return Math.max(x, 0.0);
  }

  private double dReLu(double x) {
    double res;
    if (x > 0.0) {
      res = 1.0;
    } else {
      res = 0.0;
    }
    return res;
  }
  
  public class MyPerceptron extends Perceptron {
    public MyPerceptron(int input_c) {
      super(input_c);
    }

    @Override
    protected double activation(double x) {
      return sigmoid(x);
    }

    @Override
    protected double dout_x(double x) {
      return dSigmoid(x);
    }

  }
}
