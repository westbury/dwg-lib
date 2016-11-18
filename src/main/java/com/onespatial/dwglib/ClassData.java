package com.onespatial.dwglib;
import com.onespatial.dwglib.bitstreams.BitBuffer;

public class ClassData {

	public int classNum;
	public int proxyFlags;
	public boolean wasazombie;
	public int itemclassid;
	public int numberOfObjects;
	public int dwgVersion3;
	public int maintenanceReleaseVersion3;
	public int unknown1;
	public int unknown2;
	public String appname;
	public String cplusplusclassname;
	public String classdxfname;

	public ClassData(BitBuffer bitClasses, BitBuffer bitClassesStrings) {
        classNum = bitClasses.getBS();
        proxyFlags = bitClasses.getBS();
        wasazombie = bitClasses.getB();
        itemclassid = bitClasses.getBS();
        numberOfObjects = bitClasses.getBL();
        dwgVersion3 = bitClasses.getBL();
        maintenanceReleaseVersion3 = bitClasses.getBL();
        unknown1 = bitClasses.getBL();
        unknown2 = bitClasses.getBL();

        appname = bitClassesStrings.getTU();
        cplusplusclassname = bitClassesStrings.getTU();
        classdxfname = bitClassesStrings.getTU();
	}

}
