package com.xjb.data;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImgReader {
  private static final String train_imgs = "./data/train-images.idx3-ubyte";
  private static final String train_labels = "./data/train-labels.idx1-ubyte";
  private static final String test_imgs = "./data/t10k-images.idx3-ubyte";
  private static final String test_labels = "./data/t10k-labels.idx1-ubyte";
  private static Map<String, Object> testData;
  private static Map<String, Object> trainData;

  public static Map<String, Object> loadTestData(int zoom) {
    try {
      if (testData == null) {
        testData = getData(0, 10000, zoom, "test");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return testData;
  }

  public static Map<String, Object> loadTrainData(int zoom) {
    try {
      if (trainData == null) {
        trainData = getData(0, 60000, zoom, "train");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return trainData;
  }

  public static Map<String, Object> getData(int offset, int num, int zoom, String type) throws IOException {
    double[][] inputs = new double[num][];
    double[][] ys = new double[num][];
    int i = 0;

    int[][][] imgs;
    int[] labels;
    if (type.equals("train")) {
      imgs = train_imgs(offset, num);
      labels = train_labels(offset, num);

    } else if (type.equals("test")) {
      imgs = test_imgs(offset, num);
      labels = test_labels(offset, num);

    } else {
      throw new RuntimeException("type must be 'train' or 'test': " + type);
    }

    for (int[][] img : imgs) {
      double[] input = stardand(dimReduction(zoomOut(img, zoom)));
      inputs[i] = input;

      double[] y = addReduction(labels[i]);
      ys[i] = y;
      i++;
    }

    Map<String, Object> res = new HashMap<>(4);
    res.put("inputs", inputs);
    res.put("ys", ys);
    res.put("imgs", imgs);
    res.put("labels", labels);
    return res;
  }

  private static double[] addReduction(int y) {
    int dim = 10;
    double[] res = new double[dim];
    res[y] = 1;
    return res;
  }

  private static int[] dimReduction(int[][] img) {
    int[] res = new int[img.length * img[0].length];
    for (int r = 0; r < img.length; r++) {
      for (int c = 0; c < img[0].length; c++) {
        res[r * img[0].length + c] = img[r][c];
      }
    }
    return res;
  }

  private static int[][] zoomOut(int[][] img, int zoom) {
    int res_r = img.length % zoom > 0 ? img.length / zoom + 1 : img.length / zoom;
    int res_c = img[0].length % zoom > 0 ? img[0].length / zoom + 1 : img[0].length / zoom;
    int[][] res = new int[res_r][res_c];

    for (int r = 0; r < img.length; r++) {
      for (int c = 0; c < img[0].length; c++) {
        if (r % zoom == 0 && c % zoom == 0) {
          res[r / zoom][c / zoom] = img[r][c];
        }
      }
    }
    return res;
  }

  private static double[] stardand(int[] input) {
    double[] res = new double[input.length];
    for (int i = 0; i < input.length; i++) {
      res[i] = input[i] / 127.5 - 1.0;
    }
    return res;
  }

  private static int[][][] train_imgs(int offset, int num) throws IOException {
    return imgReader(train_imgs, offset, num);
  }

  private static int[][][] test_imgs(int offset, int num) throws IOException {
    return imgReader(test_imgs, offset, num);
  }

  private static int[] train_labels(int offset, int num) throws IOException {
    return labelReader(train_labels, offset, num);
  }

  private static int[] test_labels(int offset, int num) throws IOException {
    return labelReader(test_labels, offset, num);
  }

  private static int[][][] imgReader(String file, int offset, int num) throws IOException {
    DataInputStream in = new DataInputStream(new FileInputStream(file));

    int magic_number = in.readInt();
    if (magic_number != 0x803) {
      in.close();
      throw new RuntimeException("图片文件格式错误");
    }

    int img_num = in.readInt();
    if (offset + num > img_num) {
      in.close();
      throw new RuntimeException("数量溢出，图片总数量为：" + img_num);
    }

    int img_rows = in.readInt();
    int img_columns = in.readInt();

    int[][][] imgs = new int[num][img_rows][img_columns];

    in.skip(offset * img_rows * img_columns);
    for (int i = 0; i < num; i++) {
      for (int r = 0; r < img_rows; r++) {
        for (int c = 0; c < img_columns; c++) {
          imgs[i][r][c] = in.readByte() & 0xff;
        }
      }
    }

    in.close();
    return imgs;
  }

  private static int[] labelReader(String file, int offset, int num) throws IOException {
    DataInputStream in = new DataInputStream(new FileInputStream(file));

    int magic_number = in.readInt();
    if (magic_number != 0x801) {
      in.close();
      throw new RuntimeException("标签文件格式错误");
    }

    int label_num = in.readInt();
    if (offset + num > label_num) {
      in.close();
      throw new RuntimeException("数量溢出，标签总数量为：" + label_num);
    }

    int[] labels = new int[num];

    in.skip(offset);
    for (int i = 0; i < num; i++) {
      labels[i] = in.readByte() & 0xff;
    }
    in.close();
    return labels;

  }

}
