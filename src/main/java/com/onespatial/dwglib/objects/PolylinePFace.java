package com.onespatial.dwglib.objects;

import java.util.AbstractList;
import java.util.List;

import com.onespatial.dwglib.FileVersion;
import com.onespatial.dwglib.bitstreams.BitBuffer;
import com.onespatial.dwglib.bitstreams.Handle;

public class PolylinePFace extends EntityObject {

    public int numVertexes;

    public int numFaces;
    
    private Handle[] vertexHandles;

    private Handle seqEndHandle;

	public PolylinePFace(ObjectMap objectMap) {
        super(objectMap);
    }

    @Override
    public void readObjectTypeSpecificData(BitBuffer dataStream, BitBuffer stringStream, BitBuffer handleStream, FileVersion fileVersion) {
        // 19.4.31 POLYLINE (PFACE) (29) page 127

        numVertexes = dataStream.getBS();
        numFaces = dataStream.getBS();
        int ownedObjectCount = dataStream.getBL();
        
        // The handles
        
        vertexHandles = new Handle[ownedObjectCount];
        for (int i = 0; i < ownedObjectCount; i++) {
            vertexHandles[i] = handleStream.getHandle(handleOfThisObject);
        }
        seqEndHandle = handleStream.getHandle();
        
        handleStream.advanceToByteBoundary();

        dataStream.assertEndOfStream();
        stringStream.assertEndOfStream();
        handleStream.assertEndOfStream();
    }

    public String toString() {
		return "POLYLINE (PFACE)";
	}

    public List<EntityObject> getOwnedObjects()
    {
        return new AbstractList<EntityObject>() {

            @Override
            public EntityObject get(int index)
            {
                CadObject result = objectMap.parseObject(vertexHandles[index]);
                return (EntityObject) result;
            }

            @Override
            public int size()
            {
                return vertexHandles.length;
            }
        };
    }

    public CadObject getSeqEnd() {
        CadObject result = objectMap.parseObjectPossiblyNull(seqEndHandle);
        return (CadObject) result;
    }

}
