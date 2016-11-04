package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.Point2D;
import dwglib.FileVersion;

public class LwPolyline extends EntityObject {

    public Handle referencedHandle;

    public List<Point2D> points;

    public String toString() {
        return "LWPOLYLINE";
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        boolean b1 = dataStream.getB();

        if (b1) {
            // dataStream.expectB(false);
            boolean b2 = dataStream.getB();
            if (b2) {
                throw new RuntimeException("unknown bit set on: investigation needed.");
            }

            int numberOfPoints = dataStream.getBS();

            points = new ArrayList<>(numberOfPoints);

            double x = dataStream.getRD();
            double y = dataStream.getRD();
            points.add(new Point2D(x, y));
            for (int index = 1; index < numberOfPoints; index++) {
                x = dataStream.getDD(x);
                y = dataStream.getDD(y);
                points.add(new Point2D(x, y));
            }

            dataStream.assertEndOfStream();
        }

        try {
            do {
                Handle referencedHandle = handleStream.getHandle(handleOfThisObject);
                genericHandles.add(referencedHandle);
            } while (true);
        } catch (RuntimeException e) {

        }

        handleStream.advanceToByteBoundary();

        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }
}
