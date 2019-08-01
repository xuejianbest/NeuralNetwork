package com.xjb.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.xjb.NN.NatNetModel;
import com.xjb.data.ImgReader;
import com.xjb.util.Debug;

public class TestImg {
  private final String modelPath = "./model/model.txt";
  private ImgReader ir = new ImgReader();
  NatNetModel model;

  public static void main(String[] args) throws Exception {
    long t1 = System.currentTimeMillis();
    TestImg ti = new TestImg();

    if (!new File(ti.modelPath).exists()) {
      ti.learn();
    }
    long t2 = Debug.printTime(t1, "fited");

    ti.model = NatNetModel.loadTxt(ti.modelPath);
    ti.evaluation(0, 5000);
    ti.evaluation(5000, 5000);
    ti.evaluation(0, 10000);
    Debug.printTime(t2, "evaluation");
  }

  private void learn() throws IOException {
    int zoom = 2;
    int[] layers = new int[] { 28 * 28 / zoom / zoom, 30, 10};
    NatNetModel model = new NatNetModel(layers).setMaxIter(1).setStepSize(1);

    int offset = 0;
    int num = 60000;
    Map<String, Object> data = ir.getData(num, offset, zoom, "train");
    double[][] inputs = (double[][]) data.get("inputs");
    double[][] ys = (double[][]) data.get("ys");

    model.fit(inputs, ys);
    model.saveTxt(modelPath);
  }

  private void evaluation(int offset, int num) throws IOException {
    Map<String, Object> map = ir.getData(num, offset, 2, "test");
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
    System.out.println(String.format("%d, %d, %f", right, num, right * 1.0 / num));
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
