package com.onespatial.dwglib.objects;

public class GenericObject extends NonEntityObject {

	public String objectType;

	public GenericObject(ObjectMap objectMap, int objectType) {
        super(objectMap);
		this.objectType = Integer.toString(objectType);
	}

	public GenericObject(ObjectMap objectMap, String classdxfname) {
        super(objectMap);
		this.objectType = classdxfname;
	}

	public String toString() {
		return "unknown object of type " + objectType;
	}

}
