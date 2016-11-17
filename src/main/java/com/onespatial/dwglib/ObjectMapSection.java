package com.onespatial.dwglib;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapSection {

	Map<Integer, Long> locationMap = new HashMap<>();
	
	public void add(int handleIndex, long location) {
		locationMap.put(handleIndex, location);
	}

}
