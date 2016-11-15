package objects;

import java.util.ArrayList;
import java.util.List;

import bitstreams.BitBuffer;
import bitstreams.Handle;
import bitstreams.Point3D;
import dwglib.FileVersion;

public class Insert extends NonEntityObject {

    public Point3D insertPoint;
    public double f41;
    public double f42;
    public double f43;
    public double rotation;
    public Point3D extrusion;
    public Handle blockHeaderHandle;
    public List<Handle> ownedObjectHandles;
    public Handle seqEndHandle;

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
        int ownedObjectCount = dataStream.getBL();

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
        handleStream.assertEndOfStream();
    }

    public String toString() {
        return "INSERT";
    }

}
