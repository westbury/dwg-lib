package objects;

import bitstreams.BitBuffer;
import bitstreams.Point2D;
import bitstreams.Point3D;
import dwglib.FileVersion;

public abstract class Dimension extends EntityObject {

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.21 COMMON DIMENSION DATA page 119

        int version = dataStream.getRC();
        Point3D extrusion = dataStream.get3BD();

        Point2D textMidpoint = dataStream.get2RD();
        double elevation = dataStream.getBD();
        int flags1 = dataStream.getRC();
        String userText = stringStream.getTU();
        double textRotation = dataStream.getBD();
        double horizontalDirection = dataStream.getBD();
        double insertXScale = dataStream.getBD();
        double insertYScale = dataStream.getBD();
        double insertZScale = dataStream.getBD();
        double insertRotation = dataStream.getBD();
        int attachmentPoint = dataStream.getBS();
        int linespacingStyle = dataStream.getBS();
        double linespacingFactor = dataStream.getBD();
        double actualMeasurement = dataStream.getBD();
        boolean unknown = dataStream.getB();
        boolean flipArrow1 = dataStream.getB();
        boolean flipArrow2 = dataStream.getB();
        Point2D twelvePoint = dataStream.get2RD();

        readDimensionSpecificData(dataStream, stringStream, handleStream, fileVersion);
    }

    protected abstract void readDimensionSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion);
}
