package com.xjb.util;

public class DataUtil {
  public static void main(String[] args) {
    int[] k = randomRank(6);
    Debug.printarr(k);
  }

  /**
   * 返回[min, max)之间的随机数，含min和max。
   * 
   * @param min
   * @param max
   * @return
   */
  public static double randomNum(double min, double max) {
    return Math.random() * (max - min) + min;
  }

  /**
   * 返回一个元素数量为n的整型数组，数组值为[0, n-1]，值的顺序随机
   * @param n
   * @return
   */
  public static int[] randomRank(int n) {
    int[] res = new int[n];
    for (int i = 0; i < n; i++) {
      res[i] = i;
    }

    int tmp;
    for (int i = n - 1; i >= 0; i--) {
      int j = (int) (Math.random() * (i + 1));
      tmp = res[j];
      res[j] = res[i];
      res[i] = tmp;
    }
    return res;
  }
  
  
}
