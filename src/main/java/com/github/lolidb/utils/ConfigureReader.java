package com.github.lolidb.utils;

public class ConfigureReader {
	private static ConfigureReader reader = new ConfigureReader();

	public static ConfigureReader getInstance() {
		return reader;
	}

	private ConfigureReader() {
	}

	public <T> T get(String key,T ref){
		String value = System.getProperty(key);
		return value==null?ref: (T) value;
	}
}
