package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point2D;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Text extends EntityObject {

    public double elevation;
    public Point2D insertionPoint;
    public Point2D alignmentPoint;
    public Point3D extrusion;
    public double thickness;
    public double obliqueAngle;
    public double rotationAngle;
    public double height;
    public double widthFactor;
    public String textValue;
    public int generation;
    public int horizontalAlignment;
    public int verticalAlignment;
    public Handle styleHandle;

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.3 TEXT (1) page 106    

        int dataFlags = dataStream.getUnsignedRC();
        if ((dataFlags & 0x01) == 0) {
            elevation = dataStream.getRD();
        }
        insertionPoint = dataStream.get2RD();
        if ((dataFlags & 0x02) == 0) {
            alignmentPoint = dataStream.get2DD(insertionPoint);
        }
        extrusion = dataStream.getBE();
        thickness = dataStream.getBT();
        if ((dataFlags & 0x04) == 0) {
            obliqueAngle = dataStream.getRD();
        }
        if ((dataFlags & 0x08) == 0) {
            rotationAngle = dataStream.getRD();
        }
        height = dataStream.getRD();
        if ((dataFlags & 0x10) == 0) {
            widthFactor = dataStream.getRD();
        }
        textValue = stringStream.getTU();
        if ((dataFlags & 0x20) == 0) {
            generation = dataStream.getBS();
        }
        if ((dataFlags & 0x40) == 0) {
            horizontalAlignment = dataStream.getBS();
        }
        if ((dataFlags & 0x80) == 0) {
            verticalAlignment = dataStream.getBS();
        }

        // The Handles
        
        styleHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "TEXT";
    }
}
