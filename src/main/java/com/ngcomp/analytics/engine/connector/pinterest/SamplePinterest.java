package com.ngcomp.analytics.engine.connector.pinterest;

import com.ngcomp.analytics.engine.connector.common.util.Printer;

public class SamplePinterest {
	   
	public static void main(String[]args){
		SearchPinterest pinterest = new SearchPinterest();

		
		System.out.println("Getting user pins as object array");
		Printer.printArrayToConsole(pinterest.getPinsForURL(false, false,  "http://www.pinterest.com/nicolekeegan94/penguins/"));
		System.out.println();
	}
}
