package com.xjb.util;

/**
 * 用于调试的工具类，所有方法都为静态。
 */
public class Debug {
  private static long preTime = 0;

  /**
   * 打印信息到标准输出
   * 
   * @param info
   *          信息，字符串常量
   */
  public static void printi(Object info) {
    System.out.println(info.toString());
  }

  /**
   * 打印错误信息到标准错误
   * 
   * @param err
   *          错误信息，字符串常量
   */
  public static void printe(Object err) {
    System.err.println(err.toString());
  }

  /**
   * 格式化输出字符串到标准输出
   * 
   * @param format
   *          格式化字符常量
   * @param args
   *          替换内容
   */
  public static void printi(String format, Object... args) {
    String str = String.format(format, args);
    System.out.println(str);
  }

  public static void printarr(double[] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  public static void printarr(double[][] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  public static void printarr(int[] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  public static void printarr(int[][] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  public static void printarr(Object[][] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  public static void printarr(Object[] arr) {
    String str = ToPrint.array2str(arr);
    System.out.println(str);
  }

  /**
   * 打印...done
   */
  public static void done() {
    System.out.println("...done");
  }

  public static void printTime(String msg) {
    long now = System.currentTimeMillis();
    System.out.println(String.format("********* %.2f s *********%s", (now - preTime) / 1000.0, msg));
    preTime = now;
  }
  
  public static void initTime() {
    preTime = System.currentTimeMillis();
  }
}
