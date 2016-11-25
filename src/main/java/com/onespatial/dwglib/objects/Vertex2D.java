package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Vertex2D extends EntityObject {

    public int flags;
    public Point3D point;
    public double startWidth;
    public double endWidth;
    public double bulge;
    public int vertexId;
    public double tangentDir;

    public Vertex2D(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.11 VERTEX (2D) (10) page 113

        flags = dataStream.getRC();
        point = dataStream.get3BD();
        double width = dataStream.getBD();
        if (width < 0.0) {
            startWidth = -width;
            endWidth = -width;
        } else {
            startWidth = width;
            endWidth = dataStream.getBD();
        }
        bulge = dataStream.getBD();
        vertexId = dataStream.getBL();
        tangentDir = dataStream.getBD();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "VERTEX (2D)";
    }

}
