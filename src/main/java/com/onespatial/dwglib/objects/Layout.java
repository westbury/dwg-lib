package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point2D;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Layout extends NonEntityObject {

    public String pageSetupName;
    public String printerOrConfig;
    public int plotLayoutFlags;
    public double leftMargin;
    public double bottomMargin;
    public double rightMargin;
    public double topMargin;
    public double paperWidth;
    public double paperHeight;
    public String paperSize;
    public Point2D plotOrigin;
    public int paperUnits;
    public int plotRotation;
    public int plotType;
    public Point2D windowMinimum;
    public Point2D windowMaximum;
    public double realWorldUnits;
    public double drawingUnits;
    public String currentStyleSheet;
    public int scaleType;
    public double scaleFactor;
    public Point2D paperImageOrigin;
    public int shadePlotMode;
    public int shadePlotResLevel;
    public int shadePlotCustomDpi;
    public String layoutName;
    public int tabOrder;
    public int flag;
    public Point3D ucsOrigin;
    public Point2D limitMinimum;
    public Point2D limitMaximum;
    public Point3D insertionPoint;
    public Point3D ucsXAxis;
    public Point3D ucsYAxis;
    public double elevation;
    public int orthographicViewType;
    public Point3D extentMinimum;
    public Point3D extentMaximum;
    private Handle plotViewHandle;
    private Handle visualStyleHandle;
    private Handle paperspaceBlockRecordHandle;
    private Handle lastActiveViewportHandle;
    private Handle baseUcsHandle;
    private Handle namedUcsHandle;
    private Handle[] viewportHandles;

    public Layout(ObjectMap objectMap) {
        super(objectMap);
    }
    
    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.81 LAYOUT (82)  page 196

        pageSetupName = stringStream.getTU();
        printerOrConfig = stringStream.getTU();
        plotLayoutFlags = dataStream.getBS();
        leftMargin = dataStream.getBD();
        bottomMargin = dataStream.getBD();
        rightMargin = dataStream.getBD();
        topMargin = dataStream.getBD();
        paperWidth = dataStream.getBD();
        paperHeight = dataStream.getBD();
        paperSize = stringStream.getTU();
        plotOrigin = dataStream.get2BD();
        paperUnits = dataStream.getBS();
        plotRotation = dataStream.getBS();
        plotType = dataStream.getBS();
        windowMinimum = dataStream.get2BD();
        windowMaximum = dataStream.get2BD();
        realWorldUnits = dataStream.getBD();
        drawingUnits = dataStream.getBD();
        currentStyleSheet = stringStream.getTU();
        scaleType = dataStream.getBS();
        scaleFactor = dataStream.getBD();
        paperImageOrigin = dataStream.get2BD();
        shadePlotMode = dataStream.getBS();
        shadePlotResLevel = dataStream.getBS();
        shadePlotCustomDpi = dataStream.getBS();
        layoutName = stringStream.getTU();
        tabOrder = dataStream.getBS();
        flag = dataStream.getBS();
        ucsOrigin = dataStream.get3BD();
        limitMinimum = dataStream.get2RD();
        limitMaximum = dataStream.get2RD();
        insertionPoint = dataStream.get3BD();
        ucsXAxis = dataStream.get3BD();
        ucsYAxis = dataStream.get3BD();
        elevation = dataStream.getBD();
        orthographicViewType = dataStream.getBS();
        extentMinimum = dataStream.get3BD();
        extentMaximum = dataStream.get3BD();
        
        // Only 2 bits left, so certainly not a raw long!
        // Bit long more likely.
        int viewportCount = dataStream.getBL();
    
        // The handles

        plotViewHandle = handleStream.getHandle();
        visualStyleHandle = handleStream.getHandle(handleOfThisObject);
        paperspaceBlockRecordHandle = handleStream.getHandle(handleOfThisObject);
        lastActiveViewportHandle = handleStream.getHandle(handleOfThisObject);
        baseUcsHandle = handleStream.getHandle();
        namedUcsHandle = handleStream.getHandle();
        viewportHandles = new Handle[viewportCount];
        for (int i = 0; i < viewportCount; i++) {
            viewportHandles[i] = handleStream.getHandle(handleOfThisObject);
        }
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public CadObject getPlotView() {
        CadObject result = objectMap.parseObjectPossiblyNull(plotViewHandle);
        return result;
    }

    public CadObject getVisualStyle() {
        CadObject result = objectMap.parseObjectPossiblyNull(visualStyleHandle);
        return result;
    }

    public BlockHeader getPaperspaceBlockRecord() {
        CadObject result = objectMap.parseObject(paperspaceBlockRecordHandle);
        return (BlockHeader)result;
    }

    public CadObject getLastActiveViewport() {
        CadObject result = objectMap.parseObject(lastActiveViewportHandle);
        if (!(result instanceof VPort || result instanceof ViewPort)) {
            throw new RuntimeException("unexpected object class");
        }
        return result;
    }

    public CadObject getBaseUcs() {
        CadObject result = objectMap.parseObjectPossiblyNull(baseUcsHandle);
        return result;
    }

    public CadObject getNamedUcs() {
        CadObject result = objectMap.parseObjectPossiblyNull(namedUcsHandle);
        return result;
    }

    // TODO common base for ViewPort and VPort ????
    public List<CadObject> getViewPorts()
    {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index)
            {
                CadObject result = objectMap.parseObject(viewportHandles[index]);
                if (!(result instanceof VPort || result instanceof ViewPort)) {
                    throw new RuntimeException("unexpected object class");
                }
                return (CadObject) result;
            }

            @Override
            public int size()
            {
                return viewportHandles.length;
            }
        };
    }

	public String toString() {
		return "LAYOUT";
	}

}
