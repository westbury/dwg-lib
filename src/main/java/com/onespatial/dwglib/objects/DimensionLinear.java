package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class DimensionLinear extends Dimension {

    @Override
    public void readDimensionSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.23 DIMENSION (LINEAR) (21) page 121

        Point3D thirteenPoint = dataStream.get3BD();
        Point3D fourteenPoint = dataStream.get3BD();
        Point3D tenPoint = dataStream.get3BD();

        double extensionLineRotation = dataStream.getBD();
        double dimensionRotation = dataStream.getBD();

        // Read all handles (until we figure out what they are)

        Handle dimstyleHandle = handleStream.getHandle();
        Handle anonymousBlockHandle = handleStream.getHandle();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "DIMENSION (LINEAR)";
    }
}
