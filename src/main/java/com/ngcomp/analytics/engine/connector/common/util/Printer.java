package com.ngcomp.analytics.engine.connector.common.util;

/**
 * Utility class to print objects to the console.
 * @author dprasad
 * @since 0.1
 */
public class Printer {
	
	/**
	 * Method to print an array of objects on the console
	 * @param os
	 */
	public static void printArrayToConsole(Object[] os){
		for(Object o : os)
			System.out.println(o.toString());
	}

}
