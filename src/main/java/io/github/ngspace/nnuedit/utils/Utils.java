package io.github.ngspace.nnuedit.utils;

import java.io.InputStream;
import java.util.Arrays;

public class Utils {private Utils() {}
	
	
	public static <T> T[] concatArrays(T[] arr1, T[] arr2) {
	    T[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
	    System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
	    return result;
	}
	
	
	public static int safeParseInt(String string) {try{return Integer.parseInt(string);} catch (Exception e) {return 0;}}
	
	/*
	 * Resource management
	 */
	public static InputStream getAsset(String string) {
		return Utils.class.getResourceAsStream("/"+string);
	}
	
}
