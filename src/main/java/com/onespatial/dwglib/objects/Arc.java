package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Arc extends EntityObject {

    public Point3D center;
    public double radius;
    public double thickness;
    public Point3D extrusion;
    public double startAngle;
    public double endAngle;

    public Arc(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.18 ARC (17) page 117    

        center = dataStream.get3BD();
        radius = dataStream.getBD();
        thickness = dataStream.getBT();
        extrusion = dataStream.getBE();
        startAngle = dataStream.getBD();
        endAngle = dataStream.getBD();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "ARC";
    }
}
