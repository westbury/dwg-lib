package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Leader extends EntityObject {

    public int annotType;
    public int pathType;
    public Point3D[] points;
    public Point3D endPointProj;
    public Point3D extrusion;
    public Point3D xDirection;
    public Point3D offsetToBlockInsPt;
    public double unknown1;
    /** possibly boxWidth */
    public double unknown2;
    /** possibly boxHeight */
    public double unknown3;
    public boolean hooklineOnXDir;
    public boolean arrowHeadOn;
    public int unknownInt;
    public boolean unknownBit1;
    public boolean unknownBit2;
    private Handle annotationHandle;
    private Handle dimStyleHandle;

    public Leader(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.45 LEADER (45) page 147    

        dataStream.expectB(false);
        annotType = dataStream.getBS();
        pathType = dataStream.getBS();
        int numberOfPoints = dataStream.getBL();
        
        points = new Point3D[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = dataStream.get3BD();
        }

        endPointProj = dataStream.get3BD();
        extrusion = dataStream.get3BD();
        xDirection = dataStream.get3BD();
        offsetToBlockInsPt = dataStream.get3BD();
        /*
         * There now appear to be three bit-decimals.
         * The specification say to expect 3 bit-decimals
         * followed by boxHeight and boxWidth.  So it is
         * unknown whether we have a single bit-decimal
         * followed by boxHeight and boxWidth, or whether
         * the specification is wrong in including the box height
         * and width.  To avoid guessing wrong we treat as three
         * separate unknown decimals.
         */
        unknown1 = dataStream.getBD();
        unknown2 = dataStream.getBD();
        unknown3 = dataStream.getBD();
        
        hooklineOnXDir = dataStream.getB();
        arrowHeadOn = dataStream.getB();
        unknownInt = dataStream.getBS();
        unknownBit1 = dataStream.getB();
        unknownBit2 = dataStream.getB();

        // The Handles
        
        annotationHandle = handleStream.getHandle();
        dimStyleHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "LEADER";
    }

    public CadObject getAnnotation() {
        CadObject result = objectMap.parseObject(annotationHandle);
        return (CadObject) result;
    }

    public CadObject getDimStyle() {
        CadObject result = objectMap.parseObject(dimStyleHandle);
        return (CadObject) result;
    }

}
