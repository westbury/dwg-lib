package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Point extends EntityObject {

    public Point3D point;
    
    public double thickness;
    
    public Point3D extrusion;
    
    public double xAxisAngle;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.29 POINT (27) page 125

        point = dataStream.get3BD();
        thickness = dataStream.getBT();
        extrusion = dataStream.getBE();
        xAxisAngle = dataStream.getBD();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "POINT";
    }
}
