package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Line extends EntityObject {

    public Point3D start;
    public Point3D end;
    public double thickness;
    public Point3D extrusion;

    public Line(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.20 LINE (19) page 118

        boolean zAreZero = dataStream.getB();

        double startX = dataStream.getRD();
        double endX = dataStream.getDD(startX);
        double startY = dataStream.getRD();
        double endY = dataStream.getDD(startY);
        double startZ;
        double endZ;
        if (zAreZero) {
            startZ = 0;
            endZ = 0;
        } else {
            startZ = dataStream.getRD();
            endZ = dataStream.getDD(startZ);
        }
        start = new Point3D(startX, startY, startZ);
        end = new Point3D(endX, endY, endZ);
        
        thickness = dataStream.getBT();
        extrusion = dataStream.getBE();

        // Read all handles (until we figure out what they are)

//        try {
//            do {
//                Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
//                genericHandles.add(referencedHandle);
//            } while (true);
//        } catch (RuntimeException e) {
//
//        }


        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LINE";
    }
}
