package objects;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.Point3D;
import dwglib.FileVersion;

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
