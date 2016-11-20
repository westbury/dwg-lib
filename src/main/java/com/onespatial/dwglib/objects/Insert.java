package com.onespatial.dwglib.objects;

import java.util.ArrayList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;
import com.onespatial.dwglib.bitstreams.Point3D;

public class Insert extends EntityObject {

    public Point3D insertPoint;
    public double f41;
    public double f42;
    public double f43;
    public double rotation;
    public Point3D extrusion;
    public Handle blockHeaderHandle;
    public List<Handle> ownedObjectHandles;
    public Handle seqEndHandle;

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
            f41 = dataStream.getRD();
            f42 = dataStream.getDD(f41);
            f43 = dataStream.getDD(f41);
            break;
        case 1:
            f41 = 1.0;
            f42 = dataStream.getDD(f41);
            f43 = dataStream.getDD(f41);
            break;
        case 2:
            f41 = dataStream.getRD();
            f42 = f41;
            f43 = f41;
            break;
        case 3:
            f41 = 1.0;
            f42 = 1.0;
            f43 = 1.0;
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

        ownedObjectHandles = new ArrayList<>();
        for (int i = 0; i< ownedObjectCount; i++) {
            Handle ownedAttributeHandle = handleStream.getHandle();
            ownedObjectHandles.add(ownedAttributeHandle);
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

}
