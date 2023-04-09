package com.sampleTests;

import java.io.IOException;

import com.reporter.ReportGenerator;
import com.reporter.ReportGenerator2;

public class ReportGenTest {

	public static void main(String[] args) {
		String[] methodNames = {"Method 1", "Method 2", "Method 3"};
		int[] passCount = {10, 20, 15};
		int[] failCount = {2, 3, 1};
		int[] totalCount = {12, 23, 16};

		ReportGenerator reportGenerator = new ReportGenerator(methodNames, passCount, failCount, totalCount);
		ReportGenerator2 reportGenerator2 = new ReportGenerator2(methodNames, passCount, failCount, totalCount);
		try {
			reportGenerator.generateReport("report1.html");
			reportGenerator2.generateReport("report2.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
