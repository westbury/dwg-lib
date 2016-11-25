package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Insert extends EntityObject {

    public Point3D insertPoint;
    public double xScaleFactor;
    public double yScaleFactor;
    public double zScaleFactor;
    public double rotation;
    public Point3D extrusion;
    private Handle blockHeaderHandle;
    private Handle[] ownedObjectHandles;
    private Handle seqEndHandle;

    public Insert(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.9 INSERT (7) page 111    

        insertPoint = dataStream.get3BD();
        int dataFlags = dataStream.getBB();
        switch (dataFlags) {
        case 0:
            xScaleFactor = dataStream.getRD();
            yScaleFactor = dataStream.getDD(xScaleFactor);
            zScaleFactor = dataStream.getDD(xScaleFactor);
            break;
        case 1:
            xScaleFactor = 1.0;
            yScaleFactor = dataStream.getDD(xScaleFactor);
            zScaleFactor = dataStream.getDD(xScaleFactor);
            break;
        case 2:
            xScaleFactor = dataStream.getRD();
            yScaleFactor = xScaleFactor;
            zScaleFactor = xScaleFactor;
            break;
        case 3:
            xScaleFactor = 1.0;
            yScaleFactor = 1.0;
            zScaleFactor = 1.0;
            break;
        }


        rotation = dataStream.getBD();
        extrusion = dataStream.get3BD();
        boolean hasAttributes = dataStream.getB();
        int ownedObjectCount = 0;
        if (hasAttributes) {
            ownedObjectCount = dataStream.getBL();
        }
        
        
        // The handles
        
        blockHeaderHandle = handleStream.getHandle();

        ownedObjectHandles = new Handle[ownedObjectCount];
        for (int i = 0; i< ownedObjectCount; i++) {
            ownedObjectHandles[i] = handleStream.getHandle();
        }

        if (hasAttributes) {
            seqEndHandle = handleStream.getHandle();
        }

        handleStream.advanceToByteBoundary();
        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        try {
        handleStream.assertEndOfStream();
        } catch (Exception e) {
            System.out.println("exception to be investigated");
        }
    }

    public String toString() {
        return "INSERT";
    }

    public BlockHeader getBlockHeader() {
        CadObject result = objectMap.parseObject(blockHeaderHandle);
        return (BlockHeader) result;
    }

    public List<CadObject> getOwnedObjects()
    {
        return new AbstractList<CadObject>() {

            @Override
            public CadObject get(int index)
            {
                CadObject result = objectMap.parseObject(ownedObjectHandles[index]);
                return (CadObject) result;
            }

            @Override
            public int size()
            {
                return ownedObjectHandles.length;
            }
        };
    }

    public CadObject getSeqEnd() {
        if (seqEndHandle == null) {
            return null;
        } else {
            CadObject result = objectMap.parseObject(seqEndHandle);
            return (CadObject) result;
        }
    }

}
