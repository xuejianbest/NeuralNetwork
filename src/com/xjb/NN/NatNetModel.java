package com.xjb.NN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.xjb.Test.TestImgR;
import com.xjb.util.DataUtil;
import com.xjb.util.Debug;

public class NatNetModel {
  private ArrayList<Perceptron[]> net = new ArrayList<>();

  private double stepSize = 0.5; // 训练速度
  private int maxIter = 100; // 迭代次数

  private int inputSize; // 输入层神经元数量

  public NatNetModel setStepSize(double stepSize) {
    this.stepSize = stepSize;
    return this;
  }

  public NatNetModel setMaxIter(int maxIter) {
    this.maxIter = maxIter;
    return this;
  }

  public int getInputSize() {
    return inputSize;
  }

  /**
   * 设置神经网络层数，包括输入层
   * 
   * @param layers
   * @return
   */
  private NatNetModel setLayers(int[] layers) {
    for (int i = 1; i < layers.length; i++) {
      Perceptron[] layerP = new Perceptron[layers[i]];
      for (int index = 0; index < layers[i]; index++) {
        layerP[index] = new Activation().new ReLuPerceptron(layers[i - 1]);
      }
      net.add(layerP);

      if (i == layers.length - 1) {
        Perceptron[] outputP = new OutputPerceptron[layers[i]];
        int outputPIndex = 0;
        for (int index = 0; index < layers[i]; index++) {
          outputP[index] = new OutputPerceptron(layers[i], outputPIndex++);
        }
        net.add(outputP);
      }
    }

    for (int i = 0; i < net.size() - 1; i++) {
      Perceptron[] layerP = net.get(i);
      Perceptron[] next_layerP = net.get(i + 1);
      for (int index = 0; index < layerP.length; index++) {
        layerP[index].link(next_layerP, index);
      }
    }

    this.inputSize = layers[0];
    return this;
  }

  public NatNetModel(int[] layers) {
    this.setLayers(layers);
  }

  private NatNetModel(int[] layers, int maxIter, double stepSize) {
    this.setLayers(layers);
    this.setMaxIter(maxIter);
    this.setStepSize(stepSize);
  }

  /**
   * 获取神经网络输出
   * 
   * @return
   */
  private double[] getOut() {
    Perceptron[] ps = net.get(net.size() - 1);
    double[] outs = new double[ps.length];
    for (int i = 0; i < ps.length; i++) {
      outs[i] = ps[i].getOut();
    }
    return outs;
  }

  /**
   * 损失函数，计算一组输出的值
   * 
   * @param y
   *          样本标签
   * @param out
   *          神经网络输出
   * @return
   */
  public double cost(double[] y, double[] out) {
    double err = 0;
    for (int i = 0; i < y.length; i++) {
      double delta = y[i] - out[i];
      err += delta * delta;
    }
    return err / 2;
  }

  /**
   * 损失函数，计算多组输出的值
   * 
   * @param y
   *          样本标签
   * @param out
   *          神经网络输出
   * @return
   */
  public double cost(double y[][], double out[][]) {
    double err = 0;
    for (int i = 0; i < y.length; i++) {
      err += cost(y[i], out[i]);
    }
    return err / y.length;
  }

  /**
   * 损失函数的导函数，计算一组输出的单一输出的值
   * 
   * @param y
   *          样本标签
   * @param out
   *          神经网络输出
   * @return
   */
  private double dcost_out(double y, double out) {
    return out - y;
  }

  /**
   * 预测
   * 
   * @param input
   *          一组输入
   * @return 预测值
   */
  public double[] predict(double[] input) {
    return FP(input);
  }

  /**
   * 正向传播
   * 
   * @param input
   *          一组输入值
   * @return 神经网络输出
   */
  private double[] FP(double[] input) {
    Perceptron[] layer_in = net.get(0);
    int input_c = layer_in[0].args_c();
    if (input.length > input_c) {
      throw new RuntimeException("输入过多，接收输入数量: " + input_c + "，实际数量：" + input.length);
    } else if (input.length < input_c) {
      double[] input_ = new double[input_c];
      for (int i = 0; i < input.length; i++) {
        input_[i] = input[i];
      }
      input = input_;
      throw new RuntimeException("输入过少，接收输入数量: " + input_c + "，实际数量：" + input.length);
    }

    for (Perceptron in_perceptron : layer_in) {
      in_perceptron.setIn(input);
    }

    for (Perceptron[] layer : net) {
      for (Perceptron perceptron : layer) {
        perceptron.work();
      }
    }

    return getOut();
  }

