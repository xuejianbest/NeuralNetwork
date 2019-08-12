package com.xjb.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.xjb.NN.NatNetModel;
import com.xjb.data.ImgReader;
import com.xjb.util.Debug;

public class TestImg {
  private static int zoom;
  private static String modelPath;
  private static NatNetModel model;

  public TestImg(String modelPath) {
    TestImg.modelPath = modelPath;
  }

  public TestImg() {
  }

  public static void main(String[] args) throws Exception {
    Debug.initTime();

    if (args.length < 3) {
      throw new RuntimeException("command model_file iter_num step_size [hidden_size zoom]");
    }

    TestImg ti = new TestImg(args[0]);
    File modelFile = new File(modelPath);

    if (!modelFile.exists() && args.length < 5) {
      throw new RuntimeException("model_file not exists, [hidden_size zoom] must be specified.");

    } else if (!modelFile.exists()) {
      int hidenSize = Integer.parseInt(args[3]);
      zoom = Integer.parseInt(args[4]);
      int inputSize = (28 / zoom) + (28 % zoom > 0 ? 1 : 0);
      int[] layers = new int[] { inputSize * inputSize, hidenSize, 10 };
      model = new NatNetModel(layers);

    } else {
      model = NatNetModel.loadTxt(modelFile);
      zoom = (int) Math.round(Math.sqrt(28.0 * 28 / model.getInputSize()));
    }

    int iterNum = Integer.parseInt(args[1]);
    double stepSize = new Double(args[2]);
    model.setMaxIter(iterNum).setStepSize(stepSize);

    Debug.printTime("Set model done.");

    // 开始训练
    System.out.println("learning...");
    ti.learn();

    // 保存模型
    model.saveTxt(modelPath);
    Debug.printTime("model saved.");
  }

  private void learn() throws IOException {
    Map<String, Object> trainData = ImgReader.loadTrainData(zoom);
    double[][] inputs = (double[][]) trainData.get("inputs");
    double[][] ys = (double[][]) trainData.get("ys");
    model.fit(inputs, ys);
  }

  public double fitted(int offset, int num) throws IOException {
    Map<String, Object> trainData = ImgReader.loadTrainData(zoom);
    double[][] inputs = (double[][]) trainData.get("inputs");
    int[] labels = (int[]) trainData.get("labels");

    int right = 0;
    for (int i = 0; i < num; i++) {
      double[] input = inputs[offset + i];
      double[] out = model.predict(input);

      int predict = getPredict(out);

      int label = labels[offset + i];
      if (label == predict) {
        right++;
      }
    }
    System.out.println(String.format("%d, %d, %.4f", right, num, right * 1.0 / num));
    return right * 1.0 / num;
  }

  public double evaluate(int offset, int num) throws IOException {
    Map<String, Object> testData = ImgReader.loadTestData(zoom);
    double[][] inputs = (double[][]) testData.get("inputs");
    int[] labels = (int[]) testData.get("labels");

    int right = 0;
    for (int i = 0; i < num; i++) {
      double[] input = inputs[offset + i];
      double[] out = model.predict(input);

      int predict = getPredict(out);

      int label = labels[offset + i];
      if (label == predict) {
        right++;
      }
    }
    System.out.println(String.format("%d, %d, %.4f", right, num, right * 1.0 / num));
    return right * 1.0 / num;
  }

  private int getPredict(double[] out) {
    int res = 0;
    double max = out[0];
    for (int i = 1; i < out.length; i++) {
      if (out[i] > max) {
        max = out[i];
        res = i;
      }
    }
    return res;
  }
}
