package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.Issues;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Extrusion;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.writer.BitWriter;

public class TwoDPolyline extends EntityObject {

    public boolean isClosed;
    public boolean curveFitVerticesAdded;
    public boolean splineFitVerticesAdded;
    public boolean is3DPolyline;
    public boolean is3DPolygonMesh;
    public boolean isMeshClosedInNDirection;
    public boolean isPolyfaceMesh;
    public boolean continuousLinetypePattern;
    public int curveType;
    public double startWidth;
    public double endWidth;
    public double thickness;
    public double elevation;
    public Extrusion extrusion;
    private Handle[] ownedObjectHandles;
    private Handle seqEndHandle;

    public TwoDPolyline(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.16 2D POLYLINE (15) page 115

        int flags = dataStream.getBS();

        isClosed = (flags & 0x01) != 0;
        curveFitVerticesAdded = (flags & 0x02) != 0;
        splineFitVerticesAdded = (flags & 0x04) != 0;
        is3DPolyline = (flags & 0x08) != 0;
        is3DPolygonMesh = (flags & 0x10) != 0;
        isMeshClosedInNDirection = (flags & 0x20) != 0;
        isPolyfaceMesh = (flags & 0x40) != 0;
        continuousLinetypePattern = (flags & 0x80) != 0;

        curveType = dataStream.getBS();
        startWidth = dataStream.getBD();
        endWidth = dataStream.getBD();
        thickness = dataStream.getBT();
        elevation = dataStream.getBD();
        extrusion = dataStream.getBE();
        int ownedObjectCount = dataStream.getBL();

        // The Handles

        ownedObjectHandles = new Handle[ownedObjectCount];
        for (int i = 0; i< ownedObjectCount; i++) {
            ownedObjectHandles[i] = handleStream.getHandle();
        }

        seqEndHandle = handleStream.getHandle();

        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    @Override
    protected void writeObjectTypeSpecificData(byte[] byteArray, BitWriter dataStream, BitWriter stringStream,
            BitWriter handleStream, Issues issues) {

        // 19.4.16 2D POLYLINE (15) page 115

        int flags = 0;
        if (isClosed) {
            flags |= 0x01;
        }
        if (curveFitVerticesAdded) {
            flags |= 0x02;
        }
        if (splineFitVerticesAdded) {
            flags |= 0x04;
        }
        if (is3DPolyline) {
            flags |= 0x08;
        }
        if (is3DPolygonMesh) {
            flags |= 0x10;
        }
        if (isMeshClosedInNDirection) {
            flags |= 0x20;
        }
        if (isPolyfaceMesh) {
            flags |= 0x40;
        }
        if (continuousLinetypePattern) {
            flags |= 0x80;
        }
        dataStream.putBS(flags);

        dataStream.putBS(curveType);
        dataStream.putBD(startWidth);
        dataStream.putBD(endWidth);
        dataStream.putBT(thickness);
        dataStream.putBD(elevation);
        dataStream.putBE(extrusion);

        dataStream.putBL(ownedObjectHandles.length);

        // The Handles

        for (Handle ownedObjectHandle : ownedObjectHandles) {
            handleStream.putHandle(ownedObjectHandle);
        }

        handleStream.putHandle(seqEndHandle);

    }

    @Override
    public int getObjectType() {
        return 15;
    }

    @Override
    public String toString() {
        return "2D POLYLINE";
    }

    public List<EntityObject> getOwnedObjects()
    {
        return new AbstractList<EntityObject>() {

            @Override
            public EntityObject get(int index)
            {
                CadObject result = objectMap.parseObject(ownedObjectHandles[index]);
                return (EntityObject) result;
            }

            @Override
            public int size()
            {
                return ownedObjectHandles.length;
            }
        };
    }

    public SeqEnd getSeqEnd() {
        CadObject result = objectMap.parseObjectPossiblyNull(seqEndHandle);
        return (SeqEnd) result;
    }

}
