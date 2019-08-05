package com.xjb.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.xjb.NN.NatNetModel;
import com.xjb.data.ImgReader;
import com.xjb.util.Debug;

public class TestImg {
  private final ImgReader ir = new ImgReader();
  private String modelPath;
  private NatNetModel model;
  private int zoom;

  public static void main(String[] args) throws Exception {
    long t1 = System.currentTimeMillis();

    if (args.length < 3) {
      throw new RuntimeException("command model_file iter_num step_size [hidden_size zoom]");
    }

    TestImg ti = new TestImg();
    ti.modelPath = args[0];
    File modelFile = new File(ti.modelPath);

    if (!modelFile.exists() && args.length < 5) {
      throw new RuntimeException("model_file not exists, [hidden_size zoom] must be specified.");

    } else if (!modelFile.exists()) {
      int hidenSize = Integer.parseInt(args[3]);
      ti.zoom = Integer.parseInt(args[4]);
      int inputSize = (28 / ti.zoom) + (28 % ti.zoom > 0 ? 1 : 0);
      int[] layers = new int[] { inputSize * inputSize, hidenSize, 10 };
      ti.model = new NatNetModel(layers);

    } else {
      ti.model = NatNetModel.loadTxt(modelFile);
      ti.zoom = (int) Math.round(Math.sqrt(28.0 * 28 / ti.model.getInputSize()));
    }

    int iterNum = Integer.parseInt(args[1]);
    double stepSize = new Double(args[2]);
    ti.model.setMaxIter(iterNum).setStepSize(stepSize);

    long t2 = Debug.printTime(t1, "Set model done.");

    // 开始训练
    System.out.println("learning...");
    ti.learn();
    long t3 = Debug.printTime(t2, "learn finished.");

    // 评估模型
    ti.evaluate(0, 5000);
    ti.evaluate(5000, 5000);
    double right = ti.evaluate(0, 10000);
    long t4 = Debug.printTime(t3, "evaluate finished");

    // 保存模型
    String newSavePath = String.format("%s%%%.4f", ti.modelPath.split("%")[0], right).replace("%0.", "%");
    ti.model.saveTxt(newSavePath);
    Debug.printTime(t4, "model saved.");
  }

  private void learn() throws IOException {
    int offset = 0;
    int num = 60000;
    Map<String, Object> data = ir.getData(num, offset, zoom, "train");
    double[][] inputs = (double[][]) data.get("inputs");
    double[][] ys = (double[][]) data.get("ys");

    model.fit(inputs, ys);
  }

  private double evaluate(int offset, int num) throws IOException {
    Map<String, Object> map = ir.getData(num, offset, zoom, "test");
    double[][] inputs = (double[][]) map.get("inputs");
    int[] labels = (int[]) map.get("labels");

    int right = 0;
    for (int i = 0; i < inputs.length; i++) {
      double[] input = inputs[i];
      double[] out = model.predict(input);

      int predict = getPredict(out);

      int label = labels[i];
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
