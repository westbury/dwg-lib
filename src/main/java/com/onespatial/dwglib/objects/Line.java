package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Line extends EntityObject {

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.20 LINE (19) page 118

        boolean zAreZero = dataStream.getB();

        double startX = dataStream.getRD();
        double endX = dataStream.getDD(startX);
        double startY = dataStream.getRD();
        double endY = dataStream.getDD(startY);
        if (!zAreZero) {
            double startZ = dataStream.getRD();
            double endZ = dataStream.getDD(startZ);
        }

        double thickness = dataStream.getBT();
        Point3D extrusion = dataStream.getBE();

        // Read all handles (until we figure out what they are)

        try {
            do {
                Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
                genericHandles.add(referencedHandle);
            } while (true);
        } catch (RuntimeException e) {

        }


        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LINE";
    }
}
