package com.onespatial.dwglib.objects;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.CmColor;
import com.onespatial.dwglib.bitstreams.Point2D;
import com.onespatial.dwglib.bitstreams.Point3D;

public class ViewPort extends EntityObject {

    public Point3D point;
    
    public double thickness;
    
    public Point3D extrusion;
    
    public double xAxisAngle;

    public Point3D center;

    public double width;

    public double height;

    public Point3D viewTarget;

    public Point3D viewDirection;

    public double viewTwistAngle;

    public double viewHeight;

    public double lensLength;

    public double frontClipZ;

    public double backClipZ;

    public double snapAngle;

    public Point2D viewCenter;

    public Point2D snapBase;

    public Point2D snapSpacing;

    public Point2D gridSpacing;

    public int circleZoom;

    public int gridMajor;

    public String styleSheet;

    public int renderMode;

    public boolean ucsAtOrigin;

    public boolean ucsPerViewport;

    public Point3D ucsOrigin;

    public Point3D ucsXAxis;

    public Point3D ucsYAxis;

    public double ucsElevation;

    public int ucsOrthoViewType;

    public int shadePlotMode;

    public boolean useDefLights;

    public int defLightingType;

    public double brightness;

    public double contrast;

    public CmColor ambientLightColor;

    public ViewPort(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {

        // 19.4.36 VIEWPORT (34) page 131

        center = dataStream.get3BD();
        width = dataStream.getBD();
        height = dataStream.getBD();
        viewTarget = dataStream.get3BD();
        viewDirection = dataStream.get3BD();
        viewTwistAngle = dataStream.getBD();
        viewHeight = dataStream.getBD();
        lensLength = dataStream.getBD();
        frontClipZ = dataStream.getBD();
        backClipZ = dataStream.getBD();
        snapAngle = dataStream.getBD();
        viewCenter = dataStream.get2RD();
        snapBase = dataStream.get2RD();
        snapSpacing = dataStream.get2RD();
        gridSpacing = dataStream.get2RD();
        circleZoom = dataStream.getBS();
        gridMajor = dataStream.getBS();
        int frozenLayerCount = dataStream.getBL();
        int statusFlags = dataStream.getBL();
        styleSheet = stringStream.getTU();
        renderMode = dataStream.getRC();
        ucsAtOrigin = dataStream.getB();
        ucsPerViewport = dataStream.getB();
        ucsOrigin = dataStream.get3BD();
        ucsXAxis = dataStream.get3BD();
        ucsYAxis = dataStream.get3BD();
        ucsElevation = dataStream.getBD();
        ucsOrthoViewType = dataStream.getBS();
        shadePlotMode = dataStream.getBS();
        useDefLights = dataStream.getB();
        defLightingType = dataStream.getRC();
        brightness = dataStream.getBD();
        contrast = dataStream.getBD();
        ambientLightColor = dataStream.getCMC();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
//        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "VIEWPORT";
    }
}
