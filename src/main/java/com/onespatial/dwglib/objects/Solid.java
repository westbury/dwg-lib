package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Extrusion;
import com.onespatial.dwglib.bitstreams.Point2D;

public class Solid extends EntityObject {

    public double thickness;
    public double elevation;
    public Point2D corner1;
    public Point2D corner2;
    public Point2D corner3;
    public Point2D corner4;
    public Extrusion extrusion;

    public Solid(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.33 SOLID (31) page 129

        thickness = dataStream.getBT();
        elevation = dataStream.getBD();
        corner1 = dataStream.get2RD();
        corner2 = dataStream.get2RD();
        corner3 = dataStream.get2RD();
        corner4 = dataStream.get2RD();
        extrusion = dataStream.getBE();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    @Override
    public String toString() {
        return "SOLID";
    }
}
