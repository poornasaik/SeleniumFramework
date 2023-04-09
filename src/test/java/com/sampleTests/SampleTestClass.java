package com.sampleTests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SampleTestClass {
	public static Logger log = LogManager.getLogger();

	public static void main(String[] args) {
		Thread t1 = new Thread(() -> {
			log.warn("sample warn log");
			log.info("sample info log");
			log.error("sample error log");
			log.debug("sample debug log");
			log.fatal("sample fatal log");
		});
		Thread t2 = new Thread(() -> {
			log.warn("sample1 warn log");
			log.info("sample 1 info log");
			log.error("sample1  error log");
			log.debug("sample 1 debug log");
			log.fatal("sample 1 fatal log");
		});
		t1.start();
		t2.start();
		
	}

}
