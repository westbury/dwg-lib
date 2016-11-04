package objects;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import dwglib.FileVersion;

public class Line extends EntityObject {

    public String toString() {
        return "LINE";
    }

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

        double thickness;
        boolean thicknessBit = dataStream.getB();
        if (thicknessBit) {
            thickness = 0.0;
        } else {
            thickness = dataStream.getBD();
        }

        double extrusion1, extrusion2,extrusion3;
        boolean extrusionBit = dataStream.getB();
        if (extrusionBit) {
            extrusion1 = 0.0;
            extrusion2 = 0.0;
            extrusion3 = 1.0;
        } else {
            extrusion1 = dataStream.getBD();
            extrusion2 = dataStream.getBD();
            extrusion3 = dataStream.getBD();
        }

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
}
