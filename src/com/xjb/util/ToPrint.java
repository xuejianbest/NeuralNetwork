package com.xjb.util;

public class ToPrint {
	public static String array2str(double[] param){
		String res = "[";
		for(double d : param){
			res += d + ", ";
		}
		if(!res.equals("[")){
			res = res.substring(0, res.length()-2);
		}
		return res + "]";
	}
	public static String array2str(int[] param){
		String res = "[";
		for(int d : param){
			res += d + ", ";
		}
		if(!res.equals("[")){
			res = res.substring(0, res.length()-2);
		}
		return res + "]";
	}
	
	public static String array2str(int[][] param){
		String res = "\n";
		for(int[] item : param){
			res += array2str(item) + "\n";
		}
		return res;
	}
	
	public static String array2str(double[][] param){
		String res = "\n";
		for(double[] item : param){
			res += array2str(item) + "\n";
		}
		return res;
	}
	
	public static String array2str(Object[] param){
		String res = "\n";
		for(Object item : param){
			res += item.toString() + "\n";
		}
		return res;
	}
	
	public static String array2str(Object[][] param){
		String res = "\n";
		for(Object[] item : param){
			res += array2str(item) + "\n";
		}
		return res;
	}
}
