package com.chen.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadMapUtil {
	
	public static Properties getMapProperties(){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(Constant.Map);
		Properties properties=new Properties();
		try {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}
