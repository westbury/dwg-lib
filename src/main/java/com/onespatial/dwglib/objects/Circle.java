package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Extrusion;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Circle extends EntityObject {

    public Point3D center;
    public double radius;
    public double thickness;
    public Extrusion extrusion;

    public Circle(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.19 CIRCLE (18) page 118

        center = dataStream.get3BD();
        radius = dataStream.getBD();
        thickness = dataStream.getBT();
        extrusion = dataStream.getBE();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    @Override
    public String toString() {
        return "CIRCLE";
    }
}
