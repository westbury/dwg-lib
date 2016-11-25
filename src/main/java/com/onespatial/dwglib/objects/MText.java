package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.CmColor;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class MText extends EntityObject {

    public Point3D insertionPoint;
    public Point3D extrusion;
    public Point3D xAxisDir;
    public double rectWidth;
    public double rectHeight;
    public double textHeight;
    public int attachment;
    public int drawingDir;
    public double extents;
    public double extentsWid;
    public String text;
    public int linespacingStyle;
    public double linespacingFactor;
    public boolean unknownBit;
    public double backgroundScaleFactor;
    public CmColor backgroundColor;
    public int backgroundTransparency;
    private Handle styleHandle;

    public MText(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.44 MTEXT (44) page 146    

        insertionPoint = dataStream.get3BD();
        extrusion = dataStream.get3BD();
        xAxisDir = dataStream.get3BD();
        rectWidth = dataStream.getBD();
        rectHeight = dataStream.getBD();
        textHeight = dataStream.getBD();
        attachment = dataStream.getBS();
        drawingDir = dataStream.getBS();
        extents = dataStream.getBD();
        extentsWid = dataStream.getBD();
        text = stringStream.getTU();
        linespacingStyle = dataStream.getBS();
        linespacingFactor = dataStream.getBD();
        unknownBit = dataStream.getB();
        int backgroundFlags = dataStream.getBL();
        
        if (backgroundFlags == 1) {
            backgroundScaleFactor = dataStream.getBD();
            backgroundColor = dataStream.getCMC();
            backgroundTransparency = dataStream.getBL();
        } else {
            backgroundScaleFactor = 1.5;
        }

        // The Handles
        
        styleHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "MTEXT";
    }

    public CadObject getStyle() {
        CadObject result = objectMap.parseObject(styleHandle);
        return (CadObject) result;
    }

}