  /**
   * 反向传播
   * 
   * @param y
   *          样本标签
   * @param stepSize
   *          训练速度
   */
  private void BP(double[] y, double stepSize) {
    for (int layer_c = net.size() - 1; layer_c >= 0; layer_c--) {
      Perceptron[] layer = net.get(layer_c);
      for (int index = 0; index < layer.length; index++) {
        Perceptron p = layer[index];
        if (layer_c == net.size() - 1) {
          double dc = dcost_out(y[index], p.getOut());
          p.dwork(dc);
        } else {
          p.dwork(stepSize);
        }
      }
    }
  }

  /**
   * 训练模型
   * 
   * @param input
   *          样本输入组
   * @param y
   *          样本标签组
   */
  public void fit(double[][] input, double[][] y) {
    int n = 0;
    while (n < maxIter) {
      int[] rankIndex = DataUtil.randomRank(input.length);
      for (int i : rankIndex) {
        FP(input[i]);
        // System.out.println("FP..." + toString());
        BP(y[i], stepSize);
        // System.out.println("BP..." + toString());
      }
      n++;

      TestImgR ti = new TestImgR();
      try {
        System.out.println(n + "轮： ");
        ti.evaluate(0, 5000);
        ti.evaluate(5000, 5000);
        ti.evaluate(0, 10000);
        System.out.println("train:");
        ti.fitted(0, 30000);
        ti.fitted(30000, 30000);
        ti.fitted(0, 60000);
      } catch (IOException e) {
        e.printStackTrace();
      }

      Debug.printTime(n + "轮 end");
    }
  }

  @Override
  public String toString() {
    String res = "******************************";
    for (int i = 0; i < net.size(); i++) {
      res += "\n===========layer" + i + "===========\n";
      for (Perceptron perceptron : net.get(i)) {
        res += perceptron.toString() + "\n";
      }
    }
    return res + "******************************";
  }

  /**
   * 将模型保存为可读的文本文件
   * 
   * @param model_path
   *          保存路径
   * @throws FileNotFoundException
   */
  public void saveTxt(String model_path) throws FileNotFoundException {
    PrintWriter out = new PrintWriter(model_path);
    StringBuilder sb = new StringBuilder();
    sb.append("layers\n");
    sb.append(net.get(0)[0].getW_arr().length + ",");
    for (Perceptron[] layer : net) {
      sb.append(layer.length + ",");
    }
    sb.append("\n");

    int i = 0;
    for (Perceptron[] layer : net) {
      sb.append(String.format("layer %d begin\n", i));
      for (Perceptron p : layer) {
        if(p.getW_arr() == null) {
          break;
        }
          
        for (double w : p.getW_arr()) {
          sb.append(w + ",");
        }
        sb.append("\n");
      }
      sb.append(String.format("layer %d end\n", i++));
    }

    String res = sb.toString().replaceAll("(?m),$", "");
    out.print(res);
    out.close();
    System.out.println("model saved.");
  }

  /**
   * 从可读的文本文件加载模型
   * 
   * @param model_path
   *          保存路径
   * @return
   * @throws IOException
   */
  public static NatNetModel loadTxt(File model_path) throws IOException {
    NatNetModel model;
    BufferedReader in = new BufferedReader(new FileReader(model_path));
    String contype = in.readLine();
    System.out.println("load " + contype);
    if (contype.equals("layers")) {
      String[] layersStr = in.readLine().split(",");
      int[] layersNum = new int[layersStr.length];
      for (int i = 0; i < layersNum.length; i++) {
        layersNum[i] = Integer.parseInt(layersStr[i]);
      }
      model = new NatNetModel(layersNum, 100, 0.5);

    } else {
      in.close();
      throw new RuntimeException("Can't find layers information");
    }

    for (Perceptron[] layers : model.net) {
      String layerBeginF = in.readLine(); // layer n begin
      System.out.println("load " + layerBeginF);
      for (Perceptron p : layers) {
        String[] wStrArr = in.readLine().split(",");
        double[] w_arr = new double[wStrArr.length];
        for (int i = 0; i < w_arr.length; i++) {
          w_arr[i] = new Double(wStrArr[i]);
        }
        p.setW_arr(w_arr);
      }
      String layerEndF = in.readLine(); // layer n end
      System.out.println("load " + layerEndF);
    }

    System.out.println("model loaded.");
    in.close();
    return model;
  }

  /**
   * 从可读的文本文件加载模型
   * 
   * @param model_path
   *          保存路径
   * @return
   * @throws IOException
   */
  public static NatNetModel loadTxt(String model_path) throws IOException {
    return loadTxt(new File(model_path));
  }
}
